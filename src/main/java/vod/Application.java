package vod;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import vod.Repositories.CustomerRepository;
import vod.models.Customer;

import java.util.ArrayList;


@SpringBootApplication
@EnableAutoConfiguration
@Configuration
@ComponentScan
public class Application
{
    public static void main(String[] args)
    {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    CommandLineRunner init(CustomerRepository repository)
    {
        return (a) -> {
        ArrayList<Customer> customers = new ArrayList<Customer>(){{
            add(new Customer("Acha","Bill"));
            add(new Customer("Glenn","Faison"));
            add(new Customer("Acha","Jackson"));
        }};
        repository.save(customers);};
    }
}

