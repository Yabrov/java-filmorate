package ru.yandex.practicum.filmorate.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
@ComponentScan("ru.yandex.practicum.filmorate")
public class DataSourceConfiguration {

    @Value("${spring.datasource.url}")
    private String JDBC_URL;

    @Value("${spring.datasource.username}")
    private String JDBC_USERNAME;

    @Value("${spring.datasource.password}")
    private String JDBC_PASSWORD;

    @Value("${spring.datasource.driverClassName}")
    private String JDBC_DRIVER;

    @Bean
    public HikariConfig hikariConfig() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(JDBC_URL);
        config.setUsername(JDBC_USERNAME);
        config.setPassword(JDBC_PASSWORD);
        config.setDriverClassName(JDBC_DRIVER);
        return config;
    }

    @Bean
    public HikariDataSource dataSource() {
        return new HikariDataSource(hikariConfig());
    }

    @Bean
    public JdbcTemplate getTemplate() {
        return new JdbcTemplate(dataSource());
    }

    @Bean
    PlatformTransactionManager transactionManager() {
        return new DataSourceTransactionManager(dataSource());
    }
}
