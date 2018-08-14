package jasperapp;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class DBConfig {
    @Bean(name = "dataSource")
    @Primary
    @ConfigurationProperties(prefix = "spring.datasource")
    public DataSource dataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean
    @Primary
    @ConfigurationProperties("spring.datasource1")
    public DataSourceProperties dataSource1() {
        return new DataSourceProperties();
    }

    @Bean
    @ConfigurationProperties("spring.datasource2")
    public DataSourceProperties dataSource2() {
        return new DataSourceProperties();
    }

    @Bean
    @ConfigurationProperties("spring.datasource3")
    public DataSourceProperties dataSource3() {
        return new DataSourceProperties();
    }

    @Bean
    @ConfigurationProperties("spring.datasource4")
    public DataSourceProperties dataSource4() {
        return new DataSourceProperties();
    }
}
