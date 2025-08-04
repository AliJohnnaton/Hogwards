package ru.hogwarts.school.dto;

import java.util.List;

public class FacultyResponseDto {
    private Long id;
    private String name;
    private String color;
    private List<Long> studentIds;

    public FacultyResponseDto(Long id, String name, String color, List<Long> studentIds) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.studentIds = studentIds;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }

    public List<Long> getStudentIds() {
        return studentIds;
    }
}