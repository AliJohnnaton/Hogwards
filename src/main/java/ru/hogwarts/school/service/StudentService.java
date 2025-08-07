package ru.hogwarts.school.service;

import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.hogwarts.school.dto.FacultyResponseDto;
import ru.hogwarts.school.dto.StudentRequestDto;
import ru.hogwarts.school.dto.StudentResponseDto;
import ru.hogwarts.school.exceptions.FacultyNotFoundException;
import ru.hogwarts.school.exceptions.StudentNotFoundException;
import ru.hogwarts.school.mapper.StudentMapper;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.FacultyRepository;
import ru.hogwarts.school.repository.StudentRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class StudentService {
    private final StudentRepository studentRepository;
    private final FacultyRepository facultyRepository;
    private final StudentMapper mapper;

    public StudentService(StudentRepository repository,
                          FacultyRepository facultyRepository,
                          StudentMapper mapper) {
        this.studentRepository = repository;
        this.facultyRepository = facultyRepository;
        this.mapper = mapper;
    }

    public StudentResponseDto create(StudentRequestDto dto) {
        Student student = mapper.toEntity(dto);
        if (dto.getFacultyId() != null) {
            Faculty faculty = facultyRepository.findById(dto.getFacultyId())
                    .orElseThrow(() -> new FacultyNotFoundException(dto.getFacultyId()));
            student.setFaculty(faculty);
        }
        Student saved = studentRepository.save(student);
        return mapper.toDto(saved);
    }

    public StudentResponseDto read(Long id) {
        return studentRepository.findById(id)
                .map(mapper::toDto)
                .orElseThrow(() -> new StudentNotFoundException(id));
    }

    public StudentResponseDto update(Long id, StudentRequestDto dto) {
        Student existing = studentRepository.findById(id)
                .orElseThrow(() -> new StudentNotFoundException(id));

        existing.setName(dto.getName());
        existing.setAge(dto.getAge());

        if (dto.getFacultyId() != null) {
            Faculty faculty = facultyRepository.findById(dto.getFacultyId())
                    .orElseThrow(() -> new FacultyNotFoundException(dto.getFacultyId()));
            existing.setFaculty(faculty);
        } else {
            existing.setFaculty(null);
        }

        return mapper.toDto(studentRepository.save(existing));
    }

    public void delete(Long id) {
        studentRepository.deleteById(id);
    }

    public Page<StudentResponseDto> getAll(int page, int size) {
        return studentRepository.findAll(PageRequest.of(page, size))
                .map(mapper::toDto);
    }

    public List<StudentResponseDto> findByAgeBetween(int min, int max) {
        return studentRepository.findByAgeBetween(min, max).stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    public FacultyResponseDto getFacultyByStudentId(Long studentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new StudentNotFoundException(studentId));

        if (student.getFaculty() == null) {
            throw new FacultyNotFoundException("Student has no faculty");
        }

        return new FacultyResponseDto(
                student.getFaculty().getId(),
                student.getFaculty().getName(),
                student.getFaculty().getColor(),
                student.getFaculty().getStudents().stream()
                        .map(Student::getId)
                        .collect(Collectors.toList())
        );
    }
}