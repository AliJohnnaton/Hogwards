package ru.hogwarts.school.service;

import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.hogwarts.school.dto.FacultyRequestDto;
import ru.hogwarts.school.dto.FacultyResponseDto;
import ru.hogwarts.school.dto.StudentResponseDto;
import ru.hogwarts.school.exceptions.FacultyNotFoundException;
import ru.hogwarts.school.mapper.FacultyMapper;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.repository.FacultyRepository;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Transactional
public class FacultyService {

    private static final Logger logger = LoggerFactory.getLogger(FacultyService.class);

    private final FacultyRepository facultyRepository;
    private final FacultyMapper mapper;

    public FacultyService(FacultyRepository facultyRepository, FacultyMapper mapper) {
        this.facultyRepository = facultyRepository;
        this.mapper = mapper;
    }

    public FacultyResponseDto create(FacultyRequestDto dto) {
        logger.info("Was invoked method for create faculty");
        Faculty faculty = mapper.toEntity(dto);
        Faculty saved = facultyRepository.save(faculty);
        logger.debug("Created faculty with id {}", saved.getId());
        return mapper.toDto(saved);
    }

    public FacultyResponseDto read(Long id) {
        logger.info("Was invoked method for read faculty with id {}", id);
        return facultyRepository.findById(id)
                .map(mapper::toDto)
                .orElseThrow(() -> {
                    logger.error("No faculty found with id {}", id);
                    return new FacultyNotFoundException(id);
                });
    }

    public FacultyResponseDto update(Long id, FacultyRequestDto dto) {
        logger.info("Was invoked method for update faculty with id {}", id);
        Faculty existing = facultyRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("No faculty found with id {}", id);
                    return new FacultyNotFoundException(id);
                });

        existing.setName(dto.getName());
        existing.setColor(dto.getColor());
        logger.debug("Updated faculty id {} with name '{}' and color '{}'", id, dto.getName(), dto.getColor());

        Faculty saved = facultyRepository.save(existing);
        return mapper.toDto(saved);
    }

    public void delete(Long id) {
        logger.info("Was invoked method for delete faculty with id {}", id);
        if (!facultyRepository.existsById(id)) {
            logger.warn("Trying to delete non-existing faculty with id {}", id);
        }
        facultyRepository.deleteById(id);
        logger.debug("Deleted faculty with id {}", id);
    }

    public List<FacultyResponseDto> getAll() {
        logger.info("Was invoked method for getAll faculties");
        List<FacultyResponseDto> result = facultyRepository.findAll().stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
        logger.debug("Returning {} faculties", result.size());
        return result;
    }

    public List<FacultyResponseDto> findByNameOrColor(String query) {
        logger.info("Was invoked method for findByNameOrColor with query '{}'", query);
        List<FacultyResponseDto> result = facultyRepository.findByNameOrColorIgnoreCase(query).stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
        logger.debug("Found {} faculties matching query '{}'", result.size(), query);
        return result;
    }

    public List<StudentResponseDto> getStudentsByFacultyId(Long facultyId) {
        logger.info("Was invoked method for getStudentsByFacultyId with faculty id {}", facultyId);
        Faculty faculty = facultyRepository.findById(facultyId)
                .orElseThrow(() -> {
                    logger.error("No faculty found with id {}", facultyId);
                    return new FacultyNotFoundException(facultyId);
                });

        List<StudentResponseDto> result = faculty.getStudents().stream()
                .map(s -> new StudentResponseDto(
                        s.getId(),
                        s.getName(),
                        s.getAge(),
                        facultyId))
                .collect(Collectors.toList());
        logger.debug("Returning {} students for faculty id {}", result.size(), facultyId);
        return result;
    }

    public String getLongestFacultyName() {
        logger.info("Was invoked method for getLongestFacultyName");
        String longestName = facultyRepository.findAll().stream()
                .map(Faculty::getName)
                .filter(Objects::nonNull)
                .max(Comparator.comparingInt(String::length))
                .orElse("");
        logger.debug("Longest faculty name: {}", longestName);
        return longestName;
    }

    public List<FacultyResponseDto> findByNameStartingWithA() {
        logger.info("Was invoked method for findByNameStartingWithA");
        List<Faculty> faculties = facultyRepository.findAll().stream()
                .filter(f -> f.getName() != null && f.getName().startsWith("A"))
                .toList();
        logger.debug("Found {} faculties starting with 'A'", faculties.size());
        return faculties.stream().map(mapper::toDto).toList();
    }

    public FacultyResponseDto getFacultyWithLongestName() {
        logger.info("Was invoked method for getFacultyWithLongestName");
        return facultyRepository.findAll().stream()
                .filter(f -> f.getName() != null)
                .max(Comparator.comparingInt(f -> f.getName().length()))
                .map(mapper::toDto)
                .orElseThrow(() -> new FacultyNotFoundException("No faculties found"));
    }


}
