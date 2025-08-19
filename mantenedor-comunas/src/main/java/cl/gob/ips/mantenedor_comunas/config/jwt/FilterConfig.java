package cl.gob.ips.mantenedor_comunas.config.jwt;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterConfig {

    @Bean
    public FilterRegistrationBean<JwtFilter> jwtFilter(JwtUtil jwtUtil) {
        FilterRegistrationBean<JwtFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new JwtFilter(jwtUtil)); // inyecta JwtUtil
        registration.addUrlPatterns("/*");
        registration.setOrder(1);
        return registration;
    }
}
