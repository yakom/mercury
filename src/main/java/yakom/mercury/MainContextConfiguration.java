package yakom.mercury;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@Configuration
@ComponentScan("yakom.mercury")
@EnableWebMvc
public class MainContextConfiguration {

    public static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Bean
    public ObjectMapper defaultObjectMapper() {
        return OBJECT_MAPPER;
    }
}
