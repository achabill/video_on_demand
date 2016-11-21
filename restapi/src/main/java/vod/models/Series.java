package vod.models;

import org.springframework.data.annotation.Id;

/**
 * Series object
 */
public class Series {
  /**
   * The id of the series.
   */
  @Id
  private String id;

  /**
   * The release year of the series.
   */
  private int releaseyear;
  /**
   * The title of the series.
   */
  private String title;

  /**
   * The description of the series.
   */
  private String description;

  /**
   * The cover image of the series.
   */
  private String coverimage;

  /**
   * The number of views on the series across all its episodes.
   */
  private int views;

  /**
   * The genre of the series.
   */
  private String genre;

  /**
   * The overall rating of the series.
   */
  private int overallrating;

  /**
   * The rating of the series.
   */
  private Rating rating;

  public Series() {
  }

  /**
   * Gets the {@link Series#id} instance.
   *
   * @return The id of the series.
   */
  public String getId() {
    return id;
  }

  /**
   * Sets the {@link Series#id} instance.
   *
   * @param id The id of the series.
   */
  public void setId(String id) {
    this.id = id;
  }

  /**
   * Gets the {@link Series#title} instance.
   *
   * @return The title of the series.
   */
  public String getTitle() {
    return title;
  }

  /**
   * Sets the {@link Series#title} instance.
   *
   * @param title The id of the series.
   */
  public void setTitle(String title) {
    this.title = title;
  }

  public Rating getRating() {
    return rating;
  }

  public void setRating(Rating rating) {
    this.rating = rating;
  }

  /**
   * Gets the {@link Series#description} instance.
   *
   * @return The description of the series.
   */
  public String getDescription() {
    return description;
  }

  /**
   * Sets the {@link Series#description} instance.
   *
   * @param description The id of the series.
   */
  public void setDescription(String description) {
    this.description = description;
  }

  /**
   * Gets the {@link Series#coverimage} instance.
   *
   * @return The coverimage of the series.
   */
  public String getCoverimage() {
    return coverimage;
  }

  /**
   * Sets the {@link Series#coverimage} instance.
   *
   * @param coverimage The id of the series.
   */
  public void setCoverimage(String coverimage) {
    this.coverimage = coverimage;
  }

  /**
   * Gets the {@link Series#genre} instance.
   *
   * @return The genre of the series.
   */
  public String getGenre() {
    return genre;
  }

  /**
   * Sets the {@link Series#genre} instance.
   *
   * @param genre The id of the series.
   */
  public void setGenre(String genre) {
    this.genre = genre;
  }

  /**
   * Gets the {@link Series#id} instance.
   *
   * @return The number of views of the series.
   */
  public int getViews() {
    return views;
  }

  /**
   * Sets the {@link Series#views} instance.
   *
   * @param views The id of the series.
   */
  public void setViews(int views) {
    this.views = views;
  }

  /**
   * Gets the {@link Series#releaseyear} instance.
   *
   * @return The release year of the series.
   */
  public int getReleaseyear() {
    return releaseyear;
  }

  /**
   * Sets the {@link Series#releaseyear} instance.
   *
   * @param releaseyear The release year of the series.
   */
  public void setReleaseyear(int releaseyear) {
    this.releaseyear = releaseyear;
  }

  /**
   * Gets the {@link Series#overallrating} instance.
   *
   * @return The overall rating of the series across all episodes on all seasons.
   */
  public int getOverallrating() {
    return overallrating;
  }

  /**
   * Sets the {@link Series#overallrating} instance.
   *
   * @param overallrating The id of the series.
   */
  public void setOverallrating(int overallrating) {
    this.overallrating = overallrating;
  }
}
