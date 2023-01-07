package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.RatingNotFoundException;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.storage.AbstractRepository;

@Service
public class RatingServiceImpl implements RatingService {

    private final AbstractRepository<Integer, Rating> ratingRepository;

    public RatingServiceImpl(
            @Qualifier("jdbcRatingRepository") AbstractRepository<Integer, Rating> ratingRepository) {
        this.ratingRepository = ratingRepository;
    }

    @Override
    public Rating getRatingById(Integer ratingId) {
        Rating rating = ratingRepository.findById(ratingId);
        if (rating == null) {
            throw new RatingNotFoundException(ratingId);
        }
        return rating;
    }

    @Override
    public Rating createRating(Rating rating) {
        return ratingRepository.save(rating);
    }

    @Override
    public Rating updateRating(Rating rating) {
        if (rating.getId() == null || getRatingById(rating.getId()) == null) {
            throw new RatingNotFoundException(rating);
        }
        return ratingRepository.update(rating);
    }

    @Override
    public Iterable<Rating> getAllRatings() {
        return ratingRepository.findAll();
    }

    @Override
    public Integer deleteRating(Integer ratingId) {
        Rating rating = getRatingById(ratingId);
        ratingRepository.delete(rating);
        return rating.getId();
    }
}
