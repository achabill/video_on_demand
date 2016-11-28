package vod.controllers;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tokenauth.service.TokenService;
import vod.dao.IUserDao;
import vod.exceptions.CannotDeleteRootException;
import vod.exceptions.UnauthorizedException;
import vod.exceptions.UserAlreadyExistException;
import vod.exceptions.UserNotFoundException;
import vod.models.User;

import javax.validation.Valid;
import java.util.List;


@RestController
@RequestMapping(value = "/users")
@Api(value = "User", description = "User management")
public class UsersController {

  @Autowired
  private IUserDao userDao;

  private TokenService<User> tokenService = new TokenService<>();

  public UsersController() {
  }

  @ResponseBody
  @RequestMapping(value = "/login", method = RequestMethod.POST)
  @ApiOperation(value = "Login", notes = "Logs in a user and returns an access token.")
  public ResponseEntity<UserAccessToken> login(@Valid @RequestBody User user) throws Exception {

    User _user = validateUser(user);
    String accessToken = tokenService.setToken(_user);

    HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.setLocation(ServletUriComponentsBuilder
      .fromCurrentRequest().path("/").buildAndExpand("").toUri());

    return new ResponseEntity<>(new UserAccessToken(_user, new AccessToken(accessToken)), httpHeaders, HttpStatus.CREATED);
  }

  @ResponseBody
  @RequestMapping(value = "/logout", method = RequestMethod.GET)
  @ApiOperation(value = "Logout", notes = "Logs out a user and removes the access token")
  public ResponseEntity<UserAccessToken> logout(@RequestParam(value = "accesstoken", required = true) String accessToken) throws Exception {

    tokenService.removeToken(accessToken);

    HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.setLocation(ServletUriComponentsBuilder
      .fromCurrentRequest().path("/").buildAndExpand("").toUri());

    return new ResponseEntity<>(new UserAccessToken(new User(), new AccessToken("")), httpHeaders, HttpStatus.OK);
  }

  @ResponseBody
  @RequestMapping(value = "/signup", method = RequestMethod.POST)
  @ApiOperation(value = "Signup", notes = "Signup a user with the required credentials")
  public ResponseEntity<UserAccessToken> signup(@Valid @RequestBody User user) throws Exception {

    String username = user.getUsername();
    isUsernameAvailable(username);

    if (user.getPrevilege() == null)
      user.setPrevilege("client");
    String password = tokenService.digest(user.getPassword());
    user.setPassword(password);
    User newUser = userDao.save(user);
    AccessToken accessToken = new AccessToken(tokenService.setToken(newUser));

    HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.setLocation(ServletUriComponentsBuilder
      .fromCurrentRequest().path("/").buildAndExpand("").toUri());

    return new ResponseEntity<>(new UserAccessToken(newUser, accessToken), httpHeaders, HttpStatus.CREATED);
  }

  @ResponseBody
  @RequestMapping(value = "/createuser", method = RequestMethod.POST)
  @ApiOperation(value = "Create a user", notes = "Create a user but don't sign it in.")
  public ResponseEntity<User> createUser(@Valid @RequestBody User user,
                                         @RequestParam(value = "accesstoken", required = true) String accessToken) throws Exception {
    verifyAdminToken(accessToken);

    isUsernameAvailable(user.getUsername());

    if (user.getPrevilege() == null)
      user.setPrevilege("client");
    String password = tokenService.digest(user.getPassword());
    user.setPassword(password);
    User newUser = userDao.save(user);

    HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.setLocation(ServletUriComponentsBuilder
      .fromCurrentRequest().path("/").buildAndExpand("").toUri());

    return new ResponseEntity<>(newUser, httpHeaders, HttpStatus.CREATED);
  }

  @ResponseBody
  @RequestMapping(value = "/", method = RequestMethod.GET)
  @ApiOperation(value = "Gets all users", notes = "Gets all users")
  public ResponseEntity<List<User>> getAllUsers(@RequestParam(value = "accesstoken", required = true) String accessToken) throws Exception {
    verifyAdminToken(accessToken);


    HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.setLocation(ServletUriComponentsBuilder
      .fromCurrentRequest().path("/").buildAndExpand("").toUri());

    return new ResponseEntity<>(userDao.findAll(), httpHeaders, HttpStatus.OK);
  }

  @ResponseBody
  @RequestMapping(value = "/", method = RequestMethod.DELETE)
  @ApiOperation(value = "Deletes all users", notes = "Deletes all users except root")
  public ResponseEntity<String> deleteAllUsers(@RequestParam(value = "accesstoken", required = true) String accessToken) throws Exception {

    verifyAdminToken(accessToken);

    List<User> users = userDao.findAll();
    for (int i = 0; i < users.size(); i++)
      if (users.get(i).getPrevilege() == null || !users.get(i).getPrevilege().equals("root"))
        userDao.delete(users.get(i));

    HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.setLocation(ServletUriComponentsBuilder
      .fromCurrentRequest().path("/").buildAndExpand("").toUri());

    return new ResponseEntity<>("deleted all users except root", httpHeaders, HttpStatus.ACCEPTED);
  }

  @ResponseBody
  @RequestMapping(value = "/{id}", method = RequestMethod.GET)
  @ApiOperation(value = "Get a user", notes = "Gets the user with the specified id")
  public ResponseEntity<User> getUserById(@PathVariable("id") String id,
                                          @RequestParam(value = "accesstoken", required = true) String accessToken) throws Exception {
    verifyAdminToken(accessToken);
    User user = validateUserId(id);

    HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.setLocation(ServletUriComponentsBuilder
      .fromCurrentRequest().path("/").buildAndExpand("").toUri());

    return new ResponseEntity<>(user, httpHeaders, HttpStatus.OK);

  }

  @ResponseBody
  @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
  @ApiOperation(value = "Delete a user", notes = "Deletes the user with the specified id")
  public ResponseEntity<String> deleteUserById(@PathVariable("id") String id,
                                               @RequestParam(value = "accesstoken", required = true) String accessToken) throws Exception {
    verifyAdminToken(accessToken);
    User user = validateUserId(id);

    if (!user.getPrevilege().equals("root"))
      throw new CannotDeleteRootException("Root cannot be deleted");

    userDao.delete(user);
    HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.setLocation(ServletUriComponentsBuilder
      .fromCurrentRequest().path("/").buildAndExpand("").toUri());

    return new ResponseEntity<>("deleted", httpHeaders, HttpStatus.ACCEPTED);
  }

  @ResponseBody
  @RequestMapping(value = "/{id}/update", method = RequestMethod.PUT)
  @ApiOperation(value = "Update a user", notes = "Updates the user with the specified id")
  public ResponseEntity<User> modifyUserSelf(@PathVariable("id") String id,
                                             @RequestParam(value = "accesstoken", required = true) String accessToken,
                                             @Valid @RequestBody User user) throws Exception {
    tokenService.verifyToken(accessToken);
    validateUserId(id);
    isUsernameAvailable(user.getUsername());

    user.setId(id);
    user.setPassword(tokenService.digest(user.getPassword()));
    userDao.save(user);

    HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.setLocation(ServletUriComponentsBuilder
      .fromCurrentRequest().path("/").buildAndExpand("").toUri());

    return new ResponseEntity<>(user, httpHeaders, HttpStatus.CREATED);
  }

  private void verifyAdminToken(String token){
    User u = tokenService.tokenValue(token);
    if(!u.getPrevilege().equals("root") && !u.getPrevilege().equals("admin"))
      throw new UnauthorizedException("token : " + token + " is unauthorized");
  }

  private User validateUserId(String id) {
    User user = userDao.findById(id);
    if (user == null)
      throw new UserNotFoundException("User with id does not exist");
    return user;
  }

  private User validateUser(User user) throws Exception {
    User _user = userDao.findByUsername(user.getUsername());
    if (_user == null)
      throw new UserNotFoundException("Invalid username");
    String pass = tokenService.digest(user.getPassword());
    if (!pass.equals(_user.getPassword()))
      throw new UserNotFoundException("Invalid password");
    return _user;
  }

  private void isUsernameAvailable(String username) throws Exception {
    if (userDao.findByUsername(username) != null)
      throw new UserAlreadyExistException(username + " already exist.");
  }

  private class AccessToken {
    private String accesstoken;

    public AccessToken(String accesstoken) {
      this.accesstoken = accesstoken;
    }

    public String getAccesstoken() {
      return accesstoken;
    }
  }

  private class UserAccessToken {
    private User user;
    private AccessToken accesstoken;

    public UserAccessToken(User user, AccessToken accesstoken) {
      this.user = user;
      this.accesstoken = accesstoken;
    }

    public User getUser() {
      return this.user;
    }

    public AccessToken getToken() {
      return this.accesstoken;
    }
  }
}

