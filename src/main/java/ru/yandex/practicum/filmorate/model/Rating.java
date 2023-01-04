package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Value;
import lombok.With;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Value
@Builder
public class Rating {

    @With
    Integer id;

    @With
    @NotNull
    @NotBlank
    String name;

    @JsonCreator
    public Rating(
            @JsonProperty(value = "id", required = true) Integer id,
            @JsonProperty(value = "name", defaultValue = "R") String name) {
        this.id = id;
        this.name = name;
    }
}
