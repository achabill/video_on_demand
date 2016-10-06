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
import vod.Repositories.MoviesRepository;
import vod.models.Movie;
import vod.models.Rating;

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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@SpringApplicationConfiguration(classes = Application.class)
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@EnableAutoConfiguration
public class MoviesControllerTest
{
    private MediaType contentType = new MediaType(MediaType.APPLICATION_JSON.getType(),
            MediaType.APPLICATION_JSON.getSubtype(),
            Charset.forName("utf8"));

    private MockMvc mockMvc;
    private HttpMessageConverter mappingJackson2HttpMessageConverter;
    private List<Movie> movies;

    @Autowired
    MoviesRepository repository;

    @Autowired
    WebApplicationContext webApplicationContext;

    @Autowired
    void setConverters(HttpMessageConverter<?>[] converters) {

        this.mappingJackson2HttpMessageConverter = Arrays.asList(converters).stream().filter(
                hmc -> hmc instanceof MappingJackson2HttpMessageConverter).findAny().get();

        Assert.assertNotNull("the JSON message converter must not be null",
                this.mappingJackson2HttpMessageConverter);
    }

    @Before
    public void setup() throws Exception
    {
        this.mockMvc = webAppContextSetup(webApplicationContext).build();
        this.repository.deleteAll();

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
        m2.setRating(r1);
        m2.setReleaseyear(2017);
        m2.setTitle("London has Fallen");
        m2.setViews(95000);
        m2.setGenre("thriller");
        m2.setVideofile("C:\\Users\\achab\\Music\\video\\western\\Chris Brown feat. Usher & Rick Ross - New Flame (Explicit Version).mp4");
        m2.setId("2");


        movies = new ArrayList<Movie>(){{add(m1); add(m2);}};
        repository.save(movies);
    }

    /**
     * Gets all movies in the database
     * @throws Exception
     */
    @Test
    public void getMovie() throws Exception
    {
        mockMvc.perform(get("/movies/"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$",hasSize(2)))
                .andExpect(jsonPath("$[0].id",is(this.movies.get(0).getId())))
                .andExpect(jsonPath("$[0].coverimage",is(this.movies.get(0).getCoverimage())))
                .andExpect(jsonPath("$[0].likes",is(this.movies.get(0).getLikes())))
                .andExpect(jsonPath("$[0].dislikes",is(this.movies.get(0).getDislikes())))
                .andExpect(jsonPath("$[0].description",is(this.movies.get(0).getDescription())))
                .andExpect(jsonPath("$[0].overallrating",is(this.movies.get(0).getOverallrating())))
                .andExpect(jsonPath("$[0].rating.onestar", is(this.movies.get(0).getRating().getOnestar())))
                .andExpect(jsonPath("$[0].rating.twostars", is(this.movies.get(0).getRating().getTwostars())))
                .andExpect(jsonPath("$[0].rating.threestars", is(this.movies.get(0).getRating().getThreestars())))
                .andExpect(jsonPath("$[0].rating.fourstars", is(this.movies.get(0).getRating().getFourstars())))
                .andExpect(jsonPath("$[0].rating.fivestars", is(this.movies.get(0).getRating().getFivestars())))
                .andExpect(jsonPath("$[0].title",is(this.movies.get(0).getTitle())))
                .andExpect(jsonPath("$[0].views",is(this.movies.get(0).getViews())))
                .andExpect(jsonPath("$[0].genre",is(this.movies.get(0).getGenre())))
                .andExpect(jsonPath("$[0].videofile",is(this.movies.get(0).getVideofile())))
                .andExpect(jsonPath("$[1].id",is(this.movies.get(1).getId())))
                .andExpect(jsonPath("$[1].coverimage",is(this.movies.get(1).getCoverimage())))
                .andExpect(jsonPath("$[1].likes",is(this.movies.get(1).getLikes())))
                .andExpect(jsonPath("$[1].dislikes",is(this.movies.get(1).getDislikes())))
                .andExpect(jsonPath("$[1].description",is(this.movies.get(1).getDescription())))
                .andExpect(jsonPath("$[1].overallrating",is(this.movies.get(1).getOverallrating())))
                .andExpect(jsonPath("$[1].rating.onestar", is(this.movies.get(1).getRating().getOnestar())))
                .andExpect(jsonPath("$[1].rating.twostars", is(this.movies.get(1).getRating().getTwostars())))
                .andExpect(jsonPath("$[1].rating.threestars", is(this.movies.get(1).getRating().getThreestars())))
                .andExpect(jsonPath("$[1].rating.fourstars", is(this.movies.get(1).getRating().getFourstars())))
                .andExpect(jsonPath("$[1].rating.fivestars", is(this.movies.get(1).getRating().getFivestars())))
                .andExpect(jsonPath("$[1].title",is(this.movies.get(1).getTitle())))
                .andExpect(jsonPath("$[1].views",is(this.movies.get(1).getViews())))
                .andExpect(jsonPath("$[1].genre",is(this.movies.get(1).getGenre())))
                .andExpect(jsonPath("$[1].videofile",is(this.movies.get(1).getVideofile())));

    }

    /**
     * Reads a page and assersts that its actually that page it read.
     */
    @Test
    public void readsFirstPageCorrectly() {

        Page<Movie> movies = repository.findAll(new PageRequest(0, 10));

        assertThat(movies.isFirst());
    }

    /**
     * Gets all movies starting on the first page.
     * @throws Exception The exception
     */
    @Test
    public void getMovie1() throws Exception
    {
        mockMvc.perform(get("/movies/?start=0"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$",hasSize(2)))
                .andExpect(jsonPath("$[0].id",is(this.movies.get(0).getId())))
                .andExpect(jsonPath("$[0].coverimage",is(this.movies.get(0).getCoverimage())))
                .andExpect(jsonPath("$[0].likes",is(this.movies.get(0).getLikes())))
                .andExpect(jsonPath("$[0].dislikes",is(this.movies.get(0).getDislikes())))
                .andExpect(jsonPath("$[0].description",is(this.movies.get(0).getDescription())))
                .andExpect(jsonPath("$[0].overallrating",is(this.movies.get(0).getOverallrating())))
                .andExpect(jsonPath("$[0].rating.onestar", is(this.movies.get(0).getRating().getOnestar())))
                .andExpect(jsonPath("$[0].rating.twostars", is(this.movies.get(0).getRating().getTwostars())))
                .andExpect(jsonPath("$[0].rating.threestars", is(this.movies.get(0).getRating().getThreestars())))
                .andExpect(jsonPath("$[0].rating.fourstars", is(this.movies.get(0).getRating().getFourstars())))
                .andExpect(jsonPath("$[0].rating.fivestars", is(this.movies.get(0).getRating().getFivestars())))
                .andExpect(jsonPath("$[0].title",is(this.movies.get(0).getTitle())))
                .andExpect(jsonPath("$[0].views",is(this.movies.get(0).getViews())))
                .andExpect(jsonPath("$[0].genre",is(this.movies.get(0).getGenre())))
                .andExpect(jsonPath("$[0].videofile",is(this.movies.get(0).getVideofile())))
                .andExpect(jsonPath("$[1].id",is(this.movies.get(1).getId())))
                .andExpect(jsonPath("$[1].coverimage",is(this.movies.get(1).getCoverimage())))
                .andExpect(jsonPath("$[1].likes",is(this.movies.get(1).getLikes())))
                .andExpect(jsonPath("$[1].dislikes",is(this.movies.get(1).getDislikes())))
                .andExpect(jsonPath("$[1].description",is(this.movies.get(1).getDescription())))
                .andExpect(jsonPath("$[1].overallrating",is(this.movies.get(1).getOverallrating())))
                .andExpect(jsonPath("$[1].rating.onestar", is(this.movies.get(1).getRating().getOnestar())))
                .andExpect(jsonPath("$[1].rating.twostars", is(this.movies.get(1).getRating().getTwostars())))
                .andExpect(jsonPath("$[1].rating.threestars", is(this.movies.get(1).getRating().getThreestars())))
                .andExpect(jsonPath("$[1].rating.fourstars", is(this.movies.get(1).getRating().getFourstars())))
                .andExpect(jsonPath("$[1].rating.fivestars", is(this.movies.get(1).getRating().getFivestars())))
                .andExpect(jsonPath("$[1].title",is(this.movies.get(1).getTitle())))
                .andExpect(jsonPath("$[1].views",is(this.movies.get(1).getViews())))
                .andExpect(jsonPath("$[1].genre",is(this.movies.get(1).getGenre())))
                .andExpect(jsonPath("$[1].videofile",is(this.movies.get(1).getVideofile())));
    }

    /**
     * Gets 1 movie on the first page.
     * @throws Exception
     */
    @Test
    public void getMovie2() throws Exception
    {
        mockMvc.perform(get("/movies/?start=0&count=1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$",hasSize(1)))
                .andExpect(jsonPath("$[0].id",is(this.movies.get(0).getId())))
                .andExpect(jsonPath("$[0].coverimage",is(this.movies.get(0).getCoverimage())))
                .andExpect(jsonPath("$[0].likes",is(this.movies.get(0).getLikes())))
                .andExpect(jsonPath("$[0].dislikes",is(this.movies.get(0).getDislikes())))
                .andExpect(jsonPath("$[0].description",is(this.movies.get(0).getDescription())))
                .andExpect(jsonPath("$[0].overallrating",is(this.movies.get(0).getOverallrating())))
                .andExpect(jsonPath("$[0].rating.onestar", is(this.movies.get(0).getRating().getOnestar())))
                .andExpect(jsonPath("$[0].rating.twostars", is(this.movies.get(0).getRating().getTwostars())))
                .andExpect(jsonPath("$[0].rating.threestars", is(this.movies.get(0).getRating().getThreestars())))
                .andExpect(jsonPath("$[0].rating.fourstars", is(this.movies.get(0).getRating().getFourstars())))
                .andExpect(jsonPath("$[0].rating.fivestars", is(this.movies.get(0).getRating().getFivestars())))
                .andExpect(jsonPath("$[0].title",is(this.movies.get(0).getTitle())))
                .andExpect(jsonPath("$[0].views",is(this.movies.get(0).getViews())))
                .andExpect(jsonPath("$[0].genre",is(this.movies.get(0).getGenre())))
                .andExpect(jsonPath("$[0].videofile",is(this.movies.get(0).getVideofile())));
    }

    /**
     * Gets 3 movies on the first page.
     * Will return 2 movies since only 2 movies are in the database.
     * @throws Exception
     */
    @Test
    public void getMovie22() throws Exception
    {
        mockMvc.perform(get("/movies/?start=0&count=3"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$",hasSize(2)))
                .andExpect(jsonPath("$[0].id",is(this.movies.get(0).getId())))
                .andExpect(jsonPath("$[0].coverimage",is(this.movies.get(0).getCoverimage())))
                .andExpect(jsonPath("$[0].likes",is(this.movies.get(0).getLikes())))
                .andExpect(jsonPath("$[0].dislikes",is(this.movies.get(0).getDislikes())))
                .andExpect(jsonPath("$[0].description",is(this.movies.get(0).getDescription())))
                .andExpect(jsonPath("$[0].overallrating",is(this.movies.get(0).getOverallrating())))
                .andExpect(jsonPath("$[0].rating.onestar", is(this.movies.get(0).getRating().getOnestar())))
                .andExpect(jsonPath("$[0].rating.twostars", is(this.movies.get(0).getRating().getTwostars())))
                .andExpect(jsonPath("$[0].rating.threestars", is(this.movies.get(0).getRating().getThreestars())))
                .andExpect(jsonPath("$[0].rating.fourstars", is(this.movies.get(0).getRating().getFourstars())))
                .andExpect(jsonPath("$[0].rating.fivestars", is(this.movies.get(0).getRating().getFivestars())))
                .andExpect(jsonPath("$[0].title",is(this.movies.get(0).getTitle())))
                .andExpect(jsonPath("$[0].views",is(this.movies.get(0).getViews())))
                .andExpect(jsonPath("$[0].genre",is(this.movies.get(0).getGenre())))
                .andExpect(jsonPath("$[0].videofile",is(this.movies.get(0).getVideofile())))
                .andExpect(jsonPath("$[1].id",is(this.movies.get(1).getId())))
                .andExpect(jsonPath("$[1].coverimage",is(this.movies.get(1).getCoverimage())))
                .andExpect(jsonPath("$[1].likes",is(this.movies.get(1).getLikes())))
                .andExpect(jsonPath("$[1].dislikes",is(this.movies.get(1).getDislikes())))
                .andExpect(jsonPath("$[1].description",is(this.movies.get(1).getDescription())))
                .andExpect(jsonPath("$[1].overallrating",is(this.movies.get(1).getOverallrating())))
                .andExpect(jsonPath("$[1].rating.onestar", is(this.movies.get(1).getRating().getOnestar())))
                .andExpect(jsonPath("$[1].rating.twostars", is(this.movies.get(1).getRating().getTwostars())))
                .andExpect(jsonPath("$[1].rating.threestars", is(this.movies.get(1).getRating().getThreestars())))
                .andExpect(jsonPath("$[1].rating.fourstars", is(this.movies.get(1).getRating().getFourstars())))
                .andExpect(jsonPath("$[1].rating.fivestars", is(this.movies.get(1).getRating().getFivestars())))
                .andExpect(jsonPath("$[1].title",is(this.movies.get(1).getTitle())))
                .andExpect(jsonPath("$[1].views",is(this.movies.get(1).getViews())))
                .andExpect(jsonPath("$[1].genre",is(this.movies.get(1).getGenre())))
                .andExpect(jsonPath("$[1].videofile",is(this.movies.get(1).getVideofile())));
    }

    /**
     * Gets 1 movie one the 2nd page.
     * @throws Exception
     */
    @Test
    public void getMovie23() throws Exception {
        mockMvc.perform(get("/movies/?start=1&count=1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$",hasSize(1)))
                .andExpect(jsonPath("$[0].id",is(this.movies.get(1).getId())))
                .andExpect(jsonPath("$[0].coverimage",is(this.movies.get(1).getCoverimage())))
                .andExpect(jsonPath("$[0].likes",is(this.movies.get(1).getLikes())))
                .andExpect(jsonPath("$[0].dislikes",is(this.movies.get(1).getDislikes())))
                .andExpect(jsonPath("$[0].description",is(this.movies.get(1).getDescription())))
                .andExpect(jsonPath("$[0].overallrating",is(this.movies.get(1).getOverallrating())))
                .andExpect(jsonPath("$[0].rating.onestar", is(this.movies.get(1).getRating().getOnestar())))
                .andExpect(jsonPath("$[0].rating.twostars", is(this.movies.get(1).getRating().getTwostars())))
                .andExpect(jsonPath("$[0].rating.threestars", is(this.movies.get(1).getRating().getThreestars())))
                .andExpect(jsonPath("$[0].rating.fourstars", is(this.movies.get(1).getRating().getFourstars())))
                .andExpect(jsonPath("$[0].rating.fivestars", is(this.movies.get(1).getRating().getFivestars())))
                .andExpect(jsonPath("$[0].title",is(this.movies.get(1).getTitle())))
                .andExpect(jsonPath("$[0].views",is(this.movies.get(1).getViews())))
                .andExpect(jsonPath("$[0].genre",is(this.movies.get(1).getGenre())))
                .andExpect(jsonPath("$[0].videofile",is(this.movies.get(1).getVideofile())));
    }

    /**
     * Gets 1 movie on the 3rd page.
     * Will return empty list since only 2 movies are in the database.
     * @throws Exception
     */
    @Test
    public void getMovie4() throws Exception
    {
        mockMvc.perform(get("/movies/?start=2&count=1")
                .content(json(new Movie())).contentType(contentType)).andExpect(status().isOk());
    }

    /**
     * Gets 3 movies on the 2nd page.
     * But since only 2 movies are in the database, will return 1 page. Hence, no data.
     * @throws Exception
     */
    @Test
    public void getMovie41() throws Exception
    {
        mockMvc.perform(get("/movies/?start=1&count=3")
                .content(json(new Movie())).contentType(contentType)).andExpect(status().isOk());
    }

    /**
     * Checks invalid number format for the start property
     * @throws Exception
     */
    @Test
    public void InvalidNumberPropertyException() throws Exception
    {
        mockMvc.perform(get("/movies/?start=expect_valid_number_not_string")
                .content(json(new Movie())).contentType(contentType)).andExpect(status().isBadRequest());
    }

    /**
     * Checks invalid number format for the count property.
     * @throws Exception
     */
    @Test
    public void InvalidNumberPropertyException1() throws Exception
    {
        mockMvc.perform(get("/movies/?count=expect_valid_number_not_string")
                .content(json(new Movie())).contentType(contentType)).andExpect(status().isBadRequest());
    }

    /**
     * Gets all data starting from second page.
     * @throws Exception
     */
    @Test
    public void getMovie14() throws Exception
    {
        mockMvc.perform(get("/movies/?start=2")
                .content(json(new Movie())).contentType(contentType)).andExpect(status().isOk());
    }

    /**
     * Gets 1 movie on the 1st page. 2 pages exist since there are 2 movies in the db.
     * @throws Exception
     */
    @Test
    public void getMovie5() throws Exception
    {
        mockMvc.perform(get("/movies/?count=1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$",hasSize(1)))
                .andExpect(jsonPath("$[0].id",is(this.movies.get(0).getId())))
                .andExpect(jsonPath("$[0].coverimage",is(this.movies.get(0).getCoverimage())))
                .andExpect(jsonPath("$[0].likes",is(this.movies.get(0).getLikes())))
                .andExpect(jsonPath("$[0].dislikes",is(this.movies.get(0).getDislikes())))
                .andExpect(jsonPath("$[0].description",is(this.movies.get(0).getDescription())))
                .andExpect(jsonPath("$[0].overallrating",is(this.movies.get(0).getOverallrating())))
                .andExpect(jsonPath("$[0].rating.onestar", is(this.movies.get(0).getRating().getOnestar())))
                .andExpect(jsonPath("$[0].rating.twostars", is(this.movies.get(0).getRating().getTwostars())))
                .andExpect(jsonPath("$[0].rating.threestars", is(this.movies.get(0).getRating().getThreestars())))
                .andExpect(jsonPath("$[0].rating.fourstars", is(this.movies.get(0).getRating().getFourstars())))
                .andExpect(jsonPath("$[0].rating.fivestars", is(this.movies.get(0).getRating().getFivestars())))
                .andExpect(jsonPath("$[0].title",is(this.movies.get(0).getTitle())))
                .andExpect(jsonPath("$[0].views",is(this.movies.get(0).getViews())))
                .andExpect(jsonPath("$[0].genre",is(this.movies.get(0).getGenre())))
                .andExpect(jsonPath("$[0].videofile",is(this.movies.get(0).getVideofile())));
    }

    /**
     * Gets 10 movies from from page 0.
     * Returns all 2 movies since only 2 movies are in the db.
     * @throws Exception
     */
    @Test
    public void getMovie6() throws Exception
    {
        mockMvc.perform(get("/movies/?count=10"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$",hasSize(2)))
                .andExpect(jsonPath("$[0].id",is(this.movies.get(0).getId())))
                .andExpect(jsonPath("$[0].coverimage",is(this.movies.get(0).getCoverimage())))
                .andExpect(jsonPath("$[0].likes",is(this.movies.get(0).getLikes())))
                .andExpect(jsonPath("$[0].dislikes",is(this.movies.get(0).getDislikes())))
                .andExpect(jsonPath("$[0].description",is(this.movies.get(0).getDescription())))
                .andExpect(jsonPath("$[0].overallrating",is(this.movies.get(0).getOverallrating())))
                .andExpect(jsonPath("$[0].rating.onestar", is(this.movies.get(0).getRating().getOnestar())))
                .andExpect(jsonPath("$[0].rating.twostars", is(this.movies.get(0).getRating().getTwostars())))
                .andExpect(jsonPath("$[0].rating.threestars", is(this.movies.get(0).getRating().getThreestars())))
                .andExpect(jsonPath("$[0].rating.fourstars", is(this.movies.get(0).getRating().getFourstars())))
                .andExpect(jsonPath("$[0].rating.fivestars", is(this.movies.get(0).getRating().getFivestars())))
                .andExpect(jsonPath("$[0].title",is(this.movies.get(0).getTitle())))
                .andExpect(jsonPath("$[0].views",is(this.movies.get(0).getViews())))
                .andExpect(jsonPath("$[0].genre",is(this.movies.get(0).getGenre())))
                .andExpect(jsonPath("$[0].videofile",is(this.movies.get(0).getVideofile())))
                .andExpect(jsonPath("$[1].id",is(this.movies.get(1).getId())))
                .andExpect(jsonPath("$[1].coverimage",is(this.movies.get(1).getCoverimage())))
                .andExpect(jsonPath("$[1].likes",is(this.movies.get(1).getLikes())))
                .andExpect(jsonPath("$[1].dislikes",is(this.movies.get(1).getDislikes())))
                .andExpect(jsonPath("$[1].description",is(this.movies.get(1).getDescription())))
                .andExpect(jsonPath("$[1].overallrating",is(this.movies.get(1).getOverallrating())))
                .andExpect(jsonPath("$[1].rating.onestar", is(this.movies.get(1).getRating().getOnestar())))
                .andExpect(jsonPath("$[1].rating.twostars", is(this.movies.get(1).getRating().getTwostars())))
                .andExpect(jsonPath("$[1].rating.threestars", is(this.movies.get(1).getRating().getThreestars())))
                .andExpect(jsonPath("$[1].rating.fourstars", is(this.movies.get(1).getRating().getFourstars())))
                .andExpect(jsonPath("$[1].rating.fivestars", is(this.movies.get(1).getRating().getFivestars())))
                .andExpect(jsonPath("$[1].title",is(this.movies.get(1).getTitle())))
                .andExpect(jsonPath("$[1].views",is(this.movies.get(1).getViews())))
                .andExpect(jsonPath("$[1].genre",is(this.movies.get(1).getGenre())))
                .andExpect(jsonPath("$[1].videofile",is(this.movies.get(1).getVideofile())));
    }

    /**
     * Gets all movies with genre=thriller
     * @throws Exception
     */
    @Test
    public void getMovie7() throws Exception
    {
        mockMvc.perform(get("/movies/?genre=thriller"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$",hasSize(2)))
                .andExpect(jsonPath("$[0].id",is(this.movies.get(0).getId())))
                .andExpect(jsonPath("$[0].coverimage",is(this.movies.get(0).getCoverimage())))
                .andExpect(jsonPath("$[0].likes",is(this.movies.get(0).getLikes())))
                .andExpect(jsonPath("$[0].dislikes",is(this.movies.get(0).getDislikes())))
                .andExpect(jsonPath("$[0].description",is(this.movies.get(0).getDescription())))
                .andExpect(jsonPath("$[0].overallrating",is(this.movies.get(0).getOverallrating())))
                .andExpect(jsonPath("$[0].rating.onestar", is(this.movies.get(0).getRating().getOnestar())))
                .andExpect(jsonPath("$[0].rating.twostars", is(this.movies.get(0).getRating().getTwostars())))
                .andExpect(jsonPath("$[0].rating.threestars", is(this.movies.get(0).getRating().getThreestars())))
                .andExpect(jsonPath("$[0].rating.fourstars", is(this.movies.get(0).getRating().getFourstars())))
                .andExpect(jsonPath("$[0].rating.fivestars", is(this.movies.get(0).getRating().getFivestars())))
                .andExpect(jsonPath("$[0].title",is(this.movies.get(0).getTitle())))
                .andExpect(jsonPath("$[0].views",is(this.movies.get(0).getViews())))
                .andExpect(jsonPath("$[0].genre",is(this.movies.get(0).getGenre())))
                .andExpect(jsonPath("$[0].videofile",is(this.movies.get(0).getVideofile())))
                .andExpect(jsonPath("$[1].id",is(this.movies.get(1).getId())))
                .andExpect(jsonPath("$[1].coverimage",is(this.movies.get(1).getCoverimage())))
                .andExpect(jsonPath("$[1].likes",is(this.movies.get(1).getLikes())))
                .andExpect(jsonPath("$[1].dislikes",is(this.movies.get(1).getDislikes())))
                .andExpect(jsonPath("$[1].description",is(this.movies.get(1).getDescription())))
                .andExpect(jsonPath("$[1].overallrating",is(this.movies.get(1).getOverallrating())))
                .andExpect(jsonPath("$[1].rating.onestar", is(this.movies.get(1).getRating().getOnestar())))
                .andExpect(jsonPath("$[1].rating.twostars", is(this.movies.get(1).getRating().getTwostars())))
                .andExpect(jsonPath("$[1].rating.threestars", is(this.movies.get(1).getRating().getThreestars())))
                .andExpect(jsonPath("$[1].rating.fourstars", is(this.movies.get(1).getRating().getFourstars())))
                .andExpect(jsonPath("$[1].rating.fivestars", is(this.movies.get(1).getRating().getFivestars())))
                .andExpect(jsonPath("$[1].title",is(this.movies.get(1).getTitle())))
                .andExpect(jsonPath("$[1].views",is(this.movies.get(1).getViews())))
                .andExpect(jsonPath("$[1].genre",is(this.movies.get(1).getGenre())))
                .andExpect(jsonPath("$[1].videofile",is(this.movies.get(1).getVideofile())));
    }

    /**
     * Gets all movies with genre=sex.
     * But no such genre exists. Hence, empty result.
     * @throws Exception
     */
    @Test
    public void invalidGenre() throws Exception
    {
        mockMvc.perform(get("/movies/?genre=sex")
                .content(json(new Movie())).contentType(contentType)).andExpect(status().isBadRequest());
    }

    /**
     * Gets all movies sorting them by id in ascending order. (defaults)
     * @throws Exception
     */
    @Test
    public void getMovie8() throws Exception
    {
        mockMvc.perform(get("/movies/?sort=true"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$",hasSize(2)))
                .andExpect(jsonPath("$[0].id",is(this.movies.get(0).getId())))
                .andExpect(jsonPath("$[0].coverimage",is(this.movies.get(0).getCoverimage())))
                .andExpect(jsonPath("$[0].likes",is(this.movies.get(0).getLikes())))
                .andExpect(jsonPath("$[0].dislikes",is(this.movies.get(0).getDislikes())))
                .andExpect(jsonPath("$[0].description",is(this.movies.get(0).getDescription())))
                .andExpect(jsonPath("$[0].overallrating",is(this.movies.get(0).getOverallrating())))
                .andExpect(jsonPath("$[0].rating.onestar", is(this.movies.get(0).getRating().getOnestar())))
                .andExpect(jsonPath("$[0].rating.twostars", is(this.movies.get(0).getRating().getTwostars())))
                .andExpect(jsonPath("$[0].rating.threestars", is(this.movies.get(0).getRating().getThreestars())))
                .andExpect(jsonPath("$[0].rating.fourstars", is(this.movies.get(0).getRating().getFourstars())))
                .andExpect(jsonPath("$[0].rating.fivestars", is(this.movies.get(0).getRating().getFivestars())))
                .andExpect(jsonPath("$[0].title",is(this.movies.get(0).getTitle())))
                .andExpect(jsonPath("$[0].views",is(this.movies.get(0).getViews())))
                .andExpect(jsonPath("$[0].genre",is(this.movies.get(0).getGenre())))
                .andExpect(jsonPath("$[0].videofile",is(this.movies.get(0).getVideofile())))
                .andExpect(jsonPath("$[1].id",is(this.movies.get(1).getId())))
                .andExpect(jsonPath("$[1].coverimage",is(this.movies.get(1).getCoverimage())))
                .andExpect(jsonPath("$[1].likes",is(this.movies.get(1).getLikes())))
                .andExpect(jsonPath("$[1].dislikes",is(this.movies.get(1).getDislikes())))
                .andExpect(jsonPath("$[1].description",is(this.movies.get(1).getDescription())))
                .andExpect(jsonPath("$[1].overallrating",is(this.movies.get(1).getOverallrating())))
                .andExpect(jsonPath("$[1].rating.onestar", is(this.movies.get(1).getRating().getOnestar())))
                .andExpect(jsonPath("$[1].rating.twostars", is(this.movies.get(1).getRating().getTwostars())))
                .andExpect(jsonPath("$[1].rating.threestars", is(this.movies.get(1).getRating().getThreestars())))
                .andExpect(jsonPath("$[1].rating.fourstars", is(this.movies.get(1).getRating().getFourstars())))
                .andExpect(jsonPath("$[1].rating.fivestars", is(this.movies.get(1).getRating().getFivestars())))
                .andExpect(jsonPath("$[1].title",is(this.movies.get(1).getTitle())))
                .andExpect(jsonPath("$[1].views",is(this.movies.get(1).getViews())))
                .andExpect(jsonPath("$[1].genre",is(this.movies.get(1).getGenre())))
                .andExpect(jsonPath("$[1].videofile",is(this.movies.get(1).getVideofile())));
    }

    /**
     * Gets all movies sorting them by id and in descending order
     * @throws Exception
     */
    @Test
    public void getMovie10() throws Exception
    {
        mockMvc.perform(get("/movies/?sort=true&order=desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$",hasSize(2)))
                .andExpect(jsonPath("$[0].id",is(this.movies.get(1).getId())))
                .andExpect(jsonPath("$[0].coverimage",is(this.movies.get(1).getCoverimage())))
                .andExpect(jsonPath("$[0].likes",is(this.movies.get(1).getLikes())))
                .andExpect(jsonPath("$[0].dislikes",is(this.movies.get(1).getDislikes())))
                .andExpect(jsonPath("$[0].description",is(this.movies.get(1).getDescription())))
                .andExpect(jsonPath("$[0].overallrating",is(this.movies.get(1).getOverallrating())))
                .andExpect(jsonPath("$[0].rating.onestar", is(this.movies.get(1).getRating().getOnestar())))
                .andExpect(jsonPath("$[0].rating.twostars", is(this.movies.get(1).getRating().getTwostars())))
                .andExpect(jsonPath("$[0].rating.threestars", is(this.movies.get(1).getRating().getThreestars())))
                .andExpect(jsonPath("$[0].rating.fourstars", is(this.movies.get(1).getRating().getFourstars())))
                .andExpect(jsonPath("$[0].rating.fivestars", is(this.movies.get(1).getRating().getFivestars())))
                .andExpect(jsonPath("$[0].title",is(this.movies.get(1).getTitle())))
                .andExpect(jsonPath("$[0].views",is(this.movies.get(1).getViews())))
                .andExpect(jsonPath("$[0].genre",is(this.movies.get(1).getGenre())))
                .andExpect(jsonPath("$[0].videofile",is(this.movies.get(1).getVideofile())))
                .andExpect(jsonPath("$[1].id",is(this.movies.get(0).getId())))
                .andExpect(jsonPath("$[1].coverimage",is(this.movies.get(0).getCoverimage())))
                .andExpect(jsonPath("$[1].likes",is(this.movies.get(0).getLikes())))
                .andExpect(jsonPath("$[1].dislikes",is(this.movies.get(0).getDislikes())))
                .andExpect(jsonPath("$[1].description",is(this.movies.get(0).getDescription())))
                .andExpect(jsonPath("$[1].overallrating",is(this.movies.get(0).getOverallrating())))
                .andExpect(jsonPath("$[1].rating.onestar", is(this.movies.get(0).getRating().getOnestar())))
                .andExpect(jsonPath("$[1].rating.twostars", is(this.movies.get(0).getRating().getTwostars())))
                .andExpect(jsonPath("$[1].rating.threestars", is(this.movies.get(0).getRating().getThreestars())))
                .andExpect(jsonPath("$[1].rating.fourstars", is(this.movies.get(0).getRating().getFourstars())))
                .andExpect(jsonPath("$[1].rating.fivestars", is(this.movies.get(0).getRating().getFivestars())))
                .andExpect(jsonPath("$[1].title",is(this.movies.get(0).getTitle())))
                .andExpect(jsonPath("$[1].views",is(this.movies.get(0).getViews())))
                .andExpect(jsonPath("$[1].genre",is(this.movies.get(0).getGenre())))
                .andExpect(jsonPath("$[1].videofile",is(this.movies.get(0).getVideofile())));
    }

    /**
     * Gets all movies sorting them by id and in ascending order.
     * @throws Exception
     */
    @Test
    public void getMovie100() throws Exception
    {
        mockMvc.perform(get("/movies/?sort=true&order=asc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$",hasSize(2)))
                .andExpect(jsonPath("$[0].id",is(this.movies.get(0).getId())))
                .andExpect(jsonPath("$[0].coverimage",is(this.movies.get(0).getCoverimage())))
                .andExpect(jsonPath("$[0].likes",is(this.movies.get(0).getLikes())))
                .andExpect(jsonPath("$[0].dislikes",is(this.movies.get(0).getDislikes())))
                .andExpect(jsonPath("$[0].description",is(this.movies.get(0).getDescription())))
                .andExpect(jsonPath("$[0].overallrating",is(this.movies.get(0).getOverallrating())))
                .andExpect(jsonPath("$[0].rating.onestar", is(this.movies.get(0).getRating().getOnestar())))
                .andExpect(jsonPath("$[0].rating.twostars", is(this.movies.get(0).getRating().getTwostars())))
                .andExpect(jsonPath("$[0].rating.threestars", is(this.movies.get(0).getRating().getThreestars())))
                .andExpect(jsonPath("$[0].rating.fourstars", is(this.movies.get(0).getRating().getFourstars())))
                .andExpect(jsonPath("$[0].rating.fivestars", is(this.movies.get(0).getRating().getFivestars())))
                .andExpect(jsonPath("$[0].title",is(this.movies.get(0).getTitle())))
                .andExpect(jsonPath("$[0].views",is(this.movies.get(0).getViews())))
                .andExpect(jsonPath("$[0].genre",is(this.movies.get(0).getGenre())))
                .andExpect(jsonPath("$[0].videofile",is(this.movies.get(0).getVideofile())))
                .andExpect(jsonPath("$[1].id",is(this.movies.get(1).getId())))
                .andExpect(jsonPath("$[1].coverimage",is(this.movies.get(1).getCoverimage())))
                .andExpect(jsonPath("$[1].likes",is(this.movies.get(1).getLikes())))
                .andExpect(jsonPath("$[1].dislikes",is(this.movies.get(1).getDislikes())))
                .andExpect(jsonPath("$[1].description",is(this.movies.get(1).getDescription())))
                .andExpect(jsonPath("$[1].overallrating",is(this.movies.get(1).getOverallrating())))
                .andExpect(jsonPath("$[1].rating.onestar", is(this.movies.get(1).getRating().getOnestar())))
                .andExpect(jsonPath("$[1].rating.twostars", is(this.movies.get(1).getRating().getTwostars())))
                .andExpect(jsonPath("$[1].rating.threestars", is(this.movies.get(1).getRating().getThreestars())))
                .andExpect(jsonPath("$[1].rating.fourstars", is(this.movies.get(1).getRating().getFourstars())))
                .andExpect(jsonPath("$[1].rating.fivestars", is(this.movies.get(1).getRating().getFivestars())))
                .andExpect(jsonPath("$[1].title",is(this.movies.get(1).getTitle())))
                .andExpect(jsonPath("$[1].views",is(this.movies.get(1).getViews())))
                .andExpect(jsonPath("$[1].genre",is(this.movies.get(1).getGenre())))
                .andExpect(jsonPath("$[1].videofile",is(this.movies.get(1).getVideofile())));
    }

    /**
     * Invalid order. either 'asc' or 'desc'
     * @throws Exception
     */
    @Test
    public void invalidOrderParameter() throws Exception
    {
        mockMvc.perform(get("/movies/?sort=true&order=descending")
                .content(json(new Movie())).contentType(contentType)).andExpect(status().isBadRequest());
    }

    /**
     * Invalid sort property. 'description' is not a sortable property on movies.
     * @throws Exception
     */
    @Test
    public void invalidPropertyParameter() throws Exception
    {
        mockMvc.perform(get("/movies/?sort=true&property=description")
                .content(json(new Movie())).contentType(contentType)).andExpect(status().isBadRequest());
    }

    /**
     * Gets all movies sorting them in descending order and ordering them by overallrating
     * @throws Exception
     */
    @Test
    public void getMovie11() throws Exception
    {
        mockMvc.perform(get("/movies/?sort=true&order=desc&property=overallrating"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$",hasSize(2)))
                .andExpect(jsonPath("$[0].id",is(this.movies.get(1).getId())))
                .andExpect(jsonPath("$[0].coverimage",is(this.movies.get(1).getCoverimage())))
                .andExpect(jsonPath("$[0].likes",is(this.movies.get(1).getLikes())))
                .andExpect(jsonPath("$[0].dislikes",is(this.movies.get(1).getDislikes())))
                .andExpect(jsonPath("$[0].description",is(this.movies.get(1).getDescription())))
                .andExpect(jsonPath("$[0].overallrating",is(this.movies.get(1).getOverallrating())))
                .andExpect(jsonPath("$[0].rating.onestar", is(this.movies.get(1).getRating().getOnestar())))
                .andExpect(jsonPath("$[0].rating.twostars", is(this.movies.get(1).getRating().getTwostars())))
                .andExpect(jsonPath("$[0].rating.threestars", is(this.movies.get(1).getRating().getThreestars())))
                .andExpect(jsonPath("$[0].rating.fourstars", is(this.movies.get(1).getRating().getFourstars())))
                .andExpect(jsonPath("$[0].rating.fivestars", is(this.movies.get(1).getRating().getFivestars())))
                .andExpect(jsonPath("$[0].title",is(this.movies.get(1).getTitle())))
                .andExpect(jsonPath("$[0].views",is(this.movies.get(1).getViews())))
                .andExpect(jsonPath("$[0].genre",is(this.movies.get(1).getGenre())))
                .andExpect(jsonPath("$[0].videofile",is(this.movies.get(1).getVideofile())))
                .andExpect(jsonPath("$[1].id",is(this.movies.get(0).getId())))
                .andExpect(jsonPath("$[1].coverimage",is(this.movies.get(0).getCoverimage())))
                .andExpect(jsonPath("$[1].likes",is(this.movies.get(0).getLikes())))
                .andExpect(jsonPath("$[1].dislikes",is(this.movies.get(0).getDislikes())))
                .andExpect(jsonPath("$[1].description",is(this.movies.get(0).getDescription())))
                .andExpect(jsonPath("$[1].overallrating",is(this.movies.get(0).getOverallrating())))
                .andExpect(jsonPath("$[1].rating.onestar", is(this.movies.get(0).getRating().getOnestar())))
                .andExpect(jsonPath("$[1].rating.twostars", is(this.movies.get(0).getRating().getTwostars())))
                .andExpect(jsonPath("$[1].rating.threestars", is(this.movies.get(0).getRating().getThreestars())))
                .andExpect(jsonPath("$[1].rating.fourstars", is(this.movies.get(0).getRating().getFourstars())))
                .andExpect(jsonPath("$[1].rating.fivestars", is(this.movies.get(0).getRating().getFivestars())))
                .andExpect(jsonPath("$[1].title",is(this.movies.get(0).getTitle())))
                .andExpect(jsonPath("$[1].views",is(this.movies.get(0).getViews())))
                .andExpect(jsonPath("$[1].genre",is(this.movies.get(0).getGenre())))
                .andExpect(jsonPath("$[1].videofile",is(this.movies.get(0).getVideofile())));
    }

    /**
     * Sorts all movies in descending order without sort parameter.
     * Without sort parameter, all sorting options are set to default.
     * @throws Exception
     */
    @Test
    public void getMovie17() throws Exception
    {
        mockMvc.perform(get("/movies/?order=desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$",hasSize(2)))
                .andExpect(jsonPath("$[0].id",is(this.movies.get(0).getId())))
                .andExpect(jsonPath("$[0].coverimage",is(this.movies.get(0).getCoverimage())))
                .andExpect(jsonPath("$[0].likes",is(this.movies.get(0).getLikes())))
                .andExpect(jsonPath("$[0].dislikes",is(this.movies.get(0).getDislikes())))
                .andExpect(jsonPath("$[0].description",is(this.movies.get(0).getDescription())))
                .andExpect(jsonPath("$[0].overallrating",is(this.movies.get(0).getOverallrating())))
                .andExpect(jsonPath("$[0].rating.onestar", is(this.movies.get(0).getRating().getOnestar())))
                .andExpect(jsonPath("$[0].rating.twostars", is(this.movies.get(0).getRating().getTwostars())))
                .andExpect(jsonPath("$[0].rating.threestars", is(this.movies.get(0).getRating().getThreestars())))
                .andExpect(jsonPath("$[0].rating.fourstars", is(this.movies.get(0).getRating().getFourstars())))
                .andExpect(jsonPath("$[0].rating.fivestars", is(this.movies.get(0).getRating().getFivestars())))
                .andExpect(jsonPath("$[0].title",is(this.movies.get(0).getTitle())))
                .andExpect(jsonPath("$[0].views",is(this.movies.get(0).getViews())))
                .andExpect(jsonPath("$[0].genre",is(this.movies.get(0).getGenre())))
                .andExpect(jsonPath("$[0].videofile",is(this.movies.get(0).getVideofile())))
                .andExpect(jsonPath("$[1].id",is(this.movies.get(1).getId())))
                .andExpect(jsonPath("$[1].coverimage",is(this.movies.get(1).getCoverimage())))
                .andExpect(jsonPath("$[1].likes",is(this.movies.get(1).getLikes())))
                .andExpect(jsonPath("$[1].dislikes",is(this.movies.get(1).getDislikes())))
                .andExpect(jsonPath("$[1].description",is(this.movies.get(1).getDescription())))
                .andExpect(jsonPath("$[1].overallrating",is(this.movies.get(1).getOverallrating())))
                .andExpect(jsonPath("$[1].rating.onestar", is(this.movies.get(1).getRating().getOnestar())))
                .andExpect(jsonPath("$[1].rating.twostars", is(this.movies.get(1).getRating().getTwostars())))
                .andExpect(jsonPath("$[1].rating.threestars", is(this.movies.get(1).getRating().getThreestars())))
                .andExpect(jsonPath("$[1].rating.fourstars", is(this.movies.get(1).getRating().getFourstars())))
                .andExpect(jsonPath("$[1].rating.fivestars", is(this.movies.get(1).getRating().getFivestars())))
                .andExpect(jsonPath("$[1].title",is(this.movies.get(1).getTitle())))
                .andExpect(jsonPath("$[1].views",is(this.movies.get(1).getViews())))
                .andExpect(jsonPath("$[1].genre",is(this.movies.get(1).getGenre())))
                .andExpect(jsonPath("$[1].videofile",is(this.movies.get(1).getVideofile())));
    }
    /**
     * Gets similar movies to the movie with id specified. Obviusly the movie with id is not included
     * in the returned list.
     * Returns the other movie. (id = 2)
     * @throws Exception
     */
    @Test
    public void getSimilarMovies() throws Exception
    {
        mockMvc.perform(get("/movies/1/similar"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$",hasSize(2)))
                .andExpect(jsonPath("$[0].id",is(this.movies.get(0).getId())))
                .andExpect(jsonPath("$[0].coverimage",is(this.movies.get(0).getCoverimage())))
                .andExpect(jsonPath("$[0].likes",is(this.movies.get(0).getLikes())))
                .andExpect(jsonPath("$[0].dislikes",is(this.movies.get(0).getDislikes())))
                .andExpect(jsonPath("$[0].description",is(this.movies.get(0).getDescription())))
                .andExpect(jsonPath("$[0].overallrating",is(this.movies.get(0).getOverallrating())))
                .andExpect(jsonPath("$[0].rating.onestar", is(this.movies.get(0).getRating().getOnestar())))
                .andExpect(jsonPath("$[0].rating.twostars", is(this.movies.get(0).getRating().getTwostars())))
                .andExpect(jsonPath("$[0].rating.threestars", is(this.movies.get(0).getRating().getThreestars())))
                .andExpect(jsonPath("$[0].rating.fourstars", is(this.movies.get(0).getRating().getFourstars())))
                .andExpect(jsonPath("$[0].rating.fivestars", is(this.movies.get(0).getRating().getFivestars())))
                .andExpect(jsonPath("$[0].title",is(this.movies.get(0).getTitle())))
                .andExpect(jsonPath("$[0].views",is(this.movies.get(0).getViews())))
                .andExpect(jsonPath("$[0].genre",is(this.movies.get(0).getGenre())))
                .andExpect(jsonPath("$[0].videofile",is(this.movies.get(0).getVideofile())))
                .andExpect(jsonPath("$[1].id",is(this.movies.get(1).getId())))
                .andExpect(jsonPath("$[1].coverimage",is(this.movies.get(1).getCoverimage())))
                .andExpect(jsonPath("$[1].likes",is(this.movies.get(1).getLikes())))
                .andExpect(jsonPath("$[1].dislikes",is(this.movies.get(1).getDislikes())))
                .andExpect(jsonPath("$[1].description",is(this.movies.get(1).getDescription())))
                .andExpect(jsonPath("$[1].overallrating",is(this.movies.get(1).getOverallrating())))
                .andExpect(jsonPath("$[1].rating.onestar", is(this.movies.get(1).getRating().getOnestar())))
                .andExpect(jsonPath("$[1].rating.twostars", is(this.movies.get(1).getRating().getTwostars())))
                .andExpect(jsonPath("$[1].rating.threestars", is(this.movies.get(1).getRating().getThreestars())))
                .andExpect(jsonPath("$[1].rating.fourstars", is(this.movies.get(1).getRating().getFourstars())))
                .andExpect(jsonPath("$[1].rating.fivestars", is(this.movies.get(1).getRating().getFivestars())))
                .andExpect(jsonPath("$[1].title",is(this.movies.get(1).getTitle())))
                .andExpect(jsonPath("$[1].views",is(this.movies.get(1).getViews())))
                .andExpect(jsonPath("$[1].genre",is(this.movies.get(1).getGenre())))
                .andExpect(jsonPath("$[1].videofile",is(this.movies.get(1).getVideofile())));
    }

    /**
     * Sorts the similar movies in descending order.
     * @throws Exception
     */
    public void getSimilarMovies1() throws Exception
    {
        mockMvc.perform(get("/movies/1/similar?sort=true&order=desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$",hasSize(2)))
                .andExpect(jsonPath("$[0].id",is(this.movies.get(1).getId())))
                .andExpect(jsonPath("$[0].coverimage",is(this.movies.get(1).getCoverimage())))
                .andExpect(jsonPath("$[0].likes",is(this.movies.get(1).getLikes())))
                .andExpect(jsonPath("$[0].dislikes",is(this.movies.get(1).getDislikes())))
                .andExpect(jsonPath("$[0].description",is(this.movies.get(1).getDescription())))
                .andExpect(jsonPath("$[0].overallrating",is(this.movies.get(1).getOverallrating())))
                .andExpect(jsonPath("$[0].rating.onestar", is(this.movies.get(1).getRating().getOnestar())))
                .andExpect(jsonPath("$[0].rating.twostars", is(this.movies.get(1).getRating().getTwostars())))
                .andExpect(jsonPath("$[0].rating.threestars", is(this.movies.get(1).getRating().getThreestars())))
                .andExpect(jsonPath("$[0].rating.fourstars", is(this.movies.get(1).getRating().getFourstars())))
                .andExpect(jsonPath("$[0].rating.fivestars", is(this.movies.get(1).getRating().getFivestars())))
                .andExpect(jsonPath("$[0].title",is(this.movies.get(1).getTitle())))
                .andExpect(jsonPath("$[0].views",is(this.movies.get(1).getViews())))
                .andExpect(jsonPath("$[0].genre",is(this.movies.get(1).getGenre())))
                .andExpect(jsonPath("$[0].videofile",is(this.movies.get(1).getVideofile())))
                .andExpect(jsonPath("$[0].id",is(this.movies.get(0).getId())))
                .andExpect(jsonPath("$[0].coverimage",is(this.movies.get(0).getCoverimage())))
                .andExpect(jsonPath("$[0].likes",is(this.movies.get(0).getLikes())))
                .andExpect(jsonPath("$[0].dislikes",is(this.movies.get(0).getDislikes())))
                .andExpect(jsonPath("$[0].description",is(this.movies.get(0).getDescription())))
                .andExpect(jsonPath("$[0].overallrating",is(this.movies.get(0).getOverallrating())))
                .andExpect(jsonPath("$[0].rating.onestar", is(this.movies.get(0).getRating().getOnestar())))
                .andExpect(jsonPath("$[0].rating.twostars", is(this.movies.get(0).getRating().getTwostars())))
                .andExpect(jsonPath("$[0].rating.threestars", is(this.movies.get(0).getRating().getThreestars())))
                .andExpect(jsonPath("$[0].rating.fourstars", is(this.movies.get(0).getRating().getFourstars())))
                .andExpect(jsonPath("$[0].rating.fivestars", is(this.movies.get(0).getRating().getFivestars())))
                .andExpect(jsonPath("$[0].title",is(this.movies.get(0).getTitle())))
                .andExpect(jsonPath("$[0].views",is(this.movies.get(0).getViews())))
                .andExpect(jsonPath("$[0].genre",is(this.movies.get(0).getGenre())))
                .andExpect(jsonPath("$[0].videofile",is(this.movies.get(0).getVideofile())));
    }


    /**
     * INvalid oerdre parameter
     * @throws Exception
     */
    @Test
    public void invalidOrderParameterException1() throws Exception
    {
        mockMvc.perform(get("/movies/1/similar?sort=true&order=descending")
                .content(json(new Movie())).contentType(contentType)).andExpect(status().isBadRequest());
    }

    /**
     * Gets 1 movie on 2nd page.
     * Empty result since there are 2 movies and one 1 other similar movie.
     * @throws Exception
     */
    @Test
    public void getSimiliarMovies3() throws Exception
    {
        mockMvc.perform(get("/movies/1/similar?start=1&count=1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$",hasSize(1)))
                .andExpect(jsonPath("$[0].id",is(this.movies.get(1).getId())))
                .andExpect(jsonPath("$[0].coverimage",is(this.movies.get(1).getCoverimage())))
                .andExpect(jsonPath("$[0].likes",is(this.movies.get(1).getLikes())))
                .andExpect(jsonPath("$[0].dislikes",is(this.movies.get(1).getDislikes())))
                .andExpect(jsonPath("$[0].description",is(this.movies.get(1).getDescription())))
                .andExpect(jsonPath("$[0].overallrating",is(this.movies.get(1).getOverallrating())))
                .andExpect(jsonPath("$[0].rating.onestar", is(this.movies.get(1).getRating().getOnestar())))
                .andExpect(jsonPath("$[0].rating.twostars", is(this.movies.get(1).getRating().getTwostars())))
                .andExpect(jsonPath("$[0].rating.threestars", is(this.movies.get(1).getRating().getThreestars())))
                .andExpect(jsonPath("$[0].rating.fourstars", is(this.movies.get(1).getRating().getFourstars())))
                .andExpect(jsonPath("$[0].rating.fivestars", is(this.movies.get(1).getRating().getFivestars())))
                .andExpect(jsonPath("$[0].title",is(this.movies.get(1).getTitle())))
                .andExpect(jsonPath("$[0].views",is(this.movies.get(1).getViews())))
                .andExpect(jsonPath("$[0].genre",is(this.movies.get(1).getGenre())))
                .andExpect(jsonPath("$[0].videofile",is(this.movies.get(1).getVideofile())));
    }

    /**
     * Gets 1 movie one 1st page.
     * @throws Exception
     */
    @Test
    public void getSimiliarMovies4() throws Exception
    {
        mockMvc.perform(get("/movies/1/similar?start=0&count=1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$",hasSize(1)))
                .andExpect(jsonPath("$[0].id",is(this.movies.get(0).getId())))
                .andExpect(jsonPath("$[0].coverimage",is(this.movies.get(0).getCoverimage())))
                .andExpect(jsonPath("$[0].likes",is(this.movies.get(0).getLikes())))
                .andExpect(jsonPath("$[0].dislikes",is(this.movies.get(0).getDislikes())))
                .andExpect(jsonPath("$[0].description",is(this.movies.get(0).getDescription())))
                .andExpect(jsonPath("$[0].overallrating",is(this.movies.get(0).getOverallrating())))
                .andExpect(jsonPath("$[0].rating.onestar", is(this.movies.get(0).getRating().getOnestar())))
                .andExpect(jsonPath("$[0].rating.twostars", is(this.movies.get(0).getRating().getTwostars())))
                .andExpect(jsonPath("$[0].rating.threestars", is(this.movies.get(0).getRating().getThreestars())))
                .andExpect(jsonPath("$[0].rating.fourstars", is(this.movies.get(0).getRating().getFourstars())))
                .andExpect(jsonPath("$[0].rating.fivestars", is(this.movies.get(0).getRating().getFivestars())))
                .andExpect(jsonPath("$[0].title",is(this.movies.get(0).getTitle())))
                .andExpect(jsonPath("$[0].views",is(this.movies.get(0).getViews())))
                .andExpect(jsonPath("$[0].genre",is(this.movies.get(0).getGenre())))
                .andExpect(jsonPath("$[0].videofile",is(this.movies.get(0).getVideofile())));
    }
    /**
     * Gets 2 movies starting from page 0 sorting them in desc order and ordering by overallrating
     * @throws Exception
     */
    @Test
    public void getMovies13() throws Exception
    {
        mockMvc.perform(get("/movies/?start=0&count=2&sort=true&order=desc&property=overallrating"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$",hasSize(2)))
                .andExpect(jsonPath("$[0].id",is(this.movies.get(1).getId())))
                .andExpect(jsonPath("$[0].coverimage",is(this.movies.get(1).getCoverimage())))
                .andExpect(jsonPath("$[0].likes",is(this.movies.get(1).getLikes())))
                .andExpect(jsonPath("$[0].dislikes",is(this.movies.get(1).getDislikes())))
                .andExpect(jsonPath("$[0].description",is(this.movies.get(1).getDescription())))
                .andExpect(jsonPath("$[0].overallrating",is(this.movies.get(1).getOverallrating())))
                .andExpect(jsonPath("$[0].rating.onestar", is(this.movies.get(1).getRating().getOnestar())))
                .andExpect(jsonPath("$[0].rating.twostars", is(this.movies.get(1).getRating().getTwostars())))
                .andExpect(jsonPath("$[0].rating.threestars", is(this.movies.get(1).getRating().getThreestars())))
                .andExpect(jsonPath("$[0].rating.fourstars", is(this.movies.get(1).getRating().getFourstars())))
                .andExpect(jsonPath("$[0].rating.fivestars", is(this.movies.get(1).getRating().getFivestars())))
                .andExpect(jsonPath("$[0].title",is(this.movies.get(1).getTitle())))
                .andExpect(jsonPath("$[0].views",is(this.movies.get(1).getViews())))
                .andExpect(jsonPath("$[0].genre",is(this.movies.get(1).getGenre())))
                .andExpect(jsonPath("$[0].videofile",is(this.movies.get(1).getVideofile())))
                .andExpect(jsonPath("$[1].id",is(this.movies.get(0).getId())))
                .andExpect(jsonPath("$[1].coverimage",is(this.movies.get(0).getCoverimage())))
                .andExpect(jsonPath("$[1].likes",is(this.movies.get(0).getLikes())))
                .andExpect(jsonPath("$[1].dislikes",is(this.movies.get(0).getDislikes())))
                .andExpect(jsonPath("$[1].description",is(this.movies.get(0).getDescription())))
                .andExpect(jsonPath("$[1].overallrating",is(this.movies.get(0).getOverallrating())))
                .andExpect(jsonPath("$[1].rating.onestar", is(this.movies.get(0).getRating().getOnestar())))
                .andExpect(jsonPath("$[1].rating.twostars", is(this.movies.get(0).getRating().getTwostars())))
                .andExpect(jsonPath("$[1].rating.threestars", is(this.movies.get(0).getRating().getThreestars())))
                .andExpect(jsonPath("$[1].rating.fourstars", is(this.movies.get(0).getRating().getFourstars())))
                .andExpect(jsonPath("$[1].rating.fivestars", is(this.movies.get(0).getRating().getFivestars())))
                .andExpect(jsonPath("$[1].title",is(this.movies.get(0).getTitle())))
                .andExpect(jsonPath("$[1].views",is(this.movies.get(0).getViews())))
                .andExpect(jsonPath("$[1].genre",is(this.movies.get(0).getGenre())))
                .andExpect(jsonPath("$[1].videofile",is(this.movies.get(0).getVideofile())));
    }

    /**
     * Converts an objec to Json Bourne
     * @param obj the object to convert
     * @return the json string
     * @throws IOException  exception
     */
    protected String json(Object obj) throws IOException
    {
        MockHttpOutputMessage mockHttpOutputMessage = new MockHttpOutputMessage();
        this.mappingJackson2HttpMessageConverter.write(obj,MediaType.APPLICATION_JSON, mockHttpOutputMessage);
        return mockHttpOutputMessage.getBodyAsString();
    }
}
