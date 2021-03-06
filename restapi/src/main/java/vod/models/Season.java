package vod.models;

import org.springframework.data.annotation.Id;

/**
 * A season instance of a series.
 */
public class Season {
  /**
   * The id of the seasons.
   */
  @Id
  private String id;

  /**
   * The season number.
   */
  private int seasonnumber;

  /**
   * The release year of the season.
   */
  private int releaseyear;

  /**
   * The series id.
   */
  private String seriesid;

  public String getCoverimageuuid() {
    return coverimageuuid;
  }

  public void setCoverimageuuid(String coverimageuuid) {
    this.coverimageuuid = coverimageuuid;
  }

  /**
   * The cover image.
   */
  private String coverimageuuid;

  /**
   * The number of episodes in the season.
   */
  private int numberofepisodes;
  private Rating rating;
  private int overallrating;

  public Season() {
  }

  public Rating getRating() {
    return rating;
  }

  public void setRating(Rating rating) {
    this.rating = rating;
  }

  public int getOverallrating() {
    return overallrating;
  }

  public void setOverallrating(int overallrating) {
    this.overallrating = overallrating;
  }

  /**
   * Gets the {@link Season#id} instance.
   *
   * @return The id of the season.
   */
  public String getId() {
    return id;
  }

  /**
   * Sets the {@link Season#id} instance.
   *
   * @param id The season id.
   */
  public void setId(String id) {
    this.id = id;
  }

  /**
   * Gets the {@link Season#seriesid} instance.
   *
   * @return The seriesid of the season.
   */
  public String getSeriesid() {
    return seriesid;
  }

  /**
   * Sets the {@link Season#seriesid} instance.
   *
   * @param seriesid The id of the series.
   */
  public void setSeriesid(String seriesid) {
    this.seriesid = seriesid;
  }


  /**
   * Gets the {@link Season#seasonnumber} instance.
   *
   * @return The seasonnumber of the season.
   */
  public int getSeasonnumber() {
    return seasonnumber;
  }

  /**
   * Sets the {@link Season#seasonnumber} instance.
   *
   * @param seasonnumber The season number.
   */
  public void setSeasonnumber(int seasonnumber) {
    this.seasonnumber = seasonnumber;
  }

  /**
   * Gets the {@link Season#numberofepisodes} instance.
   *
   * @return The number of episodes in the series.
   */
  public int getNumberofepisodes() {
    return numberofepisodes;
  }

  /**
   * Sets the {@link Season#numberofepisodes} instance.
   *
   * @param numberofepisodes The number of episodes in the season.
   */
  public void setNumberofepisodes(int numberofepisodes) {
    this.numberofepisodes = numberofepisodes;
  }

  /**
   * Gets the {@link Season#releaseyear} instance.
   *
   * @return The release year.
   */
  public int getReleaseyear() {
    return releaseyear;
  }

  /**
   * Sets the {@link Season#releaseyear} instance.
   *
   * @param releaseyear The release year of the season.
   */
  public void setReleaseyear(int releaseyear) {
    this.releaseyear = releaseyear;
  }
}
