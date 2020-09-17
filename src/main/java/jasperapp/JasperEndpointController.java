package jasperapp;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import java.io.File;
import java.lang.reflect.Constructor;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.AbstractCollection;
import java.util.Locale;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.regex.Matcher;


@RestController
@RequestMapping(value = "/reports")
public class JasperEndpointController {

    @Autowired
    @Qualifier("dataSource0")
    private DataSource dataSource0;

    @Autowired
    @Qualifier("dataSource1")
    private DataSource dataSource1;

    @Autowired
    @Qualifier("dataSource2")
    private DataSource dataSource2;

    @Autowired
    @Qualifier("dataSource3")
    private DataSource dataSource3;

    @Autowired
    @Qualifier("dataSource4")
    private DataSource dataSource4;

    @Value("${reports.directory}")
    private String reportsDirectory;

    @Value("${reports.locale:en_US}")
    private String reportsLocale;

    private DataSource dataSource;
    private Connection dataConn1;
    private Connection dataConn2;
    private Connection dataConn3;
    private Connection dataConn4;
    private boolean connectionsCreated = false;

    private Connection getConnection(DataSource dataSource) {
        try {
            return dataSource.getConnection();
        } catch(Exception e) {
            return null;
        }
    }

    /* Example http://localhost:8080/reports/rpt_example/?format=pdf&personid=0 */
    /* Example http://localhost:8080/reports/rpt_subfolder/rpt_example/?format=pdf&personid=0 */

    @RequestMapping(value = {"/{reportname}/", "/{reportfolder}/{reportname}/"}, method = RequestMethod.GET)
    public ModelAndView getRptByParam(final ModelMap modelMap, ModelAndView modelAndView, @PathVariable Map<String, String> pathVariables,  HttpServletRequest request) {

        Map<String, String[]> map = request.getParameterMap();
        String reportfolder = pathVariables.get("reportfolder");
        String reportname = pathVariables.get("reportname");

        String report = reportfolder != null ? reportfolder + "/" + reportname : reportname;

        if(!connectionsCreated) {
            dataConn1 = getConnection(dataSource1);
            dataConn2 = getConnection(dataSource2);
            dataConn3 = getConnection(dataSource3);
            dataConn4 = getConnection(dataSource4);
            connectionsCreated = true;
        }
        
        modelMap.put("datasource", dataSource0);
        modelMap.put("dataconn1", dataConn1);
        modelMap.put("dataconn2", dataConn2);
        modelMap.put("dataconn3", dataConn3);
        modelMap.put("dataconn4", dataConn4);

        // Attempt to parse jrxml to get parameter types
        Map<String, String> parameterTypes = new HashMap<String,String>();
        Map<String, String> parameterNestedTypes = new HashMap<String,String>();
        try {
            String filename = System.getProperty("user.dir") + "/" + reportsDirectory + "/" + report + ".jrxml";
            File inputFile = new File(filename);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(inputFile);
            NodeList nList = doc.getElementsByTagName("parameter");
            for (int i = 0; i < nList.getLength(); ++i) {
                Element eElement = (Element) nList.item(i);
                String paramName = eElement.getAttribute("name");
                String paramClass = eElement.getAttribute("class");
                String paramNestedType = eElement.getAttribute("nestedType");

                parameterTypes.put(paramName, paramClass);
                parameterNestedTypes.put(paramName, paramNestedType);
                System.out.println("** Report parameter " + paramName + ": " + paramClass + (!paramNestedType.isEmpty() ? "[" + paramNestedType + "]" : ""));
            }
        } catch(Exception e) {
            System.err.println("Error: Failed to parse report parameters " + e);
        }

        // Insert parameters into model map
        map.forEach((String k, String[] v) -> {
            if(parameterTypes.containsKey(k)) {
                try {
                    String paramType = parameterTypes.get(k);
                    String paramNestedType = parameterNestedTypes.get(k);
                    if(!paramNestedType.isEmpty()) {
                        // Is a collection
                        Class collectionClass = Class.forName(paramType);
                        Class nestedClass = Class.forName(paramNestedType);
                        Constructor collectionConstructor = collectionClass.getConstructor();
                        Constructor nestedConstructor = nestedClass.getConstructor(String.class);
                        AbstractCollection<Object> collection = (AbstractCollection<Object>) collectionConstructor.newInstance();
                        for(int i = 0; i < v.length; ++i) {
                            collection.add(nestedConstructor.newInstance(v[i]));
                        }
                        modelMap.put(k, collection);
                    } else {
                        // Is a single value
                        Class paramClass = Class.forName(paramType);
                        Constructor paramConstructor = paramClass.getConstructor(String.class);
                        modelMap.put(k, paramConstructor.newInstance(v[0]));
                    }
                } catch(Exception e) {
                    System.err.println("Error: Failed to construct parameter " + k + ": " + e);
                }
            } else {
                if(!k.equals("format")) {
                  System.err.println("Warning: Parameter " + k + " not declared in report, assuming type java.lang.String");
                }
                modelMap.put(k, v[0]);
            }
        });

        // This can be used in JasperReports to have absolute Image Paths
        modelMap.put("ROOT_DIR", System.getProperty("user.dir") + "/" + reportsDirectory + (reportfolder != null ? "/" + reportfolder : ""));

        // Set locale
        Matcher matcher = Pattern.compile("^([a-z]{2,3})_([A-Z]{2,3})$").matcher(reportsLocale);
        if(matcher.find()) {
            System.out.println("** Report locale: " + reportsLocale);
            modelMap.put("REPORT_LOCALE", new Locale(matcher.group(1), matcher.group(2)));
        }

        //It is important that the underlying Jasper Report supports the Query parameters

        modelAndView = new ModelAndView(report, modelMap);
        return modelAndView;
    }
}
