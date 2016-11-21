package vod.models;

//TODO: Document this fle.

import org.springframework.data.annotation.Id;

public class SeasonEpisode {
  @Id
  private String id;
  private String seasonid;
  private String seriesid;
  private int episodenumber;
  private int views;
  private int likes;
  private int dislikes;
  private int overallrating;
  private Rating rating;
  private String title;
  private String videofile;

  public SeasonEpisode() {
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getSeasonid() {
    return seasonid;
  }

  public void setSeasonid(String seasonid) {
    this.seasonid = seasonid;
  }

  public String getSeriesid() {
    return seriesid;
  }

  public void setSeriesid(String seriesid) {
    this.seriesid = seriesid;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public int getEpisodenumber() {
    return episodenumber;
  }

  public void setEpisodenumber(int episodenumber) {
    this.episodenumber = episodenumber;
  }

  public int getViews() {
    return views;
  }

  public void setViews(int views) {
    this.views = views;
  }

  public String getVideofile() {
    return videofile;
  }

  public void setVideofile(String videofile) {
    this.videofile = videofile;
  }

  public int getLikes() {
    return likes;
  }

  public void setLikes(int likes) {
    this.likes = likes;
  }

  public int getDislikes() {
    return dislikes;
  }

  public void setDislikes(int dislikes) {
    this.dislikes = dislikes;
  }

  public int getOverallrating() {
    return overallrating;
  }

  public void setOverallrating(int overallrating) {
    this.overallrating = overallrating;
  }

  public Rating getRating() {
    return rating;
  }

  public void setRating(Rating rating) {
    this.rating = rating;
  }

}
