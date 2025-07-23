package ru.hogwarts.school.service;

import org.springframework.stereotype.Service;
import ru.hogwarts.school.model.Student;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class StudentService {
    private final Map<Long, Student> students = new HashMap<>();
    private Long idCounter = 0L;

    public Student create(Student student) {
        long id = ++idCounter;
        student.setId(id);
        students.put(id, student);
        return student;
    }

    public Student get(Long id) {
        return students.get(id);
    }

    public Student update(Student student) {
        students.put(student.getId(), student);
        return student;
    }

    public Student delete(Long id) {
        return students.remove(id);
    }

    public Collection<Student> findByAge(int age) {
        return students.values().stream()
                .filter(s -> s.getAge() == age)
                .collect(Collectors.toList());
    }
}
