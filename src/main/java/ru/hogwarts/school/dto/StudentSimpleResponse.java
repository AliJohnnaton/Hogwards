package ru.hogwarts.school.dto;

public class StudentSimpleResponse {
    private Long id;
    private String name;
    private int age;
    private Long facultyId;

    public StudentSimpleResponse(Long id, String name, int age, Long facultyId) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.facultyId = facultyId;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

    public Long getFacultyId() {
        return facultyId;
    }
}