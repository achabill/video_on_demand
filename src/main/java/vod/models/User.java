package vod.models;

import org.springframework.data.annotation.Id;

/**
 * User object.
 */
public class User
{
    //region Properties
    /**
     * The id of the user
     */
    @Id
    private int id;

    /**
     * The userame of the user
     */
    private String username;
    //endregion

    //region Getters
    /**
     * Gets the {@link User#id} instance.
     * @return The id of the user.
     */

    public int getId(){return id;}
    /**
     * Gets the {@link User#username} instance.
     * @return The username of the user.
     */
    public String getUsername(){return username;}
    //endregion

    //region Constructors
    public User(){}
    public User(String username)
    {
        this.username = username;
    }
    public User(int id, String username)
    {
        this(username);
        this.id = id;
    }
    //endregion

    //region Setters

    /**
     * Sets the {@link User#id} instance.
     * @param id The id of the user.
     */
    public void setId(int id){this.id = id;}

    /**
     * Sets the {@link User#username} instance.
     * @param username The username of the user.
     */
    public void setUsername(String username) {this.username = username;}
    //endregion
}
