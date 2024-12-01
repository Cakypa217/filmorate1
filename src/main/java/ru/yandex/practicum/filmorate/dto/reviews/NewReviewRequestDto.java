package ru.yandex.practicum.filmorate.dto.reviews;

import lombok.Data;

@Data
public class NewReviewRequestDto {
    private Long userId;
    private Long filmId;
    private Boolean isPositive;
    private String content;
}