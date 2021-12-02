package jasperapp;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

@Configuration
public class DBConfig {

    @Bean(name = "dataSource0")
    @Primary
    @ConfigurationProperties("spring.datasource0")
    @ConditionalOnProperty(prefix = "spring.datasource0", name="jdbc-url", matchIfMissing = false)
    public DataSource dataSource0() {
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "dataSource1")
    @ConfigurationProperties("spring.datasource1")
    @ConditionalOnProperty(prefix = "spring.datasource1", name="jdbc-url", matchIfMissing = false)
    public DataSource dataSource1() {
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "dataSource2")
    @ConfigurationProperties("spring.datasource2")
    @ConditionalOnProperty(prefix = "spring.datasource2", name="jdbc-url", matchIfMissing = false)
    public DataSource dataSource2() {
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "dataSource3")
    @ConfigurationProperties("spring.datasource3")
    @ConditionalOnProperty(prefix = "spring.datasource3", name="jdbc-url", matchIfMissing = false)
    public DataSource dataSource3() {
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "dataSource4")
    @ConfigurationProperties("spring.datasource4")
    @ConditionalOnProperty(prefix = "spring.datasource4", name="jdbc-url", matchIfMissing = false)
    public DataSource dataSource4() {
        return DataSourceBuilder.create().build();
    }
}
