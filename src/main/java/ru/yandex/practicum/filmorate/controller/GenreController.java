package ru.yandex.practicum.filmorate.controller;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.AbstractGenreService;

import javax.validation.Valid;

@RestController
public class GenreController {

    private final AbstractGenreService genreService;

    public GenreController(@Qualifier("jdbcGenreService") AbstractGenreService genreService) {
        this.genreService = genreService;
    }

    @GetMapping(value = "/genres/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Genre getGenre(@PathVariable Integer id) {
        return genreService.getGenreById(id);
    }

    @PostMapping(value = "/genres", produces = MediaType.APPLICATION_JSON_VALUE)
    public Genre addGenre(@Valid @RequestBody Genre genre) {
        return genreService.createGenre(genre);
    }

    @PutMapping(value = "/genres", produces = MediaType.APPLICATION_JSON_VALUE)
    public Genre updateGenre(@Valid @RequestBody Genre genre) {
        return genreService.updateGenre(genre);
    }

    @GetMapping(value = "/genres", produces = MediaType.APPLICATION_JSON_VALUE)
    public Iterable<Genre> getAllGenres() {
        return genreService.getAllGenres();
    }

    @DeleteMapping(value = "/genres/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Integer removeGenre(@PathVariable Integer id) {
        return genreService.deleteGenre(id);
    }
}
