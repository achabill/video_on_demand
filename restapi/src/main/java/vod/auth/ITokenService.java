package vod.auth;


import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

/**
 * A service to manage JWT (Json web tokens) tokens.
 *
 * Maps string key to value V
 * @author Acha Bill <achabill12[at]gmail[dot]com>
 */

public interface ITokenService<T> {

  /**
   * Hashes the data into a fix hashcode
   *
   * @param data The data to be digested
   * @return String The digested data
   */
  public String digest(String data) throws UnsupportedEncodingException, NoSuchAlgorithmException;

  /**
   * Creates a token for the key and save it
   *
   * @param value The value
   * @return String the new token
   */
  public String setToken(Object value);

  /**
   * Gets the token associated with the value
   *
   * @param value The vlue mapped to the token
   * @return String the token
   */
  public String getToken(Object value);

  /**
   * Verify if the token exists.
   *
   * @param token
   * @throws Exception InvalidTokenException
   */
  public boolean verifyToken(String token);

  /**
   * Removes the token and associated data.
   *
   * @param token The token to be removed.
   */
  public boolean removeToken(String token);

  /**
   * Gets that value that the token is mapped.
   * Returns null if token does not exist.
   *
   * @param token The token
   * @return The value mapped
   */
  public T tokenValue(String token);

  /**
   * Clears all tokens
   */
  public void clearTokens();
}
