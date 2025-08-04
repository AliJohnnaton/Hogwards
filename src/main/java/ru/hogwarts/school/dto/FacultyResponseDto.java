package ru.hogwarts.school.dto;

import java.util.List;

public record FacultyResponseDto(Long id, String name, String color, List<Long> studentIds) {
}