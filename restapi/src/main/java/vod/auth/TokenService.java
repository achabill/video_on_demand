package vod.auth;
import vod.auth.Sha1Hex;

import org.springframework.stereotype.Service;
import vod.models.User;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static java.util.UUID.randomUUID;

/**
 * A service to manage JWT (Json web tokens) tokens.
 *
 * Maps string key to value V
 * @author Acha Bill <achabill12[at]gmail[dot]com>
 */

@Service("TokenService")
class TokenService implements ITokenService<User> {

  private final static Map<String,User> tokens = new HashMap<String,User>();
  private final Sha1Hex sha1 = new Sha1Hex();

  /**
   * Hashes the data into a fix hashcode
   *
   * @param data The data to be digested
   * @return String The digested data
   */
  @Override
  public String digest(String data) throws UnsupportedEncodingException, NoSuchAlgorithmException {
    return sha1.makeSHA1Hash(data);
  }

  /**
   * Creates a token for the key and save it
   *
   * @param value The value
   * @return String the new token
   */
  @Override
  public String setToken(Object value) {
    String token =  randomUUID().toString().replaceAll("-","");
    tokens.put(token,(User)value);
    return token;
  }

  /**
   * Gets the token associated with the value
   *
   * @param value The vlue mapped to the token
   * @return String the token
   */
  @Override
  public String getToken(Object value) {
    Iterator it = tokens.keySet().iterator();
    while(it.hasNext()){
      String key = (String)it.next();
      if(tokens.get(key).equals((String)value))
        return key;
    }
    return null;
  }


  /**
   * Verify if the token exists.
   *
   * @param token
   * @throws Exception InvalidTokenException
   */
  @Override
  public boolean verifyToken(String token){
    return tokens.containsValue(token);
  }

  /**
   * Removes the token and associated data.
   *
   * @param token The token to be removed.
   */
  @Override
  public boolean removeToken(String token) {
    return tokens.remove(token) != null;
  }

  /**
   * Gets that value that the token is mapped.
   * Returns null if token does not exist.
   *
   * @param token The token
   * @return The value mapped
   */
  @Override
  public User tokenValue(String token) {
    return tokens.get(token);
  }

  /**
   * Clears all tokens
   */
  @Override
  public void clearTokens() {
    tokens.clear();
  }
}
