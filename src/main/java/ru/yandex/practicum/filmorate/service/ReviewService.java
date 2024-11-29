package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.*;
import ru.yandex.practicum.filmorate.dto.reviews.NewReviewRequestDto;
import ru.yandex.practicum.filmorate.dto.reviews.ReviewResponseDto;
import ru.yandex.practicum.filmorate.dto.reviews.UpdateReviewRequestDto;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.mapper.ReviewMapper;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.model.enums.EventType;
import ru.yandex.practicum.filmorate.model.enums.OperationType;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final FilmRepository filmRepository;
    private final UsefulRepository usefulRepository;
    private final EventRepository eventRepository;

    public ReviewResponseDto addNewReview(NewReviewRequestDto request) {
        checkUserId(request.getUserId());
        checkFilmId(request.getFilmId());
        checkContent(request.getContent());
        checkType(request.getIsPositive());
        Review review = ReviewMapper.mapToReview(request);
        review.setCount(0);
        review = reviewRepository.addNewReview(review);
        Event event = new Event(Instant.now().toEpochMilli(), request.getUserId(), EventType.REVIEW,
                OperationType.ADD, review.getId());
        eventRepository.save(event);
        return ReviewMapper.mapToReviewDto(review);
    }

    public ReviewResponseDto updateReview(UpdateReviewRequestDto request) {
        checkContent(request.getContent());
        checkType(request.getIsPositive());
        checkReviewId(request.getReviewId());
        Optional<Review> reviewOp = reviewRepository.getReviewById(request.getReviewId());
        Review review = reviewOp.get();
        ReviewMapper.updateReview(review, request);
        review = reviewRepository.updateReview(review);
        Event event = new Event(Instant.now().toEpochMilli(), request.getUserId(), EventType.REVIEW,
                OperationType.UPDATE, review.getId());
        eventRepository.save(event);
        return ReviewMapper.mapToReviewDto(review);
    }

    public void deleteReview(Long id) {
        Review review = reviewRepository.getReviewById(id)
                .orElseThrow(() -> new NotFoundException("Отзыв с id " + id + " не найден"));
        boolean check = reviewRepository.deleteReview(id);
        if (!check) {
            throw new NotFoundException("Отзыв с id " + id + " не найден");
        } else {
            Event event = new Event(Instant.now().toEpochMilli(), review.getUserId(), EventType.REVIEW,
                    OperationType.REMOVE, id);
            eventRepository.save(event);
        }
    }

    public ReviewResponseDto getReviewById(Long id) {
        return reviewRepository.getReviewById(id)
                .map(ReviewMapper::mapToReviewDto)
                .orElseThrow(() ->
                        new NotFoundException("Отзыв не найден с id: " + id));
    }

    public List<ReviewResponseDto> getReviews(Long id, int count) {
        if (id == 0) {
            return reviewRepository.getReviewsWithLimit(count).stream()
                    .map(ReviewMapper::mapToReviewDto)
                    .collect(Collectors.toList());
        } else if (count == 0) {
            return reviewRepository.getReviewsByFilmId(id).stream()
                    .map(ReviewMapper::mapToReviewDto)
                    .collect(Collectors.toList());
        } else {
            return reviewRepository.getReviews(id, count).stream()
                    .map(ReviewMapper::mapToReviewDto)
                    .collect(Collectors.toList());
        }
    }

    public ReviewResponseDto addLike(Long id, Long userId) {
        checkReviewId(id);
        checkUserId(userId);
        usefulRepository.deleteUseful(id, userId);
        usefulRepository.addLikeToReview(id, userId);
        eventRepository.save(
                new Event(Instant.now().toEpochMilli(), userId, EventType.REVIEW, OperationType.ADD, id));
        return getReviewById(id);
    }

    public ReviewResponseDto addDislike(Long id, Long userId) {
        checkReviewId(id);
        checkUserId(userId);
        usefulRepository.deleteUseful(id, userId);
        usefulRepository.addDislikeToReview(id, userId);
        eventRepository.save(
                new Event(Instant.now().toEpochMilli(), userId, EventType.REVIEW, OperationType.ADD, id));
        return getReviewById(id);
    }

    public ReviewResponseDto deleteUseful(Long id, Long userId) {
        checkReviewId(id);
        checkUserId(userId);
        usefulRepository.deleteUseful(id, userId);
        eventRepository.save(
                new Event(Instant.now().toEpochMilli(), userId, EventType.REVIEW, OperationType.REMOVE, id));
        return getReviewById(id);
    }

    private void checkReviewId(long id) {
        if (reviewRepository.getReviewById(id).isEmpty()) {
            throw new NotFoundException("Отзыва с таким id не существует");
        }
    }

    private void checkUserId(long id) {
        if (userRepository.findById(id).isEmpty()) {
            throw new NotFoundException("Пользователя с таким id не существует");
        }
    }

    private void checkFilmId(long id) {
        if (filmRepository.findById(id).isEmpty()) {
            throw new NotFoundException("Фильма с таким id не существует");
        }
    }

    private void checkContent(String content) {
        if (content.length() > 350 || content.isEmpty() || content.isBlank()) {
            throw new ValidationException("Длинна отзыва не соответствует нужной");
        }
    }

    private void checkType(Boolean type) {
        if (type == null) {
            throw new ValidationException("Отзыв должен быть негативным или позитивным");
        }
    }
}
