package ru.yandex.practicum.filmorate.mapper;

import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.dto.reviews.NewReviewRequestDto;
import ru.yandex.practicum.filmorate.dto.reviews.ReviewResponseDto;
import ru.yandex.practicum.filmorate.dto.reviews.UpdateReviewRequestDto;
import ru.yandex.practicum.filmorate.model.Review;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public final class ReviewMapper {
    public static Review mapToReview(NewReviewRequestDto request) {
        Review review = new Review();
        review.setContent(request.getContent());
        review.setIsPositive(request.getIsPositive());
        review.setUserId(request.getUserId());
        review.setFilmId(request.getFilmId());
        return review;
    }

    public static ReviewResponseDto mapToReviewDto(Review review) {
        ReviewResponseDto responseDto = new ReviewResponseDto();
        responseDto.setReviewId(review.getId());
        responseDto.setUserId(review.getUserId());
        responseDto.setFilmId(review.getFilmId());
        responseDto.setIsPositive(review.getIsPositive());
        responseDto.setContent(review.getContent());
        responseDto.setUseful(review.getCount());
        return responseDto;
    }

    public static Review updateReview(Review review, UpdateReviewRequestDto request) {
        if (request.hasContent()) {
            review.setContent(request.getContent());
        }
        if (request.hasType()) {
            review.setIsPositive(request.getIsPositive());
        }
        return review;
    }
}