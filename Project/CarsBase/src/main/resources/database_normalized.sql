-- =============================================
-- CarsBase - Database Schema (Normalized)
-- =============================================

CREATE DATABASE IF NOT EXISTS carsbase
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;

USE carsbase;

-- =============================================
-- TABELAS DE REFERÊNCIA (LOOKUP TABLES)
-- =============================================

CREATE TABLE IF NOT EXISTS colors (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(30) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS fuel_types (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(30) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS transmissions (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(30) NOT NULL UNIQUE
);

-- =============================================
-- TABELA: users
-- =============================================
CREATE TABLE IF NOT EXISTS users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    full_name VARCHAR(100),
    role ENUM('ADMIN', 'USER', 'MANAGER') DEFAULT 'USER',
    active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    last_login TIMESTAMP NULL
);

-- =============================================
-- TABELA: brands (Marcas)
-- =============================================
CREATE TABLE IF NOT EXISTS brands (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,
    country VARCHAR(50),
    founded_year INT
);

-- =============================================
-- TABELA: car_models (Modelos de Carros)
-- Ex: Corolla, Civic, Golf (um modelo por marca)
-- =============================================
CREATE TABLE IF NOT EXISTS car_models (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    brand_id INT NOT NULL,
    UNIQUE KEY uk_model_brand (name, brand_id),
    FOREIGN KEY (brand_id) REFERENCES brands(id) ON DELETE CASCADE
);

-- =============================================
-- TABELA: cars (Unidades de Carros)
-- Cada carro físico é uma unidade de um modelo
-- =============================================
CREATE TABLE IF NOT EXISTS cars (
    id INT AUTO_INCREMENT PRIMARY KEY,
    car_model_id INT NOT NULL,
    year INT NOT NULL,
    color_id INT,
    fuel_type_id INT,
    transmission_id INT,
    price DECIMAL(12, 2) NOT NULL,
    license_plate VARCHAR(20) UNIQUE,
    mileage INT DEFAULT 0,
    available BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (car_model_id) REFERENCES car_models(id) ON DELETE CASCADE,
    FOREIGN KEY (color_id) REFERENCES colors(id),
    FOREIGN KEY (fuel_type_id) REFERENCES fuel_types(id),
    FOREIGN KEY (transmission_id) REFERENCES transmissions(id)
);

-- =============================================
-- TABELA: login_attempts
-- =============================================
CREATE TABLE IF NOT EXISTS login_attempts (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL,
    ip_address VARCHAR(45),
    success BOOLEAN DEFAULT FALSE,
    attempted_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- =============================================
-- INSERIR DADOS DE REFERÊNCIA
-- =============================================

INSERT INTO colors (name) VALUES
('Black'), ('White'), ('Silver'), ('Gray'), ('Red'),
('Blue'), ('Green'), ('Yellow'), ('Orange'), ('Brown');

INSERT INTO fuel_types (name) VALUES
('Flex'), ('Gasoline'), ('Diesel'), ('Hybrid'), ('Electric');

INSERT INTO transmissions (name) VALUES
('Manual'), ('Automatic'), ('CVT'), ('Semi-Automatic');

-- =============================================
-- INSERIR USUÁRIO ADMIN
-- =============================================
INSERT INTO users (username, password, email, full_name, role) VALUES
('admin', SHA2('admin123', 256), 'admin@carsbase.com', 'Administrador', 'ADMIN');

-- =============================================
-- VIEW: Carros com todas as informações
-- =============================================
CREATE OR REPLACE VIEW vw_cars_full AS
SELECT
    c.id,
    cm.name AS model,
    b.name AS brand,
    b.country AS brand_country,
    c.year,
    col.name AS color,
    ft.name AS fuel_type,
    tr.name AS transmission,
    c.price,
    c.license_plate,
    c.mileage,
    c.available,
    CONCAT(b.name, ' ', cm.name) AS full_name
FROM cars c
INNER JOIN car_models cm ON c.car_model_id = cm.id
INNER JOIN brands b ON cm.brand_id = b.id
LEFT JOIN colors col ON c.color_id = col.id
LEFT JOIN fuel_types ft ON c.fuel_type_id = ft.id
LEFT JOIN transmissions tr ON c.transmission_id = tr.id;

