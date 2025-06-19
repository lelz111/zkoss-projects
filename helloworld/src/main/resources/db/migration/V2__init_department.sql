-- src/main/resources/db/migration/V2__init_department.sql

CREATE TABLE IF NOT EXISTS department (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE
);

INSERT INTO department (name) VALUES
('HR'),
('IT'),
('Finance');

