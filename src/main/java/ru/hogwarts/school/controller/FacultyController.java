package ru.hogwarts.school.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.hogwarts.school.dto.FacultyRequestDto;
import ru.hogwarts.school.dto.FacultyResponseDto;
import ru.hogwarts.school.dto.StudentResponseDto;
import ru.hogwarts.school.service.FacultyService;

import java.util.List;

@RestController
@RequestMapping("/faculties")
public class FacultyController {
    private final FacultyService service;

    public FacultyController(FacultyService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<FacultyResponseDto> create(@RequestBody FacultyRequestDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<FacultyResponseDto> read(@PathVariable Long id) {
        return ResponseEntity.ok(service.read(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<FacultyResponseDto> update(@PathVariable Long id, @RequestBody FacultyRequestDto dto) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<FacultyResponseDto>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/filter")
    public ResponseEntity<List<FacultyResponseDto>> filter(@RequestParam String query) {
        return ResponseEntity.ok(service.findByNameOrColor(query));
    }

    @GetMapping("/{id}/students")
    public ResponseEntity<List<StudentResponseDto>> getStudents(@PathVariable Long id) {
        return ResponseEntity.ok(service.getStudentsByFacultyId(id));
    }
}