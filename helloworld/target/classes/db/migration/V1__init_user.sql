-- src/main/resources/db/migration/V1__init_user.sql

CREATE TABLE IF NOT EXISTS users (
    id SERIAL PRIMARY KEY,
    npk VARCHAR(50) NOT NULL UNIQUE,
    namaKaryawan VARCHAR(100) NOT NULL,
    posisi VARCHAR(100) NOT NULL,
    status VARCHAR(50) NOT NULL,
    image_data BYTEA
);

-- Insert sample users (image_data is set to NULL for now)
INSERT INTO users (npk, namaKaryawan, posisi, status, image_data) VALUES
('1001', 'Alice Johnson', 'Manager', 'Active', NULL),
('1002', 'Bob Smith', 'Developer', 'Active', NULL),
('1003', 'Charlie Brown', 'Designer', 'Inactive', NULL);