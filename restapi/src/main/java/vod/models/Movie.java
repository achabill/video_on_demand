package vod.models;

import org.springframework.data.annotation.Id;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * A movie object
 */
public class Movie {
  //region Properties.

  /**
   * The unique id of the movie.
   */
  @Id
  private String id;

  /**
   * The title of the movie.
   */
  private String title;

  /**
   * The description of the movie.
   */
  private String description;

  public String getCoverimageuuid() {
    return coverimageuuid;
  }

  public void setCoverimageuuid(String coverimageuuid) {
    this.coverimageuuid = coverimageuuid;
  }

  /**
   * The cover image of the movie.
   */
  @NotNull
  private String coverimageuuid;

  /**
   * The number of likes the movie has got.
   */

  private int likes;

  /**
   * The release year of the movie.
   */
  private int releaseyear;

  /**
   * The number of dislikes the movie has got.
   */
  private int dislikes;

  /**
   * The number of view the movie has got.
   */
  private int views;

  /**
   * The Rating the movie has got.
   *
   * @see Rating
   */
  private Rating rating;

  /**
   * The overall rating of the movie.
   */
  private int overallrating;
  /**
   * The movie cast.
   */
  private List<String> cast;

  /**
   * The genre of the movie.
   */
  private String genre;

  public String getVideouuid() {
    return videouuid;
  }

  public void setVideouuid(String videouuid) {
    this.videouuid = videouuid;
  }

  /**
   * The actual path to the video file.
   */
  @NotNull
  private String videouuid;
  //endregion

  //region Constructors
  public Movie() {
  }
  //endregion

  //region Getters

  /**
   * Gets the {@link Movie#id} instance.
   *
   * @return The id of the item.
   */
  public String getId() {
    return id;
  }

  /**
   * Sets the {@link Movie#id} instance.
   *
   * @param id The id of the movie.
   */
  public void setId(String id) {
    this.id = id;
  }

  /**
   * Gets the {@link Movie#likes} instance.
   *
   * @return The number of likes the movie has got.
   */
  public int getLikes() {
    return likes;
  }

  /**
   * Sets the {@link Movie#likes} instance.
   *
   * @param likes The number of likes on the movie.
   */
  public void setLikes(int likes) {
    this.likes = likes;
  }

  /**
   * Gets the {@link Movie#dislikes} instance.
   *
   * @return The number of dislikes the movie has got.
   */
  public int getDislikes() {
    return dislikes;
  }

  /**
   * Sets the {@link Movie#dislikes} instance.
   *
   * @param dislikes The number of dislikes on the movie.
   */
  public void setDislikes(int dislikes) {
    this.dislikes = dislikes;
  }

  /**
   * Gets the {@link Movie#views} instance.
   *
   * @return The number of views the movie has got.
   */
  public int getViews() {
    return views;
  }

  /**
   * Sets the {@link Movie#views} instance.
   *
   * @param views The number of views of the movie.
   */
  public void setViews(int views) {
    this.views = views;
  }

  /**
   * Gets the {@link Movie#title} instance.
   *
   * @return The title of the movie.
   */
  public String getTitle() {
    return title;
  }

  /**
   * Sets the {@link Movie#title} instance.
   *
   * @param title The title of the movie.
   */
  public void setTitle(String title) {
    this.title = title;
  }

  /**
   * Gets the {@link Movie#description} instance.
   *
   * @return The description of the movie.
   */
  public String getDescription() {
    return description;
  }

  /**
   * Sets the {@link Movie#description} instance.
   *
   * @param description The description of the movie.
   */
  public void setDescription(String description) {
    this.description = description;
  }

  /**
   * Gets the {@link Movie#rating} instance.
   *
   * @return The rating of the movie.
   */
  public Rating getRating() {
    return rating;
  }

  /**
   * Sets the {@link Movie#rating} instance.
   *
   * @param rating The rating on the movie.
   */
  public void setRating(Rating rating) {
    this.rating = rating;
  }

  /**
   * Gets the {@link Movie#cast} instance.
   *
   * @return The cast of the movie.
   */
  public List<String> getCast() {
    return cast;
  }

  /**
   * Sets the {@link Movie#cast} instance.
   *
   * @param cast The cast of the movies.
   */
  public void setCast(List<String> cast) {
    this.cast = cast;
  }

  /**
   * Gets the {@link Movie#overallrating} instance.
   *
   * @return The overall rating of the movie.
   */
  public int getOverallrating() {
    return overallrating;
  }

  /**
   * sets the {@link Movie#overallrating} instance.
   *
   * @param overallrating
   */
  public void setOverallrating(int overallrating) {
    this.overallrating = overallrating;
  }

  /**
   * Gets the {@link Movie#releaseyear} instance.
   *
   * @return The release year of the movie.
   */
  public int getReleaseyear() {
    return releaseyear;
  }

  /**
   * Sets the {@link Movie#releaseyear} instance.
   *
   * @param releaseyear The release year of the movie.
   */
  public void setReleaseyear(int releaseyear) {
    this.releaseyear = releaseyear;
  }

  /**
   * Gets the {@link Movie#genre} instance.
   *
   * @return The genre of the movie.
   */
  public String getGenre() {
    return genre;
  }

  /**
   * Sets the {@link Movie#genre} instance.
   *
   * @param genre The genre of the movie.
   */
  public void setGenre(String genre) {
    this.genre = genre;
  }

  //endregion

  //region Overrides
  @Override
  public boolean equals(Object obj) {
    return ((Movie) obj).getId().equals(this.id);
  }
  //endregion
}
