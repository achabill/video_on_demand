/**
 * Handles all requests to .../movies/?
 */
package vod.controllers.frontendcontrollers;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import vod.auth.ITokenService;
import vod.dao.ICommentDao;
import vod.dao.IMovieDao;
import vod.exceptions.*;
import vod.filearchive.ArchiveServiceClient;
import vod.statics.StaticFactory;
import vod.models.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

/**
 * The movies controller.
 *
 * @since 1.0
 */
@RestController
@RequestMapping(value = "/movies")
@Api(value = "movies", description = "movies API")
public class FrontendMoviesController {

  @Autowired
  ITokenService<User> tokenService;
  @Autowired
  private IMovieDao movieDao;
  @Autowired
  private ICommentDao commentDao;
  @Autowired
  private ArchiveServiceClient archiveServiceClient;

  /**
   * Gets a list of movies as prescribed by the request.
   * The optional parameters include:
   * <ul>
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
   * @param genre    The genre of the movie.
   * @param property The property of the movie.
   * @param order    The sorting order.
   * @param sort     Whether to sort.
   * @return A list of movies.
   */
  @RequestMapping(value = "/", method = RequestMethod.GET)
  @ApiOperation(value = "Get movies", notes = "Returns a list of all movies.")
  @ResponseBody
  public ResponseEntity<List<Movie>> getMovies(@RequestParam(value = "genre", required = false) String genre,
                                               @RequestParam(value = "property", required = false) String property,
                                               @RequestParam(value = "order", required = false) String order,
                                               @RequestParam(value = "sort", required = false) String sort
                                               ) throws Exception {
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
      Iterable<Movie> movies = movieDao.findByGenre(genre);
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
   * * Gets a movie specifying the id.
   * The optional parameters include
   * <ul>
   * </ul>
   *
   * @param id The id of the movie.
   * @return
   * @throws Exception
   */
  @RequestMapping(value = "/{id}", method = RequestMethod.GET)
  @ApiOperation(value = "Get a movie", notes = "Gets the movie with id = {id}")
  @ResponseBody
  public ResponseEntity<Movie> getMovie(@PathVariable("id") String id) throws Exception {
    Movie movie = validateMovieId(id);

    HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.setLocation(ServletUriComponentsBuilder
      .fromCurrentRequest().path("/").buildAndExpand(id).toUri());
    return new ResponseEntity<>(movie, httpHeaders, HttpStatus.OK);
  }

  @RequestMapping(value = "/{id}/play", method = RequestMethod.GET)
  @ApiOperation(value = "Serves a movie for playback", notes = "Gets the movie with id = {id}")
  public void getMovieFile(@PathVariable("id") String id, HttpServletRequest request, HttpServletResponse response) throws Exception {
    Movie movie = validateMovieId(id);
    movie.setViews(movie.getViews() + 1);
    movieDao.save(movie);

    archiveServiceClient.getDocumentMultipart(movie.getVideouuid(), request, response);
  }

  @RequestMapping(value = "/{id}/coverimage", method = RequestMethod.GET)
  @ApiOperation(value = "Serves the coverimage for the movie", notes = "Gets the movie with id = {id}")
  public void getCoverImageFile(@PathVariable("id") String id, HttpServletRequest request, HttpServletResponse response) throws Exception {
    Movie movie = validateMovieId(id);
    archiveServiceClient.getDocumentMultipart(movie.getCoverimageuuid(), request, response);
  }

  @ResponseBody
  @RequestMapping(value = "/search", method = RequestMethod.GET)
  @ApiOperation(value = "Get movies matching search title", notes = "If direct match, return movie. Else, return all movies containing search string")
  public ResponseEntity<List<Movie>> getMoviesBySearchTitle(@PathVariable("title") String title) throws Exception {
    HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.setLocation(ServletUriComponentsBuilder
      .fromCurrentRequest().path("/").buildAndExpand("search").toUri());

    List<Movie> movies = movieDao.findByTitle(title);
    if (movies.size() != 0)
      return new ResponseEntity<>(movies, httpHeaders, HttpStatus.OK);

    List<Movie> _movies = new ArrayList<>();
    movies = movieDao.findAll();
    for (int i = 0; i < movies.size(); i++) {
      if (movies.get(i).getTitle().toLowerCase().contains(title.toLowerCase()))
        _movies.add(movies.get(i));
    }
    return new ResponseEntity<>(_movies, httpHeaders, HttpStatus.OK);
  }

  /**
   * * Gets a list of similar movies to this movie by genre similarity.
   * Uses all parameters as /movies except genre.
   *
   * @param id       The id.
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
                                              @RequestParam(value = "sort", required = false) String sort,
                                              @RequestParam(value = "property", required = false) String property,
                                              @RequestParam(value = "order", required = false) String order) throws Exception {
    Movie movie = validateMovieId(id);

    HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.setLocation(ServletUriComponentsBuilder
      .fromCurrentRequest().path("/").build().toUri());

    Sort.Direction direction;

    //combinations or params


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

    List<Movie> movies = movieDao.findByGenre(movie.getGenre());
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
  public ResponseEntity<List<String>> getGenres() throws Exception {
    HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.setLocation(ServletUriComponentsBuilder
      .fromCurrentRequest().path("/").build().toUri());
    return new ResponseEntity<List<String>>(StaticFactory.getMovieGenres(), httpHeaders, HttpStatus.OK);
  }

  /**
   * Gets all comments for a movie.
   *
   * @param id The id of the movie.
   * @return
   * @throws Exception
   */
  @RequestMapping(value = "/{id}/comments", method = RequestMethod.GET)
  @ResponseBody
  @ApiOperation(value = "Gets comments", notes = "Gets comments for the movie with id = {id}")
  public ResponseEntity<List<Comment>> getComments(@PathVariable("id") String id) throws Exception {
    Movie movie = validateMovieId(id);


    HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.setLocation(ServletUriComponentsBuilder
      .fromCurrentRequest().path("/").buildAndExpand(id).toUri());
    List<Comment> comments = commentDao.findByMovieid(id);
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
  public ResponseEntity<?> addComment(@PathVariable("id") String id,
                                      @Valid @RequestBody Comment comment
  ) throws Exception {
    validateMovieComment(comment);
    if (comment.getUser() == null)
      comment.setUser(new User("Anonymous"));

    commentDao.save(comment);
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
    movieDao.save(movie);
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
    movieDao.save(movie);

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
  public ResponseEntity<Rating> addRating(@RequestParam("rating") String rating,
                                          @PathVariable("id") String id) throws Exception {
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

    double fx = (1 * movieRating.getOnestar()) +
      (2 * movieRating.getTwostars()) +
      (3 * movieRating.getThreestars()) +
      (4 * movieRating.getFourstars()) +
      (5 * movieRating.getFivestars());

    int x = 1 + 2 + 3 + 4 + 5;
    int overallRating = (int) Math.round(fx / x);
    movie.setOverallrating(overallRating);

    movieDao.save(movie);

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
    Movie movie = this.movieDao.findById(id);
    if (movie == null)
      throw new MovieNotFoundException(id);
    return movie;
  }

  private void verifyAdminToken(String token){
    User u = tokenService.tokenValue(token);
    if(u == null || (!u.getPrevilege().equals("root") && !u.getPrevilege().equals("admin")))
      throw new UnauthorizedException("token : " + token + " is unauthorized");
  }
}

