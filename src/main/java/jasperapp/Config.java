package jasperapp;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.jasperreports.JasperReportsMultiFormatView;
import org.springframework.web.servlet.view.jasperreports.JasperReportsViewResolver;


@Configuration
//@EnableWebMvc
public class Config extends WebMvcConfigurerAdapter {

    @Override
    public void configureDefaultServletHandling(final DefaultServletHandlerConfigurer configurer) {

        configurer.enable();
    }


    @Value("${reports.directory}")
    private String reportsDirectory;

    @Bean
    public JasperReportsViewResolver getJasperReportsViewResolver() {

        String uploadLocation = System.getProperty("user.dir") + reportsDirectory;

        JasperReportsViewResolver resolver = new JasperReportsViewResolver();

        resolver.setPrefix("file:" + uploadLocation);
        resolver.setSuffix(".jrxml");

        resolver.setReportDataKey("datasource");
        resolver.setViewNames("*");
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

}
