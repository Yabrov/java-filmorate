package ru.yandex.practicum.filmorate.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import org.assertj.core.internal.bytebuddy.utility.RandomString;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.in;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FilmController.class)
@ComponentScan("ru.yandex.practicum.filmorate")
public class FilmControllerTest extends AbstractControllerTest {

    private final Film film = Film.builder()
            .id(null)
            .name("Film test name")
            .description("Film test descr")
            .duration(120)
            .releaseDate(LocalDate.of(2022, 11, 1))
            .build();

    @Autowired
    public FilmControllerTest(@Autowired MockMvc mockMvc) {
        super(mockMvc);
    }

    @Test
    @DisplayName("Создание валидного фильма")
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    void createValidFilmTest() throws Exception {
        MockHttpServletRequestBuilder builder = post("/films")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(mapper.writeValueAsString(film));
        MvcResult result = mockMvc.perform(builder)
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.id").value("1"))
                .andReturn();
        Film createdFilm = mapper.readValue(result.getResponse().getContentAsString(), Film.class);
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
                .content(mapper.writeValueAsString(testFilm));
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
                .content(mapper.writeValueAsString(testFilm));
        mockMvc.perform(builder)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.reasons[0]", in(Arrays.asList(
                        String.format(ERROR_MES_TEMPLATE, "film", "releaseDate", "must be after 28.12.1895"),
                        String.format(ERROR_MES_TEMPLATE, "film", "releaseDate", "must not be null")
                ))));
    }

    @DisplayName("Создание фильма с пустым названием")
    @ParameterizedTest(name = "{index}. Проверка невалидности названия фильма '{arguments}'")
    @ValueSource(strings = {"", "  "})
    void createFilmWithEmptyNameTest(String name) throws Exception {
        Film testFilm = film.withName("");
        MockHttpServletRequestBuilder builder = post("/films")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(mapper.writeValueAsString(testFilm));
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
                .content(mapper.writeValueAsString(testFilm));
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
                .content(mapper.writeValueAsString(testFilm));
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
                .content(mapper.writeValueAsString(testFilm));
        MvcResult result = mockMvc.perform(builder)
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.id").value("1"))
                .andReturn();
        Film createdFilm = mapper.readValue(result.getResponse().getContentAsString(), Film.class);
        Integer expectedId = 1;
        assertEquals(testFilm.withId(expectedId), createdFilm, "Server hasn't create film.");
    }

    @Test
    @DisplayName("Создание фильма с описанием NULL")
    void createFilmWithNullDescrTest() throws Exception {
        Film testFilm = film.withDescription(null);
        MockHttpServletRequestBuilder builder = post("/films")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(mapper.writeValueAsString(testFilm));
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
                .content(mapper.writeValueAsString(testFilm));
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
                .content(mapper.writeValueAsString(testFilm));
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
                .content(mapper.writeValueAsString(testFilm));
        mockMvc.perform(builder).andExpect(status().is4xxClientError());
    }

    @Test
    @DisplayName("Обновление фильма")
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    void updateFilmTest() throws Exception {
        MockHttpServletRequestBuilder builder = post("/films")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(mapper.writeValueAsString(film));
        // Creating film
        mockMvc.perform(builder)
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE));
        Film testFilm = film.withId(1).withDescription("Updated descr");
        builder = put("/films")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(mapper.writeValueAsString(testFilm));
        MvcResult result = mockMvc.perform(builder)
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andReturn();
        Film updatedFilm = mapper.readValue(result.getResponse().getContentAsString(), Film.class);
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
                .content(mapper.writeValueAsString(film));
        // Creating film
        mockMvc.perform(builder)
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE));
        Film testFilm = film.withId(null).withDescription("Updated descr");
        builder = put("/films")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(mapper.writeValueAsString(testFilm));
        String expectedMes = "Film with id=" + testFilm.getId() + " doesn't exist.";
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
                .content(mapper.writeValueAsString(film));
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
        List<Film> resultFilms = mapper
                .readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
                });
        Integer expectedFilmsCount = 2;
        assertEquals(expectedFilmsCount, resultFilms.size(),
                "Server hasn't return right films list.");
    }
}
