package cl.gob.ips.solicitudes_pago.config;

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

public class DataSourceCoredConfig {

    @Autowired
    private Environment env;

    @Bean(name = "coredDataSource")
    public DataSource coredDataSourceDataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setUrl(env.getProperty("spring.datasource.cored.url"));
        dataSource.setUsername(env.getProperty("spring.datasource.cored.username"));
        dataSource.setPassword(env.getProperty("spring.datasource.cored.password"));
        dataSource.setDriverClassName(env.getProperty("spring.datasource.cored.driverClassName"));
        return dataSource;
    }

    @Bean(name = "coredJdbc")
    public JdbcTemplate coredJdbc(@Qualifier("coredDataSource") DataSource coredJdbc) {
        return new JdbcTemplate(coredJdbc);
    }
}