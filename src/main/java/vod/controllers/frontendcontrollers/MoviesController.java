/**
 * Handles all requests to .../movies/?
 */
package vod.controllers.frontendcontrollers;

import org.omg.CORBA.DynAnyPackage.Invalid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.NumberFormat;
import org.springframework.http.*;
import org.springframework.http.client.support.HttpRequestWrapper;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import vod.Repositories.MoviesRepository;
import vod.controllers.CustomerController;
import vod.helpers.MultipartFileSender;
import vod.helpers.StaticFactory;
import vod.models.Customer;
import vod.models.Movie;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.ws.Response;
import java.io.File;
import java.net.URI;
import java.util.*;

/**
   The movies controller.
 @sinnce 1.0
 */
@RestController
@RequestMapping(value = "/movies")
public class MoviesController {
    @Autowired
    MoviesRepository repository;

    /**
     * Gets a list of movies as prescribed by the request.
     * The optional parameters include:
     * <ul>
     * <li><strong>start.</strong> An integer specifying the start index of the movies to return.</li>
     * <li><strong>count.</strong> The number of movies to return starting at <i>start</i></li>
     * <li><strong>genre.</strong> The genre of the movies to return.</li>
     * <li><strong>releaseyear.</strong> The year the movie was released.</li>
     * <li><strong>sort.</strong> Sorts the list according to provided arguments. Can be anything [true]
     * <ul>
     * <li><strong>property.</strong> Can be [id,title,likes,views,dislikes,overallrating, releasedate]. Must be used with <i>sort</i></li>
     * <li><strong>order.</strong> Can be [desc,asc]. Must be used with <i>sort</i></li>
     * </ul></li>
     * </ul>
     * <p>With no parameter, it returns all entries</p>
     *
     * @param params The optional parameters in the query string.
     * @return The list of movies
     */
    @RequestMapping(value = "/", method = RequestMethod.GET)
    public ResponseEntity<List<Movie>> getMovies(@RequestParam Map<String, String> params) throws Exception {
        String start = params.get("start");
        String count = params.get("count");
        String genre = params.get("genre");
        String sort = params.get("sort");
        String property = params.get("property");
        String order = params.get("order");

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setLocation(ServletUriComponentsBuilder
                .fromCurrentRequest().path("/").build().toUri());

        int _start;
        int _count;
        Sort.Direction direction;

        //combinations or params
        if (start != null)
        {

            try {
                _start = Integer.parseInt(start);
            } catch (NumberFormatException e) {
                throw new ParameterNumberFormatException("start", start);
            }
        }
        else
            _start = 0;

        if(count != null) {
            try {
                _count = Integer.parseInt(count);
            } catch (NumberFormatException e) {
                throw new ParameterNumberFormatException("count", count);
            }
        }
        else
            _count = (int)repository.count();


        if(sort != null) {
            if (property != null) {
                if (!isSortableProperty(property))
                    throw new NotSortableParameterException(property);
            } else
                property = "id";

            if (order != null) {
                if (!order.equals("asc") && !order.equals("desc"))
                    throw new SortOrderParameterException(order);
            } else
                order = "asc";
        }
        else {
                property = "id";
                order = "asc";
            }

        if(order.equals("asc"))
            direction = Sort.Direction.ASC;
        else
            direction = Sort.Direction.DESC;

        if(genre != null)
            if(!isMovieGenre(genre))
                throw new InvalidGenreParameterException(genre);

        if(genre != null) {
            PageRequest pageRequest = new PageRequest(_start,_count,new Sort(direction,property));
            Iterable<Movie> movies = repository.findByGenre(genre,pageRequest);
            List<Movie> result = new ArrayList<>();
            movies.forEach(movie -> result.add(movie));
            return new ResponseEntity<List<Movie>>(result, httpHeaders, HttpStatus.OK);
        }
        else
        {
            PageRequest pageRequest = new PageRequest(_start,_count,new Sort(direction,property));
            Page<Movie> movies = repository.findAll(pageRequest);
            List<Movie> result = new ArrayList<>();
            movies.forEach(movie -> result.add(movie));
            return new ResponseEntity<List<Movie>>(result, httpHeaders, HttpStatus.OK);
        }

    }


    /**
     * Gets a movie specifying the id.
     * The optional parameters include
     * <ul>
     *     <li><strong>play</strong> Any value of play returns the movie ready for playback.</li>
     * </ul>
     * @param id The id of the movie to get.
     * @param params Extra parameters.
     * @return The movie instance.
     */
    @RequestMapping(value= "/{id}", method = RequestMethod.GET)
    public ResponseEntity<Movie> getMovie(@PathVariable("id") String id, @RequestParam Map<String,String> params, HttpServletRequest request, HttpServletResponse response) throws Exception
    {
        Movie movie = validateMovieId(id);
        String play = params.get("play");

        if(play != null)
            MultipartFileSender.fromFile(new File(movie.getVideofile())).with(request).with(response).serveResource();

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setLocation(ServletUriComponentsBuilder
                .fromCurrentRequest().path("/").buildAndExpand(id).toUri());
        return new ResponseEntity<>(movie, httpHeaders, HttpStatus.OK);
    }

    /**
     * Gets a list of similar movies to this movie by genre similarity.
     * Uses all parameters as /movies except genre.
     * @param id The id of this movie.
     * @return A list of similar movies.
     */
    @RequestMapping(value = "/{id}/similar", method=RequestMethod.GET)
    public ResponseEntity<List<Movie>> getMovie(@RequestParam Map<String, String> params, @PathVariable("id") String id)
    {
        Movie movie = validateMovieId(id);

        String start = params.get("start");
        String count = params.get("count");
        String sort = params.get("sort");
        String property = params.get("property");
        String order = params.get("order");

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setLocation(ServletUriComponentsBuilder
                .fromCurrentRequest().path("/").build().toUri());

        int _start;
        int _count;
        Sort.Direction direction;

        //combinations or params
        if (start != null)
        {

            try {
                _start = Integer.parseInt(start);
            } catch (NumberFormatException e) {
                throw new ParameterNumberFormatException("start", start);
            }
        }
        else
            _start = 0;

        if(count != null) {
            try {
                _count = Integer.parseInt(count);
            } catch (NumberFormatException e) {
                throw new ParameterNumberFormatException("count", count);
            }
        }
        else
            _count = (int)repository.count();


        if(sort != null) {
            if (property != null) {
                if (!isSortableProperty(property))
                    throw new NotSortableParameterException(property);
            } else
                property = "id";

            if (order != null) {
                if (!order.equals("asc") && !order.equals("desc"))
                    throw new SortOrderParameterException(order);
            } else
                order = "asc";
        }
        else {
            property = "id";
            order = "asc";
        }

        if(order.equals("asc"))
            direction = Sort.Direction.ASC;
        else
            direction = Sort.Direction.DESC;

        PageRequest pageRequest = new PageRequest(_start,_count,new Sort(direction,property));
        Iterable<Movie> movies = repository.findByGenre(movie.getGenre(),pageRequest);
        Iterator<Movie> it = movies.iterator();
        ArrayList<Movie> result = new ArrayList<>();
        while(it.hasNext())
            result.add(it.next());
        return new ResponseEntity<List<Movie>>(result, httpHeaders, HttpStatus.OK);
    }

    /**
     * Gets the movie genres recognized by the system.
     * @return A list of movie genres.
     */
    @RequestMapping(value = "/genres", method=RequestMethod.GET)
    public ResponseEntity<List<String>> getGenres()
    {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setLocation(ServletUriComponentsBuilder
                .fromCurrentRequest().path("/").build().toUri());
        return new ResponseEntity<List<String>>(StaticFactory.getMovieGenres(),httpHeaders,HttpStatus.OK);
    }

    /**
     * Checks is a movie with the id exists and returns is.
     * Else, throw MovieNotFoundException.
     * @param id The id of the movie to get.
     * @return The movie.
     */
    private Movie validateMovieId(String id)
    {
        Movie movie = this.repository.findById(id);
        if(movie == null)
            throw new MovieNotFoundException(id);
        return movie;
    }
    /**
     * Checks wether a movie property can be used to sort.
     * @param property The property to sort with.
     * @return True when property is valid. False otherwise.
     */
    private boolean isSortableProperty(String property)
    {
        return StaticFactory.getSortableMovieProperties().contains(property.toLowerCase());
    }

    /**
     * Checks if a string is a type of movie genre.
     * @param genre The inquiery string.
     * @return True if the string is a movie genre. False otherwise.
     */
    private boolean isMovieGenre(String genre)
    {
        return StaticFactory.getMovieGenres().contains(genre.toLowerCase());
    }

    /**
     * Thrown when the requested movie with id is not found.
     */
    @ResponseStatus(HttpStatus.NOT_FOUND)
    class MovieNotFoundException extends RuntimeException {
        public MovieNotFoundException(String id)
        {
            super("Could not find movie with id: " + id + ".");
        }
    }

    /**
     * Thrown when the query string parameter is in wrong number format.
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    class ParameterNumberFormatException extends RuntimeException {
        public ParameterNumberFormatException(String parameter, String value) {
            super("value : [" + value + "] is in wrong number format for parameter [" + parameter + "]. Integer required.");
        }
    }

    /**
     * Thrown when the sort property parameter of the query string is not sortable.
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    class NotSortableParameterException extends RuntimeException
    {
        public NotSortableParameterException(String value)
        {
            super("value: [" + value + "] is not a sortable parameter on movies.");
        }
    }

    /**
     * Thrown when the sort order parameter of the query string is not valid.
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    class SortOrderParameterException extends  RuntimeException
    {
        public SortOrderParameterException(String value)
        {
            super("value: [" + value + "] as sorting order is not recognized. Either [asc or desc]");
        }
    }

    /**
     * Thrown when the requested genre is not valid.
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    class InvalidGenreParameterException extends RuntimeException
    {
        public InvalidGenreParameterException(String value)
        {
            super("value: [" + value + "] as genre order does not exists.");
        }
    }

}
