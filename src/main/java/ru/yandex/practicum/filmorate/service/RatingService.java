package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Rating;

public interface RatingService {

    Rating getRatingById(Integer ratingId);

    Rating createRating(Rating rating);

    Rating updateRating(Rating rating);

    Iterable<Rating> getAllRatings();

    Integer deleteRating(Integer rating);
}
