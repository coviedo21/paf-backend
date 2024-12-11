package cl.gob.ips.proceso_pago.config;

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
