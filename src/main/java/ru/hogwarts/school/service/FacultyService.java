package ru.hogwarts.school.service;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import ru.hogwarts.school.dto.FacultyRequestDto;
import ru.hogwarts.school.dto.FacultyResponseDto;
import ru.hogwarts.school.dto.StudentResponseDto;
import ru.hogwarts.school.exceptions.FacultyNotFoundException;
import ru.hogwarts.school.mapper.FacultyMapper;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.repository.FacultyRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class FacultyService {
    private final FacultyRepository facultyRepository;
    private final FacultyMapper mapper;

    public FacultyService(FacultyRepository facultyRepository,
                          FacultyMapper mapper) {
        this.facultyRepository = facultyRepository;
        this.mapper = mapper;
    }

    public FacultyResponseDto create(FacultyRequestDto dto) {
        Faculty faculty = mapper.toEntity(dto);
        Faculty saved = facultyRepository.save(faculty);
        return mapper.toDto(saved);
    }

    public FacultyResponseDto read(Long id) {
        return facultyRepository.findById(id)
                .map(mapper::toDto)
                .orElseThrow(() -> new FacultyNotFoundException(id));
    }

    public FacultyResponseDto update(Long id, FacultyRequestDto dto) {
        Faculty existing = facultyRepository.findById(id)
                .orElseThrow(() -> new FacultyNotFoundException(id));

        existing.setName(dto.getName());
        existing.setColor(dto.getColor());

        return mapper.toDto(facultyRepository.save(existing));
    }

    public void delete(Long id) {
        facultyRepository.deleteById(id);
    }

    public List<FacultyResponseDto> getAll() {
        return facultyRepository.findAll().stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    public List<FacultyResponseDto> findByNameOrColor(String query) {
        return facultyRepository.findByNameOrColorIgnoreCase(query).stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    public List<StudentResponseDto> getStudentsByFacultyId(Long facultyId) {
        Faculty faculty = facultyRepository.findById(facultyId)
                .orElseThrow(() -> new FacultyNotFoundException(facultyId));

        return faculty.getStudents().stream()
                .map(s -> new StudentResponseDto(
                        s.getId(),
                        s.getName(),
                        s.getAge(),
                        facultyId))
                .collect(Collectors.toList());
    }
}