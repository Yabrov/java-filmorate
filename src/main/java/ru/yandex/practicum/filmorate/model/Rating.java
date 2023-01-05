package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Value;
import lombok.With;

import java.util.Objects;

@Value
@Builder
public class Rating {

    @With
    Integer id;

    @With
    String name;

    @JsonCreator
    public Rating(
            @JsonProperty(value = "id", required = true) Integer id,
            @JsonProperty("name") String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Rating)) return false;
        Rating rating = (Rating) o;
        return Objects.equals(id, rating.id) && Objects.equals(name, rating.name);
    }

    @Override
    public int hashCode() {
        return id;
    }
}
