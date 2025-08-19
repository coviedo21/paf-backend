package cl.gob.ips.solicitudes_pago.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors() // Habilita CORS
                .and()
                .csrf().disable() //
                .headers(headers -> headers
                        .httpStrictTransportSecurity()
                        .maxAgeInSeconds(31536000)
                        .includeSubDomains(true)
                        .and()
                        .contentTypeOptions()
                        .and()
                        .contentSecurityPolicy("default-src 'self'; frame-ancestors 'none'; script-src 'self'; style-src 'self';")
                        .and()
                        .frameOptions().deny())
                .authorizeRequests(requests -> requests
                        .anyRequest().permitAll());

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.addAllowedOrigin("*"); // en producción puedes cambiar esto
        configuration.addAllowedMethod("*");
        configuration.addAllowedHeader("*");

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}

