package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.dto.MpaDto;
import ru.yandex.practicum.filmorate.service.MpaService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/mpa")
public class MpaController {
    private final MpaService mpaService;

    @Autowired
    public MpaController(MpaService mpaService) {
        this.mpaService = mpaService;
    }

    @GetMapping
    public List<MpaDto> getAllMpa() {
        log.info("Получен запрос GET /mpa");
        List<MpaDto> mpaList = mpaService.getAllMpa();
        log.info("Отправлен ответ GET /mpa. Всего {} рейтингов MPA: {}", mpaList.size(), mpaList);
        return mpaList;
    }

    @GetMapping("/{id}")
    public MpaDto getMpaById(@PathVariable Long id) {
        log.info("Получен запрос GET /mpa/{}", id);
        MpaDto mpa = mpaService.getMpaById(id);
        log.info("Отправлен ответ GET /mpa/{} с телом: {}", id, mpa);
        return mpa;
    }
}
