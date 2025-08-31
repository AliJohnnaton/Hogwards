package ru.hogwarts.school.controller;

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
import ru.hogwarts.school.model.Avatar;
import ru.hogwarts.school.service.AvatarService;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class AvatarControllerWebMvcTest {

    private MockMvc mockMvc;

    @Mock
    private AvatarService avatarService;

    @InjectMocks
    private AvatarController avatarController;

    private final Avatar avatar1 = new Avatar("path1.png", 1024, "image/png", new byte[]{1, 2, 3}, null);
    private final Avatar avatar2 = new Avatar("path2.png", 2048, "image/png", new byte[]{4, 5, 6}, null);

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(avatarController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void getAll_ShouldReturnPageOfAvatars() throws Exception {
        Page<Avatar> page = new PageImpl<>(List.of(avatar1, avatar2), PageRequest.of(0, 10), 2);
        when(avatarService.getAllAvatars(PageRequest.of(0, 10))).thenReturn(page);

        mockMvc.perform(get("/avatars?page=0&size=10").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].filePath").value("path1.png"))
                .andExpect(jsonPath("$.content[1].filePath").value("path2.png"));
    }
}
