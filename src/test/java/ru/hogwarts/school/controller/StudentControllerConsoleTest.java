package ru.hogwarts.school.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import ru.hogwarts.school.dto.StudentResponseDto;
import ru.hogwarts.school.service.StudentService;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class StudentControllerConsoleTest {

    private StudentService studentService;
    private StudentController studentController;

    private List<StudentResponseDto> students;

    @BeforeEach
    void setUp() {
        studentService = mock(StudentService.class);
        studentController = new StudentController(studentService);

        students = List.of(
                new StudentResponseDto(1L, "Alice", 20, null),
                new StudentResponseDto(2L, "Bob", 21, null),
                new StudentResponseDto(3L, "Charlie", 22, null),
                new StudentResponseDto(4L, "David", 23, null),
                new StudentResponseDto(5L, "Eve", 24, null),
                new StudentResponseDto(6L, "Frank", 25, null)
        );
    }

    @Test
    void getAll_ShouldReturnPageOfStudents() {
        Page<StudentResponseDto> studentPage = new PageImpl<>(students, PageRequest.of(0, 10), students.size());

        when(studentService.getAll(0, 10)).thenReturn(studentPage);

        var response = studentController.getAll(0, 10).getBody();

        assertThat(response).isNotNull();
        assertThat(response.getContent()).isEqualTo(students);

        verify(studentService, times(1)).getAll(0, 10);
    }

    @Test
    void printParallel_ShouldCallService() {
        ResponseEntity<String> response = studentController.printParallel();

        assertThat(response.getBody()).isEqualTo("Printed parallel to console");
        verify(studentService, times(1)).printParallel();
    }

    @Test
    void printSynchronized_ShouldCallService() {
        ResponseEntity<String> response = studentController.printSynchronized();

        assertThat(response.getBody()).isEqualTo("Printed synchronized to console");
        verify(studentService, times(1)).printSynchronized();
    }
}
