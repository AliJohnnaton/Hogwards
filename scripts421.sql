UPDATE student
SET age = 20
WHERE age IS NULL;

UPDATE student
SET age = 16
WHERE age < 16;

DELETE FROM student s
WHERE s.id NOT IN (
    SELECT MIN(s2.id)
    FROM student s2
    GROUP BY s2.name
);

ALTER TABLE student
ADD CONSTRAINT uq_student_name UNIQUE (name);

ALTER TABLE student
ADD CONSTRAINT chk_student_age CHECK (age >= 16);

DELETE FROM faculty f
WHERE f.id NOT IN (
    SELECT MIN(f2.id)
    FROM faculty f2
    GROUP BY f2.name, f2.color
);

ALTER TABLE faculty
ADD CONSTRAINT uq_faculty_name_color UNIQUE (name, color);
