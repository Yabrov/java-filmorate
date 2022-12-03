package ru.yandex.practicum.filmorate.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import org.assertj.core.internal.bytebuddy.utility.RandomString;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.in;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class FilmControllerTest extends AbstractControllerTest {

    @Autowired
    public FilmControllerTest(MockMvc mockMvc) {
        super(mockMvc);
    }

    @Test
    @DisplayName("Создание валидного фильма")
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    void createValidFilmTest() throws Exception {
        MockHttpServletRequestBuilder builder = post("/films")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(serializeObject(film));
        MvcResult result = mockMvc.perform(builder)
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.id").value("1"))
                .andReturn();
        Film createdFilm = deserializeMvcResult(result, Film.class);
        Integer expectedId = 1;
        assertEquals(film.withId(expectedId), createdFilm, "Server hasn't create film.");
    }

    @Test
    @DisplayName("Создание фильма с недопустимой датой релиза")
    void createFilmWithWrongReleaseDateTest() throws Exception {
        LocalDate wrongReleaseDate = LocalDate.of(1830, 10, 1);
        Film testFilm = film.withReleaseDate(wrongReleaseDate);
        MockHttpServletRequestBuilder builder = post("/films")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(serializeObject(testFilm));
        mockMvc.perform(builder)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.reasons[0]")
                        .value(String.format(ERROR_MES_TEMPLATE, "film", "releaseDate", "must be after 28.12.1895")));
    }

    @Test
    @DisplayName("Создание фильма с датой релиза = null")
    void createFilmWithNullReleaseDateTest() throws Exception {
        Film testFilm = film.withReleaseDate(null);
        MockHttpServletRequestBuilder builder = post("/films")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(serializeObject(testFilm));
        mockMvc.perform(builder)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.reasons[0]", in(Arrays.asList(
                        String.format(ERROR_MES_TEMPLATE, "film", "releaseDate", "must be after 28.12.1895"),
                        String.format(ERROR_MES_TEMPLATE, "film", "releaseDate", "must not be null")
                ))));
    }

    @DisplayName("Создание фильма с пустым названием")
    @ParameterizedTest(name = "{index}. Проверка невалидности названия фильма ''{0}''")
    @ValueSource(strings = {"", "  "})
    void createFilmWithEmptyNameTest(String name) throws Exception {
        Film testFilm = film.withName("");
        MockHttpServletRequestBuilder builder = post("/films")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(serializeObject(testFilm));
        mockMvc.perform(builder)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.reasons[0]")
                        .value(String.format(ERROR_MES_TEMPLATE, "film", "name", "must not be blank")));
    }

    @Test
    @DisplayName("Создание фильма с названием NULL")
    void createFilmWithNullNameTest() throws Exception {
        Film testFilm = film.withName(null);
        MockHttpServletRequestBuilder builder = post("/films")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(serializeObject(testFilm));
        mockMvc.perform(builder)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.reasons[0]", in(Arrays.asList(
                        String.format(ERROR_MES_TEMPLATE, "film", "name", "must not be blank"),
                        String.format(ERROR_MES_TEMPLATE, "film", "name", "must not be null")
                ))));
    }

    @Test
    @DisplayName("Создание фильма с длинным описанием > 200")
    void createFilmWithDescrLenMoreThan200CharsTest() throws Exception {
        Film testFilm = film.withDescription(new RandomString(201).nextString());
        MockHttpServletRequestBuilder builder = post("/films")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(serializeObject(testFilm));
        mockMvc.perform(builder)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.reasons[0]")
                        .value(String.format(ERROR_MES_TEMPLATE, "film", "description", "size must be between 0 and 200")));
    }

    @Test
    @DisplayName("Создание фильма с пустым описанием")
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    void createFilmWithEmptyDescrTest() throws Exception {
        Film testFilm = film.withDescription("");
        MockHttpServletRequestBuilder builder = post("/films")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(serializeObject(testFilm));
        MvcResult result = mockMvc.perform(builder)
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.id").value("1"))
                .andReturn();
        Film createdFilm = deserializeMvcResult(result, Film.class);
        Integer expectedId = 1;
        assertEquals(testFilm.withId(expectedId), createdFilm, "Server hasn't create film.");
    }

    @Test
    @DisplayName("Создание фильма с описанием NULL")
    void createFilmWithNullDescrTest() throws Exception {
        Film testFilm = film.withDescription(null);
        MockHttpServletRequestBuilder builder = post("/films")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(serializeObject(testFilm));
        mockMvc.perform(builder)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.reasons[0]")
                        .value(String.format(ERROR_MES_TEMPLATE, "film", "description", "must not be null")));
    }

    @Test
    @DisplayName("Создание фильма с отрицательной продолжительностью")
    void createFilmWithNegativeDurationTest() throws Exception {
        Film testFilm = film.withDuration(-1);
        MockHttpServletRequestBuilder builder = post("/films")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(serializeObject(testFilm));
        mockMvc.perform(builder)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.reasons[0]")
                        .value(String.format(ERROR_MES_TEMPLATE, "film", "duration", "must be greater than 0")));
    }

    @Test
    @DisplayName("Создание фильма с продолжительностью = 0")
    void createFilmWithZeroDurationTest() throws Exception {
        Film testFilm = film.withDuration(0);
        MockHttpServletRequestBuilder builder = post("/films")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(serializeObject(testFilm));
        mockMvc.perform(builder)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.reasons[0]")
                        .value(String.format(ERROR_MES_TEMPLATE, "film", "duration", "must be greater than 0")));
    }

    @Test
    @DisplayName("Создание фильма равного null")
    void createNullFilmTest() throws Exception {
        Film testFilm = null;
        MockHttpServletRequestBuilder builder = post("/films")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(serializeObject(testFilm));
        mockMvc.perform(builder).andExpect(status().is4xxClientError());
    }

    @Test
    @DisplayName("Обновление фильма")
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    void updateFilmTest() throws Exception {
        MockHttpServletRequestBuilder builder = post("/films")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(serializeObject(film));
        // Creating film
        mockMvc.perform(builder)
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE));
        Film testFilm = film.withId(1).withDescription("Updated descr");
        builder = put("/films")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(serializeObject(testFilm));
        MvcResult result = mockMvc.perform(builder)
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andReturn();
        Film updatedFilm = deserializeMvcResult(result, Film.class);
        String expectedDesc = "Updated descr";
        assertEquals(
                expectedDesc, updatedFilm.getDescription(),
                "Server hasn't update film."
        );
    }

    @Test
    @DisplayName("Обновление фильма c id NULL")
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    void updateFilmWithNullIdTest() throws Exception {
        MockHttpServletRequestBuilder builder = post("/films")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(serializeObject(film));
        // Creating film
        mockMvc.perform(builder)
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE));
        Film testFilm = film.withId(null).withDescription("Updated descr");
        builder = put("/films")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(serializeObject(testFilm));
        String expectedMes = "Film with id=" + testFilm.getId() + " does not exist.";
        mockMvc.perform(builder)
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.reasons[0]").value(expectedMes));
    }

    @Test
    @DisplayName("Получение списка всех фильмов")
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    void getAllFilmsTest() throws Exception {
        MockHttpServletRequestBuilder builder = post("/films")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(serializeObject(film));
        // Creating film1
        mockMvc.perform(builder)
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE));
        // Creating film2
        mockMvc.perform(builder)
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE));
        // Getting all films
        builder = get("/films");
        MvcResult result = mockMvc.perform(builder)
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andReturn();
        List<Film> resultFilms = getMapper()
                .readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
                });
        Integer expectedFilmsCount = 2;
        assertEquals(expectedFilmsCount, resultFilms.size(),
                "Server hasn't return right films list.");
    }

    @Test
    @DisplayName("Получение существующего фильма по id")
    void getExistingFilmByIdTest() throws Exception {
        MockHttpServletRequestBuilder builder = post("/films")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(serializeObject(film));
        // Creating film
        MvcResult result = mockMvc.perform(builder)
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andReturn();
        Film createdFilm = deserializeMvcResult(result, Film.class);
        builder = get("/films/{filmId}", createdFilm.getId());
        result = mockMvc.perform(builder)
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andReturn();
        Film gottenFilm = deserializeMvcResult(result, Film.class);
        assertEquals(createdFilm, gottenFilm, "Запрашиваемый фильм не был получен.");
    }

    @Test
    @DisplayName("Получение несуществующего фильма по id")
    void getNotExistingFilmByIdTest() throws Exception {
        Integer notExistingFilmId = 9999;
        MockHttpServletRequestBuilder builder
                = get("/films/{filmId}", notExistingFilmId);
        mockMvc.perform(builder).andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Существующий пользователь лайкает существующий фильм")
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    void existingUserLikesExistingFilmTest() throws Exception {
        MockHttpServletRequestBuilder builder = post("/films")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(serializeObject(film));
        // Creating film
        MvcResult result = mockMvc.perform(builder)
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andReturn();
        Film createdFilm = deserializeMvcResult(result, Film.class);
        builder = post("/users")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(serializeObject(user));
        // Creating user
        result = mockMvc.perform(builder)
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andReturn();
        User createdUser = deserializeMvcResult(result, User.class);
        builder = put("/films/{filmId}/like/{userId}", createdFilm.getId(), createdUser.getId());
        mockMvc.perform(builder)
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.likesCount").value(1));
        builder = get("/users/{userId}", createdUser.getId());
        mockMvc.perform(builder)
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.likedFilms[0]").value(createdFilm.getId()));
    }

    @Test
    @DisplayName("Несуществующий пользователь лайкает существующий фильм")
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    void notExistingUserLikesExistingFilmTest() throws Exception {
        MockHttpServletRequestBuilder builder = post("/films")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(serializeObject(film));
        // Creating film
        MvcResult result = mockMvc.perform(builder)
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andReturn();
        Film createdFilm = deserializeMvcResult(result, Film.class);
        builder = put("/films/{filmId}/like/{userId}", createdFilm.getId(), 1);
        mockMvc.perform(builder).andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Существующий пользователь лайкает несуществующий фильм")
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    void existingUserLikesNotExistingFilmTest() throws Exception {
        MockHttpServletRequestBuilder builder = post("/users")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(serializeObject(user));
        // Creating user
        MvcResult result = mockMvc.perform(builder)
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andReturn();
        User createdUser = deserializeMvcResult(result, User.class);
        builder = put("/films/{filmId}/like/{userId}", 1, createdUser.getId());
        mockMvc.perform(builder).andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Существующий пользователь убирает свой лайк фильму")
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    void existingUserRemovesLikeFromExistingFilmTest() throws Exception {
        MockHttpServletRequestBuilder builder = post("/films")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(serializeObject(film));
        // Creating film
        MvcResult result = mockMvc.perform(builder)
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andReturn();
        Film createdFilm = deserializeMvcResult(result, Film.class);
        builder = post("/users")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(serializeObject(user));
        // Creating user
        result = mockMvc.perform(builder)
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andReturn();
        User createdUser = deserializeMvcResult(result, User.class);
        // User likes film
        builder = put("/films/{filmId}/like/{userId}", createdFilm.getId(), createdUser.getId());
        mockMvc.perform(builder)
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.likesCount").value(1));
        // User removes like from film
        builder = delete("/films/{filmId}/like/{userId}", createdFilm.getId(), createdUser.getId());
        mockMvc.perform(builder)
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.likesCount").value(0));
        builder = get("/users/{userId}", createdUser.getId());
        mockMvc.perform(builder)
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.likedFilms", empty()));
    }

    @DisplayName("Получение рейтинга фильмов")
    @ParameterizedTest(name = "{index}. Рейтинг первых ''{0}'' фильмов")
    @ValueSource(ints = {1, 3, 5, 7, 8, 10})
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    void getFilmsRatingTest(int count) throws Exception {
        MockHttpServletRequestBuilder builder;
        // Creating 10 users
        for (int i = 1; i <= 10; i++) {
            builder = post("/users")
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .content(serializeObject(user.withLogin("User " + i)));
            mockMvc.perform(builder)
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                    .andExpect(jsonPath("$.id").value(i));
        }
        // Creating 10 films
        for (int i = 1; i <= 10; i++) {
            builder = post("/films")
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .content(serializeObject(film.withName("Film " + i)));
            mockMvc.perform(builder)
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                    .andExpect(jsonPath("$.id").value(i));
        }
        // Wrapping likes
        for (int i = 1; i <= 10; i++) {
            for (int j = 10; j >= i; j--) {
                builder = put("/films/{filmId}/like/{userId}", i, j);
                mockMvc.perform(builder)
                        .andExpect(status().isOk())
                        .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                        .andExpect(jsonPath("$.likesCount").value(11 - j));
            }
        }
        // Getting rating
        builder = get("/films/popular?count={count}", count);
        mockMvc.perform(builder)
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.size()").value(count));
        for (int i = 1; i <= count; i++) {
            builder = get("/users/{userId}", i);
            mockMvc.perform(builder)
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                    .andExpect(jsonPath("$.likedFilms.size()").value(i));
        }
    }

    @Test
    @DisplayName("Проверка восстановления кол-ва лайков после обновления сущ. фильма")
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    void restoreLikesCountAfterExistingFilmUpdatingTest() throws Exception {
        MockHttpServletRequestBuilder builder = post("/users")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(serializeObject(user));
        // Creating user
        mockMvc.perform(builder)
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.id").value(1));
        builder = post("/films")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(serializeObject(film));
        // Creating film
        MvcResult result = mockMvc.perform(builder)
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.likesCount").value(0))
                .andReturn();
        Film createdFilm = deserializeMvcResult(result, Film.class);
        builder = put("/films/{filmId}/like/{userId}", createdFilm.getId(), 1);
        // User likes film
        mockMvc.perform(builder)
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.likesCount").value(1));
        builder = put("/films")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(serializeObject(createdFilm.withName("Updated film")));
        // Updating created film
        mockMvc.perform(builder)
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.likesCount").value(1));
    }

    @Test
    @DisplayName("Проверка невозможности добавления нескольких лайков фильму одним пользователем")
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    void impossibilityOfAddingManyLikesToFilmFromSingleUserTest() throws Exception {
        MockHttpServletRequestBuilder builder = post("/users")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(serializeObject(user));
        // Creating user
        mockMvc.perform(builder)
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.id").value(1));
        builder = post("/films")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(serializeObject(film));
        // Creating film
        mockMvc.perform(builder)
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.likesCount").value(0));
        builder = put("/films/{filmId}/like/{userId}", 1, 1);
        // User likes film many times
        for (int i = 0; i < 10; i++) {
            mockMvc.perform(builder)
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                    .andExpect(jsonPath("$.likesCount").value(1));
        }
    }

    @Test
    @DisplayName("Проверка невозможности удаления нескольких лайков фильму одним пользователем")
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    void impossibilityOfRemovingManyLikesToFilmFromSingleUserTest() throws Exception {
        MockHttpServletRequestBuilder builder = post("/users")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(serializeObject(user));
        // Creating user
        mockMvc.perform(builder)
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.id").value(1));
        builder = post("/films")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(serializeObject(film));
        // Creating film
        mockMvc.perform(builder)
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.likesCount").value(0));
        builder = delete("/films/{filmId}/like/{userId}", 1, 1);
        // User removes like from film many times
        for (int i = 0; i < 10; i++) {
            mockMvc.perform(builder)
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                    .andExpect(jsonPath("$.likesCount").value(0));
        }
    }
}
