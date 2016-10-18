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
import vod.models.*;
import vod.repositories.*;

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
public class FrontendSeriesControllerTest {

    @Autowired
    SeriesRepository seriesRepository;
    @Autowired
    CommentsRepository commentsRepository;
    @Autowired
    UsersRepository usersRepository;
    @Autowired
    SeasonsRepository seasonsRepository;
    @Autowired
    EpisodesRepository episodesRepository;
    @Autowired
    WebApplicationContext webApplicationContext;

    private MediaType contentType = new MediaType(MediaType.APPLICATION_JSON.getType(),
            MediaType.APPLICATION_JSON.getSubtype(),
            Charset.forName("utf8"));
    private MockMvc mockMvc;
    private HttpMessageConverter mappingJackson2HttpMessageConverter;
    private List<Series> series;
    private List<User> users;
    private List<Comment> comments;
    private List<Season> seasons;
    private List<SeasonEpisode> episodes;

    @Autowired
    void setConverters(HttpMessageConverter<?>[] converters) {

        this.mappingJackson2HttpMessageConverter = Arrays.asList(converters).stream().filter(
                hmc -> hmc instanceof MappingJackson2HttpMessageConverter).findAny().get();

        Assert.assertNotNull("the JSON message converter must not be null",
                this.mappingJackson2HttpMessageConverter);
    }

    @Before
    public void setup() throws Exception {
        mockMvc = webAppContextSetup(webApplicationContext).build();
        seriesRepository.deleteAll();
        seasonsRepository.deleteAll();
        episodesRepository.deleteAll();
        commentsRepository.deleteAll();
        usersRepository.deleteAll();

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

        Series s1 = new Series();
        s1.setOverallrating(3);
        s1.setViews(100);
        s1.setId("1");
        s1.setCoverimage("/path_to_coverimage.png");
        s1.setDescription("A hacking movie");
        s1.setGenre("science_fiction");
        Rating r1 = new Rating();
        r1.setThreestars(40);
        r1.setFourstars(40);
        s1.setRating(r1);
        s1.setReleaseyear(2012);
        s1.setTitle("Mr. Robot");

        Series s2 = new Series();
        s2.setOverallrating(5);
        s2.setTitle("Game of thrones");
        s2.setReleaseyear(2015);
        s2.setGenre("thriller");
        s2.setDescription("A very very nice movie");
        s2.setCoverimage("/path_to_coverimage.png");
        s2.setId("2");
        s2.setViews(2000);
        Rating r2 = new Rating();
        r2.setFivestars(100);
        r2.setFourstars(60);
        s2.setRating(r2);

        series = new ArrayList<Series>() {{
            add(s1);
            add(s2);
        }};
        seriesRepository.save(series);

        Season ss1 = new Season();
        ss1.setId("1");
        ss1.setSeriesid(series.get(0).getId());
        ss1.setCoverimage("/path_to_coverimage.png");
        Rating ss1r1 = new Rating();
        ss1r1.setFourstars(30);
        ss1.setRating(ss1r1);
        ss1.setNumberofepisodes(1);
        ss1.setReleaseyear(series.get(0).getReleaseyear());
        ss1.setSeasonnumber(1);

        Season ss2 = new Season();
        ss2.setId("2");
        ss2.setSeriesid(series.get(1).getId());
        ss2.setCoverimage("/path_to_coverimage.png");
        Rating ss2r2 = new Rating();
        ss2r2.setFivestars(100);
        ss2.setRating(ss2r2);
        ss2.setNumberofepisodes(1);
        ss2.setReleaseyear(series.get(1).getReleaseyear());
        ss2.setSeasonnumber(1);

        seasons = new ArrayList<Season>() {{
            add(ss1);
            add(ss2);
        }};
        seasonsRepository.save(seasons);

        SeasonEpisode e1 = new SeasonEpisode();
        e1.setDislikes(10);
        Rating er1 = new Rating();
        er1.setThreestars(2);
        e1.setRating(er1);
        e1.setTitle("Mr. Robot s01e01");
        e1.setSeriesid(series.get(0).getId());
        e1.setSeasonid(seasons.get(0).getId());
        e1.setEpisodenumber(1);
        e1.setId("1");
        e1.setLikes(40);
        e1.setOverallrating(4);
        e1.setVideofile("C:\\Users\\achab\\Music\\video\\western\\Falz - Soldier (Official Music Video) ft. SIMI-Co2sqJSzbFI.mp4");
        e1.setViews(100);

        SeasonEpisode e2 = new SeasonEpisode();
        e2.setDislikes(1);
        Rating er2 = new Rating();
        er2.setFivestars(400);
        e2.setRating(er2);
        e2.setTitle("Game of Thrones s01e01");
        e2.setSeriesid(series.get(1).getId());
        e2.setSeasonid(seasons.get(1).getId());
        e2.setEpisodenumber(1);
        e2.setId("2");
        e2.setLikes(1000);
        e2.setOverallrating(5);
        e2.setVideofile("C:\\Users\\achab\\Music\\video\\western\\Rihanna - Diamonds - YouTube.mp4");
        e2.setViews(400);

        episodes = new ArrayList<SeasonEpisode>() {{
            add(e1);
            add(e2);
        }};

        episodesRepository.save(episodes);

        Comment cseries = new Comment();
        cseries.setUser(users.get(0));
        cseries.setDate(System.currentTimeMillis());
        cseries.setSeriesid(series.get(0).getId());
        cseries.setValue("A very nice series.");
        cseries.setId("1");

        Comment cseason = new Comment();
        cseason.setUser(users.get(1));
        cseason.setDate(System.currentTimeMillis());
        cseason.setSeasonid(seasons.get(0).getId());
        cseason.setValue("Cool season..");
        cseason.setId("2");

        Comment cepisode = new Comment();
        cepisode.setUser(users.get(2));
        cepisode.setDate(System.currentTimeMillis());
        cepisode.setSeasonepisodeid(episodes.get(0).getId());
        cepisode.setValue("I enjoyed this episode.");
        cepisode.setId("3");

        comments = new ArrayList<Comment>() {{
            add(cseries);
            add(cseason);
            add(cepisode);
        }};
        commentsRepository.save(comments);
    }

    /**
     * Gets all series in the database.
     *
     * @throws Exception
     */
    @Test
    public void getAllSeriesWithSuccess() throws Exception {
        mockMvc.perform(get("/series/"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(series.get(0).getId())))
                .andExpect(jsonPath("$[0].coverimage", is(series.get(0).getCoverimage())))
                .andExpect(jsonPath("$[0].description", is(series.get(0).getDescription())))
                .andExpect(jsonPath("$[0].genre", is(series.get(0).getGenre())))
                .andExpect(jsonPath("$[0].overallrating", is(series.get(0).getOverallrating())))
                .andExpect(jsonPath("$[0].rating.onestar", is(series.get(0).getRating().getOnestar())))
                .andExpect(jsonPath("$[0].rating.twostars", is(series.get(0).getRating().getTwostars())))
                .andExpect(jsonPath("$[0].rating.threestars", is(series.get(0).getRating().getThreestars())))
                .andExpect(jsonPath("$[0].rating.fourstars", is(series.get(0).getRating().getFourstars())))
                .andExpect(jsonPath("$[0].rating.fivestars", is(series.get(0).getRating().getFivestars())))
                .andExpect(jsonPath("$[0].releaseyear", is(series.get(0).getReleaseyear())))
                .andExpect(jsonPath("$[0].title", is(series.get(0).getTitle())))
                .andExpect(jsonPath("$[0].views", is(series.get(0).getViews())))
                .andExpect(jsonPath("$[1].id", is(series.get(1).getId())))
                .andExpect(jsonPath("$[1].coverimage", is(series.get(1).getCoverimage())))
                .andExpect(jsonPath("$[1].description", is(series.get(1).getDescription())))
                .andExpect(jsonPath("$[1].genre", is(series.get(1).getGenre())))
                .andExpect(jsonPath("$[1].overallrating", is(series.get(1).getOverallrating())))
                .andExpect(jsonPath("$[1].rating.onestar", is(series.get(1).getRating().getOnestar())))
                .andExpect(jsonPath("$[1].rating.twostars", is(series.get(1).getRating().getTwostars())))
                .andExpect(jsonPath("$[1].rating.threestars", is(series.get(1).getRating().getThreestars())))
                .andExpect(jsonPath("$[1].rating.fourstars", is(series.get(1).getRating().getFourstars())))
                .andExpect(jsonPath("$[1].rating.fivestars", is(series.get(1).getRating().getFivestars())))
                .andExpect(jsonPath("$[1].releaseyear", is(series.get(1).getReleaseyear())))
                .andExpect(jsonPath("$[1].title", is(series.get(1).getTitle())))
                .andExpect(jsonPath("$[1].views", is(series.get(1).getViews())));
    }

    /**
     * Reads a page and assersts that its actually that page it read.
     */
    @Test
    public void readsFirstPageCorrectly() {

        Page<Series> series = seriesRepository.findAll(new PageRequest(0, 10));

        assertThat(series.isFirst());
    }

    /**
     * Gets all series on the first page.
     *
     * @throws Exception
     */
    @Test
    public void getAllSeriesOnPageZeroWithSuccess() throws Exception {
        mockMvc.perform(get("/series/?page=0"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(series.get(0).getId())))
                .andExpect(jsonPath("$[0].coverimage", is(series.get(0).getCoverimage())))
                .andExpect(jsonPath("$[0].description", is(series.get(0).getDescription())))
                .andExpect(jsonPath("$[0].genre", is(series.get(0).getGenre())))
                .andExpect(jsonPath("$[0].overallrating", is(series.get(0).getOverallrating())))
                .andExpect(jsonPath("$[0].rating.onestar", is(series.get(0).getRating().getOnestar())))
                .andExpect(jsonPath("$[0].rating.twostars", is(series.get(0).getRating().getTwostars())))
                .andExpect(jsonPath("$[0].rating.threestars", is(series.get(0).getRating().getThreestars())))
                .andExpect(jsonPath("$[0].rating.fourstars", is(series.get(0).getRating().getFourstars())))
                .andExpect(jsonPath("$[0].rating.fivestars", is(series.get(0).getRating().getFivestars())))
                .andExpect(jsonPath("$[0].releaseyear", is(series.get(0).getReleaseyear())))
                .andExpect(jsonPath("$[0].title", is(series.get(0).getTitle())))
                .andExpect(jsonPath("$[0].views", is(series.get(0).getViews())))
                .andExpect(jsonPath("$[1].id", is(series.get(1).getId())))
                .andExpect(jsonPath("$[1].coverimage", is(series.get(1).getCoverimage())))
                .andExpect(jsonPath("$[1].description", is(series.get(1).getDescription())))
                .andExpect(jsonPath("$[1].genre", is(series.get(1).getGenre())))
                .andExpect(jsonPath("$[1].overallrating", is(series.get(1).getOverallrating())))
                .andExpect(jsonPath("$[1].rating.onestar", is(series.get(1).getRating().getOnestar())))
                .andExpect(jsonPath("$[1].rating.twostars", is(series.get(1).getRating().getTwostars())))
                .andExpect(jsonPath("$[1].rating.threestars", is(series.get(1).getRating().getThreestars())))
                .andExpect(jsonPath("$[1].rating.fourstars", is(series.get(1).getRating().getFourstars())))
                .andExpect(jsonPath("$[1].rating.fivestars", is(series.get(1).getRating().getFivestars())))
                .andExpect(jsonPath("$[1].releaseyear", is(series.get(1).getReleaseyear())))
                .andExpect(jsonPath("$[1].title", is(series.get(1).getTitle())))
                .andExpect(jsonPath("$[1].views", is(series.get(1).getViews())));

    }

    /**
     * Gets 1 series on page 0
     *
     * @throws Exception
     */
    @Test
    public void getOneSeriesOnPageZeroWithSuccess() throws Exception {
        mockMvc.perform(get("/series/?page=0&size=1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(series.get(0).getId())))
                .andExpect(jsonPath("$[0].coverimage", is(series.get(0).getCoverimage())))
                .andExpect(jsonPath("$[0].description", is(series.get(0).getDescription())))
                .andExpect(jsonPath("$[0].genre", is(series.get(0).getGenre())))
                .andExpect(jsonPath("$[0].overallrating", is(series.get(0).getOverallrating())))
                .andExpect(jsonPath("$[0].rating.onestar", is(series.get(0).getRating().getOnestar())))
                .andExpect(jsonPath("$[0].rating.twostars", is(series.get(0).getRating().getTwostars())))
                .andExpect(jsonPath("$[0].rating.threestars", is(series.get(0).getRating().getThreestars())))
                .andExpect(jsonPath("$[0].rating.fourstars", is(series.get(0).getRating().getFourstars())))
                .andExpect(jsonPath("$[0].rating.fivestars", is(series.get(0).getRating().getFivestars())))
                .andExpect(jsonPath("$[0].releaseyear", is(series.get(0).getReleaseyear())))
                .andExpect(jsonPath("$[0].title", is(series.get(0).getTitle())))
                .andExpect(jsonPath("$[0].views", is(series.get(0).getViews())));
    }


    @Test
    public void getThreeSeriesOnPageZeroWithSuccess() throws Exception {
        mockMvc.perform(get("/series/?page=0&size=3"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(series.get(0).getId())))
                .andExpect(jsonPath("$[0].coverimage", is(series.get(0).getCoverimage())))
                .andExpect(jsonPath("$[0].description", is(series.get(0).getDescription())))
                .andExpect(jsonPath("$[0].genre", is(series.get(0).getGenre())))
                .andExpect(jsonPath("$[0].overallrating", is(series.get(0).getOverallrating())))
                .andExpect(jsonPath("$[0].rating.onestar", is(series.get(0).getRating().getOnestar())))
                .andExpect(jsonPath("$[0].rating.twostars", is(series.get(0).getRating().getTwostars())))
                .andExpect(jsonPath("$[0].rating.threestars", is(series.get(0).getRating().getThreestars())))
                .andExpect(jsonPath("$[0].rating.fourstars", is(series.get(0).getRating().getFourstars())))
                .andExpect(jsonPath("$[0].rating.fivestars", is(series.get(0).getRating().getFivestars())))
                .andExpect(jsonPath("$[0].releaseyear", is(series.get(0).getReleaseyear())))
                .andExpect(jsonPath("$[0].title", is(series.get(0).getTitle())))
                .andExpect(jsonPath("$[0].views", is(series.get(0).getViews())))
                .andExpect(jsonPath("$[1].id", is(series.get(1).getId())))
                .andExpect(jsonPath("$[1].coverimage", is(series.get(1).getCoverimage())))
                .andExpect(jsonPath("$[1].description", is(series.get(1).getDescription())))
                .andExpect(jsonPath("$[1].genre", is(series.get(1).getGenre())))
                .andExpect(jsonPath("$[1].overallrating", is(series.get(1).getOverallrating())))
                .andExpect(jsonPath("$[1].rating.onestar", is(series.get(1).getRating().getOnestar())))
                .andExpect(jsonPath("$[1].rating.twostars", is(series.get(1).getRating().getTwostars())))
                .andExpect(jsonPath("$[1].rating.threestars", is(series.get(1).getRating().getThreestars())))
                .andExpect(jsonPath("$[1].rating.fourstars", is(series.get(1).getRating().getFourstars())))
                .andExpect(jsonPath("$[1].rating.fivestars", is(series.get(1).getRating().getFivestars())))
                .andExpect(jsonPath("$[1].releaseyear", is(series.get(1).getReleaseyear())))
                .andExpect(jsonPath("$[1].title", is(series.get(1).getTitle())))
                .andExpect(jsonPath("$[1].views", is(series.get(1).getViews())));

    }

    @Test
    public void getOneSeriesOnPageOneWithSuccess() throws Exception {
        mockMvc.perform(get("/series/?page=1&size=1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(series.get(1).getId())))
                .andExpect(jsonPath("$[0].coverimage", is(series.get(1).getCoverimage())))
                .andExpect(jsonPath("$[0].description", is(series.get(1).getDescription())))
                .andExpect(jsonPath("$[0].genre", is(series.get(1).getGenre())))
                .andExpect(jsonPath("$[0].overallrating", is(series.get(1).getOverallrating())))
                .andExpect(jsonPath("$[0].rating.onestar", is(series.get(1).getRating().getOnestar())))
                .andExpect(jsonPath("$[0].rating.twostars", is(series.get(1).getRating().getTwostars())))
                .andExpect(jsonPath("$[0].rating.threestars", is(series.get(1).getRating().getThreestars())))
                .andExpect(jsonPath("$[0].rating.fourstars", is(series.get(1).getRating().getFourstars())))
                .andExpect(jsonPath("$[0].rating.fivestars", is(series.get(1).getRating().getFivestars())))
                .andExpect(jsonPath("$[0].releaseyear", is(series.get(1).getReleaseyear())))
                .andExpect(jsonPath("$[0].title", is(series.get(1).getTitle())))
                .andExpect(jsonPath("$[0].views", is(series.get(1).getViews())));

    }

    @Test
    public void getOneSeriesOnPageTwoReturnsEmptyList() throws Exception {
        mockMvc.perform(get("/series/?page=2&size=1")
                .content(json(new Series())).contentType(contentType)).andExpect(status().isOk());
    }

    @Test
    public void getThreeMoviesOnPageOneReturnsEmptyList() throws Exception {
        mockMvc.perform(get("/series/?page=1&size=3")
                .content(json(new Series())).contentType(contentType)).andExpect(status().isOk());
    }

    @Test
    public void InvalidNumberPropertyException() throws Exception {
        mockMvc.perform(get("/series/?page=expect_valid_number_not_string")
                .content(json(new Series())).contentType(contentType)).andExpect(status().isBadRequest());
    }

    @Test
    public void InvalidNumberPropertyException1() throws Exception {
        mockMvc.perform(get("/series/?size=expect_valid_number_not_string")
                .content(json(new Series())).contentType(contentType)).andExpect(status().isBadRequest());
    }

    @Test
    public void getAllSeriesOnPageTwoWithDefaultCountReturnsEmptyList() throws Exception {
        mockMvc.perform(get("/series/?page=2")
                .content(json(new Series())).contentType(contentType)).andExpect(status().isOk());
    }

    @Test
    public void getOneMovieWithDefaultPageSizeWithSuccess() throws Exception {
        mockMvc.perform(get("/series/?size=1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(series.get(0).getId())))
                .andExpect(jsonPath("$[0].coverimage", is(series.get(0).getCoverimage())))
                .andExpect(jsonPath("$[0].description", is(series.get(0).getDescription())))
                .andExpect(jsonPath("$[0].genre", is(series.get(0).getGenre())))
                .andExpect(jsonPath("$[0].overallrating", is(series.get(0).getOverallrating())))
                .andExpect(jsonPath("$[0].rating.onestar", is(series.get(0).getRating().getOnestar())))
                .andExpect(jsonPath("$[0].rating.twostars", is(series.get(0).getRating().getTwostars())))
                .andExpect(jsonPath("$[0].rating.threestars", is(series.get(0).getRating().getThreestars())))
                .andExpect(jsonPath("$[0].rating.fourstars", is(series.get(0).getRating().getFourstars())))
                .andExpect(jsonPath("$[0].rating.fivestars", is(series.get(0).getRating().getFivestars())))
                .andExpect(jsonPath("$[0].releaseyear", is(series.get(0).getReleaseyear())))
                .andExpect(jsonPath("$[0].title", is(series.get(0).getTitle())))
                .andExpect(jsonPath("$[0].views", is(series.get(0).getViews())));

    }

    @Test
    public void getTenSeriesWithDefaultPageSizeWithSuccess() throws Exception {
        mockMvc.perform(get("/series/?size=10"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(series.get(0).getId())))
                .andExpect(jsonPath("$[0].coverimage", is(series.get(0).getCoverimage())))
                .andExpect(jsonPath("$[0].description", is(series.get(0).getDescription())))
                .andExpect(jsonPath("$[0].genre", is(series.get(0).getGenre())))
                .andExpect(jsonPath("$[0].overallrating", is(series.get(0).getOverallrating())))
                .andExpect(jsonPath("$[0].rating.onestar", is(series.get(0).getRating().getOnestar())))
                .andExpect(jsonPath("$[0].rating.twostars", is(series.get(0).getRating().getTwostars())))
                .andExpect(jsonPath("$[0].rating.threestars", is(series.get(0).getRating().getThreestars())))
                .andExpect(jsonPath("$[0].rating.fourstars", is(series.get(0).getRating().getFourstars())))
                .andExpect(jsonPath("$[0].rating.fivestars", is(series.get(0).getRating().getFivestars())))
                .andExpect(jsonPath("$[0].releaseyear", is(series.get(0).getReleaseyear())))
                .andExpect(jsonPath("$[0].title", is(series.get(0).getTitle())))
                .andExpect(jsonPath("$[0].views", is(series.get(0).getViews())))
                .andExpect(jsonPath("$[1].id", is(series.get(1).getId())))
                .andExpect(jsonPath("$[1].coverimage", is(series.get(1).getCoverimage())))
                .andExpect(jsonPath("$[1].description", is(series.get(1).getDescription())))
                .andExpect(jsonPath("$[1].genre", is(series.get(1).getGenre())))
                .andExpect(jsonPath("$[1].overallrating", is(series.get(1).getOverallrating())))
                .andExpect(jsonPath("$[1].rating.onestar", is(series.get(1).getRating().getOnestar())))
                .andExpect(jsonPath("$[1].rating.twostars", is(series.get(1).getRating().getTwostars())))
                .andExpect(jsonPath("$[1].rating.threestars", is(series.get(1).getRating().getThreestars())))
                .andExpect(jsonPath("$[1].rating.fourstars", is(series.get(1).getRating().getFourstars())))
                .andExpect(jsonPath("$[1].rating.fivestars", is(series.get(1).getRating().getFivestars())))
                .andExpect(jsonPath("$[1].releaseyear", is(series.get(1).getReleaseyear())))
                .andExpect(jsonPath("$[1].title", is(series.get(1).getTitle())))
                .andExpect(jsonPath("$[1].views", is(series.get(1).getViews())));
    }

    @Test
    public void getAllSeriesWithGenreEqualThrillerWithSuccess() throws Exception {
        mockMvc.perform(get("/series/?genre=thriller"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(series.get(1).getId())))
                .andExpect(jsonPath("$[0].coverimage", is(series.get(1).getCoverimage())))
                .andExpect(jsonPath("$[0].description", is(series.get(1).getDescription())))
                .andExpect(jsonPath("$[0].genre", is(series.get(1).getGenre())))
                .andExpect(jsonPath("$[0].overallrating", is(series.get(1).getOverallrating())))
                .andExpect(jsonPath("$[0].rating.onestar", is(series.get(1).getRating().getOnestar())))
                .andExpect(jsonPath("$[0].rating.twostars", is(series.get(1).getRating().getTwostars())))
                .andExpect(jsonPath("$[0].rating.threestars", is(series.get(1).getRating().getThreestars())))
                .andExpect(jsonPath("$[0].rating.fourstars", is(series.get(1).getRating().getFourstars())))
                .andExpect(jsonPath("$[0].rating.fivestars", is(series.get(1).getRating().getFivestars())))
                .andExpect(jsonPath("$[0].releaseyear", is(series.get(1).getReleaseyear())))
                .andExpect(jsonPath("$[0].title", is(series.get(1).getTitle())))
                .andExpect(jsonPath("$[0].views", is(series.get(1).getViews())));
    }

    @Test
    public void invalidGenre() throws Exception {
        mockMvc.perform(get("/series/?genre=sex")
                .content(json(new Movie())).contentType(contentType)).andExpect(status().isBadRequest());
    }

    @Test
    public void getAllSeriesAndSortThemWithDefaultPropertyIdWithSuccess() throws Exception {
        mockMvc.perform(get("/series/?sort=true"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(series.get(0).getId())))
                .andExpect(jsonPath("$[0].coverimage", is(series.get(0).getCoverimage())))
                .andExpect(jsonPath("$[0].description", is(series.get(0).getDescription())))
                .andExpect(jsonPath("$[0].genre", is(series.get(0).getGenre())))
                .andExpect(jsonPath("$[0].overallrating", is(series.get(0).getOverallrating())))
                .andExpect(jsonPath("$[0].rating.onestar", is(series.get(0).getRating().getOnestar())))
                .andExpect(jsonPath("$[0].rating.twostars", is(series.get(0).getRating().getTwostars())))
                .andExpect(jsonPath("$[0].rating.threestars", is(series.get(0).getRating().getThreestars())))
                .andExpect(jsonPath("$[0].rating.fourstars", is(series.get(0).getRating().getFourstars())))
                .andExpect(jsonPath("$[0].rating.fivestars", is(series.get(0).getRating().getFivestars())))
                .andExpect(jsonPath("$[0].releaseyear", is(series.get(0).getReleaseyear())))
                .andExpect(jsonPath("$[0].title", is(series.get(0).getTitle())))
                .andExpect(jsonPath("$[0].views", is(series.get(0).getViews())))
                .andExpect(jsonPath("$[1].id", is(series.get(1).getId())))
                .andExpect(jsonPath("$[1].coverimage", is(series.get(1).getCoverimage())))
                .andExpect(jsonPath("$[1].description", is(series.get(1).getDescription())))
                .andExpect(jsonPath("$[1].genre", is(series.get(1).getGenre())))
                .andExpect(jsonPath("$[1].overallrating", is(series.get(1).getOverallrating())))
                .andExpect(jsonPath("$[1].rating.onestar", is(series.get(1).getRating().getOnestar())))
                .andExpect(jsonPath("$[1].rating.twostars", is(series.get(1).getRating().getTwostars())))
                .andExpect(jsonPath("$[1].rating.threestars", is(series.get(1).getRating().getThreestars())))
                .andExpect(jsonPath("$[1].rating.fourstars", is(series.get(1).getRating().getFourstars())))
                .andExpect(jsonPath("$[1].rating.fivestars", is(series.get(1).getRating().getFivestars())))
                .andExpect(jsonPath("$[1].releaseyear", is(series.get(1).getReleaseyear())))
                .andExpect(jsonPath("$[1].title", is(series.get(1).getTitle())))
                .andExpect(jsonPath("$[1].views", is(series.get(1).getViews())));
    }

    @Test
    public void getAllSeriesAndSortThemWithDefaultPropertyInDescendingOrderWithSuccess() throws Exception {
        mockMvc.perform(get("/series/?sort=true&order=desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(series.get(1).getId())))
                .andExpect(jsonPath("$[0].coverimage", is(series.get(1).getCoverimage())))
                .andExpect(jsonPath("$[0].description", is(series.get(1).getDescription())))
                .andExpect(jsonPath("$[0].genre", is(series.get(1).getGenre())))
                .andExpect(jsonPath("$[0].overallrating", is(series.get(1).getOverallrating())))
                .andExpect(jsonPath("$[0].rating.onestar", is(series.get(1).getRating().getOnestar())))
                .andExpect(jsonPath("$[0].rating.twostars", is(series.get(1).getRating().getTwostars())))
                .andExpect(jsonPath("$[0].rating.threestars", is(series.get(1).getRating().getThreestars())))
                .andExpect(jsonPath("$[0].rating.fourstars", is(series.get(1).getRating().getFourstars())))
                .andExpect(jsonPath("$[0].rating.fivestars", is(series.get(1).getRating().getFivestars())))
                .andExpect(jsonPath("$[0].releaseyear", is(series.get(1).getReleaseyear())))
                .andExpect(jsonPath("$[0].title", is(series.get(1).getTitle())))
                .andExpect(jsonPath("$[0].views", is(series.get(1).getViews())))
                .andExpect(jsonPath("$[1].id", is(series.get(0).getId())))
                .andExpect(jsonPath("$[1].coverimage", is(series.get(0).getCoverimage())))
                .andExpect(jsonPath("$[1].description", is(series.get(0).getDescription())))
                .andExpect(jsonPath("$[1].genre", is(series.get(0).getGenre())))
                .andExpect(jsonPath("$[1].overallrating", is(series.get(0).getOverallrating())))
                .andExpect(jsonPath("$[1].rating.onestar", is(series.get(0).getRating().getOnestar())))
                .andExpect(jsonPath("$[1].rating.twostars", is(series.get(0).getRating().getTwostars())))
                .andExpect(jsonPath("$[1].rating.threestars", is(series.get(0).getRating().getThreestars())))
                .andExpect(jsonPath("$[1].rating.fourstars", is(series.get(0).getRating().getFourstars())))
                .andExpect(jsonPath("$[1].rating.fivestars", is(series.get(0).getRating().getFivestars())))
                .andExpect(jsonPath("$[1].releaseyear", is(series.get(0).getReleaseyear())))
                .andExpect(jsonPath("$[1].title", is(series.get(0).getTitle())))
                .andExpect(jsonPath("$[1].views", is(series.get(0).getViews())));
    }

    @Test
    public void invalidOrderParameter() throws Exception {
        mockMvc.perform(get("/series/?sort=true&order=descending")
                .content(json(new Movie())).contentType(contentType)).andExpect(status().isBadRequest());
    }

    @Test
    public void invalidPropertyParameter() throws Exception {
        mockMvc.perform(get("/series/?sort=true&property=description")
                .content(json(new Movie())).contentType(contentType)).andExpect(status().isBadRequest());
    }

    @Test
    public void getAllSeriesSortingThemByOverallRatingOrderingThemInDescending() throws Exception {
        mockMvc.perform(get("/series/?sort=true&property=overallrating&order=desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(series.get(1).getId())))
                .andExpect(jsonPath("$[0].coverimage", is(series.get(1).getCoverimage())))
                .andExpect(jsonPath("$[0].description", is(series.get(1).getDescription())))
                .andExpect(jsonPath("$[0].genre", is(series.get(1).getGenre())))
                .andExpect(jsonPath("$[0].overallrating", is(series.get(1).getOverallrating())))
                .andExpect(jsonPath("$[0].rating.onestar", is(series.get(1).getRating().getOnestar())))
                .andExpect(jsonPath("$[0].rating.twostars", is(series.get(1).getRating().getTwostars())))
                .andExpect(jsonPath("$[0].rating.threestars", is(series.get(1).getRating().getThreestars())))
                .andExpect(jsonPath("$[0].rating.fourstars", is(series.get(1).getRating().getFourstars())))
                .andExpect(jsonPath("$[0].rating.fivestars", is(series.get(1).getRating().getFivestars())))
                .andExpect(jsonPath("$[0].releaseyear", is(series.get(1).getReleaseyear())))
                .andExpect(jsonPath("$[0].title", is(series.get(1).getTitle())))
                .andExpect(jsonPath("$[0].views", is(series.get(1).getViews())))
                .andExpect(jsonPath("$[1].id", is(series.get(0).getId())))
                .andExpect(jsonPath("$[1].coverimage", is(series.get(0).getCoverimage())))
                .andExpect(jsonPath("$[1].description", is(series.get(0).getDescription())))
                .andExpect(jsonPath("$[1].genre", is(series.get(0).getGenre())))
                .andExpect(jsonPath("$[1].overallrating", is(series.get(0).getOverallrating())))
                .andExpect(jsonPath("$[1].rating.onestar", is(series.get(0).getRating().getOnestar())))
                .andExpect(jsonPath("$[1].rating.twostars", is(series.get(0).getRating().getTwostars())))
                .andExpect(jsonPath("$[1].rating.threestars", is(series.get(0).getRating().getThreestars())))
                .andExpect(jsonPath("$[1].rating.fourstars", is(series.get(0).getRating().getFourstars())))
                .andExpect(jsonPath("$[1].rating.fivestars", is(series.get(0).getRating().getFivestars())))
                .andExpect(jsonPath("$[1].releaseyear", is(series.get(0).getReleaseyear())))
                .andExpect(jsonPath("$[1].title", is(series.get(0).getTitle())))
                .andExpect(jsonPath("$[1].views", is(series.get(0).getViews())));
    }

    @Test
    public void getAllSeriesSimilarToSeriesWithId1WithSuccess() throws Exception {
        mockMvc.perform(get("/series/1/similar")
                .content(json(new Series())).contentType(contentType)).andExpect(status().isOk());
    }

    @Test
    public void invalidOrderParameterException1() throws Exception {
        mockMvc.perform(get("/movies/1/similar?sort=true&order=descending")
                .content(json(new Series())).contentType(contentType)).andExpect(status().isBadRequest());
    }

    @Test
    public void getAllSeasonsOfSeriesWithIdWithSuccess() throws Exception {
        mockMvc.perform(get("/series/1/seasons"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(seasons.get(0).getId())))
                .andExpect(jsonPath("$[0].seriesid", is(seasons.get(0).getSeriesid())))
                .andExpect(jsonPath("$[0].releaseyear", is(seasons.get(0).getReleaseyear())))
                .andExpect(jsonPath("$[0].coverimage", is(seasons.get(0).getCoverimage())))
                .andExpect(jsonPath("$[0].numberofepisodes", is(seasons.get(0).getNumberofepisodes())))
                .andExpect(jsonPath("$[0].seasonnumber", is(seasons.get(0).getSeasonnumber())));

    }

    @Test
    public void getSeasonWithIdOfSeriesWithIdWithSuccess() throws Exception {
        mockMvc.perform(get("/series/1/seasons/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$.id", is(seasons.get(0).getId())))
                .andExpect(jsonPath("$.seriesid", is(seasons.get(0).getSeriesid())))
                .andExpect(jsonPath("$.releaseyear", is(seasons.get(0).getReleaseyear())))
                .andExpect(jsonPath("$.coverimage", is(seasons.get(0).getCoverimage())))
                .andExpect(jsonPath("$.numberofepisodes", is(seasons.get(0).getNumberofepisodes())))
                .andExpect(jsonPath("$.seasonnumber", is(seasons.get(0).getSeasonnumber())));
    }

    @Test
    public void getSeasonEpisodesWithSeasonIdWithSuccess() throws Exception {
        mockMvc.perform(get("/series/1/seasons/1/episodes"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(episodes.get(0).getId())))
                .andExpect(jsonPath("$[0].seriesid", is(episodes.get(0).getSeriesid())))
                .andExpect(jsonPath("$[0].episodenumber", is(episodes.get(0).getEpisodenumber())))
                .andExpect(jsonPath("$[0].seasonid", is(episodes.get(0).getSeasonid())))
                .andExpect(jsonPath("$[0].videofile", is(episodes.get(0).getVideofile())))
                .andExpect(jsonPath("$[0].views", is(episodes.get(0).getViews())));
    }

    @Test
    public void getSeasonEpisodeWithIdWithSeasonidWithSucess() throws Exception {
        mockMvc.perform(get("/series/1/seasons/1/episodes/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$.id", is(episodes.get(0).getId())))
                .andExpect(jsonPath("$.seriesid", is(episodes.get(0).getSeriesid())))
                .andExpect(jsonPath("$.episodenumber", is(episodes.get(0).getEpisodenumber())))
                .andExpect(jsonPath("$.seasonid", is(episodes.get(0).getSeasonid())))
                .andExpect(jsonPath("$.videofile", is(episodes.get(0).getVideofile())))
                .andExpect(jsonPath("$.views", is(episodes.get(0).getViews())));
    }

    @Test
    public void getAllCommentsForSeriesWithId1WithSuccess() throws Exception {
        mockMvc.perform(get("/series/1/comments"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].user.id", is(users.get(0).getId())))
                .andExpect(jsonPath("$[0].user.username", is(users.get(0).getUsername())))
                .andExpect(jsonPath("$[0].id", is(comments.get(0).getId())))
                .andExpect(jsonPath("$[0].date", is(comments.get(0).getDate())))
                .andExpect(jsonPath("$[0].value", is(comments.get(0).getValue())))
                .andExpect(jsonPath("$[0].seriesid", is(comments.get(0).getSeriesid())));
    }

    @Test
    public void getAllCommentsForSeasonWithId1WithSuccess() throws Exception {
        mockMvc.perform(get("/series/1/seasons/1/comments"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].user.id", is(users.get(1).getId())))
                .andExpect(jsonPath("$[0].user.username", is(users.get(1).getUsername())))
                .andExpect(jsonPath("$[0].id", is(comments.get(1).getId())))
                .andExpect(jsonPath("$[0].date", is(comments.get(1).getDate())))
                .andExpect(jsonPath("$[0].value", is(comments.get(1).getValue())))
                .andExpect(jsonPath("$[0].seriesid", is(comments.get(1).getSeriesid())));
    }

    @Test
    public void getAllCommentsForSeasonEpisodeWithId1WithSuccess() throws Exception {
        mockMvc.perform(get("/series/1/seasons/1/episodes/1/comments"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].user.id", is(users.get(2).getId())))
                .andExpect(jsonPath("$[0].user.username", is(users.get(2).getUsername())))
                .andExpect(jsonPath("$[0].id", is(comments.get(2).getId())))
                .andExpect(jsonPath("$[0].date", is(comments.get(2).getDate())))
                .andExpect(jsonPath("$[0].value", is(comments.get(2).getValue())))
                .andExpect(jsonPath("$[0].seriesid", is(comments.get(2).getSeriesid())));
    }

    @Test
    public void getAllCommentForSeriesWithIde3WithFailure() throws Exception {
        mockMvc.perform(get("/series/3/comments")
                .content(json(new Comment())).contentType(contentType)).andExpect(status().isNotFound());
    }

    @Test
    public void numberParameterFormetException1() throws Exception {
        mockMvc.perform(get("/series/1/comments?page=invalid_number_format&size=1")
                .content(json(new Comment())).contentType(contentType)).andExpect(status().isBadRequest());
    }

    @Test
    public void numberParameterFormetException2() throws Exception {
        mockMvc.perform(get("/series/1/comments?page=0&size=one")
                .content(json(new Comment())).contentType(contentType)).andExpect(status().isBadRequest());
    }

    @Test
    public void addOneCommentToASeriesWithId2WithSuccess() throws Exception {
        Comment c = new Comment();
        c.setId("4");
        c.setUser(users.get(1));
        c.setSeriesid("2");
        c.setValue("Very good and excellent series.");
        c.setDate(System.currentTimeMillis());

        mockMvc.perform(post("/series/2/comments")
                .content(json(c)).contentType(contentType)).andExpect(status().isCreated());

    }

    @Test
    public void addOneCommentToASeasonWithId2WithSuccess() throws Exception {
        Comment c = new Comment();
        c.setId("5");
        c.setUser(users.get(2));
        c.setSeasonid("2");
        c.setSeriesid("2");
        c.setValue("Very good and excellent season.");
        c.setDate(System.currentTimeMillis());

        mockMvc.perform(post("/series/2/seasons/2/comments")
                .content(json(c)).contentType(contentType)).andExpect(status().isCreated());

    }

    @Test
    public void addOneCommentToASeasonEpisodeWithId2WithSuccess() throws Exception {
        Comment c = new Comment();
        c.setId("6");
        c.setUser(users.get(1));
        c.setSeasonid("2");
        c.setSeriesid("2");
        c.setSeasonepisodeid("2");
        c.setValue("Very good and excellent season.");
        c.setDate(System.currentTimeMillis());

        mockMvc.perform(post("/series/2/seasons/2/episodes/2/comments")
                .content(json(c)).contentType(contentType)).andExpect(status().isCreated());

    }

    @Test
    public void addDisLikeToSeasonEpisodeWithId1WithSuccess() throws Exception {
        mockMvc.perform(get("/series/1/seasons/1/episodes/1/dislike"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$.property", is("dislikes")))
                .andExpect(jsonPath("$.value", is(new Integer(episodes.get(0).getDislikes() + 1).toString())));
    }

    @Test
    public void addLikeToMovieWithIdWithSuccess() throws Exception {
        mockMvc.perform(get("/series/2/seasons/2/episodes/2/like"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$.property", is("likes")))
                .andExpect(jsonPath("$.value", is(new Integer(episodes.get(1).getLikes() + 1).toString())));
    }

    @Test
    public void addRatingToSeriesWithId1WithSuccess() throws Exception {
        Series s = series.get(0);
        Rating rating = s.getRating();
        rating.setFivestars(rating.getFivestars() + 1);

        mockMvc.perform(get("/series/1/rate?rating=fivestars"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$.onestar", is(rating.getOnestar())))
                .andExpect(jsonPath("$.twostars", is(rating.getTwostars())))
                .andExpect(jsonPath("$.threestars", is(rating.getThreestars())))
                .andExpect(jsonPath("$.fourstars", is(rating.getFourstars())))
                .andExpect(jsonPath("$.fivestars", is(rating.getFivestars())));
    }

    @Test
    public void addRatingToSeasonWithId2WithSuccess() throws Exception {
        Season s = seasons.get(1);
        Rating rating = s.getRating();
        rating.setFivestars(rating.getFivestars() + 1);

        mockMvc.perform(get("/series/2/seasons/2/rate?rating=fivestars"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$.onestar", is(rating.getOnestar())))
                .andExpect(jsonPath("$.twostars", is(rating.getTwostars())))
                .andExpect(jsonPath("$.threestars", is(rating.getThreestars())))
                .andExpect(jsonPath("$.fourstars", is(rating.getFourstars())))
                .andExpect(jsonPath("$.fivestars", is(rating.getFivestars())));
    }

    @Test
    public void addRatingToEpisdoeWithId2WithSuccess() throws Exception {
        SeasonEpisode se = episodes.get(1);
        Rating rating = se.getRating();
        rating.setFivestars(rating.getFivestars() + 1);

        mockMvc.perform(get("/series/2/seasons/2/episodes/2/rate?rating=fivestars"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$.onestar", is(rating.getOnestar())))
                .andExpect(jsonPath("$.twostars", is(rating.getTwostars())))
                .andExpect(jsonPath("$.threestars", is(rating.getThreestars())))
                .andExpect(jsonPath("$.fourstars", is(rating.getFourstars())))
                .andExpect(jsonPath("$.fivestars", is(rating.getFivestars())));
    }

    @Test
    public void addRatingToSeriesWithIdWithFailure() throws Exception {
        mockMvc.perform(get("/series/1/rate?rating=tenstars")
                .content(json(new Rating())).contentType(contentType)).andExpect(status().isBadRequest());
    }

    @Test
    public void addRatingToSeriesWithInvalididWithFailure() throws Exception {
        mockMvc.perform(get("/series/10/rate?rating=twostars")
                .content(json(new Rating())).contentType(contentType)).andExpect(status().isNotFound());
    }

    @Test
    public void addOneCommentToASeriesWithNoIdWithFailure() throws Exception {
        Comment c = new Comment();
        c.setId("7");
        c.setUser(users.get(1));
        c.setValue("Very good.");
        c.setDate(System.currentTimeMillis());

        mockMvc.perform(post("/series/2/comments")
                .content(json(c)).contentType(contentType)).andExpect(status().isBadRequest());

    }

    @Test
    public void addOneCommentToASeasonWithNoIdWithFailure() throws Exception {
        Comment c = new Comment();
        c.setId("7");
        c.setUser(users.get(1));
        c.setValue("Very good.");
        c.setDate(System.currentTimeMillis());

        mockMvc.perform(post("/series/2/seasons/2/comments")
                .content(json(c)).contentType(contentType)).andExpect(status().isBadRequest());

    }

    @Test
    public void addOneCommentToAEpisodeWithNoIdWithFailure() throws Exception {
        Comment c = new Comment();
        c.setId("7");
        c.setUser(users.get(1));
        c.setValue("Very good.");
        c.setDate(System.currentTimeMillis());

        mockMvc.perform(post("/series/2/seasons/2/episodes/2/comments")
                .content(json(c)).contentType(contentType)).andExpect(status().isBadRequest());

    }

    @Test
    public void addOneCommentToASeriesWithIdNotExitingWithFailure() throws Exception {
        Comment c = new Comment();
        c.setId("9");
        c.setSeriesid("10");
        c.setUser(users.get(1));
        c.setValue("Very good.");
        c.setDate(System.currentTimeMillis());

        mockMvc.perform(post("/series/2/comments")
                .content(json(c)).contentType(contentType)).andExpect(status().isNotFound());

    }


    @Test
    public void addOneCommentToAseriesWithId2WithNoDateWithFaiulre() throws Exception {
        Comment c = new Comment();
        c.setId("8");
        c.setUser(users.get(1));
        c.setSeriesid(series.get(1).getId());
        c.setValue("Very good.");

        mockMvc.perform(post("/series/2/comments")
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
