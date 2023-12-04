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
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import pl.task.personalities.PersonalitiesApplication;
import pl.task.personalities.model.dto.request.LoginRequest;
import pl.task.personalities.model.dto.request.PersonEditRequest;
import pl.task.personalities.model.dto.request.PersonRequest;
import pl.task.personalities.model.dto.request.SignupRequest;
import pl.task.personalities.model.dto.response.EmployeeResponse;
import pl.task.personalities.model.dto.response.JwtResponse;
import pl.task.personalities.model.dto.response.PensionerResponse;
import pl.task.personalities.model.dto.response.StudentResponse;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = {PersonalitiesApplication.class}, properties = "src/test/resources/application.properties")
@AutoConfigureMockMvc
@Sql(scripts = "classpath:database_before_test.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "classpath:database_after_test.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class PersonControllerTest {
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
    @DisplayName("Should return all person")
    void getAllPerson() throws Exception {
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/persons")
                        .header("Authorization", "Bearer " + token))
                .andExpect(MockMvcResultMatchers.status().isOk()).andReturn();

        List<StudentResponse> personResponseList = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                new TypeReference<List<StudentResponse>>() {
                });

        assertEquals(9, personResponseList.size());
    }

    @Test
    @DisplayName("Should return a student with name Helen.")
    void getPersonByParameters() throws Exception {
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/persons")
                        .header("Authorization", "Bearer " + token)
                        .param("firstName", "Helen")
                        .param("lastName", "Maily"))
                .andExpect(MockMvcResultMatchers.status().isOk()).andReturn();

        List<StudentResponse> studentResponses = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                new TypeReference<List<StudentResponse>>() {
                });

        StudentResponse studentResponse = studentResponses.get(0);
        assertEquals("Helen", studentResponse.getFirstName());
        assertEquals("Maily", studentResponse.getLastName());
        assertEquals("IT", studentResponse.getFieldOfStudy());
    }

    @Test
    @DisplayName("Should return a employee with the specified parameters")
    void find_shouldReturnSpecifiedPerson() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/persons?heightFrom=179&heightTo=182&weightFrom=70&weightTo=76&type=Employee")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value(11))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].firstName").value("James"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].lastName").value("Taylor"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].height").value(180))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].weight").value(75.0))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].emailAddress").value("james.taylor@example.com"));
    }

    @Test
    @DisplayName("Should succesfully save new person with specified details")
    void save_shouldInsertNewPerson_whenSavingValidPerson() throws Exception {
        PersonRequest personRequest = new PersonRequest();
        personRequest.setTypeOfPerson("PENSIONER");

        Map<String, Object> fields = new HashMap<>();
        fields.put("firstName", "Marta");
        fields.put("lastName", "Doe");
        fields.put("pesel", "99022401621");
        fields.put("height", 180);
        fields.put("weight", 75.5);
        fields.put("emailAddress", "john.doe@example.com");
        fields.put("amountOfPension", 2300.0);
        fields.put("yearsWorked", 5);
        personRequest.setFields(fields);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/persons")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(personRequest)))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andReturn();

        PensionerResponse pensionerResponse = objectMapper.readValue(result.getResponse().getContentAsString(), PensionerResponse.class);
        assertEquals("Marta", pensionerResponse.getFirstName());
        assertEquals("Doe", pensionerResponse.getLastName());
        assertEquals(2300.0, pensionerResponse.getAmountOfPension());
        assertEquals(5, pensionerResponse.getYearsWorked());
    }

    @Test
    @DisplayName("Should not save the new person because of incorrect data")
    void save_notShouldInsertNewPerson_WhenFieldIsNotValid() throws Exception {
        PersonRequest personRequest = new PersonRequest();
        personRequest.setTypeOfPerson("PENSIONER");

        Map<String, Object> fields = new HashMap<>();
        fields.put("firstName", "Marta");
        fields.put("lastName", "Doe");
        fields.put("pesel", "99022401621");
        fields.put("height", -180);
        fields.put("weight", -75.5);
        fields.put("emailAddress", "john.doe@example.com");
        fields.put("amountOfPension", -2300.0);
        fields.put("yearsWorked", 5);
        personRequest.setFields(fields);

        mockMvc.perform(MockMvcRequestBuilders.post("/persons")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(personRequest)))
                .andExpect(status().isBadRequest());

        MvcResult resultTwo = mockMvc.perform(MockMvcRequestBuilders.get("/persons")
                        .header("Authorization", "Bearer " + token))
                .andExpect(MockMvcResultMatchers.status().isOk()).andReturn();

        List<StudentResponse> personResponseList = objectMapper.readValue(
                resultTwo.getResponse().getContentAsString(),
                new TypeReference<List<StudentResponse>>() {
                });

        assertEquals(9, personResponseList.size());
    }


    @Test
    @DisplayName("Should edit person")
    void updatePerson() throws Exception {
        PersonEditRequest personEditRequest = new PersonEditRequest();

        Map<String, Object> updates = new HashMap<>();
        updates.put("firstName", "Sylwester");
        updates.put("lastName", "Wojcik");
        updates.put("height", 185);
        updates.put("yearsWorked", 25);
        personEditRequest.setUpdates(updates);
        personEditRequest.setVersion(0);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.put("/persons/3")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(personEditRequest)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
        PensionerResponse pensionerEditResponse = objectMapper.readValue(result.getResponse().getContentAsString(), PensionerResponse.class);

        MvcResult resultTwo = mockMvc.perform(MockMvcRequestBuilders.get("/persons")
                        .header("Authorization", "Bearer " + token)
                        .param("firstName", "Sylwester")
                        .param("lastName", "Wojcik")
                        .param("yearsWorked", "25"))
                .andExpect(MockMvcResultMatchers.status().isOk()).andReturn();

        List<PensionerResponse> pensionerResponsesList = objectMapper.readValue(
                resultTwo.getResponse().getContentAsString(),
                new TypeReference<List<PensionerResponse>>() {
                });

        PensionerResponse pensionerAfterEdit = pensionerResponsesList.get(0);
        assertEquals(pensionerEditResponse.getFirstName(), pensionerAfterEdit.getFirstName());
        assertEquals(pensionerEditResponse.getLastName(), pensionerAfterEdit.getLastName());
        assertEquals(pensionerEditResponse.getYearsWorked(), pensionerAfterEdit.getYearsWorked());

    }

    @Test
    @DisplayName("Should add person from csv file and check if anyone has been added")
    public void shouldAddPersonFromCsv() throws Exception {
        String csvContent = "EMPLOYEE,Alice,Brown,12345678902,165,60.0,alice.brown@example.com\n" +
                "STUDENT,Michael,Johnson,99022401333,175,70.0,michael.johnson@example.com,Oxford,2,Business,1500.0\n" +
                "PENSIONER,Andy,Doe,99022401765,190,95.5,Andy.doe@example.com,2300.0,5";

        MockMultipartFile file = new MockMultipartFile("file", "test.csv", "text/csv", csvContent.getBytes());

        String result = mockMvc.perform(MockMvcRequestBuilders.multipart("/persons/upload")
                        .file(file)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(MockMvcResultMatchers.status().isAccepted())
                .andReturn()
                .getResponse()
                .getContentAsString();

        MvcResult resultTwo = mockMvc.perform(MockMvcRequestBuilders.get("/persons")
                        .header("Authorization", "Bearer " + token)
                        .param("firstName", "Alice")
                        .param("lastName", "Brown"))
                .andExpect(MockMvcResultMatchers.status().isOk()).andReturn();

        List<EmployeeResponse> employeeResponseList = objectMapper.readValue(
                resultTwo.getResponse().getContentAsString(),
                new TypeReference<List<EmployeeResponse>>() {
                });

        EmployeeResponse employeeResponseAfterAddFromCSV = employeeResponseList.get(0);
        assertEquals("Alice", employeeResponseAfterAddFromCSV.getFirstName());
        assertEquals("Brown", employeeResponseAfterAddFromCSV.getLastName());
    }

}