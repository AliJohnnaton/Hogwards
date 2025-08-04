SELECT id, name, age, faculty_id FROM student WHERE age BETWEEN 10 AND 20 ORDER BY age;

SELECT name FROM student ORDER BY name;

SELECT id, name, age, faculty_id FROM student WHERE LOWER(name) LIKE '%о%' ORDER BY name;

SELECT id, name, age, faculty_id FROM student WHERE age < id ORDER BY id;

SELECT id, name, age, faculty_id FROM student ORDER BY age;