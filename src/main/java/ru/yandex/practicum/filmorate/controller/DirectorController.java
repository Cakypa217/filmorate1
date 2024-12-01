package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.DirectorDto;
import ru.yandex.practicum.filmorate.service.DirectorService;

import java.util.List;

@RestController
@RequestMapping("/directors")
@RequiredArgsConstructor
@Slf4j
public class DirectorController {

    private final DirectorService directorService;

    @GetMapping
    public List<DirectorDto> findAll() {
        log.info("Получен запрос GET /directors");
        final List<DirectorDto> directors = directorService.findAll();
        log.info("Отправлен ответ GET /directors. Всего {} режиссёров : {}", directors.size(),
                directors);
        return directors;
    }

    @GetMapping("/{id}")
    public DirectorDto getById(@PathVariable Long id) {
        log.info("Получен запрос GET /directors/{}", id);
        final DirectorDto director = directorService.getById(id);
        log.info("Отправлен ответ GET /directors/{} с телом: {}", id, director);
        return director;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DirectorDto create(@Valid @RequestBody DirectorDto newDirector) {
        log.info("Получен запрос POST /directors с телом: {}", newDirector);
        final DirectorDto createdDirector = directorService.create(newDirector);
        log.info("Отправлен ответ POST /directors с телом: {}", createdDirector);
        return createdDirector;
    }

    @PutMapping
    public DirectorDto update(@Valid @RequestBody DirectorDto newDirector) {
        log.info("Получен запрос PUT /directors с телом: {}", newDirector);
        final DirectorDto updatedDirector = directorService.update(newDirector);
        log.info("Отправлен ответ PUT /directors с телом: {}", updatedDirector);
        return updatedDirector;
    }

    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable Long id) {
        log.info("Получен запрос DELETE /directors/{}", id);
        directorService.delete(id);
        log.info("Режиссёр {} удалён", id);
    }

}
