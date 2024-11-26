package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.DirectorDto;
import ru.yandex.practicum.filmorate.service.DirectorService;

import java.util.List;

@RestController
@RequestMapping("/directors")
public class DirectorController {

    private final DirectorService directorService;

    @Autowired
    public DirectorController(DirectorService directorService) {
        this.directorService = directorService;
    }

    @GetMapping
    public List<DirectorDto> findAll() {
        return directorService.findAll();
    }

    @GetMapping("/{id}")
    public DirectorDto getById(@PathVariable Long id) {
        return directorService.getById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DirectorDto create(@Valid @RequestBody DirectorDto newDirector) {
        return directorService.create(newDirector);
    }

    @PutMapping
    public DirectorDto update(@Valid @RequestBody DirectorDto newDirector) {
        return directorService.update(newDirector);
    }

    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable Long id) {
        directorService.delete(id);
    }

}
