package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import lombok.Builder;
import lombok.Value;
import lombok.With;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Value
@Builder
@NotNull
public class User {

    @With
    Integer id;

    @With
    @Email
    @NotNull
    @NotBlank
    String email;

    @With
    @NotBlank
    String login;

    @With
    String name;

    @With
    @Past
    @NotNull
    @JsonFormat(pattern = "yyyy-MM-dd")
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonDeserialize(using = LocalDateDeserializer.class)
    LocalDate birthday;

    Set<Integer> friends = new HashSet<>();

    Set<Integer> likedFilms = new HashSet<>();

    @JsonCreator
    public User(
            @JsonProperty("id") Integer id,
            @JsonProperty("email") String email,
            @JsonProperty("login") String login,
            @JsonProperty("name") String name,
            @JsonFormat(pattern = "yyyy-MM-dd")
            @JsonSerialize(using = LocalDateSerializer.class)
            @JsonDeserialize(using = LocalDateDeserializer.class)
            @JsonProperty("birthday") LocalDate birthday) {
        this.id = id;
        this.email = email;
        this.login = login;
        this.name = name;
        this.birthday = birthday;
    }
}
