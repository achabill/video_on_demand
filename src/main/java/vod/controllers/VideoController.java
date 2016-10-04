package vod.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import vod.helpers.MultipartFileSender;
import vod.models.Message;

import java.io.File;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@RestController
public class VideoController
{
    @RequestMapping( value = "/video", method = RequestMethod.GET)
    public Message getVideo(@RequestParam Map<String,String> params, HttpServletRequest request, HttpServletResponse response) throws Exception
    {
        String play = params.get("play");
        Message msg = null;

        if(play != null)
            MultipartFileSender.fromFile(new File("C:\\Users\\achab\\Videos\\Unfriended (2014) [1080p]\\Unfriended.2014.1080p.BluRay.x264.YIFY.mp4")).with(request).with(response).serveResource();
        else
            msg = new Message(1,"Hello world");
        return msg;
    }
}
