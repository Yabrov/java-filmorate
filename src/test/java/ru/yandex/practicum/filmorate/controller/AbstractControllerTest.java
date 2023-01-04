package ru.yandex.practicum.filmorate.controller;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
@EnableAutoConfiguration(exclude = {
        DataSourceTransactionManagerAutoConfiguration.class
})
@ComponentScan("ru.yandex.practicum.filmorate")
abstract class AbstractControllerTest {

    protected final static String ERROR_MES_TEMPLATE
            = "Validation exception [class: '%s', field: '%s', mes: '%s']";

    protected final MockMvc mockMvc;

    public AbstractControllerTest(MockMvc mockMvc) {
        this.mockMvc = mockMvc;
    }

    protected final Film film = Film.builder()
            .id(null)
            .name("Film test name")
            .description("Film test descr")
            .duration(120)
            .releaseDate(LocalDate.of(2022, 11, 1))
            .build();

    protected final User user = User.builder()
            .id(null)
            .login("login")
            .name("username")
            .email("test@domain.xxx")
            .birthday(LocalDate.of(1980, 1, 1))
            .build();

    protected static ObjectMapper getMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return objectMapper;
    }

    protected static <T> T deserializeMvcResult(MvcResult mvcResult, Class<T> clazz) throws Exception {
        return getMapper().readValue(mvcResult.getResponse().getContentAsString(), clazz);
    }

    protected static String serializeObject(Object obj) throws Exception {
        return getMapper().writeValueAsString(obj);
    }
}
