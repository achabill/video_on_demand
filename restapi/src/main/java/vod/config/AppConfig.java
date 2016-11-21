package vod.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

@ImportResource("classpath:/vod/config/config.xml")
@Configuration
public class AppConfig {
}
