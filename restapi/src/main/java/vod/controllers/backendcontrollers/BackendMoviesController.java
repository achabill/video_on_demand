package vod.controllers.backendcontrollers;

import archive.model.Document;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import vod.auth.ITokenService;
import vod.dao.ICommentDao;
import vod.dao.IMovieDao;
import vod.exceptions.*;
import vod.filearchive.ArchiveServiceClient;
import vod.statics.StaticFactory;
import vod.models.Comment;
import vod.models.Movie;
import vod.models.User;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * The backend movies controller
 */
@RestController
@Api(value = "Admin movies controller", description = "An interface to control the movies   database")
@RequestMapping(value = "admin/movies")
public class BackendMoviesController {

  @Autowired
  private IMovieDao movieDao;
  @Autowired
  private ICommentDao commentDao;

  @Autowired
  private ITokenService<User> tokenService;
  @Autowired
  private ArchiveServiceClient archiveServiceClient;

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
                                               @RequestParam(value = "accesstoken", required = true) String accessToken) throws Exception {
    verifyAdminToken(accessToken);

    HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.setLocation(ServletUriComponentsBuilder
      .fromCurrentRequest().path("/").build().toUri());


    Sort.Direction direction;


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
      List<Movie> movies = movieDao.findByGenre(genre);
      List<Movie> result = new ArrayList<>();
      movies.forEach(movie -> result.add(movie));
      return new ResponseEntity<List<Movie>>(result, httpHeaders, HttpStatus.OK);
    } else {
      List<Movie> movies = movieDao.findAll();
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
  public ResponseEntity<?> addMovie(@RequestParam @Valid Movie movie,
                                    @RequestParam(value = "accesstoken", required = true) String accessToken
                                    ) throws Exception {

    verifyAdminToken(accessToken);
    movieDao.save(movie);

    HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.setLocation(ServletUriComponentsBuilder
      .fromCurrentRequest().path("/").build().toUri());
    return new ResponseEntity<>(movie, httpHeaders, HttpStatus.CREATED);
  }

  @ResponseBody
  @ApiOperation(value = "Edit existing movie", notes = "Edit the movie with the specified id")
  @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
  public ResponseEntity<Movie> modifyMovie(@RequestBody Movie movie,
                                       @PathVariable String id,
                                       @RequestParam (value = "accesstoken", required = true) String accessToken) throws Exception{
    verifyAdminToken(accessToken);

    validateMovieId(id);
    movie.setId(id);

    movieDao.save(movie);
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
  public ResponseEntity<?> deleteAllMovies(@RequestParam(value = "accesstoken", required = true) String accessToken) throws Exception {

    verifyAdminToken(accessToken);
    HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.setLocation(ServletUriComponentsBuilder
      .fromCurrentRequest().path("/").build().toUri());

    List<Movie> movies = movieDao.findAll();
    List<Comment> comments = commentDao.findAll();
    for (int i = 0; i < comments.size(); i++)
      if (comments.get(i).getMovieid() != null)
        commentDao.delete(comments.get(i));
    movies.forEach(movie -> {

      try {
        archiveServiceClient.deleteDocument(movie.getCoverimageuuid());
        archiveServiceClient.deleteDocument(movie.getVideouuid());
      } catch (IOException e) {
        e.printStackTrace();
      }
    });
    movieDao.deleteAll();

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
    verifyAdminToken(accessToken);
    Movie movie = validateMovieId(id);

    HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.setLocation(ServletUriComponentsBuilder
      .fromCurrentRequest().path("/").buildAndExpand(id).toUri());
    return new ResponseEntity<>(movie, httpHeaders, HttpStatus.OK);
  }

  /**
   * Gets the movie cover image
   * @param id The id of the coverimage
   * @param accessToken The accesstoken
   * @param request The httpservlet request
   * @param response the httpservlet response
   * @throws Exception Exception
   */
  @RequestMapping(value = "/{id}/coverimage", method = RequestMethod.GET)
  @ApiOperation(value = "Get a movie", notes = "Gets the movie with id = {id}")
  public void getMovieCoverimage(@PathVariable("id") String id,
                                                  @RequestParam(value = "accesstoken", required = true) String accessToken,
                                                  HttpServletRequest request, HttpServletResponse response) throws Exception {
    verifyAdminToken(accessToken);
    Movie movie = validateMovieId(id);

    archiveServiceClient.getDocumentMultipart(movie.getCoverimageuuid(),request,response);
  }

  /**
   * Gets the movie video file
   * @param id The id of the coverimage
   * @param accessToken The accesstoken
   * @param request The httpservlet request
   * @param response the httpservlet response
   * @throws Exception Exception
   */
  @RequestMapping(value = "/{id}/play", method = RequestMethod.GET)
  @ApiOperation(value = "Get a movie video file", notes = "Gets the movie with id = {id}")
  public void getMoviePlay(@PathVariable("id") String id,
                                 @RequestParam(value = "accesstoken", required = true) String accessToken,
                                 HttpServletRequest request, HttpServletResponse response) throws Exception {
    verifyAdminToken(accessToken);
    Movie movie = validateMovieId(id);

    archiveServiceClient.getDocumentMultipart(movie.getVideouuid(),request,response);
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
    verifyAdminToken(accessToken);
    Movie movie = validateMovieId(id);

    HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.setLocation(ServletUriComponentsBuilder
      .fromCurrentRequest().path("/").buildAndExpand(id).toUri());

    List<Comment> comments = commentDao.findByMovieid(id);
    for (int i = 0; i < comments.size(); i++)
      commentDao.delete(comments.get(i));

    archiveServiceClient.deleteDocument(movie.getCoverimageuuid());
    archiveServiceClient.deleteDocument(movie.getVideouuid());
    movieDao.delete(movie);

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
    verifyAdminToken(accessToken);
    Movie movie = validateMovieId(id);

    HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.setLocation(ServletUriComponentsBuilder
      .fromCurrentRequest().path("/").buildAndExpand(id + "/comments").toUri());

    return new ResponseEntity<List<Comment>>(commentDao.findByMovieid(movie.getId()), httpHeaders, HttpStatus.OK);
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
    verifyAdminToken(accessToken);
    validateMovieId(id);
    HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.setLocation(ServletUriComponentsBuilder
      .fromCurrentRequest().path("/").buildAndExpand(id).toUri());
    List<Comment> comments = commentDao.findByMovieid(id);
    for (int i = 0; i < comments.size(); i++)
      commentDao.delete(comments.get(i));

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
    verifyAdminToken(accessToken);
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
    verifyAdminToken(accessToken);
    validateMovieId(id);

    HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.setLocation(ServletUriComponentsBuilder
      .fromCurrentRequest().path("/").buildAndExpand(id).toUri());

    commentDao.delete(commentid);
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
    Movie movie = this.movieDao.findById(id);
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
    Comment comment = this.commentDao.findById(id);
    if (comment == null)
      throw new CommentNotFoundException(id);
    return comment;
  }

  private void verifyAdminToken(String token){
    User u = tokenService.tokenValue(token);

    if(u == null || (!u.getPrevilege().equals("root") && !u.getPrevilege().equals("admin")))
      throw new UnauthorizedException("token : " + token + " is unauthorized");
  }
}
