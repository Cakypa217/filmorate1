package ru.yandex.practicum.filmorate.controller;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.reviews.NewReviewRequestDto;
import ru.yandex.practicum.filmorate.dto.reviews.ReviewResponseDto;
import ru.yandex.practicum.filmorate.dto.reviews.UpdateReviewRequestDto;
import ru.yandex.practicum.filmorate.service.ReviewService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping({"/reviews"})
@RequiredArgsConstructor
public class ReviewController {
    private final ReviewService reviewService;

    @PostMapping
    public ReviewResponseDto addNewReview(@RequestBody NewReviewRequestDto request) {
        log.info("Получен запрос POST /reviews с телом: {}", request);
        final ReviewResponseDto review = reviewService.addNewReview(request);
        log.info("Отправлен ответ POST /reviews с телом: {}", review);
        return review;
    }

    @PutMapping
    public ReviewResponseDto updateReview(@RequestBody UpdateReviewRequestDto request) {
        log.info("Получен запрос PUT /reviews с телом: {}", request);
        final ReviewResponseDto review = reviewService.updateReview(request);
        log.info("Отправлен ответ PUT /reviews с телом: {}", review);
        return review;
    }

    @DeleteMapping({"/{id}"})
    public void deleteReview(@PathVariable("id") Long id) {
        log.info("Получен запрос DELETE /reviews/{}", id);
        reviewService.deleteReview(id);
        log.info("Отзыв {} удалён", id);
    }

    @GetMapping
    public List<ReviewResponseDto> getReviews(@RequestParam(defaultValue = "0") Long filmId,
                                              @RequestParam(defaultValue = "10") int count) {
        log.info("Получен запрос GET /reviews для фильма с id {} в количестве {}", filmId, count);
        final List<ReviewResponseDto> reviews = reviewService.getReviews(filmId, count);
        log.info("Отправлен ответ GET /reviews для фильма с id {}. Всего {} отзывов: {}", filmId,
                reviews.size(), reviews);
        return reviews;
    }

    @GetMapping({"/{id}"})
    public ReviewResponseDto getReviewById(@PathVariable("id") Long id) {
        log.info("Получен запрос GET /reviews/{}", id);
        final ReviewResponseDto reviewResponseDto = reviewService.getReviewById(id);
        log.info("Отправлен ответ GET /reviews/{} с телом {}", id, reviewResponseDto);
        return reviewResponseDto;
    }

    @PutMapping({"/{id}/like/{userId}"})
    public ReviewResponseDto addLike(@PathVariable("id") Long id, @PathVariable("userId") Long userId) {
        log.info("Получен запрос PUT /reviews/{}/like/{}", id, userId);
        final ReviewResponseDto reviewResponseDto = reviewService.addLike(id, userId);
        log.info("Добавлен лайк отзыву {} от пользователя {}. Отправлен ответ: {}", id, userId,
                reviewResponseDto);
        return reviewResponseDto;
    }

    @PutMapping({"/{id}/dislike/{userId}"})
    public ReviewResponseDto addDislike(@PathVariable("id") Long id, @PathVariable("userId") Long userId) {
        log.info("Получен запрос PUT /reviews/{}/dislike/{}", id, userId);
        final ReviewResponseDto reviewResponseDto = reviewService.addDislike(id, userId);
        log.info("Добавлен дизлайк отзыву {} от пользователя {}. Отправлен ответ: {}", id, userId,
                reviewResponseDto);
        return reviewResponseDto;
    }

    @DeleteMapping({"/{id}/like/{userId}"})
    public ReviewResponseDto deleteLike(@PathVariable("id") Long id, @PathVariable("userId") Long userId) {
        log.info("Получен запрос DELETE /reviews/{}/like/{}", id, userId);
        final ReviewResponseDto reviewResponseDto = reviewService.deleteUseful(id, userId);
        log.info("Удалён лайк отзыву {} от пользователя {}. Отправлен ответ: {}", id, userId,
                reviewResponseDto);
        return reviewResponseDto;
    }

    @DeleteMapping({"/{id}/dislike/{userId}"})
    public ReviewResponseDto deleteDislike(@PathVariable("id") Long id, @PathVariable("userId") Long userId) {
        log.info("Получен запрос DELETE /reviews/{}/dislike/{}", id, userId);
        final ReviewResponseDto reviewResponseDto = reviewService.deleteUseful(id, userId);
        log.info("Удалён дизлайк отзыву {} от пользователя {}. Отправлен ответ: {}", id, userId,
                reviewResponseDto);
        return reviewResponseDto;
    }
}
