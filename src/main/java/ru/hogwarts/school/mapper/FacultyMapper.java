package ru.hogwarts.school.mapper;

import org.springframework.stereotype.Component;
import ru.hogwarts.school.dto.FacultyRequestDto;
import ru.hogwarts.school.dto.FacultyResponseDto;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;

import java.util.stream.Collectors;

@Component
public class FacultyMapper {
    public Faculty toEntity(FacultyRequestDto dto) {
        Faculty faculty = new Faculty();
        faculty.setName(dto.getName());
        faculty.setColor(dto.getColor());
        return faculty;
    }

    public FacultyResponseDto toDto(Faculty faculty) {
        return new FacultyResponseDto(
                faculty.getId(),
                faculty.getName(),
                faculty.getColor(),
                faculty.getStudents().stream()
                        .map(Student::getId)
                        .collect(Collectors.toList())
        );
    }
}