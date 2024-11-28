package ru.yandex.practicum.filmorate.dto.reviews;

import lombok.Data;

@Data
public class UpdateReviewRequestDto {
    private Long userId;
    private Long reviewId;
    private Boolean isPositive;
    private String content;

    public boolean hasType() {
        return this.isPositive != null;
    }

    public boolean hasContent() {
        return this.content != null && !this.content.isBlank() && !this.content.isEmpty();
    }
}