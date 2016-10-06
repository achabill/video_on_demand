package vod.controllers.frontendcontrollers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Home controller
 */
@RestController
public class HomeController
{
    @RequestMapping(value = "/", method= RequestMethod.GET)
    public String home()
    {
        return "Welcome to Video on Demand service for CommuFi, Skylabase";
    }
}
