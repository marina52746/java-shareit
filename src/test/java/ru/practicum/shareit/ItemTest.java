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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureTestDatabase
@AutoConfigureMockMvc
public class ItemTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    @Sql(scripts = {"/schema.sql", "/testUserData.sql"})
    void addItemOkTest() throws Exception {
        mockMvc.perform(
                post("/items")
                        .content("{\n" +
                                "  \"name\": \"Some Name\",\n" +
                                "  \"description\": \"some description\",\n" +
                                "  \"available\": true\n" +
                                "}")
                        .header("X-Sharer-User-Id", "3")
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());
        mockMvc.perform(get("/items/1").header("X-Sharer-User-Id", "3"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Some Name"))
                .andExpect(jsonPath("$.description").value("some description"))
                .andExpect(jsonPath("$.available").value(true));
        mockMvc.perform(get("/items/all"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Some Name"))
                .andExpect(jsonPath("$[0].description").value("some description"))
                .andExpect(jsonPath("$[0].available").value(true))
                .andExpect(jsonPath("$[0].owner.id").value(3))
                .andExpect(jsonPath("$[0].owner.name").value("olga"))
                .andExpect(jsonPath("$[0].owner.email").value("olga@mail.ru"));
    }

    @Test
    @Sql(scripts = {"/schema.sql", "/testUserData.sql"})
    void addItemEmptyNameTest() throws Exception {
        mockMvc.perform(
                post("/items")
                        .content("{\n" +
                                "  \"name\": \"\",\n" +
                                "  \"description\": \"description1234\",\n" +
                                "  \"available\": true\n" +
                                "}")
                        .header("X-Sharer-User-Id", "2")
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().is4xxClientError());
    }

    @Test
    @Sql(scripts = {"/schema.sql", "/testUserData.sql"})
    void addItemNullDescriptionTest() throws Exception {
        mockMvc.perform(
                post("/items")
                        .content("{\n" +
                                "  \"name\": \"namename\",\n" +
                                "  \"description\": null,\n" +
                                "  \"available\": true\n" +
                                "}")
                        .header("X-Sharer-User-Id", "4")
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().is4xxClientError());
    }

    @Test
    @Sql(scripts = {"/schema.sql", "/testUserData.sql"})
    void addItemWithoutAvailable() throws Exception {
        mockMvc.perform(
                post("/items")
                        .content("{\n" +
                                "  \"name\": \"Some Name\",\n" +
                                "  \"description\": \"some description\"\n" +
                                "}")
                        .header("X-Sharer-User-Id", "2")
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().is4xxClientError());
    }

    @Test
    @Sql(scripts = {"/schema.sql", "/testUserData.sql"})
    void addItemWithoutXSharerUser() throws Exception {
        mockMvc.perform(
                post("/items")
                        .content("{\n" +
                                "  \"name\": \"table\",\n" +
                                "  \"description\": \"description452\",\n" +
                                "  \"available\": true\n" +
                                "}")
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().is4xxClientError());
    }

    @Test
    @Sql(scripts = {"/schema.sql", "/testUserData.sql"})
    void updateItemOkTest() throws Exception {
        mockMvc.perform(
                post("/items")
                        .content("{\n" +
                                "  \"name\": \"bicycle\",\n" +
                                "  \"description\": \"bicycle description\",\n" +
                                "  \"available\": false\n" +
                                "}")
                        .header("X-Sharer-User-Id", "5")
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());
        mockMvc.perform(
                patch("/items/1")
                        .content("{\n" +
                                "  \"name\": \"almost new bicycle\",\n" +
                                "  \"description\": \"almost new bicycle description\",\n" +
                                "  \"available\": true\n" +
                                "}")
                        .header("X-Sharer-User-Id", "5")
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());
        mockMvc.perform(get("/items/1").header("X-Sharer-User-Id", "5"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("almost new bicycle"))
                .andExpect(jsonPath("$.description").value("almost new bicycle description"))
                .andExpect(jsonPath("$.available").value(true));
    }

    @Test
    @Sql(scripts = {"/schema.sql"})
    void updateItemNotExistsTest() throws Exception {
        mockMvc.perform(
                patch("/items/100")
                        .content("{\n" +
                                "  \"description\": \"updated description\",\n" +
                                "}")
                        .header("X-Sharer-User-Id", "1")
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().is4xxClientError());
    }

    @Test
    @Sql(scripts = {"/schema.sql", "/testUserData.sql"})
    void getUserItemsTest() throws Exception {
        mockMvc.perform(
                post("/items")
                        .content("{\n" +
                                "  \"name\": \"ping-pong table\",\n" +
                                "  \"description\": \"ping-pong table\",\n" +
                                "  \"available\": true\n" +
                                "}")
                        .header("X-Sharer-User-Id", "4")
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());
        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", "4"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("ping-pong table"))
                .andExpect(jsonPath("$[0].description").value("ping-pong table"))
                .andExpect(jsonPath("$[0].available").value(true));
    }

    @Test
    @Sql(scripts = {"/schema.sql", "/testUserData.sql"})
    void findByTextTest() throws Exception {
        mockMvc.perform(
                post("/items")
                        .content("{\n" +
                                "  \"name\": \"some thing\",\n" +
                                "  \"description\": \"thing\",\n" +
                                "  \"available\": true\n" +
                                "}")
                        .header("X-Sharer-User-Id", "2")
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());
        mockMvc.perform(get("/items/search?text=thIN&from=0&size=5")
                        .header("X-Sharer-User-Id", "4"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("some thing"))
                .andExpect(jsonPath("$[0].description").value("thing"))
                .andExpect(jsonPath("$[0].available").value(true));
        mockMvc.perform(get("/items/search?text=abcd") //items/search?text={text}
                        .header("X-Sharer-User-Id", "4"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    @Sql(scripts = {"/schema.sql", "/testUserData.sql", "/testItemData.sql",
            "/testBookingData.sql", "/testCommentData.sql"})
    void getItemWithBookingsAndCommentsTest() throws Exception {
        mockMvc.perform(get("/items/7").header("X-Sharer-User-Id", "3"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(7))
                .andExpect(jsonPath("$.name").value("детский бассейн"))
                .andExpect(jsonPath("$.description").value("резиновая емкость на 300 л"))
                .andExpect(jsonPath("$.lastBooking.id").value(1))
                .andExpect(jsonPath("$.lastBooking.bookerId").value(1))
                .andExpect(jsonPath("$.comments[0].id").value(1))
                .andExpect(jsonPath("$.comments[0].text").value("отличный бассейн, дети были счастливы"))
                .andExpect(jsonPath("$.comments[0].authorName").value("alex"))
                .andExpect(jsonPath("$.comments[0].authorId").value(1))
                .andExpect(jsonPath("$.comments[0].itemId").value(7))
                .andExpect(jsonPath("$.comments[0].created").isNotEmpty());
    }

    @Test
    @Sql(scripts = {"/schema.sql", "/testUserData.sql", "/testItemData.sql"})
    void checkScriptTestData() throws Exception {
        mockMvc.perform(get("/items/all"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(7));
    }
}
