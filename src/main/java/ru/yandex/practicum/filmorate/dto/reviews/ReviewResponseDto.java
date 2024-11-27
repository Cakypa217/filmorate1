package ru.yandex.practicum.filmorate.dto.reviews;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import lombok.Data;

@Data
public class ReviewResponseDto {
    @JsonProperty(access = Access.READ_ONLY)
    private Long reviewId;
    private Long userId;
    private Long filmId;
    private Boolean isPositive;
    private String content;
    private Integer useful;
}