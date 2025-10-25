package ru.hogwarts.school.service;

import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import java.util.Locale;

@Service
@Transactional
public class StudentService {

    private static final Logger logger = LoggerFactory.getLogger(StudentService.class);

    private final StudentRepository studentRepository;
    private final FacultyRepository facultyRepository;
    private final AvatarRepository avatarRepository;
    private final StudentMapper mapper;

    public StudentService(StudentRepository repository, FacultyRepository facultyRepository,
                          AvatarRepository avatarRepository, StudentMapper mapper) {
        this.studentRepository = repository;
        this.facultyRepository = facultyRepository;
        this.avatarRepository = avatarRepository;
        this.mapper = mapper;
    }

    public StudentResponseDto create(StudentRequestDto dto) {
        logger.info("Was invoked method for create student");
        Student student = mapper.toEntity(dto);
        if (dto.getFacultyId() != null) {
            Faculty faculty = facultyRepository.findById(dto.getFacultyId()).orElseThrow();
            student.setFaculty(faculty);
            logger.debug("Assigning faculty id {} to new student", dto.getFacultyId());
        }
        Student saved = studentRepository.save(student);
        logger.debug("Created student with id {}", saved.getId());
        return mapper.toDto(saved);
    }

    public StudentResponseDto read(Long id) {
        logger.info("Was invoked method for read student with id {}", id);
        return studentRepository.findById(id)
                .map(mapper::toDto)
                .orElseThrow(() -> {
                    logger.error("No student found with id {}", id);
                    return new RuntimeException("Student not found");
                });
    }

    public StudentResponseDto update(Long id, StudentRequestDto dto) {
        logger.info("Was invoked method for update student with id {}", id);
        Student existing = studentRepository.findById(id).orElseThrow(() -> {
            logger.error("No student found with id {}", id);
            return new RuntimeException("Student not found");
        });
        existing.setName(dto.getName());
        existing.setAge(dto.getAge());
        if (dto.getFacultyId() != null) {
            Faculty faculty = facultyRepository.findById(dto.getFacultyId()).orElseThrow();
            existing.setFaculty(faculty);
            logger.debug("Updated faculty for student id {} to faculty id {}", id, dto.getFacultyId());
        } else {
            existing.setFaculty(null);
            logger.debug("Removed faculty from student id {}", id);
        }
        Student saved = studentRepository.save(existing);
        logger.debug("Updated student with id {}", saved.getId());
        return mapper.toDto(saved);
    }

    public void delete(Long id) {
        logger.info("Was invoked method for delete student with id {}", id);
        Student student = studentRepository.findById(id).orElseThrow(() -> {
            logger.error("No student found with id {}", id);
            return new RuntimeException("Student not found");
        });
        if (student.getAvatar() != null) {
            avatarRepository.delete(student.getAvatar());
            logger.debug("Deleted avatar for student id {}", id);
        }
        studentRepository.delete(student);
        logger.debug("Deleted student with id {}", id);
    }

    public Page<StudentResponseDto> getAll(int page, int size) {
        logger.info("Was invoked method for getAll students, page={}, size={}", page, size);
        Page<StudentResponseDto> result = studentRepository.findAll(PageRequest.of(page, size)).map(mapper::toDto);
        logger.debug("Returning {} students", result.getContent().size());
        return result;
    }

    public List<StudentResponseDto> findByAgeBetween(int min, int max) {
        logger.info("Was invoked method for findByAgeBetween: min={}, max={}", min, max);
        List<StudentResponseDto> result = studentRepository.findByAgeBetween(min, max).stream().map(mapper::toDto)
                .toList();
        logger.debug("Found {} students", result.size());
        return result;
    }

    public FacultyResponseDto getFacultyByStudentId(Long studentId) {
        logger.info("Was invoked method for getFacultyByStudentId: {}", studentId);
        Student student = studentRepository.findById(studentId).orElseThrow(() -> {
            logger.error("No student found with id {}", studentId);
            return new RuntimeException("Student not found");
        });
        Faculty faculty = student.getFaculty();
        if (faculty == null) {
            logger.warn("Student id {} has no faculty", studentId);
            throw new FacultyNotFoundException("Student has no faculty");
        }
        logger.debug("Returning faculty id {} for student id {}", faculty.getId(), studentId);
        return new FacultyResponseDto(faculty.getId(), faculty.getName(), faculty.getColor(), faculty.getStudents()
                .stream().map(Student::getId).toList());
    }

    public long getStudentCount() {
        logger.info("Was invoked method for getStudentCount");
        long count = studentRepository.countAllStudents();
        logger.debug("Total student count: {}", count);
        return count;
    }

    public Double getAverageAge() {
        logger.info("Was invoked method for getAverageAge");
        Double avg = studentRepository.getAverageAge();
        logger.debug("Average age of students: {}", avg);
        return avg;
    }

    public List<StudentResponseDto> getLastFiveStudents() {
        logger.info("Was invoked method for getLastFiveStudents");
        List<StudentResponseDto> result = studentRepository.findLastFiveStudents().stream().map(mapper::toDto).toList();
        logger.debug("Returning {} students", result.size());
        return result;
    }

    public List<String> getNamesStartingWithA() {
        logger.info("Was invoked method for getNamesStartingWithA");
        List<String> result = studentRepository.findAll().stream()
                .map(Student::getName)
                .filter(name -> name != null && !name.isEmpty())
                .map(name -> name.toUpperCase(Locale.ROOT))
                .filter(name -> name.startsWith("A") || name.startsWith("А"))
                .sorted()
                .toList();
        logger.debug("Returning {} names starting with A", result.size());
        return result;
    }

    public long getSumOneToMillion() {
        logger.info("Was invoked method for getSumOneToMillion");
        long n = 1_000_000L;
        long sum = n * (n + 1) / 2; // 500_000_500_000
        logger.debug("Calculated sum from 1 to 1_000_000: {}", sum);
        return sum;
    }

    public void printParallel() {
        List<String> names = studentRepository.findAll().stream()
                .map(Student::getName)
                .limit(6)
                .toList();

        names.subList(0, 2).forEach(System.out::println);

        Thread t1 = new Thread(() -> names.subList(2, 4).forEach(System.out::println));
        Thread t2 = new Thread(() -> names.subList(4, 6).forEach(System.out::println));

        t1.start();
        t2.start();

        try {
            t1.join();
            t2.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void printSynchronized() {
        List<String> names = studentRepository.findAll().stream()
                .map(Student::getName)
                .limit(6)
                .toList();

        synchronized (this) {
            names.subList(0, 2).forEach(System.out::println);
        }

        Thread t1 = new Thread(() -> {
            synchronized (this) {
                names.subList(2, 4).forEach(System.out::println);
            }
        });

        Thread t2 = new Thread(() -> {
            synchronized (this) {
                names.subList(4, 6).forEach(System.out::println);
            }
        });

        t1.start();
        t2.start();

        try {
            t1.join();
            t2.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
