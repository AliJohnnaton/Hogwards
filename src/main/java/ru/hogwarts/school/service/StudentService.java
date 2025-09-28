package ru.hogwarts.school.service;

import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.hogwarts.school.dto.FacultyResponseDto;
import ru.hogwarts.school.dto.StudentRequestDto;
import ru.hogwarts.school.dto.StudentResponseDto;
import ru.hogwarts.school.exceptions.FacultyNotFoundException;
import ru.hogwarts.school.mapper.StudentMapper;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.AvatarRepository;
import ru.hogwarts.school.repository.FacultyRepository;
import ru.hogwarts.school.repository.StudentRepository;

import java.util.List;

@Service
@Transactional
public class StudentService {
    private final StudentRepository studentRepository;
    private final FacultyRepository facultyRepository;
    private final AvatarRepository avatarRepository;
    private final StudentMapper mapper;

    public StudentService(StudentRepository repository, FacultyRepository facultyRepository, AvatarRepository avatarRepository, StudentMapper mapper) {
        this.studentRepository = repository;
        this.facultyRepository = facultyRepository;
        this.avatarRepository = avatarRepository;
        this.mapper = mapper;
    }

    public StudentResponseDto create(StudentRequestDto dto) {
        Student student = mapper.toEntity(dto);
        if (dto.getFacultyId() != null) {
            Faculty faculty = facultyRepository.findById(dto.getFacultyId()).orElseThrow();
            student.setFaculty(faculty);
        }
        return mapper.toDto(studentRepository.save(student));
    }

    public StudentResponseDto read(Long id) {
        return studentRepository.findById(id).map(mapper::toDto).orElseThrow();
    }

    public StudentResponseDto update(Long id, StudentRequestDto dto) {
        Student existing = studentRepository.findById(id).orElseThrow();
        existing.setName(dto.getName());
        existing.setAge(dto.getAge());
        if (dto.getFacultyId() != null) {
            Faculty faculty = facultyRepository.findById(dto.getFacultyId()).orElseThrow();
            existing.setFaculty(faculty);
        } else {
            existing.setFaculty(null);
        }
        return mapper.toDto(studentRepository.save(existing));
    }

    public void delete(Long id) {
        Student student = studentRepository.findById(id).orElseThrow();
        if (student.getAvatar() != null) {
            avatarRepository.delete(student.getAvatar());
        }
        studentRepository.delete(student);
    }

    public Page<StudentResponseDto> getAll(int page, int size) {
        return studentRepository.findAll(PageRequest.of(page, size)).map(mapper::toDto);
    }

    public List<StudentResponseDto> findByAgeBetween(int min, int max) {
        return studentRepository.findByAgeBetween(min, max).stream().map(mapper::toDto).toList();
    }

    public FacultyResponseDto getFacultyByStudentId(Long studentId) {
        Faculty faculty = studentRepository.findById(studentId).orElseThrow().getFaculty();
        if (faculty == null) {
            throw new FacultyNotFoundException("Student has no faculty");
        }
        return new FacultyResponseDto(faculty.getId(), faculty.getName(), faculty.getColor(), faculty.getStudents().stream().map(Student::getId).toList());
    }

    public long getStudentCount() {
        return studentRepository.countAllStudents();
    }

    public Double getAverageAge() {
        return studentRepository.getAverageAge();
    }

    public List<StudentResponseDto> getLastFiveStudents() {
        return studentRepository.findLastFiveStudents().stream().map(mapper::toDto).toList();
    }
}
