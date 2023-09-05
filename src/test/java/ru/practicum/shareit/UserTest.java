package ru.practicum.shareit;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureTestDatabase
@AutoConfigureMockMvc
public class UserTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    @Sql(scripts = {"/schema.sql"})
    void addUserOkTest() throws Exception {
        mockMvc.perform(
                        post("/users")
                                .content("{\n" +
                                        "  \"id\": 1,\n" +
                                        "  \"name\": \"Some Name\",\n" +
                                        "  \"email\": \"some@mail.ru\"" +
                                        "}")
                                .contentType(MediaType.APPLICATION_JSON)
                ).andExpect(status().isOk());
        mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Some Name"))
                .andExpect(jsonPath("$.email").value("some@mail.ru"));
        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Some Name"))
                .andExpect(jsonPath("$[0].email").value("some@mail.ru"));
    }

    @Test
    @Sql(scripts = {"/schema.sql"})
    void addUserFailEmailTest() throws Exception {
        mockMvc.perform(
                post("/users")
                        .content("{\n" +
                                "  \"id\": 1,\n" +
                                "  \"name\": \"name123\",\n" +
                                "  \"email\": \"text123\"" +
                                "}")
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().is4xxClientError());
    }

    @Test
    @Sql(scripts = {"/schema.sql"})
    void addUserNullEmailTest() throws Exception {
        mockMvc.perform(
                post("/users")
                        .content("{\n" +
                                "  \"id\": 1,\n" +
                                "  \"name\": \"name567\",\n" +
                                "  \"email\": null" +
                                "}")
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().is4xxClientError());
    }

    @Test
    @Sql(scripts = {"/schema.sql"})
    void addUserNoEmailTest() throws Exception {
        mockMvc.perform(
                post("/users")
                        .content("{\n" +
                                "  \"id\": 1,\n" +
                                "  \"name\": \"anna\",\n" +
                                "}")
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().is4xxClientError());
    }

    @Test
    @Sql(scripts = {"/schema.sql"})
    void updateUserChangeNameTest() throws Exception {
        mockMvc.perform(
                post("/users")
                        .content("{\n" +
                                "  \"name\": \"Some Name\",\n" +
                                "  \"email\": \"some@mail.ru\"" +
                                "}")
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());
        mockMvc.perform(
                patch("/users/1")
                        .content("{\n" +
                                "  \"name\": \"goodname\"\n" +
                                "}")
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());
    }

    @Test
    @Sql(scripts = {"/schema.sql"})
    void updateUserChangeEmailTest() throws Exception {
        mockMvc.perform(
                post("/users")
                        .content("{\n" +
                                "  \"name\": \"darya\",\n" +
                                "  \"email\": \"darya@mail.ru\"" +
                                "}")
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());
        mockMvc.perform(
                patch("/users/1")
                        .content("{\n" +
                                "  \"email\": \"dasha@mail.ru\"\n" +
                                "}")
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());
    }

    @Test
    @Sql(scripts = {"/schema.sql"})
    void updateUserNotExistsTest() throws Exception {
        mockMvc.perform(
                patch("/users/100")
                        .content("{\n" +
                                "  \"email\": \"new@mail.ru\",\n" +
                                "}")
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().is4xxClientError());
    }

    @Test
    @Sql(scripts = {"/schema.sql"})
    void deleteUserOkTest() throws Exception {
        mockMvc.perform(
                post("/users")
                        .content("{\n" +
                                "  \"name\": \"eugen\",\n" +
                                "  \"email\": \"eugen@mail.ru\"" +
                                "}")
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());
        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(1));
        mockMvc.perform(delete("/users/1"))
                .andExpect(status().isOk());
        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    @Sql(scripts = {"/schema.sql"})
    void deleteUserNotExistTest() throws Exception {
        mockMvc.perform(delete("/users/125"))
                .andExpect(status().isNotFound());
    }

    @Test
    @Sql(scripts = {"/schema.sql", "/testUserData.sql"})
    void checkScriptTestData() throws Exception {
        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(5));
    }
}











