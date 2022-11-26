package ru.yandex.practicum.filmorate.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.in;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@ComponentScan("ru.yandex.practicum.filmorate")
public class UserControllerTest extends AbstractControllerTest {

    private final User user = User.builder()
            .id(null)
            .login("login")
            .name("username")
            .email("test@domain.xxx")
            .birthday(LocalDate.of(1980, 1, 1))
            .build();

    @Autowired
    public UserControllerTest(MockMvc mockMvc) {
        super(mockMvc);
    }

    @Test
    @DisplayName("Создание валидного пользователя")
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    void createValidUserTest() throws Exception {
        MockHttpServletRequestBuilder builder = post("/users")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(mapper.writeValueAsString(user));
        MvcResult result = mockMvc.perform(builder)
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.id").value("1"))
                .andReturn();
        User createdUser = mapper.readValue(result.getResponse().getContentAsString(), User.class);
        Integer expectedId = 1;
        assertEquals(user.withId(expectedId), createdUser, "Server hasn't create user.");
    }

    @Test
    @DisplayName("Создание пользователя с неправильным email")
    void createUserWithInvalidEmailTest() throws Exception {
        User testUser = user.withEmail("xxx");
        MockHttpServletRequestBuilder builder = post("/users")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(mapper.writeValueAsString(testUser));
        mockMvc.perform(builder)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.reasons[0]")
                        .value(String.format(ERROR_MES_TEMPLATE, "user", "email", "must be a well-formed email address")));
    }

    @Test
    @DisplayName("Создание пользователя с email NULL")
    void createUserWithNulldEmailTest() throws Exception {
        User testUser = user.withEmail(null);
        MockHttpServletRequestBuilder builder = post("/users")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(mapper.writeValueAsString(testUser));
        mockMvc.perform(builder)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.reasons[0]", in(Arrays.asList(
                        String.format(ERROR_MES_TEMPLATE, "user", "email", "must not be blank"),
                        String.format(ERROR_MES_TEMPLATE, "user", "email", "must not be null")
                ))));
    }

    @Test
    @DisplayName("Создание пользователя с датой рождения в будущем")
    void createUserWithFutureBirthdayTest() throws Exception {
        User testUser = user.withBirthday(LocalDate.of(2033, 1, 1));
        MockHttpServletRequestBuilder builder = post("/users")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(mapper.writeValueAsString(testUser));
        mockMvc.perform(builder)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.reasons[0]")
                        .value(String.format(ERROR_MES_TEMPLATE, "user", "birthday", "must be a past date")));
    }

    @Test
    @DisplayName("Создание пользователя с датой рождения NULL")
    void createUserWithNullBirthdayTest() throws Exception {
        User testUser = user.withBirthday(null);
        MockHttpServletRequestBuilder builder = post("/users")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(mapper.writeValueAsString(testUser));
        mockMvc.perform(builder)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.reasons[0]")
                        .value(String.format(ERROR_MES_TEMPLATE, "user", "birthday", "must not be null")));
    }

    @Test
    @DisplayName("Создание пользователя с пустым логином")
    void createUserWithEmptyLoginTest() throws Exception {
        User testUser = user.withLogin("");
        MockHttpServletRequestBuilder builder = post("/users")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(mapper.writeValueAsString(testUser));
        mockMvc.perform(builder)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.reasons[0]")
                        .value(String.format(ERROR_MES_TEMPLATE, "user", "login", "must not be blank")));
    }

    @Test
    @DisplayName("Обновление пользователя")
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    void updateUserTest() throws Exception {
        MockHttpServletRequestBuilder builder = post("/users")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(mapper.writeValueAsString(user));
        // Creating user
        mockMvc.perform(builder)
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE));
        User testUser = user.withId(1).withName("Updated username");
        builder = put("/users")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(mapper.writeValueAsString(testUser));
        MvcResult result = mockMvc.perform(builder)
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andReturn();
        User updatedUser = mapper.readValue(result.getResponse().getContentAsString(), User.class);
        String expectedName = "Updated username";
        assertEquals(
                expectedName, updatedUser.getName(),
                "Server hasn't update user"
        );
    }

    @Test
    @DisplayName("Обновление пользователя c id NULL")
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    void updateUserWithNullIdTest() throws Exception {
        MockHttpServletRequestBuilder builder = post("/users")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(mapper.writeValueAsString(user));
        // Creating user
        mockMvc.perform(builder)
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE));
        User testUser = user.withId(null).withName("Updated username");
        builder = put("/users")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(mapper.writeValueAsString(testUser));
        String expectedMes = "User with id=" + testUser.getId() + " doesn't exist.";
        mockMvc.perform(builder)
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.reasons[0]").value(expectedMes));
    }

    @Test
    @DisplayName("Получение списка всех пользователей")
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    void getAllUsersTest() throws Exception {
        MockHttpServletRequestBuilder builder = post("/users")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(mapper.writeValueAsString(user));
        // Creating user1
        mockMvc.perform(builder)
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE));
        // Creating user2
        mockMvc.perform(builder)
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE));
        // Getting all users
        builder = get("/users");
        MvcResult result = mockMvc.perform(builder)
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andReturn();
        List<User> resultFilms = mapper
                .readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
                });
        Integer expectedFilmsCount = 2;
        assertEquals(expectedFilmsCount, resultFilms.size(),
                "Server hasn't return right users list.");
    }
}
