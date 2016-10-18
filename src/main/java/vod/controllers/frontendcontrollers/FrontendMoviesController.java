/**
 * Handles all requests to .../movies/?
 */
package vod.controllers.frontendcontrollers;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import vod.exceptions.*;
import vod.helpers.MultipartFileSender;
import vod.helpers.StaticFactory;
import vod.models.*;
import vod.repositories.CommentsRepository;
import vod.repositories.MoviesRepository;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * The movies controller.
 *
 * @sinnce 1.0
 */
@RestController
@RequestMapping(value = "/movies")
@Api(value = "movies", description = "movies API")
public class FrontendMoviesController {
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
     * @param page     The page.
     * @param size     The size of one page.
     * @param genre    The genre of the movie.
     * @param property The property of the movie.
     * @param order    The sorting order.
     * @param sort     Whether to sort.
     * @return A list of movies.
     */
    @RequestMapping(value = "/", method = RequestMethod.GET)
    @ApiOperation(value = "Get movies", notes = "Returns a list of all movies.")
    @ResponseBody
    public ResponseEntity<List<Movie>> getMovies(@RequestParam(value = "page", required = false) String page,
                                                 @RequestParam(value = "size", required = false) String size,
                                                 @RequestParam(value = "genre", required = false) String genre,
                                                 @RequestParam(value = "property", required = false) String property,
                                                 @RequestParam(value = "order", required = false) String order,
                                                 @RequestParam(value = "sort", required = false) String sort) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setLocation(ServletUriComponentsBuilder
                .fromCurrentRequest().path("/").build().toUri());

        int _page;
        int _size;
        Sort.Direction direction;

        //combinations or params
        if (page != null) {

            try {
                _page = Integer.parseInt(page);
            } catch (NumberFormatException e) {
                throw new ParameterNumberFormatException("page", page);
            }
        } else
            _page = 0;

        if (size != null) {
            try {
                _size = Integer.parseInt(size);
            } catch (NumberFormatException e) {
                throw new ParameterNumberFormatException("size", size);
            }
        } else
            _size = (int) moviesRepository.count();


        if (sort != null) {
            if (property != null) {
                if (!StaticFactory.isSortableProperty(property))
                    throw new NotSortableParameterException(property);
            } else
                property = "id";

            if (order != null) {
                if (!order.equals("asc") && !order.equals("desc"))
                    throw new SortOrderParameterException(order);
            } else
                order = "asc";
        } else {
            property = "id";
            order = "asc";
        }

        if (order.equals("asc"))
            direction = Sort.Direction.ASC;
        else
            direction = Sort.Direction.DESC;

        if (genre != null)
            if (!StaticFactory.isMovieGenre(genre))
                throw new InvalidGenreParameterException(genre);

        if (genre != null) {
            PageRequest pageRequest = new PageRequest(_page, _size, new Sort(direction, property));
            Iterable<Movie> movies = moviesRepository.findByGenre(genre, pageRequest);
            List<Movie> result = new ArrayList<>();
            movies.forEach(movie -> result.add(movie));
            return new ResponseEntity<List<Movie>>(result, httpHeaders, HttpStatus.OK);
        } else {
            PageRequest pageRequest = new PageRequest(_page, _size, new Sort(direction, property));
            Page<Movie> movies = moviesRepository.findAll(pageRequest);
            List<Movie> result = new ArrayList<>();
            movies.forEach(movie -> result.add(movie));
            return new ResponseEntity<>(result, httpHeaders, HttpStatus.OK);
        }

    }


    /**
     * * Gets a movie specifying the id.
     * The optional parameters include
     * <ul>
     * <li><strong>play</strong> Any value of play returns the movie ready for playback.</li>
     * </ul>
     *
     * @param id       The id of the movie.
     * @param play     To play.
     * @param request  Request.
     * @param response Response.
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @ApiOperation(value = "Get a movie", notes = "Gets the movie with id = {id}")
    @ResponseBody
    public ResponseEntity<Movie> getMovie(@PathVariable("id") String id, @RequestParam(value = "play", required = false) String play, HttpServletRequest request, HttpServletResponse response) throws Exception {
        Movie movie = validateMovieId(id);

        if (play != null) {
            movie.setViews(movie.getViews() + 1);
            moviesRepository.save(movie);
            MultipartFileSender.fromFile(new File(movie.getVideofile())).with(request).with(response).serveResource();
        }

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setLocation(ServletUriComponentsBuilder
                .fromCurrentRequest().path("/").buildAndExpand(id).toUri());
        return new ResponseEntity<>(movie, httpHeaders, HttpStatus.OK);
    }

    @ResponseBody
    @RequestMapping(value = "/search", method = RequestMethod.GET)
    @ApiOperation(value = "Get movies matching search title", notes = "If direct match, return movie. Else, return all movies containing search string")
    public ResponseEntity<List<Movie>> getMoviesBySearchTitle(@PathVariable("title") String title){
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setLocation(ServletUriComponentsBuilder
                .fromCurrentRequest().path("/").buildAndExpand("search").toUri());

        List<Movie> movies = moviesRepository.findByTitle(title);
        if(movies.size() != 0)
            return new ResponseEntity<>(movies, httpHeaders, HttpStatus.OK);

        List<Movie> _movies = new ArrayList<>();
        movies = moviesRepository.findAll();
        for(int i = 0; i < movies.size(); i++){
            if(movies.get(i).getTitle().toLowerCase().contains(title.toLowerCase()))
                _movies.add(movies.get(i));
        }
        return new ResponseEntity<>(_movies,httpHeaders,HttpStatus.OK);
    }

    /**
     * * Gets a list of similar movies to this movie by genre similarity.
     * Uses all parameters as /movies except genre.
     *
     * @param id       The id.
     * @param page     The page.
     * @param size     The page size.
     * @param sort     To sort?
     * @param property Sorting property.
     * @param order    Sorting order.
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/{id}/similar", method = RequestMethod.GET)
    @ApiOperation(value = "Gets similar movies", notes = "Gets similar movies to movie with id = {id}")
    @ResponseBody
    public ResponseEntity<List<Movie>> getMovie(@PathVariable("id") String id,
                                                @RequestParam(value = "page", required = false) String page,
                                                @RequestParam(value = "size", required = false) String size,
                                                @RequestParam(value = "sort", required = false) String sort,
                                                @RequestParam(value = "property", required = false) String property,
                                                @RequestParam(value = "order", required = false) String order) throws Exception {
        Movie movie = validateMovieId(id);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setLocation(ServletUriComponentsBuilder
                .fromCurrentRequest().path("/").build().toUri());

        int _page;
        int _size;
        Sort.Direction direction;

        //combinations or params
        if (page != null) {

            try {
                _page = Integer.parseInt(page);
            } catch (NumberFormatException e) {
                throw new ParameterNumberFormatException("page", page);
            }
        } else
            _page = 0;

        if (size != null) {
            try {
                _size = Integer.parseInt(size);
            } catch (NumberFormatException e) {
                throw new ParameterNumberFormatException("size", size);
            }
        } else
            _size = (int) moviesRepository.count();


        if (sort != null) {
            if (property != null) {
                if (!StaticFactory.isSortableProperty(property))
                    throw new NotSortableParameterException(property);
            } else
                property = "id";

            if (order != null) {
                if (!order.equals("asc") && !order.equals("desc"))
                    throw new SortOrderParameterException(order);
            } else
                order = "asc";
        } else {
            property = "id";
            order = "asc";
        }

        if (order.equals("asc"))
            direction = Sort.Direction.ASC;
        else
            direction = Sort.Direction.DESC;

        PageRequest pageRequest = new PageRequest(_page, _size, new Sort(direction, property));
        Iterable<Movie> movies = moviesRepository.findByGenre(movie.getGenre(), pageRequest);
        ArrayList<Movie> result = new ArrayList<>();
        movies.forEach(m -> result.add(m));
        return new ResponseEntity<List<Movie>>(result, httpHeaders, HttpStatus.OK);
    }

    /**
     * Gets the movie genres recognized by the system.
     *
     * @return A list of movie genres.
     */
    @ResponseBody
    @RequestMapping(value = "/genres", method = RequestMethod.GET)
    @ApiOperation(value = "List of genres", notes = "Gets the list of genres supported")
    public ResponseEntity<List<String>> getGenres() {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setLocation(ServletUriComponentsBuilder
                .fromCurrentRequest().path("/").build().toUri());
        return new ResponseEntity<List<String>>(StaticFactory.getMovieGenres(), httpHeaders, HttpStatus.OK);
    }

    /**
     * Gets all comments for a movie.
     * Additional parameters include page and size for paginated requests.
     *
     * @param id   The id of the movie.
     * @param page The page number.
     * @param size The page size.
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/{id}/comments", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "Gets comments", notes = "Gets comments for the movie with id = {id}")
    public ResponseEntity<List<Comment>> getComments(@PathVariable("id") String id,
                                                     @RequestParam(value = "page", required = false) String page,
                                                     @RequestParam(value = "size", required = false) String size) throws Exception {
        Movie movie = validateMovieId(id);

        int _page;
        int _size;
        if (page != null) {
            try {
                _page = Integer.parseInt(page);
            } catch (NumberFormatException e) {
                throw new ParameterNumberFormatException("page", page);
            }
        } else
            _page = 0;
        if (size != null) {
            try {
                _size = Integer.parseInt(size);
            } catch (NumberFormatException e) {
                throw new ParameterNumberFormatException("size", size);
            }
        } else
            _size = (int) commentsRepository.count();

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setLocation(ServletUriComponentsBuilder
                .fromCurrentRequest().path("/").buildAndExpand(id).toUri());

        PageRequest pageRequest = new PageRequest(_page, _size, new Sort(Sort.Direction.ASC, "date"));
        Iterable<Comment> comments = commentsRepository.findByMovieid(id, pageRequest);
        List<Comment> result = new ArrayList<>();
        comments.forEach(c -> result.add(c));
        return new ResponseEntity<List<Comment>>(result, httpHeaders, HttpStatus.OK);
    }

    /**
     * Sets a comment to the movie
     *
     * @param id      the id of the movie.
     * @param comment The comment to set.
     * @return Nothing.
     */
    @RequestMapping(value = "/{id}/comments", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value = "Add comment", notes = "Adds a comment to the movie with id = {id}")
    public ResponseEntity<?> addComment(@PathVariable("id") String id, @Valid @RequestBody Comment comment) throws Exception {
        validateMovieComment(comment);
        if (comment.getUser() == null)
            comment.setUser(new User("Anonymous"));

        commentsRepository.save(comment);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setLocation(ServletUriComponentsBuilder
                .fromCurrentRequest().path("/").buildAndExpand(comment.getId()).toUri());
        return new ResponseEntity<>(null, httpHeaders, HttpStatus.CREATED);
    }

    /**
     * Increments the number of likes for the movie.
     *
     * @param id The id of the movie to update.
     * @return
     */
    @RequestMapping(value = "/{id}/like", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "Like a movie", notes = "Likes the movie with id = {id}")
    public ResponseEntity<PropertyValue> addLike(@PathVariable("id") String id) throws Exception {
        Movie movie = validateMovieId(id);
        movie.setLikes(movie.getLikes() + 1);
        moviesRepository.save(movie);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setLocation(ServletUriComponentsBuilder
                .fromCurrentRequest().path("/").buildAndExpand(id).toUri());
        return new ResponseEntity<>(new PropertyValue("likes", new Integer(movie.getLikes()).toString()), httpHeaders, HttpStatus.OK);

    }

    /**
     * Adds a dislike to the movie.
     *
     * @param id The id of the movie to dislike.
     * @return The new number of likes.
     * @throws Exception
     */
    @RequestMapping(value = "/{id}/dislike", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "Dislike movie", notes = "Adds a dislike to the movie with id = {id}")
    public ResponseEntity<PropertyValue> addDislike(@PathVariable("id") String id) throws Exception {
        Movie movie = validateMovieId(id);
        movie.setDislikes(movie.getDislikes() + 1);
        moviesRepository.save(movie);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setLocation(ServletUriComponentsBuilder
                .fromCurrentRequest().path("/").buildAndExpand(id).toUri());
        return new ResponseEntity<>(new PropertyValue("dislikes", new Integer(movie.getDislikes()).toString()), httpHeaders, HttpStatus.OK);

    }

    /**
     * * * Adds a rating to the movie.
     * Request parameters include the numberofstars and a value.
     * e.g ?rating = onestar
     * Ratings include [onestar, twostars, threestars, fourstars, fivestars]
     *
     * @param rating The rating.
     * @param id     The movie id.
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/{id}/rate", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "Add a rating", notes = "Adds a rating to the movie with id ={id}")
    public ResponseEntity<Rating> addRating(@RequestParam("rating") String rating, @PathVariable("id") String id) throws Exception {
        Movie movie = validateMovieId(id);
        Rating movieRating = movie.getRating();

        if (rating.equals("onestar"))
            movieRating.setOnestar(movieRating.getOnestar() + 1);
        else if (rating.equals("twostars"))
            movieRating.setTwostars(movieRating.getTwostars() + 1);
        else if (rating.equals("threestars"))
            movieRating.setThreestars(movieRating.getThreestars() + 1);
        else if (rating.equals("fourstars"))
            movieRating.setFourstars(movieRating.getFourstars() + 1);
        else if (rating.equals("fivestars"))
            movieRating.setFivestars(movieRating.getFivestars() + 1);
        else
            throw new InvalidRatingParameterException(rating);

        movie.setRating(movieRating);

        //TODO: Update overall rating.

        moviesRepository.save(movie);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setLocation(ServletUriComponentsBuilder.fromCurrentRequest().path("/" + id + "/rating").build().toUri());
        return new ResponseEntity<>(movieRating, httpHeaders, HttpStatus.OK);
    }

    /**
     * Checks that the comment has required properties.
     *
     * @param comment
     */
    private void validateMovieComment(Comment comment) throws Exception {
        if (comment.getMovieid() != null) validateMovieId(comment.getMovieid());
        else throw new MovieIdExpectedException();
    }

    /**
     * Checks is a movie with the id exists and returns is.
     * Else, throw MovieNotFoundException.
     *
     * @param id The id of the movie to get.
     * @return The movie.
     */
    private Movie validateMovieId(String id) throws Exception {
        Movie movie = this.moviesRepository.findById(id);
        if (movie == null)
            throw new MovieNotFoundException(id);
        return movie;
    }
}

