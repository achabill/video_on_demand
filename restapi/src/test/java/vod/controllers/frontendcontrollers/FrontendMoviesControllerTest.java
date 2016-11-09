package vod.controllers.frontendcontrollers;

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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@SpringApplicationConfiguration(classes = Application.class)
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@EnableAutoConfiguration
public class FrontendMoviesControllerTest {
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
    public void setup() throws Exception {
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
        m1.setVideofile("C:/Users/achab/Music/video/western/Bryson Tiller - Sorry Not Sorry-U4MHrrIQuis.mp4");
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
        m2.setVideofile("C:/Users/achab/Music/video/western/Chris Brown feat. Usher & Rick Ross - New Flame (Explicit Version).mp4");
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
     * Gets all movies in the database
     *
     * @throws Exception
     */
    @Test
    public void getAllMoviesWithSuccess() throws Exception {
        mockMvc.perform(get("/movies/"))
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

    /**
     * Reads a page and assersts that its actually that page it read.
     */
    @Test
    public void readsFirstPageCorrectly() {

        Page<Movie> movies = moviesRepository.findAll(new PageRequest(0, 10));

        assertThat(movies.isFirst());
    }

    /**
     * Gets all movies on page 0 with success.
     *
     * @throws Exception The exception
     */
    @Test
    public void getAllMoviesOnPageZeroWithSuccess() throws Exception {
        mockMvc.perform(get("/movies/?page=0"))
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

    /**
     * Gets 1 movie on page 0 with success.
     *
     * @throws Exception
     */
    @Test
    public void getOneMovieOnPageZeroWithSuccess() throws Exception {
        mockMvc.perform(get("/movies/?page=0&size=1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$", hasSize(1)))
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

    /**
     * Gets 3 movies on the first page.
     * Will return 2 movies since only 2 movies are in the database.
     *
     * @throws Exception
     */
    @Test
    public void getThreeMoviesOnPageZeroWithSuccess() throws Exception {
        mockMvc.perform(get("/movies/?page=0&size=3"))
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

    /**
     * Gets 1 movie one the 2nd page with success.
     *
     * @throws Exception
     */
    @Test
    public void GetOneMovieOnPageOneWithSuccess() throws Exception {
        mockMvc.perform(get("/movies/?page=1&size=1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(this.movies.get(1).getId())))
                .andExpect(jsonPath("$[0].coverimage", is(this.movies.get(1).getCoverimage())))
                .andExpect(jsonPath("$[0].likes", is(this.movies.get(1).getLikes())))
                .andExpect(jsonPath("$[0].dislikes", is(this.movies.get(1).getDislikes())))
                .andExpect(jsonPath("$[0].description", is(this.movies.get(1).getDescription())))
                .andExpect(jsonPath("$[0].overallrating", is(this.movies.get(1).getOverallrating())))
                .andExpect(jsonPath("$[0].rating.onestar", is(this.movies.get(1).getRating().getOnestar())))
                .andExpect(jsonPath("$[0].rating.twostars", is(this.movies.get(1).getRating().getTwostars())))
                .andExpect(jsonPath("$[0].rating.threestars", is(this.movies.get(1).getRating().getThreestars())))
                .andExpect(jsonPath("$[0].rating.fourstars", is(this.movies.get(1).getRating().getFourstars())))
                .andExpect(jsonPath("$[0].rating.fivestars", is(this.movies.get(1).getRating().getFivestars())))
                .andExpect(jsonPath("$[0].title", is(this.movies.get(1).getTitle())))
                .andExpect(jsonPath("$[0].views", is(this.movies.get(1).getViews())))
                .andExpect(jsonPath("$[0].genre", is(this.movies.get(1).getGenre())))
                .andExpect(jsonPath("$[0].videofile", is(this.movies.get(1).getVideofile())));
    }

    /**
     * Gets 1 movie on the 3rd page.
     * Will return empty list since only 2 movies are in the database.
     *
     * @throws Exception
     */
    @Test
    public void getOneMovieOnPageTwoReturnsEmptyList() throws Exception {
        mockMvc.perform(get("/movies/?page=2&size=1")
                .content(json(new Movie())).contentType(contentType)).andExpect(status().isOk());
    }

    /**
     * Gets 3 movies on the 2nd page.
     * But since only 2 movies are in the database, will return 1 page. Hence, no data.
     *
     * @throws Exception
     */
    @Test
    public void getThreeMoviesOnPageOneReturnsEmptyList() throws Exception {
        mockMvc.perform(get("/movies/?page=1&size=3")
                .content(json(new Movie())).contentType(contentType)).andExpect(status().isOk());
    }

    /**
     * Checks invalid number format for the page property
     *
     * @throws Exception
     */
    @Test
    public void InvalidNumberPropertyException() throws Exception {
        mockMvc.perform(get("/movies/?page=expect_valid_number_not_string")
                .content(json(new Movie())).contentType(contentType)).andExpect(status().isBadRequest());
    }

    /**
     * Checks invalid number format for the size property.
     *
     * @throws Exception
     */
    @Test
    public void InvalidNumberPropertyException1() throws Exception {
        mockMvc.perform(get("/movies/?size=expect_valid_number_not_string")
                .content(json(new Movie())).contentType(contentType)).andExpect(status().isBadRequest());
    }

    /**
     * Gets all data on second page.
     *
     * @throws Exception
     */
    @Test
    public void getAllMoviesOnPageTwoWithDefaultCountReturnsEmptyList() throws Exception {
        mockMvc.perform(get("/movies/?page=2")
                .content(json(new Movie())).contentType(contentType)).andExpect(status().isOk());
    }

    /**
     * Gets 1 movie on the 1st page. 2 pages exist since there are 2 movies in the db.
     *
     * @throws Exception
     */
    @Test
    public void getOneMovieOnDefaultPageWithSuccess() throws Exception {
        mockMvc.perform(get("/movies/?size=1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$", hasSize(1)))
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

    /**
     * Gets 10 movies from from page 0.
     * Returns all 2 movies since only 2 movies are in the db.
     *
     * @throws Exception
     */
    @Test
    public void getTenMoviesOnDefaultPageWithSuccess() throws Exception {
        mockMvc.perform(get("/movies/?size=10"))
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

    /**
     * Gets all movies with genre=thriller
     *
     * @throws Exception
     */
    @Test
    public void getAllMoviesWithGenreEqualsThrillerWithSuccess() throws Exception {
        mockMvc.perform(get("/movies/?genre=thriller"))
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

    /**
     * Gets all movies with genre=sex.
     * But no such genre exists. Hence, empty result.
     *
     * @throws Exception
     */
    @Test
    public void invalidGenre() throws Exception {
        mockMvc.perform(get("/movies/?genre=sex")
                .content(json(new Movie())).contentType(contentType)).andExpect(status().isBadRequest());
    }

    /**
     * Gets all movies sorting them by id in ascending order. (defaults)
     *
     * @throws Exception
     */
    @Test
    public void getAllMoviesAndSortThemWithDefaultPropertyIdWithSuccess() throws Exception {
        mockMvc.perform(get("/movies/?sort=true"))
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

    /**
     * Gets all movies sorting them by id and in descending order
     *
     * @throws Exception
     */
    @Test
    public void getAllMoviesSortingThemWithDefaultPropertyIdAndDescendingOrderWithSuccess() throws Exception {
        mockMvc.perform(get("/movies/?sort=true&order=desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(this.movies.get(1).getId())))
                .andExpect(jsonPath("$[0].coverimage", is(this.movies.get(1).getCoverimage())))
                .andExpect(jsonPath("$[0].likes", is(this.movies.get(1).getLikes())))
                .andExpect(jsonPath("$[0].dislikes", is(this.movies.get(1).getDislikes())))
                .andExpect(jsonPath("$[0].description", is(this.movies.get(1).getDescription())))
                .andExpect(jsonPath("$[0].overallrating", is(this.movies.get(1).getOverallrating())))
                .andExpect(jsonPath("$[0].rating.onestar", is(this.movies.get(1).getRating().getOnestar())))
                .andExpect(jsonPath("$[0].rating.twostars", is(this.movies.get(1).getRating().getTwostars())))
                .andExpect(jsonPath("$[0].rating.threestars", is(this.movies.get(1).getRating().getThreestars())))
                .andExpect(jsonPath("$[0].rating.fourstars", is(this.movies.get(1).getRating().getFourstars())))
                .andExpect(jsonPath("$[0].rating.fivestars", is(this.movies.get(1).getRating().getFivestars())))
                .andExpect(jsonPath("$[0].title", is(this.movies.get(1).getTitle())))
                .andExpect(jsonPath("$[0].views", is(this.movies.get(1).getViews())))
                .andExpect(jsonPath("$[0].genre", is(this.movies.get(1).getGenre())))
                .andExpect(jsonPath("$[0].videofile", is(this.movies.get(1).getVideofile())))
                .andExpect(jsonPath("$[1].id", is(this.movies.get(0).getId())))
                .andExpect(jsonPath("$[1].coverimage", is(this.movies.get(0).getCoverimage())))
                .andExpect(jsonPath("$[1].likes", is(this.movies.get(0).getLikes())))
                .andExpect(jsonPath("$[1].dislikes", is(this.movies.get(0).getDislikes())))
                .andExpect(jsonPath("$[1].description", is(this.movies.get(0).getDescription())))
                .andExpect(jsonPath("$[1].overallrating", is(this.movies.get(0).getOverallrating())))
                .andExpect(jsonPath("$[1].rating.onestar", is(this.movies.get(0).getRating().getOnestar())))
                .andExpect(jsonPath("$[1].rating.twostars", is(this.movies.get(0).getRating().getTwostars())))
                .andExpect(jsonPath("$[1].rating.threestars", is(this.movies.get(0).getRating().getThreestars())))
                .andExpect(jsonPath("$[1].rating.fourstars", is(this.movies.get(0).getRating().getFourstars())))
                .andExpect(jsonPath("$[1].rating.fivestars", is(this.movies.get(0).getRating().getFivestars())))
                .andExpect(jsonPath("$[1].title", is(this.movies.get(0).getTitle())))
                .andExpect(jsonPath("$[1].views", is(this.movies.get(0).getViews())))
                .andExpect(jsonPath("$[1].genre", is(this.movies.get(0).getGenre())))
                .andExpect(jsonPath("$[1].videofile", is(this.movies.get(0).getVideofile())));
    }

    /**
     * Gets all movies sorting them by id and in ascending order.
     *
     * @throws Exception
     */
    @Test
    public void getAllMoviesSortingThemWithDefaultPropertyIdSortingThemInAscendingOrderWithSuccess() throws Exception {
        mockMvc.perform(get("/movies/?sort=true&order=asc"))
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

    /**
     * Invalid order. either 'asc' or 'desc'
     *
     * @throws Exception
     */
    @Test
    public void invalidOrderParameter() throws Exception {
        mockMvc.perform(get("/movies/?sort=true&order=descending")
                .content(json(new Movie())).contentType(contentType)).andExpect(status().isBadRequest());
    }

    /**
     * Invalid sort property. 'description' is not a sortable property on movies.
     *
     * @throws Exception
     */
    @Test
    public void invalidPropertyParameter() throws Exception {
        mockMvc.perform(get("/movies/?sort=true&property=description")
                .content(json(new Movie())).contentType(contentType)).andExpect(status().isBadRequest());
    }

    /**
     * Gets all movies sorting them in descending order and ordering them by overallrating
     *
     * @throws Exception
     */
    @Test
    public void getAllMoviesSortingThemWithPropertyEqualsOverallRatingOrderingThemInAscendingOrderWithSuccess() throws Exception {
        mockMvc.perform(get("/movies/?sort=true&order=desc&property=overallrating"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(this.movies.get(1).getId())))
                .andExpect(jsonPath("$[0].coverimage", is(this.movies.get(1).getCoverimage())))
                .andExpect(jsonPath("$[0].likes", is(this.movies.get(1).getLikes())))
                .andExpect(jsonPath("$[0].dislikes", is(this.movies.get(1).getDislikes())))
                .andExpect(jsonPath("$[0].description", is(this.movies.get(1).getDescription())))
                .andExpect(jsonPath("$[0].overallrating", is(this.movies.get(1).getOverallrating())))
                .andExpect(jsonPath("$[0].rating.onestar", is(this.movies.get(1).getRating().getOnestar())))
                .andExpect(jsonPath("$[0].rating.twostars", is(this.movies.get(1).getRating().getTwostars())))
                .andExpect(jsonPath("$[0].rating.threestars", is(this.movies.get(1).getRating().getThreestars())))
                .andExpect(jsonPath("$[0].rating.fourstars", is(this.movies.get(1).getRating().getFourstars())))
                .andExpect(jsonPath("$[0].rating.fivestars", is(this.movies.get(1).getRating().getFivestars())))
                .andExpect(jsonPath("$[0].title", is(this.movies.get(1).getTitle())))
                .andExpect(jsonPath("$[0].views", is(this.movies.get(1).getViews())))
                .andExpect(jsonPath("$[0].genre", is(this.movies.get(1).getGenre())))
                .andExpect(jsonPath("$[0].videofile", is(this.movies.get(1).getVideofile())))
                .andExpect(jsonPath("$[1].id", is(this.movies.get(0).getId())))
                .andExpect(jsonPath("$[1].coverimage", is(this.movies.get(0).getCoverimage())))
                .andExpect(jsonPath("$[1].likes", is(this.movies.get(0).getLikes())))
                .andExpect(jsonPath("$[1].dislikes", is(this.movies.get(0).getDislikes())))
                .andExpect(jsonPath("$[1].description", is(this.movies.get(0).getDescription())))
                .andExpect(jsonPath("$[1].overallrating", is(this.movies.get(0).getOverallrating())))
                .andExpect(jsonPath("$[1].rating.onestar", is(this.movies.get(0).getRating().getOnestar())))
                .andExpect(jsonPath("$[1].rating.twostars", is(this.movies.get(0).getRating().getTwostars())))
                .andExpect(jsonPath("$[1].rating.threestars", is(this.movies.get(0).getRating().getThreestars())))
                .andExpect(jsonPath("$[1].rating.fourstars", is(this.movies.get(0).getRating().getFourstars())))
                .andExpect(jsonPath("$[1].rating.fivestars", is(this.movies.get(0).getRating().getFivestars())))
                .andExpect(jsonPath("$[1].title", is(this.movies.get(0).getTitle())))
                .andExpect(jsonPath("$[1].views", is(this.movies.get(0).getViews())))
                .andExpect(jsonPath("$[1].genre", is(this.movies.get(0).getGenre())))
                .andExpect(jsonPath("$[1].videofile", is(this.movies.get(0).getVideofile())));
    }

    /**
     * Sorts all movies in descending order without sort parameter.
     * Without sort parameter, all sorting options are set to default.
     *
     * @throws Exception
     */
    @Test
    public void GetsAllMOviesSortingThemWithDefaultPropertyIdOrderingThemInDescendingOrderWithSucccess() throws Exception {
        mockMvc.perform(get("/movies/?order=desc"))
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

    /**
     * Gets similar movies to the movie with id specified. Obviusly the movie with id is not included
     * in the returned list.
     * Returns the other movie. (id = 2)
     *
     * @throws Exception
     */
    @Test
    public void getAllMoviesSimilarToMovieWithId1WithSuccess() throws Exception {
        mockMvc.perform(get("/movies/1/similar"))
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

    /**
     * Sorts the similar movies in descending order.
     *
     * @throws Exception
     */
    public void getAllSimilarMoviesToMovieWithId1SortingThemWithDefaultPropertyIdOrderingThemInAscendingOrderWithSuccess() throws Exception {
        mockMvc.perform(get("/movies/1/similar?sort=true&order=desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(this.movies.get(1).getId())))
                .andExpect(jsonPath("$[0].coverimage", is(this.movies.get(1).getCoverimage())))
                .andExpect(jsonPath("$[0].likes", is(this.movies.get(1).getLikes())))
                .andExpect(jsonPath("$[0].dislikes", is(this.movies.get(1).getDislikes())))
                .andExpect(jsonPath("$[0].description", is(this.movies.get(1).getDescription())))
                .andExpect(jsonPath("$[0].overallrating", is(this.movies.get(1).getOverallrating())))
                .andExpect(jsonPath("$[0].rating.onestar", is(this.movies.get(1).getRating().getOnestar())))
                .andExpect(jsonPath("$[0].rating.twostars", is(this.movies.get(1).getRating().getTwostars())))
                .andExpect(jsonPath("$[0].rating.threestars", is(this.movies.get(1).getRating().getThreestars())))
                .andExpect(jsonPath("$[0].rating.fourstars", is(this.movies.get(1).getRating().getFourstars())))
                .andExpect(jsonPath("$[0].rating.fivestars", is(this.movies.get(1).getRating().getFivestars())))
                .andExpect(jsonPath("$[0].title", is(this.movies.get(1).getTitle())))
                .andExpect(jsonPath("$[0].views", is(this.movies.get(1).getViews())))
                .andExpect(jsonPath("$[0].genre", is(this.movies.get(1).getGenre())))
                .andExpect(jsonPath("$[0].videofile", is(this.movies.get(1).getVideofile())))
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


    /**
     * INvalid oerdre parameter
     *
     * @throws Exception
     */
    @Test
    public void invalidOrderParameterException1() throws Exception {
        mockMvc.perform(get("/movies/1/similar?sort=true&order=descending")
                .content(json(new Movie())).contentType(contentType)).andExpect(status().isBadRequest());
    }

    /**
     * Gets 1 movie on 2nd page.
     * Empty result since there are 2 movies and one 1 other similar movie.
     *
     * @throws Exception
     */
    @Test
    public void getOneSimilarMovieToMovieWithId1OnPageOneWithSuccess() throws Exception {
        mockMvc.perform(get("/movies/1/similar?page=1&size=1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(this.movies.get(1).getId())))
                .andExpect(jsonPath("$[0].coverimage", is(this.movies.get(1).getCoverimage())))
                .andExpect(jsonPath("$[0].likes", is(this.movies.get(1).getLikes())))
                .andExpect(jsonPath("$[0].dislikes", is(this.movies.get(1).getDislikes())))
                .andExpect(jsonPath("$[0].description", is(this.movies.get(1).getDescription())))
                .andExpect(jsonPath("$[0].overallrating", is(this.movies.get(1).getOverallrating())))
                .andExpect(jsonPath("$[0].rating.onestar", is(this.movies.get(1).getRating().getOnestar())))
                .andExpect(jsonPath("$[0].rating.twostars", is(this.movies.get(1).getRating().getTwostars())))
                .andExpect(jsonPath("$[0].rating.threestars", is(this.movies.get(1).getRating().getThreestars())))
                .andExpect(jsonPath("$[0].rating.fourstars", is(this.movies.get(1).getRating().getFourstars())))
                .andExpect(jsonPath("$[0].rating.fivestars", is(this.movies.get(1).getRating().getFivestars())))
                .andExpect(jsonPath("$[0].title", is(this.movies.get(1).getTitle())))
                .andExpect(jsonPath("$[0].views", is(this.movies.get(1).getViews())))
                .andExpect(jsonPath("$[0].genre", is(this.movies.get(1).getGenre())))
                .andExpect(jsonPath("$[0].videofile", is(this.movies.get(1).getVideofile())));
    }

    /**
     * Gets 1 movie one 1st page.
     *
     * @throws Exception
     */
    @Test
    public void getOneSimilarMovieToMovieWithId1OnPageZeroWithSuccess() throws Exception {
        mockMvc.perform(get("/movies/1/similar?page=0&size=1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$", hasSize(1)))
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

    /**
     * Gets 2 movies on 0 sorting them in desc order and ordering by overallrating
     *
     * @throws Exception
     */
    @Test
    public void getTwoMoviesOnPageZeroSortingThemWithPropertyOverallRatingOrderingThemIndDescendingOrderWithSuccess() throws Exception {
        mockMvc.perform(get("/movies/?page=0&size=2&sort=true&order=desc&property=overallrating"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(this.movies.get(1).getId())))
                .andExpect(jsonPath("$[0].coverimage", is(this.movies.get(1).getCoverimage())))
                .andExpect(jsonPath("$[0].likes", is(this.movies.get(1).getLikes())))
                .andExpect(jsonPath("$[0].dislikes", is(this.movies.get(1).getDislikes())))
                .andExpect(jsonPath("$[0].description", is(this.movies.get(1).getDescription())))
                .andExpect(jsonPath("$[0].overallrating", is(this.movies.get(1).getOverallrating())))
                .andExpect(jsonPath("$[0].rating.onestar", is(this.movies.get(1).getRating().getOnestar())))
                .andExpect(jsonPath("$[0].rating.twostars", is(this.movies.get(1).getRating().getTwostars())))
                .andExpect(jsonPath("$[0].rating.threestars", is(this.movies.get(1).getRating().getThreestars())))
                .andExpect(jsonPath("$[0].rating.fourstars", is(this.movies.get(1).getRating().getFourstars())))
                .andExpect(jsonPath("$[0].rating.fivestars", is(this.movies.get(1).getRating().getFivestars())))
                .andExpect(jsonPath("$[0].title", is(this.movies.get(1).getTitle())))
                .andExpect(jsonPath("$[0].views", is(this.movies.get(1).getViews())))
                .andExpect(jsonPath("$[0].genre", is(this.movies.get(1).getGenre())))
                .andExpect(jsonPath("$[0].videofile", is(this.movies.get(1).getVideofile())))
                .andExpect(jsonPath("$[1].id", is(this.movies.get(0).getId())))
                .andExpect(jsonPath("$[1].coverimage", is(this.movies.get(0).getCoverimage())))
                .andExpect(jsonPath("$[1].likes", is(this.movies.get(0).getLikes())))
                .andExpect(jsonPath("$[1].dislikes", is(this.movies.get(0).getDislikes())))
                .andExpect(jsonPath("$[1].description", is(this.movies.get(0).getDescription())))
                .andExpect(jsonPath("$[1].overallrating", is(this.movies.get(0).getOverallrating())))
                .andExpect(jsonPath("$[1].rating.onestar", is(this.movies.get(0).getRating().getOnestar())))
                .andExpect(jsonPath("$[1].rating.twostars", is(this.movies.get(0).getRating().getTwostars())))
                .andExpect(jsonPath("$[1].rating.threestars", is(this.movies.get(0).getRating().getThreestars())))
                .andExpect(jsonPath("$[1].rating.fourstars", is(this.movies.get(0).getRating().getFourstars())))
                .andExpect(jsonPath("$[1].rating.fivestars", is(this.movies.get(0).getRating().getFivestars())))
                .andExpect(jsonPath("$[1].title", is(this.movies.get(0).getTitle())))
                .andExpect(jsonPath("$[1].views", is(this.movies.get(0).getViews())))
                .andExpect(jsonPath("$[1].genre", is(this.movies.get(0).getGenre())))
                .andExpect(jsonPath("$[1].videofile", is(this.movies.get(0).getVideofile())));
    }

    /**
     * Gets all comments for movie id=1
     *
     * @throws Exception
     */
    @Test
    public void getAllCommentsForMovieWithId1WithSuccess() throws Exception {
        mockMvc.perform(get("/movies/1/comments"))
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

    /**
     * Gets all comments for movie id = 2
     *
     * @throws Exception
     */
    @Test
    public void getAllCommentsForMovieWithId2WithSuccess() throws Exception {
        mockMvc.perform(get("/movies/2/comments"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].user.id", is(this.users.get(2).getId())))
                .andExpect(jsonPath("$[0].user.username", is(this.users.get(2).getUsername())))
                .andExpect(jsonPath("$[0].id", is(this.comments.get(2).getId())))
                .andExpect(jsonPath("$[0].date", is(this.comments.get(2).getDate())))
                .andExpect(jsonPath("$[0].value", is(this.comments.get(2).getValue())))
                .andExpect(jsonPath("$[0].movieid", is(this.comments.get(2).getMovieid())));

    }

    /**
     * Gets all comments for movie id = 3
     *
     * @throws Exception
     */
    @Test
    public void getAllCommentForMovieWithIde3WithFailure() throws Exception {
        mockMvc.perform(get("/movies/3/comments")
                .content(json(new Comment())).contentType(contentType)).andExpect(status().isNotFound());
    }

    /**
     * Gets comments for movie id = 1 on 3rd page with size of 1.
     *
     * @throws Exception
     */
    @Test
    public void getOneCommentOnSecondPageForMovieWithId1WithEmptyResultList() throws Exception {
        mockMvc.perform(get("/movies/1/comments?page=2&size=1")
                .content(json(new Comment())).contentType(contentType)).andExpect(status().isOk());
    }

    /**
     * Invalid number format exception.
     *
     * @throws Exception
     */
    @Test
    public void numberParameterFormetException1() throws Exception {
        mockMvc.perform(get("/movies/1/comments?page=invalid_number_format&size=1")
                .content(json(new Comment())).contentType(contentType)).andExpect(status().isBadRequest());
    }

    /**
     * Invalid number format exception on size
     *
     * @throws Exception
     */
    @Test
    public void numberParameterFormetException2() throws Exception {
        mockMvc.perform(get("/movies/1/comments?page=0&size=one")
                .content(json(new Comment())).contentType(contentType)).andExpect(status().isBadRequest());
    }

    /**
     * Add comment to a movie with success
     *
     * @throws Exception
     */
    @Test
    public void addOneCommentToAMovieWithId2WithSuccess() throws Exception {
        Comment c = new Comment();
        c.setId("4");
        c.setUser(users.get(1));
        c.setMovieid(movies.get(1).getId());
        c.setValue("Very good and excellent movie.");
        c.setDate(System.currentTimeMillis());

        mockMvc.perform(post("/movies/2/comments")
                .content(json(c)).contentType(contentType)).andExpect(status().isCreated());

    }

    /**
     * Adds one like to the movie with success
     *
     * @throws Exception
     */
    @Test
    public void addLikeToMovieWithIdWithSuccess() throws Exception {
        mockMvc.perform(get("/movies/2/like"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$.property", is("likes")))
                .andExpect(jsonPath("$.value", is(new Integer(this.movies.get(1).getLikes() + 1).toString())));
    }

    /**
     * Adds one dislike to the movie with success
     *
     * @throws Exception
     */
    @Test
    public void addDisLikeToMovieWithIdWithSuccess() throws Exception {
        mockMvc.perform(get("/movies/1/dislike"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$.property", is("dislikes")))
                .andExpect(jsonPath("$.value", is(new Integer(this.movies.get(0).getDislikes() + 1).toString())));
    }

    /**
     * Adds rating to the movie with success
     *
     * @throws Exception
     */
    @Test
    public void addRatingToMovieWithIdWithSuccess() throws Exception {
        Movie movie = movies.get(1);
        Rating rating = movie.getRating();
        rating.setFivestars(rating.getFivestars() + 1);

        mockMvc.perform(get("/movies/2/rate?rating=fivestars"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$.onestar", is(rating.getOnestar())))
                .andExpect(jsonPath("$.twostars", is(rating.getTwostars())))
                .andExpect(jsonPath("$.threestars", is(rating.getThreestars())))
                .andExpect(jsonPath("$.fourstars", is(rating.getFourstars())))
                .andExpect(jsonPath("$.fivestars", is(rating.getFivestars())));
    }

    /**
     * Adds on rating to the movie with failure
     *
     * @throws Exception
     */
    @Test
    public void addRatingToMovieWithIdWithFailure() throws Exception {
        mockMvc.perform(get("/movies/2/rate?rating=tenstars")
                .content(json(new Rating())).contentType(contentType)).andExpect(status().isBadRequest());
    }

    /**
     * Add a poorly formatted comment to a movie
     *
     * @throws Exception
     */
    @Test
    public void addOneCommentToAMovieWithNoIdWithFailure() throws Exception {
        Comment c = new Comment();
        c.setId("4");
        c.setUser(users.get(1));
        c.setValue("Very good.");
        c.setDate(System.currentTimeMillis());

        mockMvc.perform(post("/movies/2/comments")
                .content(json(c)).contentType(contentType)).andExpect(status().isBadRequest());

    }

    /**
     * Adds a poorly formatted comment to a movie
     *
     * @throws Exception
     */
    @Test
    public void addOneCommentToAMovieWithId2WithNoDateWithFaiulre() throws Exception {
        Comment c = new Comment();
        c.setId("4");
        c.setUser(users.get(1));
        c.setMovieid(movies.get(1).getId());
        c.setValue("Very good.");

        mockMvc.perform(post("/movies/2/comments")
                .content(json(c)).contentType(contentType)).andExpect(status().isBadRequest());

    }


    /**
     * Add a comment to a non existent movie
     *
     * @throws Exception
     */
    @Test
    public void addCommentToAMovieWithIdNotExistingWithFailure() throws Exception {
        Comment c = new Comment();
        c.setId("4");
        c.setUser(users.get(1));
        c.setMovieid("10");
        c.setValue("Very good.");

        mockMvc.perform(post("/movies/2/comments")
                .content(json(c)).contentType(contentType)).andExpect(status().isBadRequest());

    }


    /**
     * Converts an objec to Json Bourne
     *
     * @param obj the object to convert
     * @return the json string
     * @throws IOException exception
     */
    protected String json(Object obj) throws IOException {
        MockHttpOutputMessage mockHttpOutputMessage = new MockHttpOutputMessage();
        this.mappingJackson2HttpMessageConverter.write(obj, MediaType.APPLICATION_JSON, mockHttpOutputMessage);
        return mockHttpOutputMessage.getBodyAsString();
    }
}
