package ru.yandex.practicum.filmorate.exception;

import lombok.Getter;
import ru.yandex.practicum.filmorate.model.Rating;

@Getter
public class RatingNotFoundException extends RuntimeException {

    private static final String MES_FORMAT = "Rating with id=%s does not exist.";

    private final Rating rating;

    public RatingNotFoundException(Rating rating) {
        super(String.format(MES_FORMAT, rating.getId()));
        this.rating = rating;
    }

    public RatingNotFoundException(Integer ratingId) {
        super(String.format(MES_FORMAT, ratingId));
        this.rating = null;
    }
}