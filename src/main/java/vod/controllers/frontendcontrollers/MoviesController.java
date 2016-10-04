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
import vod.helpers.StaticFactory;
import vod.models.Movie;

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
            _start = 1;

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
                if (order != "asc" && order != "desc")
                    throw new SortOrderParameterException(order);
            } else
                order = "asc";
        }
        else {
                property = "id";
                order = "asc";
            }

        if(order == "asc")
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
     * Checks wether a movie property can be used to sort.
     * @param property The property to sort with.
     * @return True when property is valid. False otherwise.
     */
    private boolean isSortableProperty(String property)
    {
        return StaticFactory.sortableMovieProperties().contains(property.toLowerCase());
    }

    /**
     * Checks if a string is a type of movie genre.
     * @param genre The inquiery string.
     * @return True if the string is a movie genre. False otherwise.
     */
    private boolean isMovieGenre(String genre)
    {
        return StaticFactory.movieGenres().contains(genre.toLowerCase());
    }

    /**
     * Thrown when the requested movie with id is not found.
     */
    @ResponseStatus(HttpStatus.NOT_FOUND)
    class MovieNotFoundException extends RuntimeException {
        public MovieNotFoundException(int id) {
            super("Could not find movie id: " + id + ".");
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
