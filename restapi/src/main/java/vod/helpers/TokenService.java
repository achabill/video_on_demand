package vod.helpers;

import org.springframework.stereotype.Service;
import vod.exceptions.InvalidAccessTokenException;
import vod.exceptions.UnauthorizedException;
import vod.models.User;

import java.util.HashMap;
import java.util.Map;

@Service
public class TokenService {

  private Map<String, User> accessTokens;

  public TokenService() {
    accessTokens = new HashMap<>();
  }

  public String digestString(String data) throws Exception {
    return "vod" + String.valueOf(data.hashCode());
  }

  public String getAccessToken(User user) throws Exception {
    String accessToken = digestString(user.getUsername() + user.getPassword());
    //if(accessTokens.containsKey(accessToken))
    //throw new AlreadyLoggedInException("User : " + user.getUsername() + " is already logged in");
    accessTokens.put(accessToken, user);
    return accessToken;
  }

  public void verifyAccessToken(String accessToken) throws Exception {
    if (!accessTokens.containsKey(accessToken))
      throw new InvalidAccessTokenException(accessToken);
  }

  public void removeAccessToken(String accessToken) throws Exception {
    verifyAccessToken(accessToken);
    accessTokens.remove(accessToken);
  }

  public void verifyAdmin(String accessToken) throws Exception {
    verifyAccessToken(accessToken);
    User u = accessTokens.get(accessToken);
    if (!(u.getPrevilege().equals("admin") || u.getPrevilege().equals("root")))
      throw new UnauthorizedException("Unauthorized request");
  }

  public void clearAccessTokens() {
    accessTokens = new HashMap<>();
  }

}
