package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.MpaRepository;
import ru.yandex.practicum.filmorate.dto.MpaDto;
import ru.yandex.practicum.filmorate.mapper.MpaMapper;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class MpaService {
    private final MpaRepository mpaRepository;

    public List<MpaDto> getAllMpa() {
        log.info("Получен запрос getAllMpa");
        List<Mpa> mpaList = mpaRepository.findAll();
        log.info("Получен список всех рейтингов MPA. Количество: {}", mpaList.size());
        return mpaList.stream()
                .map(MpaMapper::mapToMpaDto)
                .collect(Collectors.toList());
    }

    public MpaDto getMpaById(Long id) {
        log.info("Получен запрос getMpaById с id: {}", id);
        Mpa mpa = mpaRepository.MpaById(id);
        log.info("Отправлен ответ MpaMapper.mapToMpaDto(mpa): {}", MpaMapper.mapToMpaDto(mpa));
        return MpaMapper.mapToMpaDto(mpa);
    }
}
