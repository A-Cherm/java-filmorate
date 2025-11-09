package ru.yandex.practicum.filmorate.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.GenreService;

import java.util.List;

@RestController
@RequestMapping("/genres")
@RequiredArgsConstructor
@Tag(name = "Жанры фильмов", description = "Получение жанров")
public class GenreController {
    private final GenreService genreService;

    @GetMapping
    @Operation(summary = "Получение жанров",
            responses = {
                    @ApiResponse(responseCode = "200", description = "ОК")
            })
    public List<Genre> getGenres() {
        return genreService.getGenres();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Получение жанра",
            responses = {
                    @ApiResponse(responseCode = "200", description = "ОК"),
                    @ApiResponse(responseCode = "404", description = "Нет жанра с данным id", content = @Content)
            })
    public Genre getGenre(@PathVariable int id) {
        return genreService.getGenre(id);
    }

}
