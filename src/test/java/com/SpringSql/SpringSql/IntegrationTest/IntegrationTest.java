package com.SpringSql.SpringSql.IntegrationTest;

import com.SpringSql.SpringSql.DTO.ChangeManagerRequest;
import com.SpringSql.SpringSql.Model.EmployeeManager;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.Instant;

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class IntegrationTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;


    @Test
    @Order(1)
    public void testAddEmployee_Manager() throws Exception {

        //Add Manager with id 1
        EmployeeManager employee1 = new EmployeeManager();
        employee1.setName("John Doe");
        employee1.setEmail("john.doe@example.com");
        employee1.setDesignation("Account Manager");
        employee1.setMobile("1234567890");
        employee1.setDepartment("sales");
        employee1.setManagerId(0);

        mvc.perform(MockMvcRequestBuilders
                .post("/addEmployee")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(employee1)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Employee added as Manager successfully with ID: 1"));

        
        //Add Manager with id 2

        EmployeeManager employee2 = new EmployeeManager();
        employee2.setName("Amal");
        employee2.setEmail("amal@example.com");
        employee2.setDesignation("Account Manager");
        employee2.setMobile("1234567890");
        employee2.setDepartment("delivery");
        employee2.setManagerId(0);
        
        mvc.perform(MockMvcRequestBuilders
                .post("/addEmployee")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(employee2)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Employee added as Manager successfully with ID: 2"));
        
        //Checks existing manager in same department

        EmployeeManager employee3 = new EmployeeManager();
        employee3.setName("Amal");
        employee3.setEmail("amal@example.com");
        employee3.setDesignation("Account Manager");
        employee3.setMobile("1234567890");
        employee3.setDepartment("delivery");
        employee3.setManagerId(0);
                
        mvc.perform(MockMvcRequestBuilders
                .post("/addEmployee")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(employee3)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.Error").value("A manager already exists in the department: delivery"));

        //Manager without designation account manager

        EmployeeManager employee4 = new EmployeeManager();
        employee4.setName("Amal");
        employee4.setEmail("amal@example.com");
        employee4.setDesignation("Associate");
        employee4.setMobile("1234567890");
        employee4.setDepartment("delivery");
        employee4.setManagerId(0);
        
        mvc.perform(MockMvcRequestBuilders
                .post("/addEmployee")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(employee4)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.['Validation Errors']").exists())
                .andExpect(jsonPath("$.['Validation Errors'][0]").value("Manager ID 0 should have designation as Account Manager. Employee cannot be added."));

        //Manager without manager id 0 

        EmployeeManager employee5 = new EmployeeManager();
        employee5.setName("Amal");
        employee5.setEmail("amal@example.com");
        employee5.setDesignation("Account Manager");
        employee5.setMobile("1234567890");
        employee5.setDepartment("delivery");
        employee5.setManagerId(5);
        
        mvc.perform(MockMvcRequestBuilders
                .post("/addEmployee")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(employee5)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.['Validation Errors']").exists())
                .andExpect(jsonPath("$.['Validation Errors'][0]").value("Account Manager must have Manager ID set to 0. Employee cannot be added."));
        
        //Validation Errors
        employee5.setName("Amal");
        employee5.setEmail("amalexample.com");
        employee5.setDesignation("Acct Manager");
        employee5.setMobile("1234567e0");
        employee5.setDepartment("delery");
        employee5.setManagerId(5);
        
        mvc.perform(MockMvcRequestBuilders
                .post("/addEmployee")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(employee5)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.['Validation Errors']").exists())
                .andExpect(jsonPath("$.['Validation Errors'][0]").value("Designation can only be Account Manager or associate."))
                .andExpect(jsonPath("$.['Validation Errors'][1]").value("Invalid department. Must be one of: sales,delivery,QA,engineering,BA."))
                .andExpect(jsonPath("$.['Validation Errors'][2]").value("Invalid email format."))
                .andExpect(jsonPath("$.['Validation Errors'][3]").value("Invalid mobile number. It must be a 10-digit number."));
    }

    @Test
    @Order(2)
    public void testAddEmployee() throws Exception {
        //Add employee with id 3

        EmployeeManager employee1 = new EmployeeManager();
        employee1.setName("Arthur");
        employee1.setEmail("arthur@example.com");
        employee1.setDesignation("associate");
        employee1.setMobile("1234567890");
        employee1.setDepartment("sales");
        employee1.setManagerId(1);
        Instant dateOfJoining1 = Instant.parse("2019-08-14T11:00:00.000Z");
        employee1.setDateOfJoining(dateOfJoining1);

        mvc.perform(MockMvcRequestBuilders
                .post("/addEmployee")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(employee1)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Employee added successfully under Manager with ID: 1"));

        //Manager not found
        
        EmployeeManager employee2 = new EmployeeManager();
        employee2.setName("Arthur");
        employee2.setEmail("arthur@example.com");
        employee2.setDesignation("associate");
        employee2.setMobile("1234567890");
        employee2.setDepartment("QA");
        employee2.setManagerId(4);
        
        mvc.perform(MockMvcRequestBuilders
                .post("/addEmployee")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(employee2)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.Error").value("Manager with ID 4 not found. Employee cannot be added."));

        //Is not a manager

        EmployeeManager employee3 = new EmployeeManager();
        employee3.setName("Arthur");
        employee3.setEmail("arthur@example.com");
        employee3.setDesignation("associate");
        employee3.setMobile("1234567890");
        employee3.setDepartment("engineering");
        employee3.setManagerId(3);
                
        mvc.perform(MockMvcRequestBuilders
                .post("/addEmployee")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(employee3)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.Error").value("Employee with ID 3 is not a manager. Employee cannot be added."));
    
        //Manager and employee should be same department

        EmployeeManager employee4 = new EmployeeManager();
        employee4.setName("Arthur");
        employee4.setEmail("arthur@example.com");
        employee4.setDesignation("associate");
        employee4.setMobile("1234567890");
        employee4.setDepartment("BA");
        employee4.setManagerId(1);
                        
        mvc.perform(MockMvcRequestBuilders
                .post("/addEmployee")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(employee4)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.['Validation Errors']").exists())
                .andExpect(jsonPath("$.['Validation Errors'][0]").value("Employee and manager must belong to the same department. Employee cannot be added."));
    
        //Add employee with id 4

        EmployeeManager employee5 = new EmployeeManager();
        employee5.setName("Arjun");
        employee5.setEmail("arthur@example.com");
        employee5.setDesignation("associate");
        employee5.setMobile("1234567890");
        employee5.setDepartment("sales");
        employee5.setManagerId(1);
        Instant dateOfJoining2 = Instant.parse("2012-08-14T11:00:00.000Z");
        employee5.setDateOfJoining(dateOfJoining2);

        mvc.perform(MockMvcRequestBuilders
                .post("/addEmployee")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(employee5)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Employee added successfully under Manager with ID: 1"));

    }

    @Test
    @Order(3)
    public void testGetEmployee() throws Exception {

        //Get all employee under a manager
        mvc.perform(MockMvcRequestBuilders
                .get("/getEmployee")
                .param("managerId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].managerName").value("John Doe"))
                .andExpect(jsonPath("$[0].department").value("sales"))
                .andExpect(jsonPath("$[0].employeeList[0].name").value("Arthur"));

        //Get all employee under a manager with id and yearsofexperience
        mvc.perform(MockMvcRequestBuilders
                .get("/getEmployee")
                .param("managerId", "1")
                .param("yearsOfExperience", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].managerName").value("John Doe"))
                .andExpect(jsonPath("$[0].department").value("sales"))
                .andExpect(jsonPath("$[0].employeeList[0].name").value("Arjun"));

        //Get all employee with yearsofexperience
        mvc.perform(MockMvcRequestBuilders
                .get("/getEmployee")
                .param("yearsOfExperience", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].managerName").value("John Doe"))
                .andExpect(jsonPath("$[0].department").value("sales"))
                .andExpect(jsonPath("$[0].employeeList[0].name").value("Arjun"));

        //Get all employee 
        mvc.perform(MockMvcRequestBuilders
                .get("/getEmployee"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].managerName").value("John Doe"))
                .andExpect(jsonPath("$[0].department").value("sales"))
                .andExpect(jsonPath("$[0].employeeList[0].name").value("Arthur"))
                .andExpect(jsonPath("$[0].employeeList[1].name").value("Arjun"));

        //No employee
        mvc.perform(MockMvcRequestBuilders
                .get("/getEmployee")
                .param("managerId", "4"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.Error").value("No Employee found."));
    }

    @Test
    @Order(4)
    public void testChangeEmployeeManager() throws Exception {
        //Change success
        ChangeManagerRequest request1 = new ChangeManagerRequest(3, 2);

        mvc.perform(MockMvcRequestBuilders
                .put("/changeEmployeeManager")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request1)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Arthur's manager has been successfully changed from John Doe to Amal."));
        //Employee not found
        ChangeManagerRequest request2 = new ChangeManagerRequest(7, 2);

        mvc.perform(MockMvcRequestBuilders
                .put("/changeEmployeeManager")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request2)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.Error").value("Employee with ID 7 not found."));
        
        //Manager is not found
        ChangeManagerRequest request3 = new ChangeManagerRequest(4, 7);

        mvc.perform(MockMvcRequestBuilders
                .put("/changeEmployeeManager")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request3)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.Error").value("New manager with ID 7 not found."));

        //same Manager
        ChangeManagerRequest request4 = new ChangeManagerRequest(3, 2);

        mvc.perform(MockMvcRequestBuilders
                .put("/changeEmployeeManager")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request4)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.Error").value("Employee is currently under the given manager. No changes required."));
        
        //Employee is a manager
        ChangeManagerRequest request5 = new ChangeManagerRequest(1, 2);

        mvc.perform(MockMvcRequestBuilders
                .put("/changeEmployeeManager")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request5)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.Error").value("Employee is a manager cannot be changed"));
    }

    @Test
    @Order(5)
    public void testDeleteEmployee() throws Exception {
        //Delete success
        mvc.perform(MockMvcRequestBuilders
                .delete("/deleteEmployee")
                .param("employeeId", "4"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Successfully deleted Arjun from the organization."));
        //Employee not found
        mvc.perform(MockMvcRequestBuilders
                .delete("/deleteEmployee")
                .param("employeeId", "9"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.Error").value("Employee with ID 9 not found."));

        //Manager with employees
        mvc.perform(MockMvcRequestBuilders
                .delete("/deleteEmployee")
                .param("employeeId", "2"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.Error").value("Cannot delete Employee with ID 2 as they are a manager with subordinates."));

    }

    @Autowired
    JdbcTemplate jdbcTemplate;

    @AfterAll
    void tearDown() {
        jdbcTemplate.execute("DROP TABLE IF EXISTS Test");
    }
}
