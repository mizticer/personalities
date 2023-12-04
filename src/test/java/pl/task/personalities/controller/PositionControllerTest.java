package pl.task.personalities.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import pl.task.personalities.PersonalitiesApplication;
import pl.task.personalities.model.dto.request.LoginRequest;
import pl.task.personalities.model.dto.request.PositionEditRequest;
import pl.task.personalities.model.dto.request.PositionRequest;
import pl.task.personalities.model.dto.request.SignupRequest;
import pl.task.personalities.model.dto.response.EmployeeResponse;
import pl.task.personalities.model.dto.response.JwtResponse;
import pl.task.personalities.model.dto.response.PositionResponse;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = {PersonalitiesApplication.class}, properties = "src/test/resources/application.properties")
@AutoConfigureMockMvc
@Sql(scripts = "classpath:database_before_test.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "classpath:database_after_test.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class PositionControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    private String token;

    @BeforeEach
    void createAdminAndLogin() throws Exception {
        SignupRequest signupRequest = new SignupRequest();
        signupRequest.setUsername("adminUser");
        signupRequest.setEmail("admin@example.com");
        signupRequest.setPassword("adminPassword");
        signupRequest.setRole(Collections.singleton("admin"));

        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signupRequest)))
                .andExpect(status().isOk());

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("adminUser");
        loginRequest.setPassword("adminPassword");

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/signin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        JwtResponse jwtResponse = objectMapper.readValue(content, JwtResponse.class);
        token = jwtResponse.getAccessToken();
    }

    @Test
    @DisplayName("Should add new current position to employee")
    void addCurrentPositionToEmployee() throws Exception {
        PositionRequest positionRequest = new PositionRequest();
        positionRequest.setName("Software Engineer");
        positionRequest.setSalary(9000.0);
        positionRequest.setStartDate(LocalDate.of(2022, 2, 23));

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/employee/9/position")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(positionRequest)))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andReturn();

        PositionResponse positionResponse = objectMapper.readValue(result.getResponse().getContentAsString(), PositionResponse.class);

        MvcResult resultTwo = mockMvc.perform(MockMvcRequestBuilders.get("/persons")
                        .header("Authorization", "Bearer " + token)
                        .param("firstName", "Robert")
                        .param("lastName", "Smith")
                        .param("pesel", "12345678901"))
                .andExpect(MockMvcResultMatchers.status().isOk()).andReturn();

        List<EmployeeResponse> employeeResponseList = objectMapper.readValue(
                resultTwo.getResponse().getContentAsString(),
                new TypeReference<List<EmployeeResponse>>() {
                });

        EmployeeResponse employeeResponse = employeeResponseList.get(0);
        assertEquals(positionResponse.getSalary(), employeeResponse.getCurrentSalary());
        assertEquals(positionResponse.getStartDate(), employeeResponse.getCurrentEmploymentStartDate());
        assertEquals(positionResponse.getName(), employeeResponse.getCurrentPosition());
    }

    @Test
    @DisplayName("Should add old position to employee and should not add a temporally overlapping position ")
    void addOldPositionToEmployee() throws Exception {
        PositionRequest positionRequest = new PositionRequest();
        positionRequest.setName("Junior Software Engineer");
        positionRequest.setSalary(5000.0);
        positionRequest.setStartDate(LocalDate.of(2022, 2, 23));
        positionRequest.setEndDate(LocalDate.of(2023, 2, 23));

        PositionRequest positionRequestSecond = new PositionRequest();
        positionRequestSecond.setName("Senior Software Engineer");
        positionRequestSecond.setSalary(15000.0);
        positionRequestSecond.setStartDate(LocalDate.of(2022, 4, 23));
        positionRequestSecond.setEndDate(LocalDate.of(2022, 9, 23));

        mockMvc.perform(MockMvcRequestBuilders.post("/employee/9/position")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(positionRequest)))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andReturn();

        mockMvc.perform(MockMvcRequestBuilders.post("/employee/9/position")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(positionRequestSecond)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andReturn();

        MvcResult resultTwo = mockMvc.perform(MockMvcRequestBuilders.get("/persons")
                        .header("Authorization", "Bearer " + token)
                        .param("firstName", "Robert")
                        .param("lastName", "Smith")
                        .param("pesel", "12345678901"))
                .andExpect(MockMvcResultMatchers.status().isOk()).andReturn();

        List<EmployeeResponse> employeeResponseList = objectMapper.readValue(
                resultTwo.getResponse().getContentAsString(),
                new TypeReference<List<EmployeeResponse>>() {
                });

        EmployeeResponse employeeResponse = employeeResponseList.get(0);
        assertEquals(null, employeeResponse.getCurrentSalary());
        assertEquals(null, employeeResponse.getCurrentEmploymentStartDate());
        assertEquals(null, employeeResponse.getCurrentPosition());
    }

    @Test
    @DisplayName("Should edit the employee's position to actual position")
    void editPosition() throws Exception {
        PositionRequest positionRequest = new PositionRequest();
        positionRequest.setName("Junior Software Engineer");
        positionRequest.setSalary(5000.0);
        positionRequest.setStartDate(LocalDate.of(2022, 2, 23));
        positionRequest.setEndDate(LocalDate.of(2023, 2, 23));

        PositionEditRequest positionEditRequest = new PositionEditRequest();
        positionEditRequest.setName("Senior Software Engineer");
        positionEditRequest.setSalary(8000.0);
        positionEditRequest.setStartDate(LocalDate.of(2023, 2, 23));
        positionEditRequest.setEndDate(null);

        MvcResult addPosition = mockMvc.perform(MockMvcRequestBuilders.post("/employee/9/position")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(positionRequest)))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andReturn();
        MvcResult editPosition = mockMvc.perform(MockMvcRequestBuilders.put("/employee/position/1")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(positionEditRequest)))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andReturn();

        PositionResponse positionEditedResponse = objectMapper.readValue(editPosition.getResponse().getContentAsString(), PositionResponse.class);

        MvcResult resultEmployee = mockMvc.perform(MockMvcRequestBuilders.get("/persons")
                        .header("Authorization", "Bearer " + token)
                        .param("firstName", "Robert")
                        .param("lastName", "Smith")
                        .param("pesel", "12345678901"))
                .andExpect(MockMvcResultMatchers.status().isOk()).andReturn();

        List<EmployeeResponse> employeeResponseList = objectMapper.readValue(
                resultEmployee.getResponse().getContentAsString(),
                new TypeReference<List<EmployeeResponse>>() {
                });

        EmployeeResponse employeeResponse = employeeResponseList.get(0);
        assertEquals(positionEditedResponse.getSalary(), employeeResponse.getCurrentSalary());
        assertEquals(positionEditedResponse.getStartDate(), employeeResponse.getCurrentEmploymentStartDate());
        assertEquals(positionEditedResponse.getName(), employeeResponse.getCurrentPosition());

    }
}
