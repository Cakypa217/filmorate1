package ru.yandex.practicum.filmorate.model;

import lombok.Data;

@Data
public class Useful {
    private Long id;
    private Long userId;
    private Long reviewId;
    private Long usefulCount;
}
