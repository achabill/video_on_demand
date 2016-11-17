package vod.controllers.backendcontrollers;

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
import vod.helpers.StaticFactory;
import vod.helpers.TokenService;
import vod.models.Comment;
import vod.models.Movie;
import vod.repositories.CommentsRepository;
import vod.repositories.MoviesRepository;

import java.util.ArrayList;
import java.util.List;

/**
 * The backend movies controller
 */
@RestController
@Api(value = "Admin movies controller", description = "An interface to control the movies   database")
@RequestMapping(value = "admin/movies")
public class BackendMoviesController {

    @Autowired
    private MoviesRepository moviesRepository;
    @Autowired
    private CommentsRepository commentsRepository;
    @Autowired
    private TokenService tokenService;

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
                                                 @RequestParam(value = "sort", required = false) String sort,
                                                 @RequestParam(value = "accesstoken", required = true) String accessToken) throws Exception{
        tokenService.verifyAdmin(accessToken);

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
     * Adds a movie to the database
     *
     * @param movie The movie to add
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/", method = RequestMethod.POST)
    @ApiOperation(value = "Adds a movie", notes = "Adds a new movie to the database")
    @ResponseBody
    public ResponseEntity<?> addMovie(@RequestBody Movie movie,
                                      @RequestParam(value = "accesstoken", required = true) String accessToken) throws Exception {

        tokenService.verifyAdmin(accessToken);
        moviesRepository.save(movie);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setLocation(ServletUriComponentsBuilder
                .fromCurrentRequest().path("/").build().toUri());
        return new ResponseEntity<>(movie, httpHeaders, HttpStatus.CREATED);
    }

    /**
     * Deletes all movies
     *
     * @return
     */
    @ResponseBody
    @ApiOperation(value = "Delete all movies", notes = "Deletes all movies and associated comments in the database")
    @RequestMapping(value = "/", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteAllMovies(@RequestParam(value = "accesstoken", required = true) String accessToken) throws Exception{

        tokenService.verifyAdmin(accessToken);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setLocation(ServletUriComponentsBuilder
                .fromCurrentRequest().path("/").build().toUri());

        List<Movie> movies = moviesRepository.findAll();
        List<Comment> comments = commentsRepository.findAll();
        for (int i = 0; i < comments.size(); i++)
            if (comments.get(i).getMovieid() != null)
                commentsRepository.delete(comments.get(i));
        moviesRepository.deleteAll();

        return new ResponseEntity<>("deleted all movies", httpHeaders, HttpStatus.OK);
    }


    /**
     * Get 1 movie
     *
     * @param id
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @ApiOperation(value = "Get a movie", notes = "Gets the movie with id = {id}")
    @ResponseBody
    public ResponseEntity<Movie> getMovie(@PathVariable("id") String id,
                                          @RequestParam(value = "accesstoken", required = true) String accessToken) throws Exception {
        tokenService.verifyAdmin(accessToken);
        Movie movie = validateMovieId(id);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setLocation(ServletUriComponentsBuilder
                .fromCurrentRequest().path("/").buildAndExpand(id).toUri());
        return new ResponseEntity<>(movie, httpHeaders, HttpStatus.OK);
    }

    /**
     * Delete the movie with id = id
     *
     * @param id The id of the movie
     * @return The movie.
     * @throws Exception
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    @ApiOperation(value = "Delete a movie", notes = "Deletes the movie with id = {id}")
    @ResponseBody
    public ResponseEntity<Movie> deleteMovie(@PathVariable("id") String id,
                                             @RequestParam(value = "accesstoken", required = true) String accessToken) throws Exception {
      tokenService.verifyAdmin(accessToken);
        Movie movie = validateMovieId(id);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setLocation(ServletUriComponentsBuilder
                .fromCurrentRequest().path("/").buildAndExpand(id).toUri());

        List<Comment> comments = commentsRepository.findByMovieid(id, new PageRequest(0, (int) commentsRepository.count()));
        for (int i = 0; i < comments.size(); i++)
            commentsRepository.delete(comments.get(i));
        moviesRepository.delete(movie);

        return new ResponseEntity<>(movie, httpHeaders, HttpStatus.OK);
    }

    /**
     * Get the comments with id = id
     *
     * @param id
     * @return The comments
     * @throws Exception
     */
    @RequestMapping(value = "/{id}/comments", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "Gets comments", notes = "Gets comments for the movie with id = {id}")
    public ResponseEntity<List<Comment>> getComments(@PathVariable("id") String id,
                                                     @RequestParam(value = "accesstoken", required = true) String accessToken) throws Exception {
        tokenService.verifyAdmin(accessToken);
      Movie movie = validateMovieId(id);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setLocation(ServletUriComponentsBuilder
                .fromCurrentRequest().path("/").buildAndExpand(id + "/comments").toUri());

        return new ResponseEntity<List<Comment>>(commentsRepository.findByMovieid(movie.getId(), new PageRequest(0, (int) commentsRepository.count())), httpHeaders, HttpStatus.OK);
    }

    /**
     * Deletes all comments for a movie.
     *
     * @param id
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/{id}/comments", method = RequestMethod.DELETE)
    @ResponseBody
    @ApiOperation(value = "Delete all comment for a movie", notes = "Deletes the comment for a movie from the database")
    public ResponseEntity<?> deleteComments(@PathVariable("id") String id,
                                            @RequestParam(value = "accesstoken", required = true) String accessToken) throws Exception {
      tokenService.verifyAdmin(accessToken);
        validateMovieId(id);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setLocation(ServletUriComponentsBuilder
                .fromCurrentRequest().path("/").buildAndExpand(id).toUri());
        List<Comment> comments = commentsRepository.findByMovieid(id, new PageRequest(0, (int) commentsRepository.count()));
        for (int i = 0; i < comments.size(); i++)
            commentsRepository.delete(comments.get(i));

        return new ResponseEntity<>("deleted comments for movie with id: " + id, httpHeaders, HttpStatus.OK);
    }

    /**
     * Gets a comment by id
     *
     * @param id        The id of the movie
     * @param commentid The id of the comment
     * @return The comment
     * @throws Exception
     */
    @RequestMapping(value = "/{id}/comments/{commentid}", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "Get a comment by id", notes = "Gets the comment with the specified id")
    public ResponseEntity<Comment> getOneComment(@PathVariable("id") String id,
                                                 @PathVariable("commentid") String commentid,
                                                 @RequestParam(value = "accesstoken", required = true) String accessToken) throws Exception {
        tokenService.verifyAdmin(accessToken);
      validateMovieId(id);
        Comment comment = validateCommentid(commentid);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setLocation(ServletUriComponentsBuilder
                .fromCurrentRequest().path("/").buildAndExpand(id + "/comments" + commentid).toUri());
        return new ResponseEntity<Comment>(comment, httpHeaders, HttpStatus.OK);
    }

    /**
     * Deletes a comment on a movie by id
     *
     * @param id        The if of the movie
     * @param commentid The id of the comment
     * @return Nothing
     * @throws Exception
     */
    @RequestMapping(value = "/{id}/comments/{commentid}", method = RequestMethod.DELETE)
    @ResponseBody
    @ApiOperation(value = "Delete one comment", notes = "Deletes the comment from the database")
    public ResponseEntity<?> deleteComment(@PathVariable("id") String id,
                                           @PathVariable("commentid") String commentid,
                                           @RequestParam(value = "accesstoken", required = true) String accessToken) throws Exception {
        tokenService.verifyAdmin(accessToken);
        validateMovieId(id);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setLocation(ServletUriComponentsBuilder
                .fromCurrentRequest().path("/").buildAndExpand(id).toUri());

        commentsRepository.delete(commentid);
        return new ResponseEntity<>("deleted comment with id: " + commentid, httpHeaders, HttpStatus.CREATED);
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

    /**
     * Validate a comment
     *
     * @param id
     * @return
     * @throws Exception
     */
    private Comment validateCommentid(String id) throws Exception {
        Comment comment = this.commentsRepository.findById(id);
        if (comment == null)
            throw new CommentNotFoundException(id);
        return comment;
    }
}
