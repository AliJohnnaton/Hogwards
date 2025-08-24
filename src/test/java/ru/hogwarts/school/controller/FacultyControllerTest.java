package ru.hogwarts.school.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import ru.hogwarts.school.model.Faculty;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class FacultyControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    private final String BASE_URL = "/faculties";

    @Test
    void testCreateFaculty() {
        Faculty faculty = new Faculty();
        faculty.setName("Гриффиндор");
        faculty.setColor("красный");

        ResponseEntity<Faculty> response = restTemplate.postForEntity(
                BASE_URL, faculty, Faculty.class);

        assertEquals(HttpStatus.CREATED, response.getStatusCode()); // ИЗМЕНИТЬ
        assertNotNull(response.getBody());
        assertEquals("Гриффиндор", response.getBody().getName());
        assertEquals("красный", response.getBody().getColor());
    }

    @Test
    void testGetFacultyById() {
        // Сначала создаем факультет
        Faculty faculty = new Faculty();
        faculty.setName("Слизерин");
        faculty.setColor("зеленый");
        Faculty createdFaculty = restTemplate.postForObject(BASE_URL, faculty, Faculty.class);

        // Затем получаем его по ID
        ResponseEntity<Faculty> response = restTemplate.getForEntity(
                BASE_URL + "/" + createdFaculty.getId(), Faculty.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Слизерин", response.getBody().getName());
        assertEquals("зеленый", response.getBody().getColor());
    }

    @Test
    void testUpdateFaculty() {
        // Создаем факультет
        Faculty faculty = new Faculty();
        faculty.setName("Когтевран");
        faculty.setColor("синий");
        Faculty createdFaculty = restTemplate.postForObject(BASE_URL, faculty, Faculty.class);

        // Обновляем данные
        Faculty updatedFaculty = new Faculty();
        updatedFaculty.setName("Когтевран");
        updatedFaculty.setColor("голубой");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Faculty> request = new HttpEntity<>(updatedFaculty, headers);

        ResponseEntity<Faculty> response = restTemplate.exchange(
                BASE_URL + "/" + createdFaculty.getId(),
                HttpMethod.PUT, request, Faculty.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Когтевран", response.getBody().getName());
        assertEquals("голубой", response.getBody().getColor());
    }

    @Test
    void testDeleteFaculty() {
        Faculty faculty = new Faculty();
        faculty.setName("Пуффендуй");
        faculty.setColor("желтый");
        Faculty createdFaculty = restTemplate.postForObject(BASE_URL, faculty, Faculty.class);

        restTemplate.delete(BASE_URL + "/" + createdFaculty.getId());

        // Ожидаем 500, так как исключение FacultyNotFoundException
        // выбрасывается и возвращается как 500
        ResponseEntity<Faculty> response = restTemplate.getForEntity(
                BASE_URL + "/" + createdFaculty.getId(), Faculty.class);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode()); // ИЗМЕНИТЬ
    }

    @Test
    void testGetAllFaculties() {
        ResponseEntity<String> response = restTemplate.getForEntity(BASE_URL, String.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void testGetFacultiesByColor() {
        Faculty faculty = new Faculty();
        faculty.setName("Гриффиндор");
        faculty.setColor("красный");
        restTemplate.postForObject(BASE_URL, faculty, Faculty.class);

        // Используем параметр query вместо color
        ResponseEntity<String> response = restTemplate.getForEntity(
                BASE_URL + "/filter?query=красный", String.class); // ИЗМЕНИТЬ

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().contains("Гриффиндор"));
    }

    @Test
    void testGetFacultiesByNameOrColor() {
        Faculty faculty = new Faculty();
        faculty.setName("Гриффиндор");
        faculty.setColor("красный");
        restTemplate.postForObject(BASE_URL, faculty, Faculty.class);

        // Используем параметр query вместо name
        ResponseEntity<String> response = restTemplate.getForEntity(
                BASE_URL + "/filter?query=Гриффиндор", String.class); // ИЗМЕНИТЬ

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().contains("Гриффиндор"));
    }

    @Test
    void testGetStudentsByFacultyId() {
        // Создаем факультет
        Faculty faculty = new Faculty();
        faculty.setName("Гриффиндор");
        faculty.setColor("красный");
        Faculty createdFaculty = restTemplate.postForObject(BASE_URL, faculty, Faculty.class);

        // Получаем студентов факультета (пока пустой список)
        ResponseEntity<String> response = restTemplate.getForEntity(
                BASE_URL + "/" + createdFaculty.getId() + "/students", String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }
}