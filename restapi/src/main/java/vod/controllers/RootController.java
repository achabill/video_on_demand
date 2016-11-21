package vod.controllers;

import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import vod.filestorage.MultipartFileSender;
import vod.helpers.TokenService;
import vod.models.User;
import vod.repositories.UsersRepository;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;

@RestController
@Api(value = "The root controller")
public class RootController {

  @Autowired
  private UsersRepository usersRepository;
  @Autowired
  private TokenService tokenService;

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

  @RequestMapping(value = "/resource", method = RequestMethod.GET)
  private void getFileResource(@RequestParam(value = "path", required = true) String path, HttpServletRequest request, HttpServletResponse response) throws Exception {
    File file = new File(path);
    MultipartFileSender.fromFile(file).with(request).with(response).serveResource();
  }

  private void checkIfRootExistsandCreateRoot() throws Exception {
    User root = usersRepository.findById("1");
    if (root == null) {
      root = new User();
      root.setPrevilege("root");
      root.setId("1");
      root.setUsername("root");
      root.setPassword(tokenService.digestString("root"));
      usersRepository.save(root);
    }
  }
}
