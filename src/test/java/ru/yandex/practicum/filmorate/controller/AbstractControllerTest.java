package ru.yandex.practicum.filmorate.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.test.web.servlet.MockMvc;

abstract class AbstractControllerTest {

    protected final static String ERROR_MES_TEMPLATE = "Validation exception [class: '%s', field: '%s', mes: '%s']";

    protected MockMvc mockMvc;
    protected final ObjectMapper mapper;

    public AbstractControllerTest(MockMvc mockMvc) {
        this.mockMvc = mockMvc;
        mapper = new ObjectMapper();
    }

    abstract void setUp();
}
