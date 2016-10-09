package vod;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import vod.Repositories.MoviesRepository;
import vod.models.Movie;
import vod.models.Rating;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;


@SpringBootApplication
@EnableAutoConfiguration
@Configuration
@ComponentScan
public class Application
{
    @Autowired
    MoviesRepository repository;

    public static void main(String[] args)
    {
        SpringApplication.run(Application.class, args);
    }

    /*
    @Bean
    CommandLineRunner init() throws MalformedURLException {

        repository.deleteAll();

        Movie m1 = new Movie();
        m1.setCoverimage("C:\\somepath\\image1.jpg");
        m1.setDescription("A horror movie");
        m1.setDislikes(2);
        m1.setLikes(100);
        m1.setOverallrating(2);
        Rating r1 = new Rating();
        r1.setFivestars(100);
        r1.setOnestar(10);
        m1.setRating(r1);
        m1.setReleaseyear(2016);
        m1.setTitle("Json Bourne");
        m1.setViews(1000);
        m1.setGenre("thriller");
        m1.setVideofile("C:\\Users\\achab\\Music\\video\\western\\Bryson Tiller - Sorry Not Sorry-U4MHrrIQuis.mp4");

        Movie m2 = new Movie();
        m2.setCoverimage("C:\\somepath\\image2.jpg");
        m2.setDescription("A Science Fiction movie");
        m2.setDislikes(1000);
        m2.setLikes(5000);
        m2.setOverallrating(5);
        Rating r2 = new Rating();
        r2.setThreestars(28);
        r2.setTwostars(5);
        m2.setRating(r1);
        m2.setReleaseyear(2017);
        m2.setTitle("London has Fallen");
        m2.setViews(95000);
        m2.setGenre("science_fiction");
        m2.setVideofile("C:\\Users\\achab\\Music\\video\\western\\Chris Brown feat. Usher & Rick Ross - New Flame (Explicit Version).mp4");


        List<Movie> movies = new ArrayList<Movie>(){{add(m1); add(m2);}};
        return (evt-> repository.save(movies));

    }
    */

}

