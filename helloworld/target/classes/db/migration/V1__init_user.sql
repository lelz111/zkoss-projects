-- src/main/resources/db/migration/V1__init_user.sql

--CREATE TABLE IF NOT EXISTS users (
--    id SERIAL PRIMARY KEY,
--    npk VARCHAR(50) NOT NULL UNIQUE,
--    namaKaryawan VARCHAR(100) NOT NULL,
--    posisi VARCHAR(100) NOT NULL,
--    status VARCHAR(50) NOT NULL,
--    image_data BYTEA
--);
--
---- Insert sample users (image_data is set to NULL for now)
--INSERT INTO users (npk, namaKaryawan, posisi, status, image_data) VALUES
--('1001', 'Alice Johnson', 'Manager', 'Active', NULL),
--('1002', 'Bob Smith', 'Developer', 'Active', NULL),
--('1003', 'Charlie Brown', 'Designer', 'Inactive', NULL);

-- Create users table
CREATE TABLE IF NOT EXISTS users (
    id SERIAL PRIMARY KEY,
    npk VARCHAR(50) NOT NULL UNIQUE,
    namaKaryawan VARCHAR(100) NOT NULL,
    posisi VARCHAR(100) NOT NULL,
    status VARCHAR(50) NOT NULL,
    image_data BYTEA,
    password VARCHAR(255) NOT NULL
);

-- Insert sample users with password (bcrypt of "password")
INSERT INTO users (npk, namaKaryawan, posisi, status, image_data, password) VALUES
('1001', 'Alice Johnson', 'Manager', 'Active', NULL, '$2a$12$M0Yj4bYiwSdnuQMjJTV.Su2F7Y2ybHJL6gxVvCInh/9y21Clpm5Mm'),
('1002', 'Bob Smith', 'Developer', 'Active', NULL, '$2a$10$g0rwrxHccjK/N4OkXos7B.H3P33RYRQxw8mSkNn7Z6FxjUN6HaYpi'),
('1003', 'Charlie Brown', 'Designer', 'Inactive', NULL, '$2a$10$g0rwrxHccjK/N4OkXos7B.H3P33RYRQxw8mSkNn7Z6FxjUN6HaYpi');

-- Create roles table
CREATE TABLE IF NOT EXISTS roles (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE
);

-- Insert default roles
INSERT INTO roles (name) VALUES
('ADMIN'),
('USER');

-- Create join table for users and roles
CREATE TABLE IF NOT EXISTS user_roles (
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE
);

-- Assign roles to users
-- Alice (npk 1001) = ADMIN
INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id FROM users u, roles r
WHERE u.npk = '1001' AND r.name = 'ADMIN';

-- Bob & Charlie = USER
INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id FROM users u, roles r
WHERE u.npk IN ('1002', '1003') AND r.name = 'USER';
