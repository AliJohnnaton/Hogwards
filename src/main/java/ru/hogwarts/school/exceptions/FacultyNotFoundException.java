package ru.hogwarts.school.exceptions;

public class FacultyNotFoundException extends RuntimeException {
    public FacultyNotFoundException(Long id) {
        super("Факультет с ID:" + id + " не найден.");
    }

    public FacultyNotFoundException(String message) {
        super(message);
    }
}
