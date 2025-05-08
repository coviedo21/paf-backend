package cl.gob.ips.proceso_pago.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;

@Configuration
public class DataSourceCtaCteConfig {

    @Autowired
    private Environment env;

    @Bean(name = "ctaCteDataSource")
    public DataSource ctaCteDataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setUrl(env.getProperty("spring.datasource.url"));
        dataSource.setUsername(env.getProperty("spring.datasource.username"));
        dataSource.setPassword(env.getProperty("spring.datasource.password"));
        dataSource.setDriverClassName(env.getProperty("spring.datasource.driverClassName"));
        return dataSource;
    }

    @Bean(name = "ctaCteJdbc")
    public JdbcTemplate ctaCteJdbc(@Qualifier("ctaCteDataSource") DataSource ctaCteJdbc) {
        return new JdbcTemplate(ctaCteJdbc);
    }
}