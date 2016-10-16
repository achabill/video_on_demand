package vod;

import com.fasterxml.classmate.TypeResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;
import vod.repositories.MoviesRepository;

@SpringBootApplication
@EnableAutoConfiguration
@Configuration
@ComponentScan
@EnableSwagger2
public class Application {
    @Autowired
    MoviesRepository repository;
    @Autowired
    private TypeResolver typeResolver;

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public Docket booksApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                //.groupName("Books");
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.any())
                .paths(PathSelectors.any())
                .build();
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("Video on Demand")
                .description("Rest API for the video on demand service for commuFi, Skylabase")
                .contact("www.skylabase.com")
                .version("2.0")
                .build();
    }

}

