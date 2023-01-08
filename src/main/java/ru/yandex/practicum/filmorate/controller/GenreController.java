package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.GenreService;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
public class GenreController {

    private final GenreService genreService;

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
