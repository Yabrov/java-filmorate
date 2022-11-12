package ru.yandex.practicum.filmorate.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
public class UserControllerTest {

    private User user;

    private final ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    public void createTestFilmObject() {
        user = new User();
        user.setId(null);
        user.setName("username");
        user.setLogin("login");
        user.setEmail("test@domain.xxx");
        user.setBirthday(LocalDate.of(1980, 1, 1));
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
                .andReturn();
        User createdUser = mapper.readValue(result.getResponse().getContentAsString(), User.class);
        Integer expectedId = 1;
        assertEquals(
                expectedId, createdUser.getId(),
                "Server hasn't create user with id=" + expectedId
        );
    }

    @Test
    @DisplayName("Создание пользователя с неправильным email")
    void createUserWithInvalidEmailTest() throws Exception {
        user.setEmail("xxx");
        MockHttpServletRequestBuilder builder = post("/users")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(mapper.writeValueAsString(user));
        String expectedMes = "Validation exception " +
                "[class: 'user', field: 'email', reason: 'must be a well-formed email address']";
        mockMvc.perform(builder)
                .andExpect(status().isBadRequest())
                .andExpect(content().string(expectedMes));
    }

    @Test
    @DisplayName("Создание пользователя с датой рождения в будущем")
    void createUserWithFutureBirthdayTest() throws Exception {
        user.setBirthday(LocalDate.of(2033, 1, 1));
        MockHttpServletRequestBuilder builder = post("/users")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(mapper.writeValueAsString(user));
        String expectedMes = "Validation exception " +
                "[class: 'user', field: 'birthday', reason: 'must be a past date']";
        mockMvc.perform(builder)
                .andExpect(status().isBadRequest())
                .andExpect(content().string(expectedMes));
    }

    @Test
    @DisplayName("Создание пользователя с пустым логином")
    void createUserWithEmptyLoginTest() throws Exception {
        user.setLogin("");
        MockHttpServletRequestBuilder builder = post("/users")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(mapper.writeValueAsString(user));
        String expectedMes = "Validation exception " +
                "[class: 'user', field: 'login', reason: 'must not be blank']";
        mockMvc.perform(builder)
                .andExpect(status().isBadRequest())
                .andExpect(content().string(expectedMes));
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
        user.setId(1);
        user.setName("Updated username");
        builder = put("/users")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(mapper.writeValueAsString(user));
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
    @DisplayName("Обновление пользователя c id=null")
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    void updateUserWithNullIdTest() throws Exception {
        MockHttpServletRequestBuilder builder = post("/users")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(mapper.writeValueAsString(user));
        // Creating user
        mockMvc.perform(builder)
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE));
        user.setId(null);
        user.setName("Updated username");
        builder = put("/users")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(mapper.writeValueAsString(user));
        String expectedMes = "User with id=" + user.getId() + " doesn't exist.";
        mockMvc.perform(builder)
                .andExpect(status().isNotFound())
                .andExpect(content().string(expectedMes));
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
                .readValue(result.getResponse().getContentAsString(), new TypeReference<>() {});
        Integer expectedFilmsCount = 2;
        assertEquals(expectedFilmsCount, resultFilms.size(),
                "Server hasn't return right users list.");
    }
}
