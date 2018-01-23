package jasperapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;


@SpringBootApplication
public class JasperReportApp extends SpringBootServletInitializer {
    public static void main(final String[] args) throws Exception {
        SpringApplication.run(JasperReportApp.class, args);
    }

    @Override
    protected SpringApplicationBuilder configure(final SpringApplicationBuilder application) {

        return application.sources(JasperReportApp.class);
    }



}