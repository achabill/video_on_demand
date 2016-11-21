package vod.controllers.backendcontrollers;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import vod.exceptions.*;
import vod.filestorage.MultipartFileSender;
import vod.filestorage.StorageService;
import vod.helpers.StaticFactory;
import vod.helpers.TokenService;
import vod.models.*;
import vod.repositories.CommentsRepository;
import vod.repositories.EpisodesRepository;
import vod.repositories.SeasonsRepository;
import vod.repositories.SeriesRepository;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * The rest backend controller for series.
 */
@RestController
@Api(value = "Admin series controller", description = "An interface to control the series database")
@RequestMapping(value = "admin/series")
public class BackendSeriesController {

  @Autowired
  SeriesRepository seriesRepository;
  @Autowired
  CommentsRepository commentsRepository;
  @Autowired
  EpisodesRepository episodesRepository;
  @Autowired
  SeasonsRepository seasonsRepository;
  @Autowired
  TokenService tokenService;
  @Autowired
  StorageService storageService;

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
   * @param genre    The series genre.
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
    tokenService.verifyAdmin(accessToken);
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
      Iterable<Series> series = seriesRepository.findByGenre(genre);
      List<Series> result = new ArrayList<>();
      series.forEach(s -> result.add(s));
      return new ResponseEntity<>(result, httpHeaders, HttpStatus.OK);
    } else {
      List<Series> series = seriesRepository.findAll();

      List<Series> result = new ArrayList<>();
      series.forEach(s -> result.add(s));
      return new ResponseEntity<>(result, httpHeaders, HttpStatus.OK);
    }
  }

  /**
   * Deletes all series from the system.
   *
   * @return
   */
  @RequestMapping(value = "/", method = RequestMethod.DELETE)
  @ResponseBody
  @ApiOperation(value = "Deletes all series", notes = "Deletes all series including season, etc..")
  public ResponseEntity<?> deleteAllSeries(@RequestParam(value = "accesstoken", required = true) String accessToken) throws Exception {
    tokenService.verifyAdmin(accessToken);
    List<Series> series = seriesRepository.findAll();

    for (int i = 0; i < series.size(); i++) {
      List<Season> seasons = seasonsRepository.findBySeriesid(series.get(i).getId());
      for (int j = 0; j < seasons.size(); j++) {
        List<SeasonEpisode> episodes = episodesRepository.findBySeasonid(seasons.get(i).getId());
        for (int k = 0; k < episodes.size(); k++) {
          List<Comment> comments = commentsRepository.findBySeasonepisodeid(episodes.get(k).getId());
          commentsRepository.delete(comments);
          storageService.delete(episodes.get(k).getVideofile());
        }
        episodesRepository.delete(episodes);
        List<Comment> comments = commentsRepository.findBySeasonid(seasons.get(j).getId());
        commentsRepository.delete(comments);
        storageService.delete(seasons.get(j).getCoverimage());
      }
      List<Comment> comments = commentsRepository.findBySeriesid(series.get(i).getId());
      commentsRepository.delete(comments);
      seasonsRepository.delete(seasons);
      storageService.delete(series.get(i).getCoverimage());
    }
    seriesRepository.delete(series);

    HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.setLocation(ServletUriComponentsBuilder
      .fromCurrentRequest().path("/").build().toUri());
    return new ResponseEntity<>(new PropertyValue("", ""), httpHeaders, HttpStatus.OK);
  }

  /**
   * Adds on series to the database
   *
   * @param series
   * @return
   */
  @ApiOperation(value = "Add a series", notes = "Adds a series to the database")
  @ResponseBody
  @RequestMapping(value = "/", method = RequestMethod.POST)
  public ResponseEntity<?> addSeries(@RequestBody Series series,
                                     @RequestParam(value = "accesstoken", required = true) String accessToken) throws Exception {
    tokenService.verifyAdmin(accessToken);
    HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.setLocation(ServletUriComponentsBuilder
      .fromCurrentRequest().path("/").build().toUri());
    seriesRepository.save(series);
    return new ResponseEntity<>(series, httpHeaders, HttpStatus.CREATED);
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
    tokenService.verifyAdmin(accessToken);
    Series series = validateSeriesId(id);
    HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.setLocation(ServletUriComponentsBuilder
      .fromCurrentRequest().path("/").buildAndExpand(id).toUri());
    return new ResponseEntity<>(series, httpHeaders, HttpStatus.OK);
  }

  /**
   * Deletes the series with the specified id.
   *
   * @param id The id of the series to delete
   * @return
   * @throws Exception
   */
  @ResponseBody
  @ApiOperation(value = "Delete series by id", notes = "Delete the series with the specified id")
  @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
  public ResponseEntity<?> deleteSeriesById(@PathVariable("id") String id,
                                            @RequestParam(value = "accesstoken", required = true) String accessToken) throws Exception {

    tokenService.verifyAdmin(accessToken);
    Series series = validateSeriesId(id);
    List<Season> seasons = seasonsRepository.findBySeriesid(series.getId());

    for (int i = 0; i < seasons.size(); i++) {
      List<SeasonEpisode> episodes = episodesRepository.findBySeasonid(seasons.get(i).getId());
      for (int j = 0; j < episodes.size(); j++) {
        List<Comment> comments = commentsRepository.findBySeasonepisodeid(episodes.get(j).getId());
        commentsRepository.delete(comments);
        storageService.delete(episodes.get(j).getVideofile());
      }
      episodesRepository.delete(episodes);
      List<Comment> comments = commentsRepository.findBySeasonid(seasons.get(i).getId());
      commentsRepository.delete(comments);
      storageService.delete(seasons.get(i).getCoverimage());
    }
    storageService.delete(series.getCoverimage());
    List<Comment> comments = commentsRepository.findBySeriesid(id);
    commentsRepository.delete(comments);
    seasonsRepository.delete(seasons);
    seriesRepository.delete(series);

    HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.setLocation(ServletUriComponentsBuilder
      .fromCurrentRequest().path("/").buildAndExpand(id).toUri());
    return new ResponseEntity<>(new PropertyValue("id", id.toString()), httpHeaders, HttpStatus.OK);
  }

  /**
   * Gets the seasons of the series with id.
   *
   * @param id The id of the series.
   * @return The seasons of the series.
   */
  @ApiOperation(value = "Gets the seasons of the series", notes = "Gets the season of the series with id = {id}")
  @RequestMapping(value = "/{id}/seasons", method = RequestMethod.GET)
  @ResponseBody
  public ResponseEntity<List<Season>> getSeriesSeasons(@PathVariable("id") String id,
                                                       @RequestParam(value = "accesstoken", required = true) String accessToken) throws Exception {
    tokenService.verifyAdmin(accessToken);
    Series series = validateSeriesId(id);
    List<Season> seasons = seasonsRepository.findBySeriesid(id);

    HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.setLocation(ServletUriComponentsBuilder
      .fromCurrentRequest().path("/").buildAndExpand(id + "/seasons").toUri());
    return new ResponseEntity<>(seasons, httpHeaders, HttpStatus.OK);
  }

  /**
   * Adds a season to the database.
   *
   * @param season The season to add.
   * @return The season added
   */
  @ResponseBody
  @RequestMapping(value = "/{id}/seasons", method = RequestMethod.POST)
  @ApiOperation(value = "Adds a season", notes = "Adds a season to the series")
  public ResponseEntity<?> addSeriesSeason(@PathVariable("id") String id,
                                           @RequestBody Season season,
                                           @RequestParam(value = "accesstoken", required = true) String accessToken) throws Exception {
    tokenService.verifyAdmin(accessToken);
    validateSeriesId(id);
    HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.setLocation(ServletUriComponentsBuilder
      .fromCurrentRequest().path("/").buildAndExpand(id + "/seasons").toUri());
    seasonsRepository.save(season);
    return new ResponseEntity<>(season, httpHeaders, HttpStatus.CREATED);
  }

  /**
   * Deletes all seasons of a series
   *
   * @param id The id of the series
   * @return
   * @throws Exception
   */
  @RequestMapping(value = "/{id}/seasons", method = RequestMethod.DELETE)
  @ResponseBody
  @ApiOperation(value = "Deletes all seasons of a series", notes = "Deletes the seasons of the series with the specified id.")
  public ResponseEntity<?> deleteAllSeasonsOfSeries(@PathVariable("id") String id,
                                                    @RequestParam(value = "accesstoken", required = true) String accessToken) throws Exception {
    tokenService.verifyAdmin(accessToken);
    validateSeriesId(id);
    List<Season> seasons = seasonsRepository.findBySeriesid(id);

    for (int i = 0; i < seasons.size(); i++) {
      List<SeasonEpisode> episodes = episodesRepository.findBySeasonid(seasons.get(i).getId());
      for (int j = 0; j < episodes.size(); j++) {
        List<Comment> comments = commentsRepository.findBySeasonepisodeid(episodes.get(j).getId());
        commentsRepository.delete(comments);
        storageService.delete(episodes.get(j).getVideofile());
      }
      storageService.delete(seasons.get(i).getCoverimage());
      episodesRepository.delete(episodes);
      List<Comment> comments = commentsRepository.findBySeasonid(seasons.get(i).getId());
      commentsRepository.delete(comments);
    }
    seasonsRepository.delete(seasons);

    HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.setLocation(ServletUriComponentsBuilder
      .fromCurrentRequest().path("/").buildAndExpand(id + "/seasons").toUri());
    return new ResponseEntity<>(new PropertyValue("id", id.toString()), httpHeaders, HttpStatus.OK);
  }

  /**
   * Gets a season of a series.
   *
   * @param seriesid The series id.
   * @param seasonid The season id.
   * @return The season.
   * @throws Exception
   */
  @ApiOperation(value = "Get season by id", notes = "Gets the season of a series specifying the series id and season id.")
  @ResponseBody
  @RequestMapping(value = "/{seriesid}/seasons/{seasonid}", method = RequestMethod.GET)
  public ResponseEntity<Season> getSeasonOfSeries(@PathVariable("seriesid") String seriesid,
                                                  @PathVariable("seasonid") String seasonid,
                                                  @RequestParam(value = "accesstoken", required = true) String accessToken) throws Exception {
    tokenService.verifyAdmin(accessToken);
    Series series = validateSeriesId(seriesid);
    Season season = validateSeasonId(seasonid);

    HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.setLocation(ServletUriComponentsBuilder
      .fromCurrentRequest().path("/").buildAndExpand(seriesid + "/seasons/" + seasonid).toUri());
    return new ResponseEntity<>(season, httpHeaders, HttpStatus.OK);
  }

  /**
   * Deletes a season of a series specified by the id.
   *
   * @param seriesid the series id.
   * @param seasonid The season id.
   * @return
   * @throws Exception
   */
  @ResponseBody
  @ApiOperation(value = "Deletes a season by id", notes = "Deletes the season with the specified id")
  @RequestMapping(value = "/{seriesid}/seasons/{seasonid}/", method = RequestMethod.DELETE)
  public ResponseEntity<?> deleteSeasonById(@PathVariable("seriesid") String seriesid,
                                            @PathVariable("seasonid") String seasonid,
                                            @RequestParam(value = "accesstoken", required = true) String accessToken) throws Exception {
    tokenService.verifyAdmin(accessToken);
    Series series = validateSeriesId(seriesid);
    Season season = validateSeasonId(seasonid);

    HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.setLocation(ServletUriComponentsBuilder
      .fromCurrentRequest().path("/").buildAndExpand(seriesid + "/seasons/" + seasonid).toUri());

    List<SeasonEpisode> episodes = episodesRepository.findBySeasonid(seasonid);
    for (int i = 0; i < episodes.size(); i++) {
      List<Comment> comments = commentsRepository.findBySeasonepisodeid(episodes.get(i).getId());
      commentsRepository.delete(comments);
      storageService.delete(episodes.get(i).getVideofile());
    }
    episodesRepository.delete(episodes);

    List<Comment> comments1 = commentsRepository.findBySeasonid(seasonid);
    commentsRepository.delete(comments1);
    storageService.delete(season.getCoverimage());
    seasonsRepository.delete(season);

    return new ResponseEntity<>(new PropertyValue("seasonid", seasonid.toString()), httpHeaders, HttpStatus.OK);
  }


  /**
   * Gets the episodes of a season.
   *
   * @param seriesid The series id.
   * @param seasonid The season id.
   * @return The episodes in the season.
   * @throws Exception
   */
  @ApiOperation(value = "Get episodes of a season", notes = "Gets the episodes of a season with specified id.")
  @ResponseBody
  @RequestMapping(value = "/{seriesid}/seasons/{seasonid}/episodes", method = RequestMethod.GET)
  public ResponseEntity<List<SeasonEpisode>> getSeasonsEpisodes(@PathVariable("seriesid") String seriesid,
                                                                @PathVariable("seasonid") String seasonid,
                                                                @RequestParam(value = "accesstoken", required = true) String accessToken) throws Exception {
    tokenService.verifyAdmin(accessToken);
    Series series = validateSeriesId(seriesid);
    Season season = validateSeasonId(seasonid);
    List<SeasonEpisode> seasonEpisodes = episodesRepository.findBySeasonid(seasonid);

    HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.setLocation(ServletUriComponentsBuilder
      .fromCurrentRequest().path("/").buildAndExpand(seriesid + "/seasons/" + seasonid + "/episodes").toUri());
    return new ResponseEntity<>(seasonEpisodes, httpHeaders, HttpStatus.OK);
  }

  /**
   * Adds an episode to a season of a series
   *
   * @param seriesid The series id
   * @param seasonid The season id
   * @param episode  The episode
   * @return The episode added
   * @throws Exception
   */
  @ResponseBody
  @RequestMapping(value = "/{seriesid}/seasons/{seasonid}/episodes", method = RequestMethod.POST)
  @ApiOperation(value = "Adds an episode to the season", notes = "Adds an episode to the season with the specified id.")
  public ResponseEntity<?> addSeasonEpisode(@PathVariable("seriesid") String seriesid,
                                            @PathVariable("seasonid") String seasonid,
                                            @RequestBody SeasonEpisode episode,
                                            @RequestParam(value = "accesstoken", required = true) String accessToken) throws Exception {
    tokenService.verifyAdmin(accessToken);
    validateSeriesId(seriesid);
    validateSeasonId(seasonid);

    HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.setLocation(ServletUriComponentsBuilder
      .fromCurrentRequest().path("/").buildAndExpand(seriesid + "/seasons/" + seasonid + "/episodes").toUri());
    episodesRepository.save(episode);
    return new ResponseEntity<>(episode, httpHeaders, HttpStatus.CREATED);
  }

  /**
   * Deletes  all episodes of the season.
   *
   * @param seriesid
   * @param seasonid
   * @return
   * @throws Exception
   */
  @ResponseBody
  @RequestMapping(value = "/{seriesid}/seasons/{seasonid}/episodes", method = RequestMethod.DELETE)
  @ApiOperation(value = "Deletes all season episodes", notes = "Deletes all episodes of the season specified")
  public ResponseEntity<?> deleteAllSeasonEpisodes(@PathVariable("seriesid") String seriesid,
                                                   @PathVariable("seasonid") String seasonid,
                                                   @RequestParam(value = "accesstoken", required = true) String accessToken) throws Exception {
    tokenService.verifyAdmin(accessToken);
    validateSeriesId(seriesid);
    validateSeasonId(seasonid);

    HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.setLocation(ServletUriComponentsBuilder
      .fromCurrentRequest().path("/").buildAndExpand(seriesid + "/seasons/" + seasonid + "/episodes").toUri());

    List<SeasonEpisode> episodes = episodesRepository.findBySeasonid(seasonid);
    for (int i = 0; i < episodes.size(); i++) {
      List<Comment> comments = commentsRepository.findBySeasonepisodeid(episodes.get(i).getId());
      commentsRepository.delete(comments);
      storageService.delete(episodes.get(i).getVideofile());
    }
    episodesRepository.delete(episodes);

    return new ResponseEntity<>(new PropertyValue("", seasonid.toString()), httpHeaders, HttpStatus.OK);
  }


  /**
   * Gets the an episode of a season.
   *
   * @param id       The episode id.
   * @param seasonid The season id.
   * @param seriesid The series id.
   * @param play     To play?
   * @param request  HttpRequest
   * @param response HttpResponse
   * @return The episode.
   * @throws Exception
   */
  @RequestMapping(value = "/{seriesid}/seasons/{seasonid}/episodes/{id}", method = RequestMethod.GET)
  @ApiOperation(value = "Gets a season epidoe", notes = "Gets an episode of a season in a series")
  @ResponseBody
  public ResponseEntity<SeasonEpisode> getSeasonEpisode(@PathVariable("id") String id,
                                                        @PathVariable("seasonid") String seasonid,
                                                        @PathVariable("seriesid") String seriesid,
                                                        @RequestParam(value = "play", required = false) String play,
                                                        @RequestParam(value = "accesstoken", required = true) String accessToken, HttpServletRequest request, HttpServletResponse response) throws Exception {
    tokenService.verifyAdmin(accessToken);
    Season season = validateSeasonId(seasonid);
    Series series = validateSeriesId(seriesid);
    SeasonEpisode seasonEpisode = validateSeasonEpisodeId(id);

    HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.setLocation(ServletUriComponentsBuilder
      .fromCurrentRequest().path("/").buildAndExpand(seriesid + "/seasons/" + seasonid + "/episodes" + id).toUri());
    if (play != null)
      MultipartFileSender.fromFile(new File(seasonEpisode.getVideofile())).with(request).with(response).serveResource();

    return new ResponseEntity<>(seasonEpisode, httpHeaders, HttpStatus.OK);
  }

  /**
   * Deletes a season epidode specified by id
   *
   * @param seriesid The series id
   * @param seasonid The season id
   * @param id       The id of the episode
   * @return
   * @throws Exception
   */
  @ResponseBody
  @ApiOperation(value = "Delete a season episode specified by id", notes = "Deletes one season episode")
  @RequestMapping(value = "/{seriesid}/seasons/{seasonid}/episodes/{id}", method = RequestMethod.DELETE)
  public ResponseEntity<?> deleteSpecifiedSeasonEpisode(@PathVariable("seriesid") String seriesid,
                                                        @PathVariable("seasonid") String seasonid,
                                                        @PathVariable("id") String id,
                                                        @RequestParam(value = "accesstoken", required = true) String accessToken) throws Exception {
    tokenService.verifyAdmin(accessToken);
    validateSeasonId(seasonid);
    validateSeriesId(seriesid);
    SeasonEpisode episode = validateSeasonEpisodeId(id);

    List<Comment> comments = commentsRepository.findBySeasonepisodeid(id);
    commentsRepository.delete(comments);
    storageService.delete(episode.getVideofile());
    episodesRepository.delete(episode);

    HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.setLocation(ServletUriComponentsBuilder
      .fromCurrentRequest().path("/").buildAndExpand(seriesid + "/seasons/" + seasonid + "/episodes" + id).toUri());
    return new ResponseEntity<>(new PropertyValue("id", id), httpHeaders, HttpStatus.OK);
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
  @ApiOperation(value = "Gets all comments for series", notes = "Gets comments for the series with id = {id}")
  public ResponseEntity<List<Comment>> getSeriesComments(@PathVariable("id") String id,
                                                         @RequestParam(value = "accesstoken", required = true) String accessToken) throws Exception {
    tokenService.verifyAdmin(accessToken);
    Series series = validateSeriesId(id);


    HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.setLocation(ServletUriComponentsBuilder
      .fromCurrentRequest().path("/").buildAndExpand(id + "/comments").toUri());

    Iterable<Comment> comments = commentsRepository.findBySeriesid(id);
    List<Comment> result = new ArrayList<>();
    comments.forEach(c -> result.add(c));
    return new ResponseEntity<List<Comment>>(result, httpHeaders, HttpStatus.OK);
  }

  /**
   * Deletes all comments for a series
   *
   * @param id The series id
   * @return The id of the series.
   * @throws Exception
   */
  @RequestMapping(value = "/{id}/comments", method = RequestMethod.DELETE)
  @ResponseBody
  @ApiOperation(value = "Delets all comments for a series", notes = "Delets all comments for the series specified")
  public ResponseEntity<?> deleteAllSeriesComments(@PathVariable("id") String id,
                                                   @RequestParam(value = "accesstoken", required = true) String accessToken) throws Exception {
    tokenService.verifyAdmin(accessToken);
    Series series = validateSeriesId(id);

    List<Comment> comments = commentsRepository.findBySeriesid(id);
    commentsRepository.delete(comments);

    HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.setLocation(ServletUriComponentsBuilder
      .fromCurrentRequest().path("/").buildAndExpand(id + "/comments").toUri());
    return new ResponseEntity<>(new PropertyValue("id", id.toString()), httpHeaders, HttpStatus.OK);
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
  @ApiOperation(value = "Gets all comments for season", notes = "Gets comments for the season with id = {id}")
  public ResponseEntity<List<Comment>> getSeasonComments(@PathVariable("seriesid") String seriesid,
                                                         @PathVariable("seasonid") String seasonid,
                                                         @RequestParam(value = "accesstoken", required = true) String accessToken) throws Exception {
    tokenService.verifyAdmin(accessToken);
    validateSeriesId(seriesid);
    validateSeasonId(seasonid);


    HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.setLocation(ServletUriComponentsBuilder
      .fromCurrentRequest().path("/").buildAndExpand(seasonid + "/seasons/" + seasonid + "/comments").toUri());
    List<Comment> comments = commentsRepository.findBySeasonid(seasonid);
    List<Comment> result = new ArrayList<>();
    comments.forEach(c -> result.add(c));
    return new ResponseEntity<List<Comment>>(result, httpHeaders, HttpStatus.OK);
  }

  /**
   * Deletes all comments for a season
   *
   * @param seriesid The series of the season
   * @param seasonid Teh season id
   * @return The id of the season with deleted comments
   * @throws Exception
   */
  @RequestMapping(value = "/{seriesid}/seasons/{seasonid}/comments", method = RequestMethod.DELETE)
  @ResponseBody
  @ApiOperation(value = "Deletes comments for season", notes = "Deletes comments for the season with id = {id}")
  public ResponseEntity<?> deleteSeasonComments(@PathVariable("seriesid") String seriesid,
                                                @PathVariable("seasonid") String seasonid,
                                                @RequestParam(value = "accesstoken", required = true) String accessToken) throws Exception {
    tokenService.verifyAdmin(accessToken);
    validateSeasonId(seasonid);
    validateSeriesId(seriesid);

    List<Comment> comments = commentsRepository.findBySeasonid(seasonid);
    commentsRepository.delete(comments);

    HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.setLocation(ServletUriComponentsBuilder
      .fromCurrentRequest().path("/").buildAndExpand(seriesid + "/seasons/" + seasonid + "/comments").toUri());
    return new ResponseEntity<>(new PropertyValue("seasonid", seasonid), httpHeaders, HttpStatus.OK);
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
  public ResponseEntity<List<Comment>> getSeasonEpisodeComments(@PathVariable("seriesid") String seriesid,
                                                                @PathVariable("seasonid") String seasonid,
                                                                @PathVariable("seasonepisodeid") String seasonepisodeid,
                                                                @RequestParam(value = "accesstoken", required = true) String accessToken) throws Exception {
    tokenService.verifyAdmin(accessToken);
    validateSeriesId(seriesid);
    validateSeasonId(seasonid);
    validateSeasonEpisodeId(seasonepisodeid);

    HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.setLocation(ServletUriComponentsBuilder
      .fromCurrentRequest().path("/").buildAndExpand(seasonid + "/seasons/" + seasonid + "/episodes/" + seasonepisodeid + "/comments").toUri());

    List<Comment> comments = commentsRepository.findBySeasonepisodeid(seasonepisodeid);
    List<Comment> result = new ArrayList<>();
    comments.forEach(c -> result.add(c));
    return new ResponseEntity<List<Comment>>(result, httpHeaders, HttpStatus.OK);
  }

  /**
   * Delets season episode comments
   *
   * @param seriesid        The series id of the episode
   * @param seasonid        The season id of the episode
   * @param seasonepisodeid The id of the episode
   * @return The id of the episode whose comments have been deleted
   * @throws Exception
   */
  @RequestMapping(value = "/{seriesid}/seasons/{seasonid}/episodes/{seasonepisodeid}/comments", method = RequestMethod.DELETE)
  @ResponseBody
  @ApiOperation(value = "Deletes comments for season episode", notes = "Deletes comments for the episode with id = {id}")
  public ResponseEntity<?> deleteSeasonEpisodeComments(@PathVariable("seriesid") String seriesid,
                                                       @PathVariable("seasonid") String seasonid,
                                                       @PathVariable("seasonepisodeid") String seasonepisodeid,
                                                       @RequestParam(value = "accesstoken", required = true) String accessToken) throws Exception {
    tokenService.verifyAdmin(accessToken);
    validateSeriesId(seriesid);
    validateSeasonEpisodeId(seasonid);
    validateSeasonEpisodeId(seasonepisodeid);

    List<Comment> comments = commentsRepository.findBySeasonepisodeid(seasonepisodeid);
    commentsRepository.delete(comments);

    HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.setLocation(ServletUriComponentsBuilder
      .fromCurrentRequest().path("/").buildAndExpand(seriesid + "/seasons/" + seasonid + "/episodes/" + seasonepisodeid + "/comments").toUri());
    return new ResponseEntity<>(new PropertyValue("seasonepisodeid", seasonepisodeid), httpHeaders, HttpStatus.OK);
  }

  @RequestMapping(value = "/{seriesid}/seasons/{seasonid}/episodes/{seasonepisodeid}/comments/{commentid}", method = RequestMethod.DELETE)
  @ResponseBody
  @ApiOperation(value = "Delets the comment for season episode", notes = "Deletes the comment for the episode with id = {id}")
  public ResponseEntity<?> deleteSeasonEpisodeCommentWithId(@PathVariable("seriesid") String seriesid,
                                                            @PathVariable("seasonid") String seasonid,
                                                            @PathVariable("seasonepisodeid") String seasonepisodeid,
                                                            @PathVariable("commentid") String commentid,
                                                            @RequestParam(value = "accesstoken", required = true) String accessToken) throws Exception {
    tokenService.verifyAdmin(accessToken);
    validateSeriesId(seriesid);
    validateSeasonEpisodeId(seasonepisodeid);
    validateSeasonId(seasonid);
    Comment comment = validateCommentId(commentid);

    commentsRepository.delete(comment);
    HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.setLocation(ServletUriComponentsBuilder
      .fromCurrentRequest().path("/").buildAndExpand(seriesid + "/seasons/" + seasonid + "/episodes/" + seasonepisodeid + "/comments" + commentid).toUri());
    return new ResponseEntity<>(comment, httpHeaders, HttpStatus.OK);

  }

  /**
   * Validates that the comment with id exists.
   *
   * @param id The comment id.
   * @return
   */
  private Comment validateCommentId(String id) {
    Comment c = commentsRepository.findById(id);
    if (c == null)
      throw new CommentNotFoundException(id);
    return c;
  }

  /**
   * Validates a series id.
   *
   * @param id The id to validate.
   * @return The series with id.
   * @throws Exception
   */
  private Series validateSeriesId(String id) throws Exception {
    Series s = seriesRepository.findById(id);
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
    Season season = seasonsRepository.findById(id);
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
    SeasonEpisode seasonEpisode = episodesRepository.findById(id);
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
