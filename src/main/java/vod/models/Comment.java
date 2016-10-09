package vod.models;

import org.springframework.data.annotation.Id;

import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * A comment
 */
public class Comment
{
    //region Properties
    /**
     * The id of the comment.
     */
    @Id
    private String id;

    /**
     * The date the comment was posted.
     */
    @NotNull
    private Long date;

    /**
     * The user who posted the comment.
     */
    private User user;

    /**
     * The id of the movie against this comment.
     */
    private String movieid;

    /**
     * The id of the series against this comment.
     */
    private String seriesid;

    /**
     * The id of the music against this comment.
     */
    private String musicid;

    /**
     * The valud of the commet. The contents.
     */
    private String value;
    //endregion

    //region Constructors
    public Comment(){}
    //endregion

    //region Getters

    /**
     * Gets the {@link Comment#id} instance.
     * @return The id of the comment.
     */
    public String getId(){return id;}

    /**
     * Gets the {@link Comment#date} instance.
     * @return The date the comment was posted.
     */
    public Long getDate(){return date;}

    /**
     * Gets the {@link Comment#user} instance.
     * @return The user who posted the comment.
     */
    public User getUser(){return user;}

    /**
     * Gets the {@link Comment#movieid} instance.
     * @return The id of the movie against this comment.
     */
    public String getMovieid(){return movieid;}

    /**
     * Gets the {@link Comment#seriesid} instance.
     * @return The id of the series against this comment.
     */
    public String getSeriesid(){return seriesid;}

    /**
     * Gets the {@link Comment#musicid} instance.
     * @return The id of the music against this comment.
     */
    public String getMusicid(){return musicid;}

    /**
     * Gets the {@link Comment#value} instance.
     * @return The comment contents.
     */
    public String getValue(){return value;}
    //endregion

    //region Setters
    /**
     * Sets the {@link Comment#id} instance.
     * @param id The id of this commment.
     */
    public void setId(String id){this.id = id;}

    /**
     * Sets the {@link Comment#date} instance.
     * @param date The date this comment was posted.
     */
    public void setDate(Long date){this.date = date;}

    /**
     * Sets the {@link Comment#user} instance.
     * @param user The user who posted this comment.
     */
    public void setUser(User user){this.user = user;}

    /**
     * Sets the {@link Comment#movieid} instance.
     * @param movieid The id of the movie against this comment.
     */
    public void setMovieid(String movieid){this.movieid = movieid;}

    /**
     * Sets the {@link Comment#seriesid} instance.
     * @param seriesid The id of the series against this comment.
     */
    public void setSeriesid(String seriesid){this.seriesid = seriesid;}

    /**
     * Sets the {@link Comment#seriesid} instance.
     * @param musicid The id of the music against this comment.
     */
    public void setMusicid(String musicid){this.musicid = musicid;}

    /**
     * Sets the {@link Comment#value} instance.
     * @param value The value to set.
     */
    public void setValue(String value){this.value = value;}
    //endregion
}
