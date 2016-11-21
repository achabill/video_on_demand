package vod.controllers.backendcontrollers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Home controller
 */
@RestController
@RequestMapping("/admin")
public class BackendHomeController {
  @RequestMapping("/")
  public String home() {
    return "Paths include: \n" + "/admin/movies\n" + "/admin/series\n";
  }
}
