package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.RatingNotFoundException;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.storage.AbstractRepository;

@Service
@RequiredArgsConstructor
public class JdbcRatingService implements AbstractRatingService {

    private final AbstractRepository<Integer, Rating> jdbcRatingRepository;

    @Override
    public Rating getRatingById(Integer ratingId) {
        Rating rating = jdbcRatingRepository.findById(ratingId);
        if (rating == null) {
            throw new RatingNotFoundException(ratingId);
        }
        return rating;
    }

    @Override
    public Rating createRating(Rating rating) {
        return jdbcRatingRepository.save(rating);
    }

    @Override
    public Rating updateRating(Rating rating) {
        if (rating.getId() == null || getRatingById(rating.getId()) == null) {
            throw new RatingNotFoundException(rating);
        }
        return jdbcRatingRepository.update(rating);
    }

    @Override
    public Iterable<Rating> getAllRatings() {
        return jdbcRatingRepository.findAll();
    }

    @Override
    public Integer deleteRating(Integer ratingId) {
        Rating rating = getRatingById(ratingId);
        jdbcRatingRepository.delete(rating);
        return rating.getId();
    }
}
