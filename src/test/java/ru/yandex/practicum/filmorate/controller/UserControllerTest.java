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

import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.in;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@ComponentScan("ru.yandex.practicum.filmorate")
public class UserControllerTest extends AbstractControllerTest {

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
                .content(getMapper().writeValueAsString(user));
        MvcResult result = mockMvc.perform(builder)
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.id").value("1"))
                .andReturn();
        User createdUser = getMapper().readValue(result.getResponse().getContentAsString(), User.class);
        Integer expectedId = 1;
        assertEquals(user.withId(expectedId), createdUser, "Server hasn't create user.");
    }

    @Test
    @DisplayName("Создание пользователя с неправильным email")
    void createUserWithInvalidEmailTest() throws Exception {
        User testUser = user.withEmail("xxx");
        MockHttpServletRequestBuilder builder = post("/users")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(getMapper().writeValueAsString(testUser));
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
                .content(getMapper().writeValueAsString(testUser));
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
                .content(getMapper().writeValueAsString(testUser));
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
                .content(getMapper().writeValueAsString(testUser));
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
                .content(getMapper().writeValueAsString(testUser));
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
                .content(getMapper().writeValueAsString(user));
        // Creating user
        mockMvc.perform(builder)
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE));
        User testUser = user.withId(1).withName("Updated username");
        builder = put("/users")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(getMapper().writeValueAsString(testUser));
        MvcResult result = mockMvc.perform(builder)
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andReturn();
        User updatedUser = getMapper().readValue(result.getResponse().getContentAsString(), User.class);
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
                .content(getMapper().writeValueAsString(user));
        // Creating user
        mockMvc.perform(builder)
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE));
        User testUser = user.withId(null).withName("Updated username");
        builder = put("/users")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(getMapper().writeValueAsString(testUser));
        String expectedMes = "User with id=" + testUser.getId() + " does not exist.";
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
                .content(getMapper().writeValueAsString(user));
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
        List<User> resultFilms = getMapper()
                .readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
                });
        Integer expectedFilmsCount = 2;
        assertEquals(expectedFilmsCount, resultFilms.size(),
                "Server hasn't return right users list.");
    }

    @Test
    @DisplayName("Получение существующего пользователя по id")
    void getExistingUserByIdTest() throws Exception {
        MockHttpServletRequestBuilder builder = post("/users")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(getMapper().writeValueAsString(user));
        MvcResult result = mockMvc.perform(builder)
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andReturn();
        User createdUser = getMapper().readValue(result.getResponse().getContentAsString(), User.class);
        builder = get("/users/{userId}", createdUser.getId());
        result = mockMvc.perform(builder)
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andReturn();
        User gottenUser = getMapper().readValue(result.getResponse().getContentAsString(), User.class);
        assertEquals(createdUser, gottenUser, "Запрашиваемый пользователь не был получен.");
    }

    @Test
    @DisplayName("Получение несуществующего пользователя по id")
    void getNotExistingUserByIdTest() throws Exception {
        Integer notExistingUserId = 9999;
        MockHttpServletRequestBuilder builder
                = get("/users/{userId}", notExistingUserId);
        mockMvc.perform(builder).andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Существующий пользователь добавляет в друзья сущ. пользователя")
    void ExistingUserAddsExistingUserToFriendsTest() throws Exception {
        MockHttpServletRequestBuilder builder = post("/users")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(getMapper().writeValueAsString(user));
        // Creating user
        MvcResult result = mockMvc.perform(builder)
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andReturn();
        User createdUser = getMapper().readValue(result.getResponse().getContentAsString(), User.class);
        builder = post("/users")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(getMapper().writeValueAsString(user.withName("User friend")));
        // Creating friend
        result = mockMvc.perform(builder)
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andReturn();
        User createdFriend = getMapper().readValue(result.getResponse().getContentAsString(), User.class);
        // User becomes friends with friend
        builder = put("/users/{id}/friends/{friendId}", createdUser.getId(), createdFriend.getId());
        mockMvc.perform(builder)
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.friends[0]").value(createdFriend.getId()));
    }

    @Test
    @DisplayName("Несуществующий пользователь добавляет в друзья сущ. пользователя")
    void NotExistingUserAddsExistingUserToFriendsTest() throws Exception {
        MockHttpServletRequestBuilder builder = post("/users")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(getMapper().writeValueAsString(user.withName("User friend")));
        // Creating friend
        MvcResult result = mockMvc.perform(builder)
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andReturn();
        User createdFriend = getMapper().readValue(result.getResponse().getContentAsString(), User.class);
        // Not existing user becomes friends with friend
        Integer notExistingUserId = 9999;
        builder = put("/users/{id}/friends/{friendId}", notExistingUserId, createdFriend.getId());
        mockMvc.perform(builder).andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Существующий пользователь добавляет в друзья себя")
    void ExistingUserAddsHimselfToFriendsTest() throws Exception {
        MockHttpServletRequestBuilder builder = post("/users")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(getMapper().writeValueAsString(user.withName("User friend")));
        // Creating friend
        MvcResult result = mockMvc.perform(builder)
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andReturn();
        User createdUser = getMapper().readValue(result.getResponse().getContentAsString(), User.class);
        // User adds himself
        builder = put("/users/{id}/friends/{friendId}", createdUser.getId(), createdUser.getId());
        mockMvc.perform(builder).andExpect(status().isInternalServerError());
    }

    @Test
    @DisplayName("Существующий пользователь удаляет сущ. друга")
    void ExistingUserRemovesExistingFriendTest() throws Exception {
        MockHttpServletRequestBuilder builder = post("/users")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(getMapper().writeValueAsString(user));
        // Creating user
        MvcResult result = mockMvc.perform(builder)
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andReturn();
        User createdUser = getMapper().readValue(result.getResponse().getContentAsString(), User.class);
        builder = post("/users")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(getMapper().writeValueAsString(user.withName("User friend")));
        // Creating friend
        result = mockMvc.perform(builder)
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andReturn();
        User createdFriend = getMapper().readValue(result.getResponse().getContentAsString(), User.class);
        // User becomes friends with friend
        builder = put("/users/{id}/friends/{friendId}", createdUser.getId(), createdFriend.getId());
        mockMvc.perform(builder)
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.friends[0]").value(createdFriend.getId()));
        // User stops friendship with friend
        builder = delete("/users/{id}/friends/{friendId}", createdUser.getId(), createdFriend.getId());
        mockMvc.perform(builder)
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.friends", empty()));
    }

    @Test
    @DisplayName("Получение списка друзей сущ. пользователя")
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    void getExistingUserFriendsListTest() throws Exception {
        MockHttpServletRequestBuilder builder;
        // Creating 10 users
        for (int i = 1; i <= 10; i++) {
            builder = post("/users")
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .content(getMapper().writeValueAsString(user.withLogin("User " + i)));
            mockMvc.perform(builder)
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                    .andExpect(jsonPath("$.id").value(i));
        }
        // Adding all users to first user friends
        for (int i = 2; i <= 10; i++) {
            builder = put("/users/{id}/friends/{friendId}", 1, i);
            mockMvc.perform(builder)
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                    .andExpect(jsonPath("$.friends.size()").value(i - 1));
        }
        for (int i = 2; i <= 10; i++) {
            builder = get("/users/{id}", i);
            mockMvc.perform(builder)
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                    .andExpect(jsonPath("$.friends[0]").value(1));
        }
        builder = get("/users/{id}/friends", 1);
        mockMvc.perform(builder)
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.size()").value(9));
    }

    @Test
    @DisplayName("Получение списка общих друзей двух сущ. пользователей")
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    void getMutualFriendsListBetweenTwoExistingUsersTest() throws Exception {
        MockHttpServletRequestBuilder builder;
        // Creating 10 users
        for (int i = 1; i <= 10; i++) {
            builder = post("/users")
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .content(getMapper().writeValueAsString(user.withLogin("User " + i)));
            mockMvc.perform(builder)
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                    .andExpect(jsonPath("$.id").value(i));
        }
        // Adding users [2-5] to user 1 friends
        for (int i = 2; i <= 5; i++) {
            builder = put("/users/{id}/friends/{friendId}", 1, i);
            mockMvc.perform(builder)
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                    .andExpect(jsonPath("$.friends.size()").value(i - 1));
        }
        // Adding users [5-9] to user 9 friends
        for (int i = 5; i <= 9; i++) {
            builder = put("/users/{id}/friends/{friendId}", 10, i);
            mockMvc.perform(builder)
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                    .andExpect(jsonPath("$.friends.size()").value(i - 4));
        }
        builder = get("/users/{id}/friends/common/{otherId}", 1, 10);
        mockMvc.perform(builder)
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$.[0].id").value(5));
    }
}
