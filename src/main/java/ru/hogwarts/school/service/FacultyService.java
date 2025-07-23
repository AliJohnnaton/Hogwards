package ru.hogwarts.school.service;

import org.springframework.stereotype.Service;
import ru.hogwarts.school.model.Faculty;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class FacultyService {
    private final Map<Long, Faculty> faculties = new HashMap<>();
    private Long idCounter = 0L;

    public Faculty create(Faculty faculty) {
        long id = ++idCounter;
        faculty.setId(id);
        faculties.put(id, faculty);
        return faculty;
    }

    public Faculty get(Long id) {
        return faculties.get(id);
    }

    public Faculty update(Faculty faculty) {
        faculties.put(faculty.getId(), faculty);
        return faculty;
    }

    public Faculty delete(Long id) {
        return faculties.remove(id);
    }

    public Collection<Faculty> findByColor(String color) {
        return faculties.values().stream()
                .filter(f -> f.getColor().equalsIgnoreCase(color))
                .collect(Collectors.toList());
    }
}
