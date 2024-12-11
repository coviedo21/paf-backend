package cl.gob.ips.cuenta_bancaria.config;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class restTemplateConfig {

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder){

        return builder.build();


    }

}
