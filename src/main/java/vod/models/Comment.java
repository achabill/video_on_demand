package vod.models;

import org.springframework.data.annotation.Id;

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
    private int id;

    /**
     * The date the comment was posted.
     */
    private Date date;

    /**
     * The user who posted the comment.
     */
    private User user;

    /**
     * The id of the movie against this comment.
     */
    private int movieid;
    //endregion

    //region Constructors
    public Comment(){}

    public Comment(Date date, User user, int movieid)
    {
        this.date = date;
        this.user = user;
        this.movieid = movieid;
    }
    public Comment(int id, Date date, User user, int movieid)
    {
        this(date,user,movieid);
        this.id = id;
    }
    //endregion

    //region Getters

    /**
     * Gets the {@link Comment#id} instance.
     * @return The id of the comment.
     */
    public int getId(){return id;}

    /**
     * Gets the {@link Comment#date} instance.
     * @return The date the comment was posted.
     */
    public Date getDate(){return date;}

    /**
     * Gets the {@link Comment#user} instance.
     * @return The user who posted the comment.
     */
    public User getUser(){return user;}

    /**
     * Gets the {@link Comment#movieid} instance.
     * @return The id of the movie against this comment.
     */
    public int getMovieid(){return movieid;}
    //endregion

    //region Setters
    /**
     * Sets the {@link Comment#id} instance.
     * @param id The id of this commment.
     */
    public void setId(int id){this.id = id;}

    /**
     * Sets the {@link Comment#date} instance.
     * @param date The date this comment was posted.
     */
    public void setDate(Date date){this.date = date;}

    /**
     * Sets the {@link Comment#user} instance.
     * @param user The user who posted this comment.
     */
    public void setUser(User user){this.user = user;}

    /**
     * Sets the {@link Comment#movieid} instance.
     * @param movieid The id of the movie against this comment.
     */
    public void setMovieid(int movieid){this.movieid = movieid;}
    //endregion
}
