package cl.gob.ips.proceso_pago.config;

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
public class DataSourcePafConfig {

    @Autowired
    private Environment env;

    @Primary
    @Bean(name = "pafDataSource")
    public DataSource pafDataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setUrl(env.getProperty("spring.datasource.paf.url"));
        dataSource.setUsername(env.getProperty("spring.datasource.paf.username"));
        dataSource.setPassword(env.getProperty("spring.datasource.paf.password"));
        dataSource.setDriverClassName(env.getProperty("spring.datasource.paf.driverClassName"));
        return dataSource;
    }

    @Primary
    @Bean(name = "pafJdbc")
    public JdbcTemplate pafJdbc(
            @Qualifier("pafDataSource") DataSource pafJdbc) {
        return new JdbcTemplate(pafJdbc);
    }
}