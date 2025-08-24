package ru.hogwarts.school.controller;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.hogwarts.school.dto.FacultyResponseDto;
import ru.hogwarts.school.dto.StudentRequestDto;
import ru.hogwarts.school.dto.StudentResponseDto;
import ru.hogwarts.school.exceptions.FacultyNotFoundException;
import ru.hogwarts.school.exceptions.StudentNotFoundException;
import ru.hogwarts.school.service.StudentService;

import java.util.List;

@RestController
@RequestMapping("/students")
public class StudentController {
    private final StudentService service;

    public StudentController(StudentService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<StudentResponseDto> create(@RequestBody StudentRequestDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<StudentResponseDto> read(@PathVariable Long id) {
        return ResponseEntity.ok(service.read(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<StudentResponseDto> update(@PathVariable Long id, @RequestBody StudentRequestDto dto) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<Page<StudentResponseDto>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(service.getAll(page, size));
    }

    @GetMapping("/age-between")
    public ResponseEntity<List<StudentResponseDto>> findByAgeBetween(
            @RequestParam int min,
            @RequestParam int max) {
        return ResponseEntity.ok(service.findByAgeBetween(min, max));
    }

    @GetMapping("/{id}/faculty")
    public ResponseEntity<FacultyResponseDto> getFaculty(@PathVariable Long id) {
        return ResponseEntity.ok(service.getFacultyByStudentId(id));
    }
    @ExceptionHandler(StudentNotFoundException.class)
    public ResponseEntity<String> handleStudentNotFound(StudentNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(FacultyNotFoundException.class)
    public ResponseEntity<String> handleFacultyNotFound(FacultyNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }
}