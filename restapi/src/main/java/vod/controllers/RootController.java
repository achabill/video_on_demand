package vod.controllers;

import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import vod.auth.ITokenService;
import vod.dao.IUserDao;
import vod.models.User;


@RestController
@Api(value = "The root controller")
public class RootController {

  @Autowired
  private IUserDao userDao;

  @Autowired
  private ITokenService<User> tokenService;

  @RequestMapping("/")
  public ResponseEntity<String> home() throws Exception {
    checkIfRootExistsandCreateRoot();

    HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.setLocation(ServletUriComponentsBuilder
      .fromCurrentRequest().path("/").buildAndExpand("").toUri());

    String response = "Welcome to video on Demand API, Commufy - Skylablase.\n" +
      "Paths include:\n" +
      "/admin\n" +
      "/users\n" +
      "/movies\n" +
      "/series\n";

    return new ResponseEntity<>(response, httpHeaders, HttpStatus.CREATED);
  }


  private void checkIfRootExistsandCreateRoot() throws Exception {
    User root = userDao.findById("1");
    if (root == null) {
      root = new User();
      root.setPrevilege("root");
      root.setId("1");
      root.setUsername("root");
      root.setPassword(tokenService.digest("root"));
      userDao.save(root);
    }
  }
}
