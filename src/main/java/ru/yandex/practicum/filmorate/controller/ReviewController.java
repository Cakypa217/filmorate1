package ru.yandex.practicum.filmorate.controller;

import lombok.AllArgsConstructor;
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
@AllArgsConstructor
public class ReviewController {
    ReviewService reviewService;

    @PostMapping
    public ReviewResponseDto addNewReview(@RequestBody NewReviewRequestDto request) {
        log.info("пришел запрос на создание нового отзыва");
        return reviewService.addNewReview(request);
    }

    @PutMapping
    public ReviewResponseDto updateReview(@RequestBody UpdateReviewRequestDto request) {
        log.info("пришел запрос на изменение отзыва с id " + request.getReviewId());
        return reviewService.updateReview(request);
    }

    @DeleteMapping({"/{id}"})
    public void deleteReview(@PathVariable("id") Long id) {
        log.info("пришел запрос на удаление отзыва с id " + id);
        reviewService.deleteReview(id);
    }

    @GetMapping
    public List<ReviewResponseDto> getReviews(@RequestParam(defaultValue = "0") Long filmId,
                                              @RequestParam(defaultValue = "10") int count) {
        log.info("пришел запрос на получение отзывов для фильма с id " + filmId + " в количестве " + count);
        return reviewService.getReviews(filmId, count);
    }

    @GetMapping({"/{id}"})
    public ReviewResponseDto getReviewById(@PathVariable("id") Long id) {
        log.info("пришел запрос на получение отзыва с id " + id);
        return reviewService.getReviewById(id);
    }

    @PutMapping({"/{id}/like/{userId}"})
    public ReviewResponseDto addLike(@PathVariable("id") Long id, @PathVariable("userId") Long userId) {
        log.info("пришел запрос на добавление лайка к отзыву с id " + id + " от пользователя с id" + userId);
        return reviewService.addLike(id, userId);
    }

    @PutMapping({"/{id}/dislike/{userId}"})
    public ReviewResponseDto addDislike(@PathVariable("id") Long id, @PathVariable("userId") Long userId) {
        log.info("пришел запрос на добавление дизлайка к отзыву с id " + id + " от пользователя с id" + userId);
        return reviewService.addDislike(id, userId);
    }

    @DeleteMapping({"/{id}/like/{userId}"})
    public ReviewResponseDto deleteLike(@PathVariable("id") Long id, @PathVariable("userId") Long userId) {
        log.info("пришел запрос на удаление лайка к отзыву с id " + id + " от пользователя с id" + userId);
        return reviewService.deleteUseful(id, userId);
    }

    @DeleteMapping({"/{id}/dislike/{userId}"})
    public ReviewResponseDto deleteDislike(@PathVariable("id") Long id, @PathVariable("userId") Long userId) {
        log.info("пришел запрос на удаление дизлайка к отзыву с id " + id + " от пользователя с id" + userId);
        return reviewService.deleteUseful(id, userId);
    }
}
