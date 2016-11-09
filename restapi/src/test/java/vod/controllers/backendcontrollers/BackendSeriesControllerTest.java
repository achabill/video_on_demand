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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

*/
/**
 * Test for the backend series Controller.
 */

/*
@SpringApplicationConfiguration(classes = Application.class)
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@EnableAutoConfiguration
public class BackendSeriesControllerTest {

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

    @Test
    public void getAllSeriesWithSuccess() throws Exception{
        mockMvc.perform(get("admin/series/"))
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
    public void addOneSeriesWithSuccess() throws Exception{
        Series s = new Series();
        s.setOverallrating(4);
        s.setTitle("Person of Interest");
        s.setReleaseyear(2013);
        s.setGenre("thriller");
        s.setDescription("A very very nice movie");
        s.setCoverimage("/path_to_coverimage.png");
        s.setId("3");
        s.setViews(2000);
        Rating r = new Rating();
        r.setFivestars(100);
        r.setFourstars(20);
        s.setRating(r);

        mockMvc.perform(post("/admin/series/")
                .content(json(s)).contentType(contentType)).andExpect(status().isCreated());
    }

    @Test
    public void getOneSeriesWithIdWithSuccess() throws Exception{
        Series s = new Series();
        s.setOverallrating(4);
        s.setTitle("Person of Interest");
        s.setReleaseyear(2013);
        s.setGenre("thriller");
        s.setDescription("A very very nice movie");
        s.setCoverimage("/path_to_coverimage.png");
        s.setId("3");
        s.setViews(2000);
        Rating r = new Rating();
        r.setFivestars(100);
        r.setFourstars(20);
        s.setRating(r);

        mockMvc.perform(get("admin/series/3"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$.id", is(s.getId())))
                .andExpect(jsonPath("$.coverimage", is(s.getCoverimage())))
                .andExpect(jsonPath("$.description", is(s.getDescription())))
                .andExpect(jsonPath("$.genre", is(s.getGenre())))
                .andExpect(jsonPath("$.overallrating", is(s.getOverallrating())))
                .andExpect(jsonPath("$.rating.onestar", is(s.getRating().getOnestar())))
                .andExpect(jsonPath("$.rating.twostars", is(s.getRating().getTwostars())))
                .andExpect(jsonPath("$.rating.threestars", is(s.getRating().getThreestars())))
                .andExpect(jsonPath("$.rating.fourstars", is(s.getRating().getFourstars())))
                .andExpect(jsonPath("$.rating.fivestars", is(s.getRating().getFivestars())))
                .andExpect(jsonPath("$.releaseyear", is(s.getReleaseyear())))
                .andExpect(jsonPath("$.title", is(s.getTitle())))
                .andExpect(jsonPath("$.views", is(s.getViews())));
    }

    /*
    @Test
    public void deleteOneSeriesWithSuccessWithId() throws Exception{
        Series s = new Series();
        s.setOverallrating(4);
        s.setTitle("Person of Interest");
        s.setReleaseyear(2013);
        s.setGenre("thriller");
        s.setDescription("A very very nice movie");
        s.setCoverimage("/path_to_coverimage.png");
        s.setId("3");
        s.setViews(2000);
        Rating r = new Rating();
        r.setFivestars(100);
        r.setFourstars(20);
        s.setRating(r);

        mockMvc.perform(delete("admin/series/3"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(s.getId())))
                .andExpect(jsonPath("$[0].coverimage", is(s.getCoverimage())))
                .andExpect(jsonPath("$[0].description", is(s.getDescription())))
                .andExpect(jsonPath("$[0].genre", is(s.getGenre())))
                .andExpect(jsonPath("$[0].overallrating", is(s.getOverallrating())))
                .andExpect(jsonPath("$[0].rating.onestar", is(s.getRating().getOnestar())))
                .andExpect(jsonPath("$[0].rating.twostars", is(s.getRating().getTwostars())))
                .andExpect(jsonPath("$[0].rating.threestars", is(s.getRating().getThreestars())))
                .andExpect(jsonPath("$[0].rating.fourstars", is(s.getRating().getFourstars())))
                .andExpect(jsonPath("$[0].rating.fivestars", is(s.getRating().getFivestars())))
                .andExpect(jsonPath("$[0].releaseyear", is(s.getReleaseyear())))
                .andExpect(jsonPath("$[0].title", is(s.getTitle())))
                .andExpect(jsonPath("$[0].views", is(s.getViews())));
    }

    @Test
    public void getAllSeasonsOfSeriesWithIdWithSuccess() throws Exception{
        mockMvc.perform(get("admin/series/1/seasons"))
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
    public void addSeasonOfSeriesWithIdSuccess() throws Exception
    {
        Season s = new Season();
        s.setSeasonnumber(2);
        s.setSeriesid(series.get(0).getId());
        s.setReleaseyear(2017);
        s.setId("3");
        s.setCoverimage("/path_to_coverimgae.png");

        mockMvc.perform(post("/admin/series/1/seasons/")
                .content(json(s)).contentType(contentType)).andExpect(status().isCreated());

    }

    @Test
    public void getSeasonWithIdWithSeriesIdWithSuccess() throws Exception{
        mockMvc.perform(get("admin/series/2/seasons/2"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$.id", is(seasons.get(1).getId())))
                .andExpect(jsonPath("$.seriesid", is(seasons.get(1).getSeriesid())))
                .andExpect(jsonPath("$.releaseyear", is(seasons.get(1).getReleaseyear())))
                .andExpect(jsonPath("$.coverimage", is(seasons.get(1).getCoverimage())))
                .andExpect(jsonPath("$.numberofepisodes", is(seasons.get(1).getNumberofepisodes())))
                .andExpect(jsonPath("$.seasonnumber", is(seasons.get(1).getSeasonnumber())));
    }

    /*
    @Test
    public void deleteSeasonWithIdWithSeriesIdWithSuccess() throws Exception{
        Season s = new Season();
        s.setSeasonnumber(2);
        s.setSeriesid(series.get(0).getId());
        s.setReleaseyear(2017);
        s.setId("3");
        s.setCoverimage("/path_to_coverimgae.png");

        mockMvc.perform(delete("/admin/series/1/seasons/3"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(s.getId())))
                .andExpect(jsonPath("$[0].seriesid", is(s.getSeriesid())))
                .andExpect(jsonPath("$[0].releaseyear", is(s.getReleaseyear())))
                .andExpect(jsonPath("$[0].coverimage", is(s.getCoverimage())))
                .andExpect(jsonPath("$[0].numberofepisodes", is(s.getNumberofepisodes())))
                .andExpect(jsonPath("$[0].seasonnumber", is(s.getSeasonnumber())));
    }


    @Test
    public void getSeasonEpisodesWithSeasonIdWithSuccess() throws Exception{
        mockMvc.perform(get("admin/series/1/seasons/1/episodes"))
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
    public void getSeasonEpisodeWithIdWithSeasonIdWithSuccess() throws Exception{
        mockMvc.perform(get("admin/series/1/seasons/1/episodes/1"))
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
    public void addSeasonEpisodeWithSeasonIdWithSuccess() throws Exception{
        SeasonEpisode episode = new SeasonEpisode();
        episode.setViews(100);
        episode.setId("3");
        episode.setSeriesid("2");
        episode.setSeasonid("2");


        mockMvc.perform(post("/admin/series/2/seasons/2/episodes")
                .content(json(episode)).contentType(contentType))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$.id", is(episode.getId())))
                .andExpect(jsonPath("$.seriesid", is(episode.getSeriesid())))
                .andExpect(jsonPath("$.seasonid", is(episode.getSeasonid())))
                .andExpect(jsonPath("$.views", is(episode.getViews())));
    }

    @Test
    public void deleteSeasonEpisodeWithIdWithSuccess() throws Exception{
        mockMvc.perform(delete("/admin/series/2/seasons/2/episodes/2"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType));
    }

    @Test
    public void deleteCommentsForEpisodeWithSeasonIdWithSuccess()throws Exception{
        mockMvc.perform(delete("/admin/series/1/seasons/1/episodes/1/comments"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType));
    }

    @Test
    public void deleteCommentForSeasonEpisodeWithIdWithSeasonIdWithSuccess() throws Exception{
        mockMvc.perform(delete("/admin/series/1/seasons/1/episodes/1/comments"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType));
    }

    @Test
    public void deleteCommentsForSeasonWithSeriesIdWithSuccess() throws Exception{
        mockMvc.perform(delete("/admin/series/1/seasons/1/comments"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType));
    }

    @Test
    public void deleteCommentsForSeriesWithId() throws Exception{
        mockMvc.perform(delete("/admin/series/1/comments"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType));
    }
    @Test
    public void deleteAllSeriesWithSuccess() throws Exception{
        mockMvc.perform(delete("admin/series/"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType));
    }


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