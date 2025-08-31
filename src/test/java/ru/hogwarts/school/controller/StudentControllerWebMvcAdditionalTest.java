package ru.hogwarts.school.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.hogwarts.school.dto.StudentResponseDto;
import ru.hogwarts.school.service.StudentService;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class StudentControllerWebMvcAdditionalTest {

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private StudentService studentService;

    @InjectMocks
    private StudentController studentController;

    private final StudentResponseDto student1 = new StudentResponseDto(1L, "Гарри Поттер", 17, null);
    private final StudentResponseDto student2 = new StudentResponseDto(2L, "Гермиона Грейнджер", 17, null);

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(studentController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void countAllStudents_ShouldReturnCount() throws Exception {
        when(studentService.getStudentCount()).thenReturn(100L);

        mockMvc.perform(get("/students/count"))
                .andExpect(status().isOk())
                .andExpect(content().string("100"));
    }

    @Test
    void getAverageAge_ShouldReturnAverage() throws Exception {
        when(studentService.getAverageAge()).thenReturn(18.5);

        mockMvc.perform(get("/students/average-age"))
                .andExpect(status().isOk())
                .andExpect(content().string("18.5"));
    }

    @Test
    void getLastFiveStudents_ShouldReturnList() throws Exception {
        when(studentService.getLastFiveStudents()).thenReturn(List.of(student1, student2));

        mockMvc.perform(get("/students/last-five"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Гарри Поттер"))
                .andExpect(jsonPath("$[1].name").value("Гермиона Грейнджер"));
    }
}
