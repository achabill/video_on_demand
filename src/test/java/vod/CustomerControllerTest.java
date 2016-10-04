package vod;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.mock.http.MockHttpOutputMessage;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import vod.Repositories.CustomerRepository;
import vod.models.Customer;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
public class CustomerControllerTest
{
    private MediaType contentType = new MediaType(MediaType.APPLICATION_JSON.getType(),
            MediaType.APPLICATION_JSON.getSubtype(),
            Charset.forName("utf8"));

    private MockMvc mockMvc;
    private String id1 = "1";
    private String id2 = "2";
    private HttpMessageConverter mappingJackson2HttpMessageConverter;
    private List<Customer> customers;

    @Autowired
    CustomerRepository repository;

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

        Customer c1 = new Customer("a","b");
        c1.setId(id1);
        Customer c2 = new Customer("c","d");
        c2.setId(id2);

        customers = new ArrayList<Customer>(){{add(c1); add(c2);}};
        repository.save(customers);
    }

    @Test
    public void customerNotFoundOnGet() throws Exception
    {
        mockMvc.perform(get("/customers/3")
                .content(json(new Customer())).contentType(contentType)).andExpect(status().isNotFound());
    }
    @Test
    public void customerNotFoundOnUpdate() throws Exception
    {
        mockMvc.perform(put("/customers/3")
                .content(json(new Customer("x","b"))).contentType(contentType)).andExpect(status().isNotFound());
    }

    @Test
    public void customerUpdateSuccessful() throws Exception
    {
        mockMvc.perform(put("/customers/1")
                .content(json(new Customer("y","b"))).contentType(contentType)).andExpect(status().isAccepted());
    }

    @Test
    public void getOneCustomer() throws Exception
    {
        mockMvc.perform(get("/customers/" + id1))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$.id", is(this.customers.get(0).getId())))
                .andExpect(jsonPath("$.firstname", is(this.customers.get(0).getFirstname())))
                .andExpect(jsonPath("$.lastname", is(this.customers.get(0).getLastname())));

    }

    @Test
    public void getAllCustomers() throws Exception
    {
        mockMvc.perform(get("/customers/"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$",hasSize(2)))
                .andExpect(jsonPath("$[0].id",is(this.customers.get(0).getId())))
                .andExpect(jsonPath("$[0].firstname", is(this.customers.get(0).getFirstname())))
                .andExpect(jsonPath("$[0].lastname", is(this.customers.get(0).getLastname())))
                .andExpect(jsonPath("$[1].id", is(this.customers.get(1).getId())))
                .andExpect(jsonPath("$[1].firstname", is(this.customers.get(1).getFirstname())))
                .andExpect(jsonPath("$[1].lastname", is(this.customers.get(1).getLastname())));
    }

    @Test
    public void addCustomer() throws Exception
    {
        Customer c = new Customer("11","12");
        c.setId("10");

        String customerJson = json(c);
        this.mockMvc.perform(post("/customers/")
                .contentType(contentType)
                .content(customerJson))
                .andExpect(status().isCreated());
    }

    protected String json(Object obj) throws IOException
    {
        MockHttpOutputMessage mockHttpOutputMessage = new MockHttpOutputMessage();
        this.mappingJackson2HttpMessageConverter.write(obj,MediaType.APPLICATION_JSON, mockHttpOutputMessage);
        return mockHttpOutputMessage.getBodyAsString();
    }
}
