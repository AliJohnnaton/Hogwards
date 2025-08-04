package ru.hogwarts.school.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class FacultyRequestDto {
    @NotBlank
    @Size(min = 2, max = 50)
    private String name;

    @NotBlank
    private String color;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }
}