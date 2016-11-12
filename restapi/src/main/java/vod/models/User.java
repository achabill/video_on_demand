package vod.models;

import org.springframework.data.annotation.Id;

import javax.validation.constraints.NotNull;

/**
 * User object.
 */
public class User {
  //region Properties
  /**
   * The id of the user
   */
  @Id
  private String id;

  /**
   * The userame of the user
   */
  @NotNull
  private String username;
  //endregion
  @NotNull
  private String password;

  private String previlege;

  //region Getters

  //region Constructors
  public User() {
  }

  public User(String username) {
    this.username = username;
  }
  //endregion

  public User(String id, String username) {
    this(username);
    this.id = id;
  }

  /**
   * Gets the {@link User#id} instance.
   *
   * @return The id of the user.
   */

  public String getPassword() {
    return password;
  }

  public String getPrevilege() {
    return previlege;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public void setPrevilege(String previlege) {
    this.previlege = previlege;
  }

  public String getId() {
    return id;
  }

  /**
   * Sets the {@link User#id} instance.
   *
   * @param id The id of the user.
   */
  public void setId(String id) {
    this.id = id;
  }
  //endregion

  //region Setters

  /**
   * Gets the {@link User#username} instance.
   *
   * @return The username of the user.
   */
  public String getUsername() {
    return username;
  }

  /**
   * Sets the {@link User#username} instance.
   *
   * @param username The username of the user.
   */
  public void setUsername(String username) {
    this.username = username;
  }
  //endregion
}
