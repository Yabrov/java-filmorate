package ru.yandex.practicum.filmorate.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.internal.bytebuddy.utility.RandomString;
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
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(FilmController.class)
public class FilmControllerTest {

    private Film film;

    private final ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    public void createTestFilmObject() {
        film = new Film();
        film.setId(null);
        film.setName("Film test name");
        film.setDescription("Film test descr");
        film.setDuration(120);
        film.setReleaseDate(LocalDate.of(2022, 11, 1));
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
                .andReturn();
        Film createdFilm = mapper.readValue(result.getResponse().getContentAsString(), Film.class);
        Integer expectedId = 1;
        assertEquals(
                expectedId, createdFilm.getId(),
                "Server hasn't create film with id=" + expectedId
        );
    }

    @Test
    @DisplayName("Создание фильма с недопустимой датой релиза")
    void createFilmWithWrongReleaseDateTest() throws Exception {
        LocalDate wrongReleaseDate = LocalDate.of(1830, 10, 1);
        film.setReleaseDate(wrongReleaseDate);
        MockHttpServletRequestBuilder builder = post("/films")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(mapper.writeValueAsString(film));
        String expectedMes = "Release date " + wrongReleaseDate + " is wrong.";
        mockMvc.perform(builder)
                .andExpect(status().isBadRequest())
                .andExpect(content().string(expectedMes));
    }

    @Test
    @DisplayName("Создание фильма с датой релиза = null")
    void createFilmWithNullReleaseDateTest() throws Exception {
        film.setReleaseDate(null);
        MockHttpServletRequestBuilder builder = post("/films")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(mapper.writeValueAsString(film));
        String expectedMes = "Validation exception " +
                "[class: 'film', field: 'releaseDate', reason: 'must not be null']";
        mockMvc.perform(builder)
                .andExpect(status().isBadRequest())
                .andExpect(content().string(expectedMes));
    }

    @Test
    @DisplayName("Создание фильма с пустым названием")
    void createFilmWithEmptyNameTest() throws Exception {
        film.setName("");
        MockHttpServletRequestBuilder builder = post("/films")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(mapper.writeValueAsString(film));
        String expectedMes = "Validation exception " +
                "[class: 'film', field: 'name', reason: 'must not be blank']";
        mockMvc.perform(builder)
                .andExpect(status().isBadRequest())
                .andExpect(content().string(expectedMes));
    }

    @Test
    @DisplayName("Создание фильма с названием NULL")
    void createFilmWithNullNameTest() throws Exception {
        film.setName(null);
        MockHttpServletRequestBuilder builder = post("/films")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(mapper.writeValueAsString(film));
        String expectedMes = "Validation exception " +
                "[class: 'film', field: 'name', reason: 'must not be blank']";
        mockMvc.perform(builder)
                .andExpect(status().isBadRequest())
                .andExpect(content().string(expectedMes));
    }

    @Test
    @DisplayName("Создание фильма с длинным описанием > 200")
    void createFilmWithDescrLenMoreThan200CharsTest() throws Exception {
        film.setDescription(new RandomString(201).nextString());
        MockHttpServletRequestBuilder builder = post("/films")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(mapper.writeValueAsString(film));
        String expectedMes = "Validation exception " +
                "[class: 'film', field: 'description', reason: 'size must be between 0 and 200']";
        mockMvc.perform(builder)
                .andExpect(status().isBadRequest())
                .andExpect(content().string(expectedMes));
    }

    @Test
    @DisplayName("Создание фильма с пустым описанием")
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    void createFilmWithEmptyDescrTest() throws Exception {
        film.setDescription("");
        MockHttpServletRequestBuilder builder = post("/films")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(mapper.writeValueAsString(film));
        MvcResult result = mockMvc.perform(builder)
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andReturn();
        Film resultFilm = mapper.readValue(result.getResponse().getContentAsString(), Film.class);
        Integer expectedId = 1;
        assertEquals(
                expectedId, resultFilm.getId(),
                "Server hasn't create film with id=" + expectedId
        );
    }

    @Test
    @DisplayName("Создание фильма с описанием=null")
    void createFilmWithNullDescrTest() throws Exception {
        film.setDescription(null);
        MockHttpServletRequestBuilder builder = post("/films")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(mapper.writeValueAsString(film));
        String expectedMes = "Validation exception " +
                "[class: 'film', field: 'description', reason: 'must not be null']";
        mockMvc.perform(builder)
                .andExpect(status().isBadRequest())
                .andExpect(content().string(expectedMes));
    }

    @Test
    @DisplayName("Создание фильма с отрицательной продолжительностью")
    void createFilmWithNegativeDurationTest() throws Exception {
        film.setDuration(-1);
        MockHttpServletRequestBuilder builder = post("/films")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(mapper.writeValueAsString(film));
        String expectedMes = "Validation exception " +
                "[class: 'film', field: 'duration', reason: 'must be greater than 0']";
        mockMvc.perform(builder)
                .andExpect(status().isBadRequest())
                .andExpect(content().string(expectedMes));
    }

    @Test
    @DisplayName("Создание фильма с продолжительностью = 0")
    void createFilmWithZeroDurationTest() throws Exception {
        film.setDuration(0);
        MockHttpServletRequestBuilder builder = post("/films")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(mapper.writeValueAsString(film));
        String expectedMes = "Validation exception " +
                "[class: 'film', field: 'duration', reason: 'must be greater than 0']";
        mockMvc.perform(builder)
                .andExpect(status().isBadRequest())
                .andExpect(content().string(expectedMes));
    }

    @Test
    @DisplayName("Создание фильма равного null")
    void createNullFilmTest() throws Exception {
        film = null;
        MockHttpServletRequestBuilder builder = post("/films")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(mapper.writeValueAsString(film));
        mockMvc.perform(builder).andExpect(status().isInternalServerError());
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
        film.setId(1);
        film.setDescription("Updated descr");
        builder = put("/films")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(mapper.writeValueAsString(film));
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
    @DisplayName("Обновление фильма c id=null")
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    void updateFilmWithNullIdTest() throws Exception {
        MockHttpServletRequestBuilder builder = post("/films")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(mapper.writeValueAsString(film));
        // Creating film
        mockMvc.perform(builder)
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE));
        film.setId(null);
        film.setDescription("Updated descr");
        builder = put("/films")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(mapper.writeValueAsString(film));
        String expectedMes = "Film with id=" + film.getId() + " doesn't exist.";
        mockMvc.perform(builder)
                .andExpect(status().isNotFound())
                .andExpect(content().string(expectedMes));
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
                .readValue(result.getResponse().getContentAsString(), new TypeReference<>() {});
        Integer expectedFilmsCount = 2;
        assertEquals(expectedFilmsCount, resultFilms.size(),
                "Server hasn't return right films list.");
    }
}
