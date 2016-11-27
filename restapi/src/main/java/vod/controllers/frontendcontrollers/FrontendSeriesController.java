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
import tokenauth.service.TokenService;
import vod.dao.ICommentDao;
import vod.dao.IEpisodeDao;
import vod.dao.ISeasonDao;
import vod.dao.ISeriesDao;
import vod.exceptions.*;
import vod.filearchive.ArchiveServiceClient;
import vod.filestorage.StorageService;
import vod.helpers.StaticFactory;
import vod.models.*;
import vod.repositories.CommentsRepository;
import vod.repositories.EpisodesRepository;
import vod.repositories.SeasonsRepository;
import vod.repositories.SeriesRepository;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

/**
 * The series front end controller
 */
@RestController
@RequestMapping(value = "/series")
@Api(value = "Series", description = "Series API")
public class FrontendSeriesController {
  @Autowired
  private ISeriesDao seriesDao;
  @Autowired
  private ISeasonDao seasonDao;
  @Autowired
  private IEpisodeDao episodeDao;
  @Autowired
  private ICommentDao commentDao;
  @Autowired
  private ArchiveServiceClient archiveServiceClient;
  @Autowired
  private TokenService tokenService;

  /**
   * Gets a list of series as prescribed by the request.
   * The optional parameters include:
   * <ul>
   * <li><strong>genre.</strong> The genre of the serie to return.</li>
   * <li><strong>releaseyear.</strong> The year the serie was released.</li>
   * <li><strong>sort.</strong> Sorts the list according to provided arguments. Can be anything [true]
   * <ul>
   * <li><strong>property.</strong> Can be [id,title,likes,views,dislikes,overallrating, releasedate]. Must be used with <i>sort</i></li>
   * <li><strong>order.</strong> Can be [desc,asc]. Must be used with <i>sort</i></li>
   * </ul></li>
   *
   * @param genre    The serie genre.
   * @param property The property to sort.
   * @param order    The sorting order.
   * @param sort     The sort
   * @return
   */
  @RequestMapping(value = "/", method = RequestMethod.GET)
  @ResponseBody
  @ApiOperation(value = "Get series", notes = "Gets all series")
  public ResponseEntity<List<Series>> getAllSeries(@RequestParam(value = "genre", required = false) String genre,
                                                   @RequestParam(value = "property", required = false, defaultValue = "id") String property,
                                                   @RequestParam(value = "order", required = false, defaultValue = "asc") String order,
                                                   @RequestParam(value = "sort", required = false, defaultValue = "true") String sort,
                                                   @RequestParam(value = "accesstoken", required = true) String accessToken) throws Exception {
    tokenService.verifyAccessToken(accessToken);
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

    if (genre != null)
      if (!StaticFactory.isMovieGenre(genre))
        throw new InvalidGenreParameterException(genre);

    if (genre != null) {
      Iterable<Series> series = seriesDao.findByGenre(genre);
      List<Series> result = new ArrayList<>();
      series.forEach(s -> result.add(s));
      return new ResponseEntity<>(result, httpHeaders, HttpStatus.OK);
    } else {
      List<Series> series = seriesDao.findAll();

      List<Series> result = new ArrayList<>();
      series.forEach(s -> result.add(s));
      return new ResponseEntity<>(result, httpHeaders, HttpStatus.OK);
    }
  }

  /**
   * Gets the series by id.
   *
   * @param id The id of the series.
   * @return Returns the series.
   */
  @ApiOperation(value = "Get series by id", notes = "Gets the series with the specified id")
  @ResponseBody
  @RequestMapping(value = "/{id}", method = RequestMethod.GET)
  public ResponseEntity<Series> getSeriesById(@PathVariable("id") String id,
                                              @RequestParam(value = "accesstoken", required = true) String accessToken) throws Exception {
    tokenService.verifyAccessToken(accessToken);
    Series series = validateSeriesId(id);
    HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.setLocation(ServletUriComponentsBuilder
      .fromCurrentRequest().path("/").buildAndExpand(id).toUri());
    return new ResponseEntity<>(series, httpHeaders, HttpStatus.OK);
  }

  /**
   * Gets series matching title
   *
   * @param title
   * @return A list of movies matching the title
   */
  @RequestMapping(value = "/search", method = RequestMethod.GET)
  @ResponseBody
  @ApiOperation(value = "Search for a movie", notes = "Returns a list of movies matching the search")
  public ResponseEntity<List<Series>> getSeriesBySearchTitle(@RequestParam(value = "accesstoken", required = true) String accessToken,
                                                             @RequestParam(value = "title", required = true) String title) throws Exception {
    tokenService.verifyAccessToken(accessToken);
    HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.setLocation(ServletUriComponentsBuilder
      .fromCurrentRequest().path("/").buildAndExpand("search").toUri());
    List<Series> series = seriesDao.findByTitle(title);
    if (series.size() != 0)
      return new ResponseEntity<>(series, httpHeaders, HttpStatus.OK);

    series = seriesDao.findAll();
    List<Series> _series = new ArrayList<>();
    for (int i = 0; i < series.size(); i++) {
      if (series.get(i).getTitle().toLowerCase().contains(title.toLowerCase()))
        _series.add(series.get(i));
    }
    return new ResponseEntity<>(_series, httpHeaders, HttpStatus.OK);
  }

  /**
   * Gets the seasons of the series with id.
   *
   * @param id The id of the series.
   * @return The seasons of the series.
   */
  @ApiOperation(value = "Gets the seaons of the series", notes = "Gets the season of the series with id id {id}")
  @RequestMapping(value = "/{id}/seasons", method = RequestMethod.GET)
  @ResponseBody
  public ResponseEntity<List<Season>> getSeriesSeasons(@RequestParam(value = "accesstoken", required = true) String accessToken,
                                                       @PathVariable("id") String id) throws Exception {
    tokenService.verifyAccessToken(accessToken);
    Series series = validateSeriesId(id);
    List<Season> seasons = seasonDao.findBySeriesid(id);

    HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.setLocation(ServletUriComponentsBuilder
      .fromCurrentRequest().path("/").buildAndExpand(id + "/seasons").toUri());
    return new ResponseEntity<>(seasons, httpHeaders, HttpStatus.OK);
  }


  /**
   * Gets a season of a series.
   *
   * @param seriesid The series id.
   * @param seasonid The season id.
   * @return The season.
   * @throws Exception
   */
  @ApiOperation(value = "Get season", notes = "Gets the season of a series specifying the series id and season id.")
  @ResponseBody
  @RequestMapping(value = "/{seriesid}/seasons/{seasonid}", method = RequestMethod.GET)
  public ResponseEntity<Season> getSeasonOfSeries(@RequestParam(value = "accesstoken", required = true) String accessToken,
                                                  @PathVariable("seriesid") String seriesid,
                                                  @PathVariable("seasonid") String seasonid) throws Exception {
    tokenService.verifyAccessToken(accessToken);
    Series series = validateSeriesId(seriesid);
    Season season = validateSeasonId(seasonid);

    HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.setLocation(ServletUriComponentsBuilder
      .fromCurrentRequest().path("/").buildAndExpand(seriesid + "/seasons/" + seasonid).toUri());
    return new ResponseEntity<>(season, httpHeaders, HttpStatus.OK);
  }

  /**
   * Gets the episodes of a season.
   *
   * @param seriesid The series id.
   * @param seasonid The season id.
   * @return The episodes in the season.
   * @throws Exception exception
   */
  @ApiOperation(value = "Get episodes of season", notes = "Gets the episodes of a season")
  @ResponseBody
  @RequestMapping(value = "/{seriesid}/seasons/{seasonid}/episodes", method = RequestMethod.GET)
  public ResponseEntity<List<SeasonEpisode>> getSeasonsEpisodes(@RequestParam(value = "accesstoken", required = true) String accessToken, @PathVariable("seriesid") String seriesid,
                                                                @PathVariable("seasonid") String seasonid) throws Exception {
    tokenService.verifyAccessToken(accessToken);
    Series series = validateSeriesId(seriesid);
    Season season = validateSeasonId(seasonid);
    List<SeasonEpisode> seasonEpisodes = episodeDao.findBySeasonid(seasonid);

    HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.setLocation(ServletUriComponentsBuilder
      .fromCurrentRequest().path("/").buildAndExpand(seriesid + "/seasons/" + seasonid + "/episodes").toUri());
    return new ResponseEntity<>(seasonEpisodes, httpHeaders, HttpStatus.OK);
  }

  /**
   * Gets the an episode of a season.
   *
   * @param id       The episode id.
   * @param seasonid The season id.
   * @param seriesid The series id.
   * @return The episode.
   * @throws Exception
   */
  @RequestMapping(value = "/{seriesid}/seasons/{seasonid}/episodes/{id}", method = RequestMethod.GET)
  @ApiOperation(value = "Gets a season epidoe", notes = "Gets an episode of a season in a series")
  @ResponseBody
  public ResponseEntity<SeasonEpisode> getSeasonEpisode(@RequestParam(value = "accesstoken", required = true) String accessToken, @PathVariable("id") String id,
                                                        @PathVariable("seasonid") String seasonid,
                                                        @PathVariable("seriesid") String seriesid) throws Exception {
    tokenService.verifyAccessToken(accessToken);
    Season season = validateSeasonId(seasonid);
    Series series = validateSeriesId(seriesid);
    SeasonEpisode seasonEpisode = validateSeasonEpisodeId(id);

    HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.setLocation(ServletUriComponentsBuilder
      .fromCurrentRequest().path("/").buildAndExpand(seriesid + "/seasons/" + seasonid + "/episodes" + id).toUri());

    return new ResponseEntity<>(seasonEpisode, httpHeaders, HttpStatus.OK);
  }

  @RequestMapping(value = "/{seriesid}/seasons/{seasonid}/episodes/{id}/play", method = RequestMethod.GET)
  @ApiOperation(value = "Serves a season episode for playback", notes = "Gets the season episode with id = {id}")
  public void getMovieFile(@RequestParam(value = "accesstoken", required = true) String accessToken,
                           @PathVariable("id") String id,
                           @PathVariable("seasonid") String seasonid,
                           @PathVariable("seriesid") String seriesid, HttpServletRequest request, HttpServletResponse response) throws Exception {
    tokenService.verifyAccessToken(accessToken);
    validateSeriesId(seriesid);
    validateSeasonId(seasonid);
    SeasonEpisode seasonEpisode = validateSeasonEpisodeId(id);

    seasonEpisode.setViews(seasonEpisode.getViews() + 1);
    episodeDao.save(seasonEpisode);
    storageService.serve(seasonEpisode.getVideofile(), request, response);
  }

  @RequestMapping(value = "/{seriesid}/seasons/{seasonid}/coverimage", method = RequestMethod.GET)
  @ApiOperation(value = "Serves the coverimage for the season ", notes = "Gets the season with id = {id}")
  public void getCoverImageForSeason(@RequestParam(value = "accesstoken", required = true) String accessToken,
                                     @PathVariable("seasonid") String seasonid,
                                     @PathVariable("seriesid") String seriesid, HttpServletRequest request, HttpServletResponse response) throws Exception {
    tokenService.verifyAccessToken(accessToken);
    validateSeriesId(seriesid);
    Season season = validateSeasonId(seasonid);
    storageService.serve(season.getCoverimage(), request, response);
  }

  @RequestMapping(value = "/{seriesid}/coverimage", method = RequestMethod.GET)
  @ApiOperation(value = "Serves the coverimage for the series ", notes = "Gets the series with id = {id}")
  public void getCoverImageForSeries(@RequestParam(value = "accesstoken", required = true) String accessToken,
                                     @PathVariable("seriesid") String seriesid, HttpServletRequest request, HttpServletResponse response) throws Exception {
    tokenService.verifyAccessToken(accessToken);
    Series series = validateSeriesId(seriesid);
    storageService.serve(series.getCoverimage(), request, response);
  }

  /**
   * Gets similar series.
   *
   * @param id The id of the series.
   * @return Similar movies.
   * @throws Exception
   */
  @RequestMapping(value = "/{id}/similar", method = RequestMethod.GET)
  @ApiOperation(value = "Get similar series", notes = "Gets similar movies by genre")
  @ResponseBody
  public ResponseEntity<List<Series>> getSimilarSeries(@PathVariable("id") String id,
                                                       @RequestParam(value = "accesstoken", required = true) String accessToken) throws Exception {
    tokenService.verifyAccessToken(accessToken);
    Series series = validateSeriesId(id);

    HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.setLocation(ServletUriComponentsBuilder
      .fromCurrentRequest().path("/").buildAndExpand(id + "/similar").toUri());

    List<Series> similarSeries = seriesDao.findByGenre(series.getGenre());
    return new ResponseEntity<>(similarSeries, httpHeaders, HttpStatus.OK);
  }

  /**
   * Gets the movie genres recognized by the system.
   *
   * @return A list of movie genres.
   */
  @ResponseBody
  @RequestMapping(value = "/genres", method = RequestMethod.GET)
  @ApiOperation(value = "List of genres", notes = "Gets the list of genres supported")
  public ResponseEntity<List<String>> getGenres(@RequestParam(value = "accesstoken", required = true) String accessToken) throws Exception {
    tokenService.verifyAccessToken(accessToken);
    HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.setLocation(ServletUriComponentsBuilder
      .fromCurrentRequest().path("/").buildAndExpand("genres").toUri());
    return new ResponseEntity<List<String>>(StaticFactory.getMovieGenres(), httpHeaders, HttpStatus.OK);
  }

  /**
   * Gets all comments for a series.
   *
   * @param id The id of the series.
   * @return
   * @throws Exception
   */
  @RequestMapping(value = "/{id}/comments", method = RequestMethod.GET)
  @ResponseBody
  @ApiOperation(value = "Gets comments for series", notes = "Gets comments for the series with id = {id}")
  public ResponseEntity<List<Comment>> getSeriesComments(@RequestParam(value = "accesstoken", required = true) String accessToken,
                                                         @PathVariable("id") String id) throws Exception {
    tokenService.verifyAccessToken(accessToken);
    Series series = validateSeriesId(id);


    HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.setLocation(ServletUriComponentsBuilder
      .fromCurrentRequest().path("/").buildAndExpand(id + "/comments").toUri());
    List<Comment> comments = commentDao.findBySeriesid(id);
    List<Comment> result = new ArrayList<>();
    comments.forEach(c -> result.add(c));
    return new ResponseEntity<List<Comment>>(result, httpHeaders, HttpStatus.OK);
  }

  /**
   * Gets all comments for a season.
   *
   * @param seriesid The series id.
   * @param seasonid The seasonid.
   * @return List of comments.
   * @throws Exception
   */
  @RequestMapping(value = "/{seriesid}/seasons/{seasonid}/comments", method = RequestMethod.GET)
  @ResponseBody
  @ApiOperation(value = "Gets comments for season", notes = "Gets comments for the season with id = {id}")
  public ResponseEntity<List<Comment>> getSeasonComments(@RequestParam(value = "accesstoken", required = true) String accessToken,
                                                         @PathVariable("seriesid") String seriesid,
                                                         @PathVariable("seasonid") String seasonid) throws Exception {
    tokenService.verifyAccessToken(accessToken);
    validateSeriesId(seriesid);
    validateSeasonId(seasonid);

    HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.setLocation(ServletUriComponentsBuilder
      .fromCurrentRequest().path("/").buildAndExpand(seasonid + "/seasons/" + seasonid + "/comments").toUri());
    List<Comment> comments = commentDao.findBySeasonid(seasonid);
    List<Comment> result = new ArrayList<>();
    comments.forEach(c -> result.add(c));
    return new ResponseEntity<List<Comment>>(result, httpHeaders, HttpStatus.OK);
  }

  /**
   * Gets comments for a seasonepisode.
   *
   * @param seriesid        The series id.
   * @param seasonid        The season id.
   * @param seasonepisodeid The episode id.
   * @return List of comments.
   * @throws Exception
   */
  @RequestMapping(value = "/{seriesid}/seasons/{seasonid}/episodes/{seasonepisodeid}/comments", method = RequestMethod.GET)
  @ResponseBody
  @ApiOperation(value = "Gets comments for season episode", notes = "Gets comments for the episode with id = {id}")
  public ResponseEntity<List<Comment>> getSeasonEpisodecomments(@PathVariable("seriesid") String seriesid,
                                                                @RequestParam(value = "accesstoken", required = true) String accessToken,
                                                                @PathVariable("seasonid") String seasonid,
                                                                @PathVariable("seasonepisodeid") String seasonepisodeid) throws Exception {
    tokenService.verifyAccessToken(accessToken);
    validateSeriesId(seriesid);
    validateSeasonId(seasonid);
    validateSeasonEpisodeId(seasonepisodeid);

    HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.setLocation(ServletUriComponentsBuilder
      .fromCurrentRequest().path("/").buildAndExpand(seasonid + "/seasons/" + seasonid + "/episodes/" + seasonepisodeid + "/comments").toUri());
    List<Comment> comments = commentDao.findBySeasonepisodeid(seasonepisodeid);
    List<Comment> result = new ArrayList<>();
    comments.forEach(c -> result.add(c));
    return new ResponseEntity<List<Comment>>(result, httpHeaders, HttpStatus.OK);
  }

  /**
   * Adds a comment to the series
   *
   * @param id      the id of the series.
   * @param comment The comment to set.
   * @return Nothing.
   */
  @RequestMapping(value = "/{id}/comments", method = RequestMethod.POST)
  @ResponseBody
  @ApiOperation(value = "Add comment to series", notes = "Adds a comment to the series with id = {id}")
  public ResponseEntity<?> addSeriesComment(@PathVariable("id") String id,
                                            @RequestParam(value = "accesstoken", required = true) String accessToken,
                                            @Valid @RequestBody Comment comment) throws Exception {
    tokenService.verifyAccessToken(accessToken);
    validateSeriesId(id);
    validateSeriesComment(comment);

    if (comment.getUser() == null)
      comment.setUser(new User("Anonymous"));

    commentDao.save(comment);
    HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.setLocation(ServletUriComponentsBuilder
      .fromCurrentRequest().path("/").buildAndExpand(comment.getId()).toUri());
    return new ResponseEntity<>(comment, httpHeaders, HttpStatus.CREATED);
  }

  /**
   * Adds a comment to a season.
   *
   * @param seriesid The series id.
   * @param seasonid The season id.
   * @param comment  The comment.
   * @return
   * @throws Exception
   */
  @RequestMapping(value = "/{seriesid}/seasons/{seasonid}/comments", method = RequestMethod.POST)
  @ResponseBody
  @ApiOperation(value = "Add comment to season", notes = "Adds a comment to the season with id = {seasonid}")
  public ResponseEntity<?> addSeasonComment(@PathVariable("seriesid") String seriesid,
                                            @PathVariable("seasonid") String seasonid,
                                            @RequestParam(value = "accesstoken", required = true) String accessToken,
                                            @Valid @RequestBody Comment comment) throws Exception {
    tokenService.verifyAccessToken(accessToken);
    validateSeriesId(seriesid);
    validateSeasonId(seasonid);

    validateSeasonComment(comment);
    if (comment.getUser() == null)
      comment.setUser(new User("Anonymous"));

    commentDao.save(comment);
    HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.setLocation(ServletUriComponentsBuilder
      .fromCurrentRequest().path("/").buildAndExpand(seriesid + "/seasons/" + seasonid + "/comments").toUri());
    return new ResponseEntity<>(comment, httpHeaders, HttpStatus.CREATED);
  }

  /**
   * Adds a comment to episode.
   *
   * @param seriesid
   * @param seasonid
   * @param seasonepisodeid
   * @param comment
   * @return
   * @throws Exception
   */
  @RequestMapping(value = "/{seriesid}/seasons/{seasonid}/episodes/{seasonepisodeid}/comments", method = RequestMethod.POST)
  @ResponseBody
  @ApiOperation(value = "Add comment to season", notes = "Adds a comment to the season with id = {seasonid}")
  public ResponseEntity<?> addSeasonEpisodeComment(@PathVariable("seriesid") String seriesid,
                                                   @PathVariable("seasonid") String seasonid,
                                                   @RequestParam(value = "accesstoken", required = true) String accessToken,
                                                   @PathVariable("seasonepisodeid") String seasonepisodeid,
                                                   @Valid @RequestBody Comment comment) throws Exception {
    tokenService.verifyAccessToken(accessToken);
    validateSeriesId(seriesid);
    validateSeasonId(seasonid);
    validateSeasonEpisodeId(seasonepisodeid);

    validateSeasonEpisodeComment(comment);
    if (comment.getUser() == null)
      comment.setUser(new User("Anonymous"));

    commentDao.save(comment);
    HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.setLocation(ServletUriComponentsBuilder
      .fromCurrentRequest().path("/").buildAndExpand(seriesid + "/seasons/" + seasonid + "/episodes/" + seasonepisodeid + "/comments").toUri());
    return new ResponseEntity<>(comment, httpHeaders, HttpStatus.CREATED);
  }

  /**
   * Increments the number of likes for the season episode.
   *
   * @param seriesid        The series id.
   * @param seasonepisodeid The episode id.
   * @param seasonid        The season id.
   * @return New number of likes.
   * @throws Exception
   */
  @RequestMapping(value = "/{seriesid}/seasons/{seasonid}/episodes/{seasonepisodeid}/like", method = RequestMethod.GET)
  @ResponseBody
  @ApiOperation(value = "Like a movie", notes = "Likes the movie with id = {id}")
  public ResponseEntity<PropertyValue> addLike(@PathVariable("seriesid") String seriesid,
                                               @PathVariable("seasonid") String seasonid,
                                               @RequestParam(value = "accesstoken", required = true) String accessToken,
                                               @PathVariable("seasonepisodeid") String seasonepisodeid) throws Exception {
    tokenService.verifyAccessToken(accessToken);
    validateSeriesId(seriesid);
    validateSeasonId(seasonid);
    SeasonEpisode seasonEpisode = validateSeasonEpisodeId(seasonepisodeid);

    seasonEpisode.setLikes(seasonEpisode.getLikes() + 1);
    episodeDao.save(seasonEpisode);
    HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.setLocation(ServletUriComponentsBuilder
      .fromCurrentRequest().path("/").buildAndExpand(seriesid + "/seasons/" + seasonid + "/episodes/" + seasonepisodeid).toUri());
    return new ResponseEntity<>(new PropertyValue("likes", new Integer(seasonEpisode.getLikes()).toString()), httpHeaders, HttpStatus.OK);

  }

  /**
   * Increments the number of dislikes for the season episode.
   *
   * @param seriesid  The series id.
   * @param episodeid The episode id.
   * @param seasonid  The season id.
   * @return New number of likes.
   * @throws Exception
   */
  @RequestMapping(value = "/{seriesid}/seasons/{seasonid}/episodes/{episodeid}/dislike", method = RequestMethod.GET)
  @ResponseBody
  @ApiOperation(value = "Like a movie", notes = "Likes the movie with id = {id}")
  public ResponseEntity<PropertyValue> addDislike(@PathVariable("seriesid") String seriesid,
                                                  @RequestParam(value = "accesstoken", required = true) String accessToken,
                                                  @PathVariable("episodeid") String episodeid,
                                                  @PathVariable("seasonid") String seasonid) throws Exception {
    tokenService.verifyAccessToken(accessToken);
    validateSeriesId(seriesid);
    validateSeasonId(seasonid);
    SeasonEpisode seasonEpisode = validateSeasonEpisodeId(episodeid);

    seasonEpisode.setDislikes(seasonEpisode.getDislikes() + 1);
    episodeDao.save(seasonEpisode);
    HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.setLocation(ServletUriComponentsBuilder
      .fromCurrentRequest().path("/").buildAndExpand(seriesid + "/seasons/" + seasonid + "/episodes/" + episodeid).toUri());
    return new ResponseEntity<>(new PropertyValue("dislikes", new Integer(seasonEpisode.getDislikes()).toString()), httpHeaders, HttpStatus.OK);

  }

  /**
   * * * Adds a rating to the series.
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
  @ApiOperation(value = "Add a rating to series", notes = "Adds a rating to the series with id ={id}")
  public ResponseEntity<Rating> addSeriesRating(@RequestParam("rating") String rating,
                                                @RequestParam(value = "accesstoken", required = true) String accessToken,
                                                @PathVariable("id") String id) throws Exception {
    tokenService.verifyAccessToken(accessToken);
    Series series = validateSeriesId(id);
    Rating seriesRating = series.getRating();

    if (rating.equals("onestar"))
      seriesRating.setOnestar(seriesRating.getOnestar() + 1);
    else if (rating.equals("twostars"))
      seriesRating.setTwostars(seriesRating.getTwostars() + 1);
    else if (rating.equals("threestars"))
      seriesRating.setThreestars(seriesRating.getThreestars() + 1);
    else if (rating.equals("fourstars"))
      seriesRating.setFourstars(seriesRating.getFourstars() + 1);
    else if (rating.equals("fivestars"))
      seriesRating.setFivestars(seriesRating.getFivestars() + 1);
    else
      throw new InvalidRatingParameterException(rating);

    series.setRating(seriesRating);

    double fx = (1 * seriesRating.getOnestar()) +
      (2 * seriesRating.getTwostars()) +
      (3 * seriesRating.getThreestars()) +
      (4 * seriesRating.getFourstars()) +
      (5 * seriesRating.getFivestars());

    int x = 1 + 2 + 3 + 4 + 5;
    int overallRating = (int) Math.round(fx / x);
    series.setOverallrating(overallRating);

    seriesDao.save(series);

    HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.setLocation(ServletUriComponentsBuilder.fromCurrentRequest().path("/" + id + "/rating").build().toUri());
    return new ResponseEntity<>(seriesRating, httpHeaders, HttpStatus.OK);
  }

  /**
   * Sets season rating.
   *
   * @param rating   The rating.
   * @param seasonid The season id.
   * @param seriesid The series id.
   * @return The new rating.
   * @throws Exception
   */
  @RequestMapping(value = "/{seriesid}/seasons/{seasonid}/rate", method = RequestMethod.GET)
  @ResponseBody
  @ApiOperation(value = "Add a rating to season", notes = "Adds a rating to the season")
  public ResponseEntity<Rating> addSeasonRating(@RequestParam("rating") String rating,
                                                @PathVariable("seasonid") String seasonid,
                                                @RequestParam(value = "accesstoken", required = true) String accessToken,
                                                @PathVariable("seriesid") String seriesid) throws Exception {
    tokenService.verifyAccessToken(accessToken);
    validateSeriesId(seriesid);
    Season season = validateSeasonId(seasonid);
    Rating seasonRating = season.getRating();

    if (rating.equals("onestar"))
      seasonRating.setOnestar(seasonRating.getOnestar() + 1);
    else if (rating.equals("twostars"))
      seasonRating.setTwostars(seasonRating.getTwostars() + 1);
    else if (rating.equals("threestars"))
      seasonRating.setThreestars(seasonRating.getThreestars() + 1);
    else if (rating.equals("fourstars"))
      seasonRating.setFourstars(seasonRating.getFourstars() + 1);
    else if (rating.equals("fivestars"))
      seasonRating.setFivestars(seasonRating.getFivestars() + 1);
    else
      throw new InvalidRatingParameterException(rating);

    season.setRating(seasonRating);

    double fx = (1 * seasonRating.getOnestar()) +
      (2 * seasonRating.getTwostars()) +
      (3 * seasonRating.getThreestars()) +
      (4 * seasonRating.getFourstars()) +
      (5 * seasonRating.getFivestars());

    int x = 1 + 2 + 3 + 4 + 5;
    int overallRating = (int) Math.round(fx / x);
    season.setOverallrating(overallRating);

    seasonDao.save(season);

    HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.setLocation(ServletUriComponentsBuilder.fromCurrentRequest().path(seriesid + "/seasons/" + seasonid + "/rating").build().toUri());
    return new ResponseEntity<>(seasonRating, httpHeaders, HttpStatus.OK);
  }

  /**
   * Adds a season episode rating.
   *
   * @param rating    The rating
   * @param seasonid  The season id
   * @param seriesid  The series id
   * @param episodeid The episod id
   * @return The rating
   * @throws Exception
   */
  @RequestMapping(value = "/{seriesid}/seasons/{seasonid}/episodes/{episodeid}/rate", method = RequestMethod.GET)
  @ResponseBody
  @ApiOperation(value = "Add a rating to season episode", notes = "Adds a rating to the season episode")
  public ResponseEntity<Rating> addSeasonEpisodeRating(@RequestParam("rating") String rating,
                                                       @RequestParam(value = "accesstoken", required = true) String accessToken,
                                                       @PathVariable("seasonid") String seasonid,
                                                       @PathVariable("seriesid") String seriesid,
                                                       @PathVariable("episodeid") String episodeid) throws Exception {
    tokenService.verifyAccessToken(accessToken);
    validateSeriesId(seriesid);
    validateSeasonId(seasonid);
    SeasonEpisode seasonEpisode = validateSeasonEpisodeId(episodeid);

    Rating seasonEpisodeRating = seasonEpisode.getRating();

    if (rating.equals("onestar"))
      seasonEpisodeRating.setOnestar(seasonEpisodeRating.getOnestar() + 1);
    else if (rating.equals("twostars"))
      seasonEpisodeRating.setTwostars(seasonEpisodeRating.getTwostars() + 1);
    else if (rating.equals("threestars"))
      seasonEpisodeRating.setThreestars(seasonEpisodeRating.getThreestars() + 1);
    else if (rating.equals("fourstars"))
      seasonEpisodeRating.setFourstars(seasonEpisodeRating.getFourstars() + 1);
    else if (rating.equals("fivestars"))
      seasonEpisodeRating.setFivestars(seasonEpisodeRating.getFivestars() + 1);
    else
      throw new InvalidRatingParameterException(rating);

    seasonEpisode.setRating(seasonEpisodeRating);

    double fx = (1 * seasonEpisodeRating.getOnestar()) +
      (2 * seasonEpisodeRating.getTwostars()) +
      (3 * seasonEpisodeRating.getThreestars()) +
      (4 * seasonEpisodeRating.getFourstars()) +
      (5 * seasonEpisodeRating.getFivestars());

    int x = 1 + 2 + 3 + 4 + 5;
    int overallRating = (int) Math.round(fx / x);
    seasonEpisode.setOverallrating(overallRating);

    episodeDao.save(seasonEpisode);

    HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.setLocation(ServletUriComponentsBuilder.fromCurrentRequest().path(seriesid + "/seasons/" + seasonid + "/rating").build().toUri());
    return new ResponseEntity<>(seasonEpisodeRating, httpHeaders, HttpStatus.OK);
  }

  /**
   * Validates a series id.
   *
   * @param id The id to validate.
   * @return The series with id.
   * @throws Exception
   */
  private Series validateSeriesId(String id) throws Exception {
    Series s = seriesDao.findById(id);
    if (s != null)
      return s;
    throw new SeriesNotFoundException(id);
  }

  /**
   * Validates a season id.
   *
   * @param id The season id.
   * @return The season.
   * @throws Exception
   */
  private Season validateSeasonId(String id) throws Exception {
    Season season = seasonDao.findById(id);
    if (season != null)
      return season;
    throw new SeriesSeasonNotFoundException(id);
  }

  /**
   * Validates an episode id.
   *
   * @param id The id of the episode.
   * @return The episode.
   * @throws Exception
   */
  private SeasonEpisode validateSeasonEpisodeId(String id) throws Exception {
    SeasonEpisode seasonEpisode = episodeDao.findById(id);
    if (seasonEpisode != null)
      return seasonEpisode;
    throw new SeasonEpisodeNotFoundException(id);
  }

  /**
   * Checks that the comment has required properties to add to series comments.
   *
   * @param comment
   */
  private void validateSeriesComment(Comment comment) throws Exception {
    if (comment.getSeriesid() != null)
      validateSeriesId(comment.getSeriesid());
    else throw new SeriesIdExpectedException();
  }

  /**
   * Checks that the comment has required properties to add to season comments.
   *
   * @param comment The comment.
   * @throws Exception
   */
  private void validateSeasonComment(Comment comment) throws Exception {
    if (comment.getSeasonid() != null)
      validateSeasonId(comment.getSeasonid());
    else throw new SeasonIdExptectedException();
  }

  /**
   * Checks that the comment has required properties to add to season episode comment.
   *
   * @param comment The comment.
   * @throws Exception
   */
  private void validateSeasonEpisodeComment(Comment comment) throws Exception {
    if (comment.getSeasonepisodeid() != null)
      validateSeasonEpisodeId(comment.getSeasonepisodeid());
    else throw new SeasonEpisodeIdExpectedException();
  }
}
