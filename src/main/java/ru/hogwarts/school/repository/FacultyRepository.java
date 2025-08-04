package ru.hogwarts.school.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.hogwarts.school.model.Faculty;

import java.util.List;

public interface FacultyRepository extends JpaRepository<Faculty, Long> {
    @Query("SELECT f FROM Faculty f WHERE LOWER(f.name) LIKE LOWER(concat('%', :query, '%')) OR LOWER(f.color) LIKE LOWER(concat('%', :query, '%'))")
    List<Faculty> findByNameOrColorIgnoreCase(@Param("query") String query);
}