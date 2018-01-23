package jasperapp;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.web.servlet.ModelAndView;



@RestController
@RequestMapping(value = "/reports")
public class JasperEndpointController {
    private static final String FILE_FORMAT = "format";

    private static final String DATASOURCE = "datasource";

    @Autowired
    @Qualifier("dataSource")
    private DataSource dataSource;

    /* Example http://localhost:8080/reports/rpt_example/?format=pdf&id=0 */

    @RequestMapping(value = "{reportname}", method = RequestMethod.GET)
    public ModelAndView getRptByParam(final ModelMap modelMap, ModelAndView modelAndView, @PathVariable("reportname") final String reportname, @RequestParam(FILE_FORMAT) final String format, @RequestParam("id") final String id) {

        // connecting to H2
        modelMap.put(DATASOURCE, dataSource);
        modelMap.put(FILE_FORMAT, format);

        // Add Parameters you wish to query for here (and of course in the jrxml. file...)
        modelMap.put("personid", id);

        modelAndView = new ModelAndView(reportname, modelMap);
        return modelAndView;
    }
}

