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
public class BookingTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    @Sql(scripts = {"/schema.sql", "/testUserData.sql", "/testItemData.sql"})
    void addBookingTest() throws Exception {
        mockMvc.perform(
                post("/bookings")
                        .content("{\n" +
                                "  \"itemId\": 2,\n" +
                                "  \"start\": \"2023-10-25T09:00:00\",\n" +
                                "  \"end\": \"2023-10-30T09:00:00\"" +
                                "}")
                        .header("X-Sharer-User-Id", "3")
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());
        mockMvc.perform(get("/bookings/1").header("X-Sharer-User-Id", "3"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.start").value("2023-10-25T09:00:00"))
                .andExpect(jsonPath("$.end").value("2023-10-30T09:00:00"))
                .andExpect(jsonPath("$.status").value("WAITING"))
                .andExpect(jsonPath("$.booker.id").value(3))
                .andExpect(jsonPath("$.item.id").value(2))
                .andExpect(jsonPath("$.item.name").value("дрель"));
        mockMvc.perform(get("/bookings/all"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].start").value("2023-10-25T09:00:00"))
                .andExpect(jsonPath("$[0].end").value("2023-10-30T09:00:00"))
                .andExpect(jsonPath("$[0].status").value("WAITING"))
                .andExpect(jsonPath("$[0].booker.id").value(3))
                .andExpect(jsonPath("$[0].item.id").value(2))
                .andExpect(jsonPath("$[0].item.name").value("дрель"));
    }

    @Test
    @Sql(scripts = {"/schema.sql", "/testUserData.sql", "/testItemData.sql"})
    void addBookingItemIdNotExistTest() throws Exception {
        mockMvc.perform(
                post("/bookings")
                        .content("{\n" +
                                "  \"itemId\": 54,\n" +
                                "  \"start\": \"2023-10-25T09:00:00\",\n" +
                                "  \"end\": \"2023-10-30T09:00:00\"" +
                                "}")
                        .header("X-Sharer-User-Id", "4")
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().is4xxClientError());
    }

    @Test
    @Sql(scripts = {"/schema.sql", "/testUserData.sql", "/testItemData.sql"})
    void addBookingItemSharerIdNotExistTest() throws Exception {
        mockMvc.perform(
                post("/bookings")
                        .content("{\n" +
                                "  \"itemId\": 5,\n" +
                                "  \"start\": \"2023-10-25T09:00:00\",\n" +
                                "  \"end\": \"2023-10-30T09:00:00\"" +
                                "}")
                        .header("X-Sharer-User-Id", "64")
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().is4xxClientError());
    }

    @Test
    @Sql(scripts = {"/schema.sql", "/testUserData.sql", "/testItemData.sql"})
    void addBookingStartNullTest() throws Exception {
        mockMvc.perform(
                post("/bookings")
                        .content("{\n" +
                                "  \"itemId\": 5,\n" +
                                "  \"start\": null,\n" +
                                "  \"end\": \"2023-10-30T09:00:00\"" +
                                "}")
                        .header("X-Sharer-User-Id", "2")
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().is4xxClientError());
    }

    @Test
    @Sql(scripts = {"/schema.sql", "/testUserData.sql", "/testItemData.sql"})
    void addBookingEndBeforeStartTest() throws Exception {
        mockMvc.perform(
                post("/bookings")
                        .content("{\n" +
                                "  \"itemId\": 5,\n" +
                                "  \"start\": \"2023-10-25T09:00:00\",\n" +
                                "  \"end\": \"2023-10-10T09:00:00\"" +
                                "}")
                        .header("X-Sharer-User-Id", "2")
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().is4xxClientError());
    }

    @Test
    @Sql(scripts = {"/schema.sql", "/testUserData.sql"})
    void addBookingItemNotAvailableTest() throws Exception {
        mockMvc.perform(
                post("/items")
                        .content("{\n" +
                                "  \"name\": \"bicycle\",\n" +
                                "  \"description\": \"bicycle description\",\n" +
                                "  \"available\": false\n" +
                                "}")
                        .header("X-Sharer-User-Id", "5")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        mockMvc.perform(
                post("/bookings")
                        .content("{\n" +
                                "  \"itemId\": 1,\n" +
                                "  \"start\": \"2023-10-25T09:00:00\",\n" +
                                "  \"end\": \"2023-10-30T09:00:00\"" +
                                "}")
                        .header("X-Sharer-User-Id", "2")
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().is4xxClientError());
    }

    @Test
    @Sql(scripts = {"/schema.sql", "/testUserData.sql", "/testItemData.sql"})
    void updateBookingTest() throws Exception {
        mockMvc.perform(
                post("/bookings")
                        .content("{\n" +
                                "  \"itemId\": 2,\n" +
                                "  \"start\": \"2023-10-25T09:00:00\",\n" +
                                "  \"end\": \"2023-10-30T09:00:00\"" +
                                "}")
                        .header("X-Sharer-User-Id", "5")
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());
        mockMvc.perform(
                patch("/bookings/1/?approved=true")
                        .header("X-Sharer-User-Id", "2")
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());
        mockMvc.perform(get("/bookings/1").header("X-Sharer-User-Id", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.start").value("2023-10-25T09:00:00"))
                .andExpect(jsonPath("$.end").value("2023-10-30T09:00:00"))
                .andExpect(jsonPath("$.status").value("APPROVED"))
                .andExpect(jsonPath("$.booker.id").value(5))
                .andExpect(jsonPath("$.item.id").value(2))
                .andExpect(jsonPath("$.item.name").value("дрель"));
    }

    @Test
    @Sql(scripts = {"/schema.sql", "/testUserData.sql"})
    void updateBookingNotExistTest() throws Exception {
        mockMvc.perform(
                patch("/bookings/99/?approved=false")
                        .header("X-Sharer-User-Id", "2")
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().is4xxClientError());
    }

    @Test
    @Sql(scripts = {"/schema.sql", "/testUserData.sql", "/testItemData.sql"})
    void updateBookingByBookerTest() throws Exception {
        mockMvc.perform(
                post("/bookings")
                        .content("{\n" +
                                "  \"itemId\": 2,\n" +
                                "  \"start\": \"2023-10-25T09:00:00\",\n" +
                                "  \"end\": \"2023-10-30T09:00:00\"" +
                                "}")
                        .header("X-Sharer-User-Id", "3")
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());
        mockMvc.perform(
                patch("/bookings/1/?approved=true")
                        .header("X-Sharer-User-Id", "3")
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().is4xxClientError());
    }

    @Test    //bookings?state={state}
    @Sql(scripts = {"/schema.sql", "/testUserData.sql", "/testItemData.sql", "/testBookingData.sql"})
    void getBookerBookingsAllFutureTest() throws Exception {
        mockMvc.perform(get("/bookings?state=ALL")
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(3));
    }

    @Test    //bookings?state={state}
    @Sql(scripts = {"/schema.sql", "/testUserData.sql", "/testItemData.sql", "/testBookingData.sql"})
    void getBookerBookingsStateCurrentTest() throws Exception {
        mockMvc.perform(get("/bookings?state=CURRENT")
                        .header("X-Sharer-User-Id", "2"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test    //bookings?state={state}
    @Sql(scripts = {"/schema.sql", "/testUserData.sql", "/testItemData.sql", "/testBookingData.sql"})
    void getBookerBookingsStatePastTest() throws Exception {
        mockMvc.perform(get("/bookings?state=PAST")
                        .header("X-Sharer-User-Id", "3"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test    //bookings?state={state}
    @Sql(scripts = {"/schema.sql", "/testUserData.sql", "/testItemData.sql", "/testBookingData.sql"})
    void getBookerBookingsStateWaitingTest() throws Exception {
        mockMvc.perform(get("/bookings?state=WAITING")
                        .header("X-Sharer-User-Id", "4"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test    //bookings?state={state}
    @Sql(scripts = {"/schema.sql", "/testUserData.sql", "/testItemData.sql", "/testBookingData.sql"})
    void getBookerBookingsStateFutureTest() throws Exception {
        mockMvc.perform(get("/bookings?state=FUTURE")
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(7))
                .andExpect(jsonPath("$[0].start").value("2023-09-12T12:00:00"))
                .andExpect(jsonPath("$[0].end").value("2023-09-26T12:00:00"))
                .andExpect(jsonPath("$[0].status").value("APPROVED"))
                .andExpect(jsonPath("$[0].booker.id").value(1))
                .andExpect(jsonPath("$[0].item.id").value(2))
                .andExpect(jsonPath("$[0].item.name").value("дрель"));
    }

    @Test    //bookings?state={state}
    @Sql(scripts = {"/schema.sql", "/testUserData.sql", "/testItemData.sql", "/testBookingData.sql"})
    void getBookerBookingsStateUnsupportedTest() throws Exception {
        mockMvc.perform(get("/bookings?state=UNKNOWN")
                .header("X-Sharer-User-Id", "1"))
                .andExpect(status().is4xxClientError());
    }

    @Test    //bookings?state={state}
    @Sql(scripts = {"/schema.sql", "/testUserData.sql", "/testItemData.sql", "/testBookingData.sql"})
    void getBookerBookingsStateRejectedTest() throws Exception {
        mockMvc.perform(get("/bookings?state=REJECTED")
                        .header("X-Sharer-User-Id", "2"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    @Sql(scripts = {"/schema.sql", "/testUserData.sql", "/testItemData.sql", "/testBookingData.sql"})
    void getOwnerBookingsStatePastTest() throws Exception {
        mockMvc.perform(get("/bookings/owner?state=PAST")
                        .header("X-Sharer-User-Id", "3"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(3))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].start").value("2023-08-20T09:00:00"))
                .andExpect(jsonPath("$[0].end").value("2023-08-30T12:00:00"))
                .andExpect(jsonPath("$[0].status").value("APPROVED"))
                .andExpect(jsonPath("$[0].booker.id").value(1))
                .andExpect(jsonPath("$[0].item.id").value(7))
                .andExpect(jsonPath("$[0].item.name").value("детский бассейн"));
    }

    @Test
    @Sql(scripts = {"/schema.sql", "/testUserData.sql", "/testItemData.sql", "/testBookingData.sql"})
    void getOwnerBookingsStateWaitingTest() throws Exception {
        mockMvc.perform(get("/bookings/owner?state=WAITING")
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    @Sql(scripts = {"/schema.sql", "/testUserData.sql", "/testItemData.sql", "/testBookingData.sql"})
    void getOwnerBookingsStateAllTest() throws Exception {
        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", "3"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(5));
    }

    @Test
    @Sql(scripts = {"/schema.sql", "/testUserData.sql", "/testItemData.sql", "/testBookingData.sql"})
    void getOwnerBookingsStateCurrentTest() throws Exception {
        mockMvc.perform(get("/bookings/owner?state=CURRENT")
                        .header("X-Sharer-User-Id", "3"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    @Sql(scripts = {"/schema.sql", "/testUserData.sql", "/testItemData.sql", "/testBookingData.sql"})
    void getOwnerBookingsStateFutureTest() throws Exception {
        mockMvc.perform(get("/bookings/owner?state=FUTURE")
                        .header("X-Sharer-User-Id", "3"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    @Sql(scripts = {"/schema.sql", "/testUserData.sql", "/testItemData.sql", "/testBookingData.sql"})
    void getOwnerBookingsStateRejectedTest() throws Exception {
        mockMvc.perform(get("/bookings/owner?state=REJECTED")
                        .header("X-Sharer-User-Id", "5"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    @Sql(scripts = {"/schema.sql", "/testUserData.sql", "/testItemData.sql", "/testBookingData.sql"})
    void getOwnerBookingsStateUnknownTest() throws Exception {
        mockMvc.perform(get("/bookings/owner?state=NOT_FOUND_STATE")
                        .header("X-Sharer-User-Id", "6"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @Sql(scripts = {"/schema.sql", "/testUserData.sql", "/testItemData.sql", "/testBookingData.sql"})
    void getBookingsByIdTest() throws Exception {
        mockMvc.perform(get("/bookings/8")
                        .header("X-Sharer-User-Id", "3"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(8))
                .andExpect(jsonPath("$.start").value("2023-09-26T09:00:00"))
                .andExpect(jsonPath("$.end").value("2023-09-29T12:00:00"))
                .andExpect(jsonPath("$.status").value("WAITING"))
                .andExpect(jsonPath("$.booker.id").value(3))
                .andExpect(jsonPath("$.item.id").value(3))
                .andExpect(jsonPath("$.item.name").value("паяльник"));
    }

    @Test
    @Sql(scripts = {"/schema.sql", "/testUserData.sql", "/testItemData.sql", "/testBookingData.sql"})
    void checkScriptTestData() throws Exception {
        mockMvc.perform(get("/bookings/all"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(10));
    }
}
