package ru.practicum.shareit;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
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
public class CommentTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @Sql(scripts = {"/schema.sql", "/testUserData.sql", "/testItemData.sql", "/testBookingData.sql"})
    void addCommentOkTest() throws Exception {
        mockMvc.perform(
                post("/items/1/comment")
                        .content("{\n" +
                                "  \"text\": \"good thing\"\n" +
                                "}")
                        .header("X-Sharer-User-Id", "4")
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());
        mockMvc.perform(get("/items/1").header("X-Sharer-User-Id", "5"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("перфоратор"))
                .andExpect(jsonPath("$.description").value("большой и шумный, то есть, мощный перфоратор"))
                .andExpect(jsonPath("$.available").value(true))
                .andExpect(jsonPath("$.comments[0].id").value(1))
                .andExpect(jsonPath("$.comments[0].text").value("good thing"))
                .andExpect(jsonPath("$.comments[0].authorName").value("sergey"))
                .andExpect(jsonPath("$.comments[0].authorId").value(4))
                .andExpect(jsonPath("$.comments[0].itemId").value(1))
                .andExpect(jsonPath("$.comments[0].created").isNotEmpty());

        mockMvc.perform(get("/items/allComments"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    @Sql(scripts = {"/schema.sql", "/testUserData.sql", "/testItemData.sql", "/testBookingData.sql"})
    void addCommentBeforeBookingTest() throws Exception {
        mockMvc.perform(
                post("/items/3/comment")
                        .content("{\n" +
                                "  \"text\": \"good thing\"\n" +
                                "}")
                        .header("X-Sharer-User-Id", "3")
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().is4xxClientError());
    }

    @Test
    @Sql(scripts = {"/schema.sql", "/testUserData.sql", "/testItemData.sql", "/testBookingData.sql"})
    void addCommentItemNotExistTest() throws Exception {
        mockMvc.perform(
                post("/items/33/comment")
                        .content("{\n" +
                                "  \"text\": \"good thing\"\n" +
                                "}")
                        .header("X-Sharer-User-Id", "3")
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().is4xxClientError());
    }

    @Test
    @Sql(scripts = {"/schema.sql", "/testUserData.sql", "/testItemData.sql", "/testBookingData.sql"})
    void addCommentUserNotExistTest() throws Exception {
        mockMvc.perform(
                post("/items/6/comment")
                        .content("{\n" +
                                "  \"text\": \"good thing\"\n" +
                                "}")
                        .header("X-Sharer-User-Id", "55")
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().is4xxClientError());
    }

    @Test
    @Sql(scripts = {"/schema.sql", "/testUserData.sql", "/testItemData.sql",
            "/testBookingData.sql", "/testCommentData.sql"})
    void checkScriptTestData() throws Exception {
        mockMvc.perform(get("/items/allComments"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(2));
    }
}
