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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureTestDatabase
@AutoConfigureMockMvc
public class ItemRequestTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    @Sql(scripts = {"/schema.sql", "/testUserData.sql"})
    void addItemRequestOkTest() throws Exception {
        mockMvc.perform(
                post("/requests")
                        .content("{\n" +
                                "  \"description\": \"want some thing\"" +
                                "}")
                        .header("X-Sharer-User-Id", "2")
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());
        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", "2")) //own requests
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].description").value("want some thing"))
                .andExpect(jsonPath("$[0].created").isNotEmpty())
                .andExpect(jsonPath("$[0].items").exists());
        mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", "2")) //other user requests for user 2
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(0));
        mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", "1")) //other user requests for user 1
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].description").value("want some thing"))
                .andExpect(jsonPath("$[0].created").isNotEmpty())
                .andExpect(jsonPath("$[0].items").exists());
    }

    @Test
    @Sql(scripts = {"/schema.sql", "/testUserData.sql"})
    void addItemRequestAndItemOkTest() throws Exception {
        mockMvc.perform(
                post("/requests")
                        .content("{\n" +
                                "  \"description\": \"want motocycle\"" +
                                "}")
                        .header("X-Sharer-User-Id", "4")
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());
        mockMvc.perform(
                post("/items")
                        .content("{\n" +
                                "  \"name\": \"motocycle\",\n" +
                                "  \"description\": \"good motocycle\",\n" +
                                "  \"available\": true,\n" +
                                "  \"requestId\": 1\n" +
                                "}")
                        .header("X-Sharer-User-Id", "3")
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());
        mockMvc.perform(get("/items/1").header("X-Sharer-User-Id", "3"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("motocycle"))
                .andExpect(jsonPath("$.description").value("good motocycle"))
                .andExpect(jsonPath("$.available").value(true))
                .andExpect(jsonPath("$.comments").exists());
    }

    @Test
    @Sql(scripts = {"/schema.sql", "/testUserData.sql"})
    void getItemRequestByIdTest() throws Exception {
        mockMvc.perform(
                post("/requests")
                        .content("{\n" +
                                "  \"description\": \"want motocycle\"" +
                                "}")
                        .header("X-Sharer-User-Id", "4")
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());
        mockMvc.perform(get("/requests/1").header("X-Sharer-User-Id", "3"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.description").value("want motocycle"))
                .andExpect(jsonPath("$.created").exists())
                .andExpect(jsonPath("$.items").exists());
    }
}
