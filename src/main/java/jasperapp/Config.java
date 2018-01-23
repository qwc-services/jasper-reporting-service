package jasperapp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.jasperreports.JasperReportsMultiFormatView;
import org.springframework.web.servlet.view.jasperreports.JasperReportsViewResolver;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


@Configuration
@EnableWebMvc
public class Config extends WebMvcConfigurerAdapter {
    @Override
    public void configureDefaultServletHandling(final DefaultServletHandlerConfigurer configurer) {

        configurer.enable();
    }

    @Bean
    public JasperReportsViewResolver getJasperReportsViewResolver() {

        JasperReportsViewResolver resolver = new JasperReportsViewResolver();
        resolver.setPrefix("classpath:/");
        resolver.setSuffix(".jrxml");

        resolver.setReportDataKey("datasource");
        resolver.setViewNames("rpt_*");
        resolver.setViewClass(JasperReportsMultiFormatView.class);
        resolver.setOrder(0);
        return resolver;
    }

    @Bean
    public ViewResolver htmlViewResolver() {

        InternalResourceViewResolver resolver = new InternalResourceViewResolver();

        resolver.setPrefix("/pages/");
        resolver.setSuffix(".html");
        resolver.setOrder(1);
        return resolver;
    }

    // For local Test only:

    /*
    @Component
    public class StartUpInit {
        @Autowired
        @Qualifier("dataSource")
        DataSource dataSource;

        @PostConstruct
        public void init() {

            Connection connection;
            try {
                connection = dataSource.getConnection();
                try {

                    PreparedStatement cleanPreparedStatement = null;
                    PreparedStatement createPreparedStatement = null;
                    PreparedStatement insertPreparedStatement = null;
                    PreparedStatement selectPreparedStatement = null;

                    String CleanQuery = "DROP TABLE PERSON";
                    String CreateQuery = "CREATE TABLE PERSON(id int primary key, name varchar(255))";
                    String InsertQuery = "INSERT INTO PERSON" + "(id, name) values" + "(?,?)";
                    String SelectQuery = "select * from PERSON";

                    cleanPreparedStatement = connection.prepareStatement(CleanQuery);
                    cleanPreparedStatement.executeUpdate();
                    cleanPreparedStatement.close();

                    createPreparedStatement = connection.prepareStatement(CreateQuery);
                    createPreparedStatement.executeUpdate();
                    createPreparedStatement.close();

                    connection.setAutoCommit(false);

                    insertPreparedStatement = connection.prepareStatement(InsertQuery);
                    insertPreparedStatement.setInt(1, 1);
                    insertPreparedStatement.setString(2, "Jose");
                    insertPreparedStatement.executeUpdate();
                    insertPreparedStatement.close();

                    insertPreparedStatement = connection.prepareStatement(InsertQuery);
                    insertPreparedStatement.setInt(1, 2);
                    insertPreparedStatement.setString(2, "Obelix");
                    insertPreparedStatement.executeUpdate();
                    insertPreparedStatement.close();

                    selectPreparedStatement = connection.prepareStatement(SelectQuery);
                    ResultSet rs = selectPreparedStatement.executeQuery();
                    System.out.println("H2 Database inserted through PreparedStatement");
                    while (rs.next()) {
                        System.out.println("Id " + rs.getInt("id") + " Name " + rs.getString("name"));
                    }
                    selectPreparedStatement.close();

                    connection.commit();

                } catch (SQLException e) {
                    e.printStackTrace();
                } finally {
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    } */
}
