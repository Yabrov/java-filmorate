package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Value;
import lombok.With;

@With
@Value
@Builder
public class Genre implements Comparable<Genre> {

    Integer id;

    String name;

    @JsonCreator
    public Genre(
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

    @Override
    public int compareTo(Genre o) {
        return id.compareTo(o.id);
    }
}
