package ru.yandex.practicum.filmorate.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Сущность возрастного рейтинга")
public class Mpa {
    @Schema(description = "Id рейтинга", example = "1")
    private int id;
    @Schema(description = "Название рейтинга", example = "PG-13")
    private String name;
}
