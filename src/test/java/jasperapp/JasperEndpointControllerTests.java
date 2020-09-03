/*
 * Copyright 2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package jasperapp;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource(properties = "reports.directory=/src/test/resources/")
@AutoConfigureMockMvc
public class JasperEndpointControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    @Qualifier("dataSource0")
    private DataSource dataSource;

    @Before
    public void initializeDB () {
        Connection connection;
        try {
            connection = dataSource.getConnection();
            try {

                PreparedStatement cleanPreparedStatement;
                PreparedStatement createPreparedStatement;
                PreparedStatement insertPreparedStatement;
                PreparedStatement selectPreparedStatement;

                String CleanQuery = "DROP TABLE IF EXISTS PERSON";
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

    @Test
    public void gettingReportsShouldReturnDefaultMessage() throws Exception {

        this.mockMvc.perform(get("/reports/rpt_example/?format=pdf&personid=1"))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    public void gettingReportsInSubfolderShouldReturnDefaultMessage() throws Exception {

        this.mockMvc.perform(get("/reports/subfolder/rpt_example/?format=pdf&personid=1"))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    public void gettingReportsShouldReturnTheCorrectFormat() throws Exception {
        /* HTML */
        this.mockMvc.perform(get("/reports/rpt_example/?format=html&personid=1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/html"));

        /* CSV */
        this.mockMvc.perform(get("/reports/rpt_example/?format=csv&personid=1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/csv"));

        /* PDF */
        this.mockMvc.perform(get("/reports/rpt_example/?format=pdf&personid=1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/pdf"));

        /* CSV */
        this.mockMvc.perform(get("/reports/rpt_example/?format=xls&personid=1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/vnd.ms-excel"));
    }

    @Test
    public void gettingReportsShouldAcceptParameters() throws Exception {

        /* Should select first person */

        MvcResult mvcResult = this.mockMvc.perform(get("/reports/rpt_example/?format=csv&personid=1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String content = mvcResult.getResponse().getContentAsString();

        assertTrue(content.contains("Jose"));
        assertFalse(content.contains("Obelix"));

        /* Should select second person */

        MvcResult mvcResult2 = this.mockMvc.perform(get("/reports/rpt_example/?format=csv&personid=2"))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String content2 = mvcResult2.getResponse().getContentAsString();

        assertFalse(content2.contains("Jose"));
        assertTrue(content2.contains("Obelix"));

    }


}
