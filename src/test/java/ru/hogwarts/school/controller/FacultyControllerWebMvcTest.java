package ru.hogwarts.school.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.hogwarts.school.dto.FacultyRequestDto;
import ru.hogwarts.school.dto.FacultyResponseDto;
import ru.hogwarts.school.dto.StudentResponseDto;
import ru.hogwarts.school.exceptions.FacultyNotFoundException;
import ru.hogwarts.school.service.FacultyService;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class FacultyControllerWebMvcTest {

    private MockMvc mockMvc;

    @Mock
    private FacultyService facultyService;

    @InjectMocks
    private FacultyController facultyController;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final FacultyResponseDto facultyResponse = new FacultyResponseDto(1L, "Гриффиндор", "красный", List.of());
    private final StudentResponseDto studentResponse = new StudentResponseDto(1L, "Гарри Поттер", 17, 1L);

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(facultyController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void create_ShouldReturnCreatedFaculty() throws Exception {
        FacultyRequestDto requestDto = new FacultyRequestDto();
        requestDto.setName("Гриффиндор");
        requestDto.setColor("красный");

        when(facultyService.create(any(FacultyRequestDto.class))).thenReturn(facultyResponse);

        mockMvc.perform(post("/faculties")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Гриффиндор"))
                .andExpect(jsonPath("$.color").value("красный"));
    }

    @Test
    void read_ShouldReturnFaculty() throws Exception {
        when(facultyService.read(1L)).thenReturn(facultyResponse);

        mockMvc.perform(get("/faculties/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Гриффиндор"));
    }

    @Test
    void read_WhenFacultyNotFound_ShouldThrowException() throws Exception {
        when(facultyService.read(999L))
                .thenThrow(new FacultyNotFoundException("Факультет с ID:999 не найден."));

        mockMvc.perform(get("/faculties/999"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.TEXT_PLAIN_VALUE + ";charset=UTF-8"))
                .andExpect(content().string("Факультет с ID:999 не найден."));
    }

    @Test
    void update_ShouldReturnUpdatedFaculty() throws Exception {
        FacultyRequestDto requestDto = new FacultyRequestDto();
        requestDto.setName("Гриффиндор");
        requestDto.setColor("алый");

        FacultyResponseDto updatedResponse = new FacultyResponseDto(1L, "Гриффиндор", "алый", List.of());
        when(facultyService.update(eq(1L), any(FacultyRequestDto.class))).thenReturn(updatedResponse);

        mockMvc.perform(put("/faculties/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.color").value("алый"));
    }

    @Test
    void delete_ShouldReturnNoContent() throws Exception {
        doNothing().when(facultyService).delete(1L);

        mockMvc.perform(delete("/faculties/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void getAll_ShouldReturnAllFaculties() throws Exception {
        when(facultyService.getAll()).thenReturn(List.of(facultyResponse));

        mockMvc.perform(get("/faculties"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Гриффиндор"));
    }

    @Test
    void filter_ShouldReturnFilteredFaculties() throws Exception {
        when(facultyService.findByNameOrColor("красный")).thenReturn(List.of(facultyResponse));

        mockMvc.perform(get("/faculties/filter?query=красный"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Гриффиндор"));
    }

    @Test
    void getStudents_ShouldReturnFacultyStudents() throws Exception {
        when(facultyService.getStudentsByFacultyId(1L)).thenReturn(List.of(studentResponse));

        mockMvc.perform(get("/faculties/1/students"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Гарри Поттер"));
    }

    @Test
    void getStudents_WhenFacultyNotFound_ShouldThrowException() throws Exception {
        when(facultyService.getStudentsByFacultyId(999L))
                .thenThrow(new FacultyNotFoundException("Факультет с ID:999 не найден."));

        mockMvc.perform(get("/faculties/999/students"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.TEXT_PLAIN_VALUE + ";charset=UTF-8"))
                .andExpect(content().string("Факультет с ID:999 не найден."));
    }
}