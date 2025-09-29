ALTER TABLE student
    ADD CONSTRAINT chk_student_age CHECK (age >= 16),
    ADD CONSTRAINT uq_student_name UNIQUE (name),
    ALTER COLUMN name SET NOT NULL,
    ALTER COLUMN age SET DEFAULT 20;

ALTER TABLE faculty
    ADD CONSTRAINT uq_faculty_name_color UNIQUE (name, color);
