package ru.hogwarts.school.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.StudentRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class StudentServiceTest {

    private StudentRepository studentRepository;
    private StudentService studentService;

    @BeforeEach
    void setUp() {
        studentRepository = mock(StudentRepository.class);
        studentService = new StudentService(studentRepository, null, null, null);
    }

    @Test
    void testGetNamesStartingWithA() {
        Student s1 = new Student();
        s1.setId(1L);
        s1.setName("Alice");
        s1.setAge(20);

        Student s2 = new Student();
        s2.setId(2L);
        s2.setName("Bob");
        s2.setAge(22);

        Student s3 = new Student();
        s3.setId(3L);
        s3.setName("Андрей");
        s3.setAge(19);

        when(studentRepository.findAll()).thenReturn(List.of(s1, s2, s3));


        List<String> result = studentService.getNamesStartingWithA();

        assertEquals(List.of("ALICE", "АНДРЕЙ"), result);
        verify(studentRepository, times(1)).findAll();
    }

    @Test
    void testGetSumOneToMillion() {
        long sum = studentService.getSumOneToMillion();
        assertEquals(500_000_500_000L, sum);
    }

}
