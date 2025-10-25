package ru.hogwarts.school.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.repository.FacultyRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class FacultyServiceTest {

    private FacultyRepository facultyRepository;
    private FacultyService facultyService;

    @BeforeEach
    void setUp() {
        facultyRepository = mock(FacultyRepository.class);
        facultyService = new FacultyService(facultyRepository, null); // mapper не нужен
    }

    @Test
    void testGetLongestFacultyName() {
        Faculty f1 = new Faculty();
        f1.setId(1L);
        f1.setName("Red");

        Faculty f2 = new Faculty();
        f2.setId(2L);
        f2.setName("GreenFaculty");

        Faculty f3 = new Faculty();
        f3.setId(3L);
        f3.setName("Blue");

        when(facultyRepository.findAll()).thenReturn(List.of(f1, f2, f3));


        String longest = facultyService.getLongestFacultyName();

        assertEquals("GreenFaculty", longest);
        verify(facultyRepository, times(1)).findAll();
    }
}
