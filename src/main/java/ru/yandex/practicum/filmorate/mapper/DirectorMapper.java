package ru.yandex.practicum.filmorate.mapper;

import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.dto.DirectorDto;
import ru.yandex.practicum.filmorate.model.Director;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class DirectorMapper {
    public static DirectorDto mapToDirectorDto(Director director) {
        return DirectorDto.builder()
                .id(director.getId())
                .name(director.getName())
                .build();
    }

    public static Director mapDtoToDirector(DirectorDto directorDto) {
        return Director.builder()
                .id(directorDto.getId())
                .name(directorDto.getName())
                .build();
    }
}
