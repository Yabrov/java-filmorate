package ru.yandex.practicum.filmorate.utils;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class LocalDateConvertor {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public static String toSqlString(@NotNull LocalDate localDate) {
        return formatter.format(localDate);
    }

    public static LocalDate fromSqlString(@NotNull @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$") String string) {
        return LocalDate.parse(string, formatter);
    }
}
