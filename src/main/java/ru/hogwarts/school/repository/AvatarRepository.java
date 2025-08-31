package ru.hogwarts.school.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import ru.hogwarts.school.model.Avatar;

import java.util.Optional;

public interface AvatarRepository extends JpaRepository<Avatar, Long> {

    Optional<Avatar> findByStudentId(Long studentId);

    boolean existsByStudentId(Long studentId);

    @Transactional
    @Modifying
    @Query("DELETE FROM Avatar a WHERE a.student.id = :studentId")
    void deleteByStudentId(@Param("studentId") Long studentId);

    @Query("SELECT COUNT(a) FROM Avatar a WHERE a.student.id = :studentId")
    long countByStudentId(@Param("studentId") Long studentId);

    @Query("SELECT a FROM Avatar a")
    Page<Avatar> findAll(Pageable pageable);

    @Query("SELECT a FROM Avatar a WHERE a.fileSize > :minSize")
    Page<Avatar> findByFileSizeGreaterThan(@Param("minSize") long minSize, Pageable pageable);
}
