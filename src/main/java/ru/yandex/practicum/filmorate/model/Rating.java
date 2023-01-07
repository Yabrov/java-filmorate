package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Value;
import lombok.With;

@With
@Value
@Builder
public class Rating {

    Integer id;

    String name;

    @JsonCreator
    public Rating(
            @JsonProperty(
                    value = "id",
                    required = true
            )
            Integer id,
            @JsonProperty("name")
            String name) {
        this.id = id;
        this.name = name;
    }
}
