package vod.controllers.backendcontrollers;

/*
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.mock.http.MockHttpOutputMessage;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;
import vod.Application;
import vod.models.Comment;
import vod.models.Movie;
import vod.models.Rating;
import vod.models.User;
import vod.repositories.CommentsRepository;
import vod.repositories.MoviesRepository;
import vod.repositories.UsersRepository;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@SpringApplicationConfiguration(classes = Application.class)
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@EnableAutoConfiguration
public class BackendMoviesControllerTest {
    @Autowired
    MoviesRepository moviesRepository;
    @Autowired
    CommentsRepository commentsRepository;
    @Autowired
    UsersRepository usersRepository;
    @Autowired
    WebApplicationContext webApplicationContext;

    private MediaType contentType = new MediaType(MediaType.APPLICATION_JSON.getType(),
            MediaType.APPLICATION_JSON.getSubtype(),
            Charset.forName("utf8"));
    private MockMvc mockMvc;
    private HttpMessageConverter mappingJackson2HttpMessageConverter;
    private List<Movie> movies;
    private List<User> users;
    private List<Comment> comments;

    @Autowired
    void setConverters(HttpMessageConverter<?>[] converters) {

        this.mappingJackson2HttpMessageConverter = Arrays.asList(converters).stream().filter(
                hmc -> hmc instanceof MappingJackson2HttpMessageConverter).findAny().get();

        Assert.assertNotNull("the JSON message converter must not be null",
                this.mappingJackson2HttpMessageConverter);
    }

    @Before
    public void setup() throws Exception{
        this.mockMvc = webAppContextSetup(webApplicationContext).build();
        this.moviesRepository.deleteAll();
        this.commentsRepository.deleteAll();
        this.usersRepository.deleteAll();

        User u1 = new User("user1");
        u1.setId("1");
        User u2 = new User("user2");
        u2.setId("2");
        User u3 = new User("user3");
        u2.setId("3");

        users = new ArrayList<User>() {{
            add(u1);
            add(u2);
            add(u3);
        }};
        usersRepository.save(users);

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
        m1.setId("1");

        Movie m2 = new Movie();
        m2.setCoverimage("C:\\somepath\\image2.jpg");
        m2.setDescription("A nice movie");
        m2.setDislikes(1000);
        m2.setLikes(5000);
        m2.setOverallrating(5);
        Rating r2 = new Rating();
        r2.setThreestars(28);
        r2.setTwostars(5);
        m2.setRating(r2);
        m2.setReleaseyear(2017);
        m2.setTitle("London has Fallen");
        m2.setViews(95000);
        m2.setGenre("thriller");
        m2.setVideofile("C:\\Users\\achab\\Music\\video\\western\\Chris Brown feat. Usher & Rick Ross - New Flame (Explicit Version).mp4");
        m2.setId("2");

        movies = new ArrayList<Movie>() {{
            add(m1);
            add(m2);
        }};
        moviesRepository.save(movies);

        Comment c1 = new Comment();
        c1.setUser(users.get(0));
        c1.setDate(System.currentTimeMillis());
        c1.setMovieid(movies.get(0).getId());
        c1.setValue("A very nice film.");
        c1.setId("1");

        Comment c2 = new Comment();
        c2.setUser(users.get(1));
        c2.setDate(System.currentTimeMillis());
        c2.setMovieid(movies.get(0).getId());
        c2.setValue("Cool movie..");
        c2.setId("2");

        Comment c3 = new Comment();
        c3.setUser(users.get(2));
        c3.setDate(System.currentTimeMillis());
        c3.setMovieid(movies.get(1).getId());
        c3.setValue("I enjoyed it.");
        c3.setId("3");

        comments = new ArrayList<Comment>() {{
            add(c1);
            add(c2);
            add(c3);
        }};
        commentsRepository.save(comments);

    }

    /**
     * Gets all comments in the database.
     * @throws Exception
     */
/*
    @Test
    public void getAllMoviesWithSuccess() throws Exception {
        mockMvc.perform(get("admin/movies/"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(this.movies.get(0).getId())))
                .andExpect(jsonPath("$[0].coverimage", is(this.movies.get(0).getCoverimage())))
                .andExpect(jsonPath("$[0].likes", is(this.movies.get(0).getLikes())))
                .andExpect(jsonPath("$[0].dislikes", is(this.movies.get(0).getDislikes())))
                .andExpect(jsonPath("$[0].description", is(this.movies.get(0).getDescription())))
                .andExpect(jsonPath("$[0].overallrating", is(this.movies.get(0).getOverallrating())))
                .andExpect(jsonPath("$[0].rating.onestar", is(this.movies.get(0).getRating().getOnestar())))
                .andExpect(jsonPath("$[0].rating.twostars", is(this.movies.get(0).getRating().getTwostars())))
                .andExpect(jsonPath("$[0].rating.threestars", is(this.movies.get(0).getRating().getThreestars())))
                .andExpect(jsonPath("$[0].rating.fourstars", is(this.movies.get(0).getRating().getFourstars())))
                .andExpect(jsonPath("$[0].rating.fivestars", is(this.movies.get(0).getRating().getFivestars())))
                .andExpect(jsonPath("$[0].title", is(this.movies.get(0).getTitle())))
                .andExpect(jsonPath("$[0].views", is(this.movies.get(0).getViews())))
                .andExpect(jsonPath("$[0].genre", is(this.movies.get(0).getGenre())))
                .andExpect(jsonPath("$[0].videofile", is(this.movies.get(0).getVideofile())))
                .andExpect(jsonPath("$[1].id", is(this.movies.get(1).getId())))
                .andExpect(jsonPath("$[1].coverimage", is(this.movies.get(1).getCoverimage())))
                .andExpect(jsonPath("$[1].likes", is(this.movies.get(1).getLikes())))
                .andExpect(jsonPath("$[1].dislikes", is(this.movies.get(1).getDislikes())))
                .andExpect(jsonPath("$[1].description", is(this.movies.get(1).getDescription())))
                .andExpect(jsonPath("$[1].overallrating", is(this.movies.get(1).getOverallrating())))
                .andExpect(jsonPath("$[1].rating.onestar", is(this.movies.get(1).getRating().getOnestar())))
                .andExpect(jsonPath("$[1].rating.twostars", is(this.movies.get(1).getRating().getTwostars())))
                .andExpect(jsonPath("$[1].rating.threestars", is(this.movies.get(1).getRating().getThreestars())))
                .andExpect(jsonPath("$[1].rating.fourstars", is(this.movies.get(1).getRating().getFourstars())))
                .andExpect(jsonPath("$[1].rating.fivestars", is(this.movies.get(1).getRating().getFivestars())))
                .andExpect(jsonPath("$[1].title", is(this.movies.get(1).getTitle())))
                .andExpect(jsonPath("$[1].views", is(this.movies.get(1).getViews())))
                .andExpect(jsonPath("$[1].genre", is(this.movies.get(1).getGenre())))
                .andExpect(jsonPath("$[1].videofile", is(this.movies.get(1).getVideofile())));

    }

    @Test
    public void addOneMovieWithSuccess() throws Exception{
        Movie m3 = new Movie();
        m3.setCoverimage("C:\\somepath\\image2.jpg");
        m3.setDescription("A nice movie");
        m3.setDislikes(10);
        m3.setLikes(40);
        m3.setOverallrating(5);
        Rating r3 = new Rating();
        r3.setThreestars(3);
        r3.setTwostars(90);
        m3.setRating(r3);
        m3.setReleaseyear(2018);
        m3.setTitle("White house down");
        m3.setViews(1000);
        m3.setGenre("comedy");
        m3.setVideofile("C:\\Users\\achab\\Music\\video\\western\\Chris Brown feat. Usher & Rick Ross - New Flame (Explicit Version).mp4");
        m3.setId("3");

        mockMvc.perform(post("/admin/movies/")
                .content(json(m3)).contentType(contentType)).andExpect(status().isCreated());
    }

    @Test
    public void getMovieWithIdWithSuccess() throws Exception {
        mockMvc.perform(get("admin/movies/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(this.movies.get(0).getId())))
                .andExpect(jsonPath("$[0].coverimage", is(this.movies.get(0).getCoverimage())))
                .andExpect(jsonPath("$[0].likes", is(this.movies.get(0).getLikes())))
                .andExpect(jsonPath("$[0].dislikes", is(this.movies.get(0).getDislikes())))
                .andExpect(jsonPath("$[0].description", is(this.movies.get(0).getDescription())))
                .andExpect(jsonPath("$[0].overallrating", is(this.movies.get(0).getOverallrating())))
                .andExpect(jsonPath("$[0].rating.onestar", is(this.movies.get(0).getRating().getOnestar())))
                .andExpect(jsonPath("$[0].rating.twostars", is(this.movies.get(0).getRating().getTwostars())))
                .andExpect(jsonPath("$[0].rating.threestars", is(this.movies.get(0).getRating().getThreestars())))
                .andExpect(jsonPath("$[0].rating.fourstars", is(this.movies.get(0).getRating().getFourstars())))
                .andExpect(jsonPath("$[0].rating.fivestars", is(this.movies.get(0).getRating().getFivestars())))
                .andExpect(jsonPath("$[0].title", is(this.movies.get(0).getTitle())))
                .andExpect(jsonPath("$[0].views", is(this.movies.get(0).getViews())))
                .andExpect(jsonPath("$[0].genre", is(this.movies.get(0).getGenre())))
                .andExpect(jsonPath("$[0].videofile", is(this.movies.get(0).getVideofile())));

    }

    @Test
    public void deleteMovieWithIdWithSuccess() throws Exception{
        mockMvc.perform(delete("/admin/movies/3"))
                .andExpect(content().contentType(contentType))
                .andExpect(status().isOk());
    }

    @Test
    public void getAllCommentsForMovieWithIdWithSuccess() throws Exception {
        mockMvc.perform(get("admin/movies/1/comments"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].user.id", is(this.users.get(0).getId())))
                .andExpect(jsonPath("$[0].user.username", is(this.users.get(0).getUsername())))
                .andExpect(jsonPath("$[0].id", is(this.comments.get(0).getId())))
                .andExpect(jsonPath("$[0].date", is(this.comments.get(0).getDate())))
                .andExpect(jsonPath("$[0].value", is(this.comments.get(0).getValue())))
                .andExpect(jsonPath("$[0].movieid", is(this.comments.get(0).getMovieid())))
                .andExpect(jsonPath("$[1].user.id", is(this.users.get(1).getId())))
                .andExpect(jsonPath("$[1].user.username", is(this.users.get(1).getUsername())))
                .andExpect(jsonPath("$[1].id", is(this.comments.get(1).getId())))
                .andExpect(jsonPath("$[1].date", is(this.comments.get(1).getDate())))
                .andExpect(jsonPath("$[1].value", is(this.comments.get(1).getValue())))
                .andExpect(jsonPath("$[1].movieid", is(this.comments.get(1).getMovieid())));
    }

    @Test
    public void deleteAllCommentsForMovieWithIdWithSuccess() throws Exception{
        Comment c3 = new Comment();
        c3.setUser(users.get(2));
        c3.setDate(System.currentTimeMillis());
        c3.setMovieid(movies.get(1).getId());
        c3.setValue("I enjoyed it.");
        c3.setId("3");

        mockMvc.perform(delete("admin/movies/2/comments"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].user.id", is(c3.getId())))
                .andExpect(jsonPath("$[0].user.username", is(this.users.get(1).getUsername())))
                .andExpect(jsonPath("$[0].id", is(c3.getId())))
                .andExpect(jsonPath("$[0].date", is(c3.getDate())))
                .andExpect(jsonPath("$[0].value", is(c3.getValue())))
                .andExpect(jsonPath("$[0].movieid", is(c3.getMovieid())));
    }

    @Test
    public void getCommentWithIdWithSuccess() throws Exception{
        mockMvc.perform(get("admin/movies/1/comments/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].user.id", is(this.comments.get(0).getId())))
                .andExpect(jsonPath("$[0].user.username", is(this.users.get(0).getUsername())))
                .andExpect(jsonPath("$[0].id", is(this.comments.get(0).getId())))
                .andExpect(jsonPath("$[0].date", is(this.comments.get(0).getDate())))
                .andExpect(jsonPath("$[0].value", is(this.comments.get(0).getValue())))
                .andExpect(jsonPath("$[0].movieid", is(this.comments.get(0).getMovieid())));
    }

    @Test
    public void deleteCommentWithIdWithSuccess() throws Exception{
        Comment c1 = new Comment();
        c1.setUser(users.get(0));
        c1.setDate(System.currentTimeMillis());
        c1.setMovieid(movies.get(0).getId());
        c1.setValue("A very nice film.");
        c1.setId("1");

        mockMvc.perform(delete("admin/movies/1/comments/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].user.id", is(c1.getId())))
                .andExpect(jsonPath("$[0].user.username", is(this.users.get(0).getUsername())))
                .andExpect(jsonPath("$[0].id", is(c1.getId())))
                .andExpect(jsonPath("$[0].date", is(c1.getDate())))
                .andExpect(jsonPath("$[0].value", is(c1.getValue())))
                .andExpect(jsonPath("$[0].movieid", is(c1.getMovieid())));
    }

    public void deleteAllMoviesWithSuccess() throws Exception{
        mockMvc.perform(delete("admin/movies/"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType));
    }
*/

/**
 * Converts an objec to Json Bourne
 *
 * @param obj the object to convert
 * @return the json string
 * @throws IOException exception
 */
    /*
    protected String json(Object obj) throws IOException {
        MockHttpOutputMessage mockHttpOutputMessage = new MockHttpOutputMessage();
        this.mappingJackson2HttpMessageConverter.write(obj, MediaType.APPLICATION_JSON, mockHttpOutputMessage);
        return mockHttpOutputMessage.getBodyAsString();
    }
}
*/