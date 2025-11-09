package ru.yandex.practicum.filmorate.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/films")
@Tag(name = "Фильмы", description = "Управление фильмами")
public class FilmController {
    private final FilmService filmService;

    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @GetMapping
    @Operation(summary = "Получение фильмов",
            responses = {
                    @ApiResponse(responseCode = "200", description = "ОК")
            })
    public Collection<Film> getFilms() {
        return filmService.getFilms();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Получение фильма",
            responses = {
                    @ApiResponse(responseCode = "200", description = "ОК"),
                    @ApiResponse(responseCode = "404", description = "Нет фильма с данным id", content = @Content)
            })
    public Film getFilm(@PathVariable int id) {
        return filmService.getFilm(id);
    }

    @GetMapping("/popular")
    @Operation(summary = "Получение популярных фильмов",
            responses = {
                    @ApiResponse(responseCode = "200", description = "ОК")
            })
    public List<Film> getPopular(@RequestParam(defaultValue = "10") int count) {
        return filmService.getPopular(count);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Создание фильма",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Фильм создан")
            })
    public Film createFilm(@RequestBody Film film) {
        return filmService.createFilm(film);
    }

    @PutMapping
    @Operation(summary = "Обновление фильма",
            responses = {
                    @ApiResponse(responseCode = "200", description = "ОК")
            })
    public Film updateFilm(@RequestBody Film film) {
        return filmService.updateFilm(film);
    }

    @PutMapping("/{id}/like/{userId}")
    @Operation(summary = "Добавление лайка фильму",
            responses = {
                    @ApiResponse(responseCode = "200", description = "ОК"),
                    @ApiResponse(responseCode = "404", description = "Нет фильма с данным id", content = @Content)
            })
    public void addLike(@PathVariable int id, @PathVariable int userId) {
        filmService.addLike(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    @Operation(summary = "Удаление лайка",
            responses = {
                    @ApiResponse(responseCode = "200", description = "ОК"),
                    @ApiResponse(responseCode = "404", description = "Нет фильма с данным id", content = @Content)
            })
    public void deleteLike(@PathVariable int id, @PathVariable int userId) {
        filmService.deleteLike(id, userId);
    }
}
