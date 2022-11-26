package ru.yandex.practicum.filmorate.controller;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.test.web.servlet.MockMvc;

abstract class AbstractControllerTest {

    protected final static String ERROR_MES_TEMPLATE = "Validation exception [class: '%s', field: '%s', mes: '%s']";

    protected final MockMvc mockMvc;
    protected final ObjectMapper mapper;

    public AbstractControllerTest(MockMvc mockMvc) {
        this.mockMvc = mockMvc;
        this.mapper = getMapper();
    }

    private static ObjectMapper getMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return objectMapper;
    }
}
