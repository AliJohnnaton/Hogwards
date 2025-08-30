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
import ru.hogwarts.school.dto.FacultyResponseDto;
import ru.hogwarts.school.dto.StudentRequestDto;
import ru.hogwarts.school.dto.StudentResponseDto;
import ru.hogwarts.school.exceptions.FacultyNotFoundException;
import ru.hogwarts.school.service.StudentService;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class StudentControllerWebMvcTest {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final StudentResponseDto studentResponse = new StudentResponseDto(1L, "Гарри Поттер", 17, null);
    private final FacultyResponseDto facultyResponse = new FacultyResponseDto(1L, "Гриффиндор", "красный", List.of());
    private MockMvc mockMvc;
    @Mock
    private StudentService studentService;
    @InjectMocks
    private StudentController studentController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(studentController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void create_ShouldReturnCreatedStudent() throws Exception {
        StudentRequestDto requestDto = new StudentRequestDto();
        requestDto.setName("Гарри Поттер");
        requestDto.setAge(17);

        when(studentService.create(any(StudentRequestDto.class))).thenReturn(studentResponse);

        mockMvc.perform(post("/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Гарри Поттер"))
                .andExpect(jsonPath("$.age").value(17));
    }

    @Test
    void read_ShouldReturnStudent() throws Exception {
        when(studentService.read(1L)).thenReturn(studentResponse);

        mockMvc.perform(get("/students/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Гарри Поттер"));
    }

    @Test
    void update_ShouldReturnUpdatedStudent() throws Exception {
        StudentRequestDto requestDto = new StudentRequestDto();
        requestDto.setName("Гарри Поттер");
        requestDto.setAge(18);

        StudentResponseDto updatedResponse = new StudentResponseDto(1L, "Гарри Поттер", 18, null);
        when(studentService.update(eq(1L), any(StudentRequestDto.class))).thenReturn(updatedResponse);

        mockMvc.perform(put("/students/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.age").value(18));
    }

    @Test
    void delete_ShouldReturnNoContent() throws Exception {
        doNothing().when(studentService).delete(1L);

        mockMvc.perform(delete("/students/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void getAll_ShouldReturnPageOfStudents() throws Exception {
        Page<StudentResponseDto> page = new PageImpl<>(List.of(studentResponse), PageRequest.of(0, 10), 1);
        when(studentService.getAll(0, 10)).thenReturn(page);

        mockMvc.perform(get("/students?page=0&size=10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name").value("Гарри Поттер"));
    }

    @Test
    void findByAgeBetween_ShouldReturnStudents() throws Exception {
        when(studentService.findByAgeBetween(16, 19)).thenReturn(List.of(studentResponse));

        mockMvc.perform(get("/students/age-between?min=16&max=19"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Гарри Поттер"));
    }

    @Test
    void getFaculty_ShouldReturnFaculty() throws Exception {
        when(studentService.getFacultyByStudentId(1L)).thenReturn(facultyResponse);

        mockMvc.perform(get("/students/1/faculty"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Гриффиндор"));
    }

    @Test
    void getFaculty_WhenStudentHasNoFaculty_ShouldThrowException() throws Exception {
        when(studentService.getFacultyByStudentId(1L))
                .thenThrow(new FacultyNotFoundException("Student has no faculty"));

        mockMvc.perform(get("/students/1/faculty"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Student has no faculty"));
    }
}