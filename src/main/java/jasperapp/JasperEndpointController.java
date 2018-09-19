package jasperapp;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;


@RestController
@RequestMapping(value = "/reports")
public class JasperEndpointController {
    @Autowired
    @Qualifier("dataSource0")
    private DataSourceProperties dataSourceProps0;

    @Autowired
    @Qualifier("dataSource1")
    private DataSourceProperties dataSourceProps1;

    @Autowired
    @Qualifier("dataSource2")
    private DataSourceProperties dataSourceProps2;

    @Autowired
    @Qualifier("dataSource3")
    private DataSourceProperties dataSourceProps3;

    @Autowired
    @Qualifier("dataSource4")
    private DataSourceProperties dataSourceProps4;

    private DataSource dataSource;
    private Connection dataConn1;
    private Connection dataConn2;
    private Connection dataConn3;
    private Connection dataConn4;
    private boolean connectionsCreated = false;


    private DataSource getDataSource(DataSourceProperties props) {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(props.getUrl());
        config.setDriverClassName(props.getDriverClassName());
        config.setUsername(props.getUsername());
        config.setPassword(props.getPassword());
        config.setMinimumIdle(1);
        config.setMaximumPoolSize(5);

        try {
            return new HikariDataSource(config);
        } catch(Exception e) {
            return null;
        }
    }

    private Connection getConnection(DataSourceProperties props) {
        try {
            return getDataSource(props).getConnection();
        } catch(Exception e) {
            return null;
        }
    }

    /* Example http://localhost:8080/reports/rpt_example/?format=pdf&personid=0 */
    /* Example http://localhost:8080/reports/rpt_subfolder/rpt_example/?format=pdf&personid=0 */

    @RequestMapping(value = {"/{reportname}/", "/{reportfolder}/{reportname}/"}, method = RequestMethod.GET)
    public ModelAndView getRptByParam(final ModelMap modelMap, ModelAndView modelAndView, @PathVariable Map<String, String> pathVariables,  HttpServletRequest request) {

        if(!connectionsCreated) {
            dataSource = getDataSource(dataSourceProps0);
            dataConn1 = getConnection(dataSourceProps1);
            dataConn2 = getConnection(dataSourceProps2);
            dataConn3 = getConnection(dataSourceProps3);
            dataConn4 = getConnection(dataSourceProps4);
            connectionsCreated = true;
        }


        Map<String, String[]> map = request.getParameterMap();
        String reportfolder = pathVariables.get("reportfolder");
        String reportname = pathVariables.get("reportname");

        String report = reportfolder != null ? reportfolder + "/" + reportname : reportname;

        modelMap.put("datasource", dataSource);
        modelMap.put("dataconn1", dataConn1);
        modelMap.put("dataconn2", dataConn2);
        modelMap.put("dataconn3", dataConn3);
        modelMap.put("dataconn4", dataConn4);

        map.forEach((String k, String[] v) -> {
            if (v[0].matches("\\d+")) {
                //its a number:
                modelMap.put(k, Integer.parseInt(v[0]));
            } else {
                modelMap.put(k, v[0]);
            }
        });

        // This can be used in JasperReports to have absolute Image Paths
        modelMap.put("ROOT_DIR", System.getProperty("user.dir") + "/reports" + (reportfolder != null ? "/" + reportfolder : ""));

        //It is important that the underlying Jasper Report supports the Query parameters

        modelAndView = new ModelAndView(report, modelMap);
        return modelAndView;
    }
}

