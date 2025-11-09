package ru.yandex.practicum.filmorate.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Сущность жанра")
public class Genre {
    @Schema(description = "Id жанра", example = "1")
    private int id;
    @Schema(description = "Название жанра", example = "Комедия")
    private String name;
}
