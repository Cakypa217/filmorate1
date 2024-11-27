package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.DirectorRepository;
import ru.yandex.practicum.filmorate.dto.DirectorDto;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.DirectorMapper;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class DirectorService {

    private final DirectorRepository directorRepository;

    public List<DirectorDto> findAll() {
        return directorRepository.findAll().stream()
                .map(DirectorMapper::mapToDirectorDto)
                .collect(Collectors.toList());
    }

    public DirectorDto getById(Long id) {
        return directorRepository.getById(id)
                .map(DirectorMapper::mapToDirectorDto)
                .orElseThrow(() -> new NotFoundException("Режиссер с id = " + id + "не найден"));
    }

    public DirectorDto create(DirectorDto newDirector) {
        return DirectorMapper.mapToDirectorDto(directorRepository.create(DirectorMapper.mapDtoToDirector(newDirector)));
    }

    public DirectorDto update(DirectorDto newDirector) {
        return DirectorMapper.mapToDirectorDto(directorRepository.update(DirectorMapper.mapDtoToDirector(newDirector)));
    }

    public boolean delete(Long id) {
        if (!directorRepository.delete(id)) {
            throw new NotFoundException("Режиссер с id = " + id + "не найден");
        }
        return true;
    }

}
