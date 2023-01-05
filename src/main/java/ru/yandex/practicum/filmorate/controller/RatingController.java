package ru.yandex.practicum.filmorate.controller;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.service.AbstractRatingService;

import javax.validation.Valid;

@RestController
public class RatingController {

    private final AbstractRatingService ratingService;

    public RatingController(@Qualifier("jdbcRatingService") AbstractRatingService ratingService) {
        this.ratingService = ratingService;
    }

    @GetMapping(value = "/mpa/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Rating getRating(@PathVariable Integer id) {
        return ratingService.getRatingById(id);
    }

    @PostMapping(value = "/mpa", produces = MediaType.APPLICATION_JSON_VALUE)
    public Rating addRating(@Valid @RequestBody Rating rating) {
        return ratingService.createRating(rating);
    }

    @PutMapping(value = "/mpa", produces = MediaType.APPLICATION_JSON_VALUE)
    public Rating updateRating(@Valid @RequestBody Rating rating) {
        return ratingService.updateRating(rating);
    }

    @GetMapping(value = "/mpa", produces = MediaType.APPLICATION_JSON_VALUE)
    public Iterable<Rating> getAllRatings() {
        return ratingService.getAllRatings();
    }

    @DeleteMapping(value = "/mpa/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Integer removeRating(@PathVariable Integer id) {
        return ratingService.deleteRating(id);
    }
}
