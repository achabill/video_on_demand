package vod.controllers;


import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import vod.models.Message;

import java.util.concurrent.atomic.AtomicLong;

@RestController
public class HomeController
{
    private static AtomicLong counter = new AtomicLong();
    @RequestMapping(value = "/", method = RequestMethod.GET)
    public Message welcome()
    {
        return new Message((int)counter.getAndIncrement(), "Hello, World!");
    }
}
