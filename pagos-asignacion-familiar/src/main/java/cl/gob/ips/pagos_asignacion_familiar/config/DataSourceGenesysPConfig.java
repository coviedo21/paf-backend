package cl.gob.ips.pagos_asignacion_familiar.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

@Configuration

public class DataSourceGenesysPConfig {

    @Autowired
    private Environment env;

    @Bean(name = "genesysPDataSource")
    public DataSource genesysPDataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setUrl(env.getProperty("spring.datasource.url"));
        dataSource.setUsername(env.getProperty("spring.datasource.username"));
        dataSource.setPassword(env.getProperty("spring.datasource.password"));
        dataSource.setDriverClassName(env.getProperty("spring.datasource.driverClassName"));
        return dataSource;
    }

    @Bean(name = "genesysPJdbc")
    public JdbcTemplate genesysPJdbc(@Qualifier("genesysPDataSource") DataSource genesysPJdbc) {
        return new JdbcTemplate(genesysPJdbc);
    }
}