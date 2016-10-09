/**
 * Handles all requests to .../movies/?
 */
package vod.controllers.frontendcontrollers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import vod.Repositories.CommentsRepository;
import vod.Repositories.MoviesRepository;
import vod.helpers.MultipartFileSender;
import vod.helpers.StaticFactory;
import vod.models.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.File;
import java.util.*;

/**
   The movies controller.
 @sinnce 1.0
 */
@RestController
@RequestMapping(value = "/movies")
public class MoviesController {
    @Autowired
    private MoviesRepository moviesRepository;
    @Autowired
    private CommentsRepository commentsRepository;

    /**
     * Gets a list of movies as prescribed by the request.
     * The optional parameters include:
     * <ul>
     * <li><strong>page.</strong> An integer specifying the page of the movies to return.</li>
     * <li><strong>size.</strong> The size of one page</i></li>
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
        String page = params.get("page");
        String size = params.get("size");
        String genre = params.get("genre");
        String sort = params.get("sort");
        String property = params.get("property");
        String order = params.get("order");

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setLocation(ServletUriComponentsBuilder
                .fromCurrentRequest().path("/").build().toUri());

        int _page;
        int _size;
        Sort.Direction direction;

        //combinations or params
        if (page != null)
        {

            try {
                _page = Integer.parseInt(page);
            } catch (NumberFormatException e) {
                throw new ParameterNumberFormatException("page", page);
            }
        }
        else
            _page = 0;

        if(size != null) {
            try {
                _size = Integer.parseInt(size);
            } catch (NumberFormatException e) {
                throw new ParameterNumberFormatException("size", size);
            }
        }
        else
            _size = (int)moviesRepository.count();


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
            PageRequest pageRequest = new PageRequest(_page,_size,new Sort(direction,property));
            Iterable<Movie> movies = moviesRepository.findByGenre(genre,pageRequest);
            List<Movie> result = new ArrayList<>();
            movies.forEach(movie -> result.add(movie));
            return new ResponseEntity<List<Movie>>(result, httpHeaders, HttpStatus.OK);
        }
        else
        {
            PageRequest pageRequest = new PageRequest(_page,_size,new Sort(direction,property));
            Page<Movie> movies = moviesRepository.findAll(pageRequest);
            List<Movie> result = new ArrayList<>();
            movies.forEach(movie -> result.add(movie));
            return new ResponseEntity<>(result, httpHeaders, HttpStatus.OK);
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
    public ResponseEntity<List<Movie>> getMovie(@RequestParam Map<String, String> params, @PathVariable("id") String id) throws Exception
    {
        Movie movie = validateMovieId(id);

        String page = params.get("page");
        String size = params.get("size");
        String sort = params.get("sort");
        String property = params.get("property");
        String order = params.get("order");

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setLocation(ServletUriComponentsBuilder
                .fromCurrentRequest().path("/").build().toUri());

        int _page;
        int _size;
        Sort.Direction direction;

        //combinations or params
        if (page != null)
        {

            try {
                _page = Integer.parseInt(page);
            } catch (NumberFormatException e) {
                throw new ParameterNumberFormatException("page", page);
            }
        }
        else
            _page = 0;

        if(size != null) {
            try {
                _size = Integer.parseInt(size);
            } catch (NumberFormatException e) {
                throw new ParameterNumberFormatException("size", size);
            }
        }
        else
            _size = (int)moviesRepository.count();


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

        PageRequest pageRequest = new PageRequest(_page,_size,new Sort(direction,property));
        Iterable<Movie> movies = moviesRepository.findByGenre(movie.getGenre(),pageRequest);
        ArrayList<Movie> result = new ArrayList<>();
        movies.forEach(m -> result.add(m));
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
     * Gets all comments for a movie.
     * Additional parameters include page and size for paginated requests.
     * @param id The id of the movie.
     * @return The list of comments.
     */
    @RequestMapping(value  = "/{id}/comments", method = RequestMethod.GET)
    public ResponseEntity<List<Comment>> getComments(@PathVariable ("id") String id, @RequestParam Map<String,String> params) throws Exception
    {
        Movie movie = validateMovieId(id);

        String page = params.get("page");
        String size = params.get("size");

        int _page;
        int _size;
        if(page != null) {
            try {
                _page = Integer.parseInt(page);
            } catch (NumberFormatException e) {
                throw new ParameterNumberFormatException("page", page);
            }
        }else
            _page = 0;
        if(size != null) {
            try {
                _size = Integer.parseInt(size);
            } catch (NumberFormatException e) {
                throw new ParameterNumberFormatException("size", size);
            }
        }else
            _size = (int)commentsRepository.count();

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setLocation(ServletUriComponentsBuilder
                .fromCurrentRequest().path("/").buildAndExpand(id).toUri());

        PageRequest pageRequest = new PageRequest(_page,_size,new Sort(Sort.Direction.ASC,"date"));
        Iterable<Comment> comments = commentsRepository.findByMovieid(id,pageRequest);
        List<Comment> result = new ArrayList<>();
        comments.forEach(c -> result.add(c));
        return new ResponseEntity<List<Comment>>(result, httpHeaders, HttpStatus.OK);
    }

    /**
     * Sets a comment to the movie
     * @param id the id of the movie.
     * @param comment The comment to set.
     * @return Nothing.
     */
    @RequestMapping(value = "/{id}/comments", method=RequestMethod.POST)
    public ResponseEntity<?> addComment(@PathVariable ("id") String id, @Valid @RequestBody Comment comment) throws Exception
    {
        validateComment(comment);
        if(comment.getUser() == null)
            comment.setUser(new User("Anonymous"));

        commentsRepository.save(comment);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setLocation(ServletUriComponentsBuilder
                .fromCurrentRequest().path("/").buildAndExpand(comment.getId()).toUri());
        return new ResponseEntity<>(null,httpHeaders,HttpStatus.CREATED);
    }

    /**
     * Increments the number of likes for the movie.
     * @param id The id of the movie to update.
     * @return
     */
    @RequestMapping(value = "/{id}/like", method=RequestMethod.GET)
    public ResponseEntity<PropertyValue> addLike(@PathVariable ("id") String id) throws Exception
    {
        Movie movie = validateMovieId(id);
        movie.setLikes(movie.getLikes() + 1);
        moviesRepository.save(movie);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setLocation(ServletUriComponentsBuilder
                .fromCurrentRequest().path("/").buildAndExpand(id).toUri());
        return new ResponseEntity<>(new PropertyValue("likes",new Integer(movie.getLikes()).toString()),httpHeaders,HttpStatus.OK);

    }

    /**
     * Adds a dislike to the movie.
     * @param id The id of the movie to dislike.
     * @return The new number of likes.
     * @throws Exception
     */
    @RequestMapping(value = "/{id}/dislike", method=RequestMethod.GET)
    public ResponseEntity<PropertyValue> addDislike(@PathVariable ("id") String id) throws Exception
    {
        Movie movie = validateMovieId(id);
        movie.setDislikes(movie.getDislikes() + 1);
        moviesRepository.save(movie);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setLocation(ServletUriComponentsBuilder
                .fromCurrentRequest().path("/").buildAndExpand(id).toUri());
        return new ResponseEntity<>(new PropertyValue("dislikes", new Integer(movie.getDislikes()).toString()),httpHeaders,HttpStatus.OK);

    }

    /**
     * * Adds a rating to the movie.
     * Request parameters include the numberofstars and a value.
     * e.g ?rating = onestar
     * Ratings include [onestar, twostars, threestars, fourstars, fivestars]
     * @param params The rating parameter
     * @param id The id of the movie to rate
     * @return The new rating.
     * @throws Exception
     */
    @RequestMapping (value = "/{id}/rate", method=RequestMethod.GET)
    public ResponseEntity<Rating> addRating(@RequestParam Map<String,String> params, @PathVariable ("id") String id) throws Exception
    {
        Movie movie = validateMovieId(id);
        Rating movieRating = movie.getRating();
        String rating = params.get("rating");

        if(rating.equals("onestar"))
                movieRating.setOnestar(movieRating.getOnestar() + 1);
        else if(rating.equals("twostars"))
                movieRating.setTwostars(movieRating.getTwostars() + 1);
        else if(rating.equals("threestars"))
            movieRating.setThreestars(movieRating.getThreestars() + 1);
        else if(rating.equals("fourstars"))
            movieRating.setFourstars(movieRating.getFourstars() + 1);
        else if(rating.equals("fivestars"))
            movieRating.setFivestars(movieRating.getFivestars() + 1);
        else
            throw new InvalidRatingParameterException(rating);

        movie.setRating(movieRating);
        moviesRepository.save(movie);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setLocation(ServletUriComponentsBuilder.fromCurrentRequest().path("/" + id + "/rating").build().toUri());
        return new ResponseEntity<>(movieRating, httpHeaders, HttpStatus.OK);
    }

     /**
     * Checks that the comment has required properties.
     * @param comment
     */
    private void validateComment(Comment comment) throws Exception
    {
        if(comment.getMovieid()!=null) validateMovieId(comment.getMovieid());
        else throw new MovieIdExpectedException();
    }
    /**
     * Checks is a movie with the id exists and returns is.
     * Else, throw MovieNotFoundException.
     * @param id The id of the movie to get.
     * @return The movie.
     */
    private Movie validateMovieId(String id) throws Exception
    {
        Movie movie = this.moviesRepository.findById(id);
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

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public class MovieIdExpectedException extends  Exception
    {
        MovieIdExpectedException(){
            super("Expected movie id in path");
        }
    }
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    class CommentNotProperlyFormatted extends RuntimeException {
        public CommentNotProperlyFormatted(String property)
        {
            super("The property :" + property + " cannot be null");
        }
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

    @ResponseStatus (HttpStatus.BAD_REQUEST)
    class InvalidRatingParameterException extends  RuntimeException
    {
        public InvalidRatingParameterException(String value)
        {
            super("value: [ " + value + " ] for rating is invalid. Rating values include" +
            " [onestar, twostars, threestars, fourstars, fivestars].");
        }
    }
}

