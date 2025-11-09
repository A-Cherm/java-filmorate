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
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.MpaService;

import java.util.List;

@RestController
@RequestMapping("/mpa")
@RequiredArgsConstructor
@Tag(name = "Возрастные рейтинги", description = "Получение рейтингов")
public class MpaController {
    private final MpaService mpaService;

    @GetMapping
    @Operation(summary = "Получение рейтингов",
            responses = {
                    @ApiResponse(responseCode = "200", description = "ОК")
            })
    public List<Mpa> getRatings() {
        return mpaService.getRatings();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Получение рейтинга",
            responses = {
                    @ApiResponse(responseCode = "200", description = "ОК"),
                    @ApiResponse(responseCode = "404", description = "Нет рейтинга с данным id", content = @Content)
            })
    public Mpa getRating(@PathVariable int id) {
        return mpaService.getRating(id);
    }

}
