-- =============================================
-- CarsBase - Database Schema
-- Banco de dados relacional para gerenciamento
-- de carros, marcas e usuários
-- =============================================

-- Criar o banco de dados
CREATE DATABASE IF NOT EXISTS carsbase
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;

USE carsbase;

-- =============================================
-- TABELA: users (Sistema de Login)
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
    last_login TIMESTAMP NULL,

    INDEX idx_username (username),
    INDEX idx_email (email)
) ENGINE=InnoDB;

-- =============================================
-- TABELA: brands (Marcas de Carros)
-- =============================================
CREATE TABLE IF NOT EXISTS brands (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,
    country VARCHAR(50),
    founded_year INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    INDEX idx_name (name),
    INDEX idx_country (country)
) ENGINE=InnoDB;

-- =============================================
-- TABELA: cars (Carros)
-- =============================================
CREATE TABLE IF NOT EXISTS cars (
    id INT AUTO_INCREMENT PRIMARY KEY,
    model VARCHAR(100) NOT NULL,
    brand_id INT NOT NULL,
    year INT NOT NULL,
    color VARCHAR(30),
    price DECIMAL(12, 2) NOT NULL,
    license_plate VARCHAR(20) UNIQUE,
    mileage INT DEFAULT 0,
    fuel_type ENUM('Flex', 'Gasoline', 'Diesel', 'Hybrid', 'Electric') DEFAULT 'Flex',
    transmission ENUM('Manual', 'Automatic', 'CVT', 'Semi-Automatic') DEFAULT 'Manual',
    available BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    FOREIGN KEY (brand_id) REFERENCES brands(id) ON DELETE CASCADE ON UPDATE CASCADE,

    INDEX idx_model (model),
    INDEX idx_brand (brand_id),
    INDEX idx_year (year),
    INDEX idx_price (price),
    INDEX idx_available (available),
    INDEX idx_license_plate (license_plate)
) ENGINE=InnoDB;

-- =============================================
-- TABELA: user_sessions (Sessões de Login)
-- =============================================
CREATE TABLE IF NOT EXISTS user_sessions (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    session_token VARCHAR(255) NOT NULL UNIQUE,
    ip_address VARCHAR(45),
    user_agent VARCHAR(255),
    expires_at TIMESTAMP NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,

    INDEX idx_session_token (session_token),
    INDEX idx_user_id (user_id),
    INDEX idx_expires (expires_at)
) ENGINE=InnoDB;

-- =============================================
-- TABELA: login_attempts (Tentativas de Login)
-- =============================================
CREATE TABLE IF NOT EXISTS login_attempts (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL,
    ip_address VARCHAR(45),
    success BOOLEAN DEFAULT FALSE,
    attempted_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    INDEX idx_username (username),
    INDEX idx_ip (ip_address),
    INDEX idx_attempted (attempted_at)
) ENGINE=InnoDB;

-- =============================================
-- INSERIR DADOS INICIAIS
-- =============================================

-- Inserir usuário admin padrão (senha: admin123)
-- Nota: Em produção, use hash bcrypt ou similar
INSERT INTO users (username, password, email, full_name, role) VALUES
('admin', SHA2('admin123', 256), 'admin@carsbase.com', 'Administrador', 'ADMIN'),
('manager', SHA2('manager123', 256), 'manager@carsbase.com', 'Gerente', 'MANAGER'),
('user', SHA2('user123', 256), 'user@carsbase.com', 'Usuário Padrão', 'USER');

-- =============================================
-- INSERIR MARCAS (brands.yml)
-- =============================================
INSERT INTO brands (id, name, country, founded_year) VALUES
-- Marcas Japonesas
(1, 'Toyota', 'Japan', 1937),
(2, 'Honda', 'Japan', 1948),
(3, 'Nissan', 'Japan', 1933),
-- Marcas Alemãs
(4, 'Volkswagen', 'Germany', 1937),
(5, 'BMW', 'Germany', 1916),
(6, 'Mercedes-Benz', 'Germany', 1926),
(7, 'Audi', 'Germany', 1909),
-- Marcas Americanas
(8, 'Ford', 'USA', 1903),
(9, 'Chevrolet', 'USA', 1911),
(10, 'Jeep', 'USA', 1941),
-- Marcas Coreanas
(11, 'Hyundai', 'South Korea', 1967),
(12, 'Kia', 'South Korea', 1944),
-- Marcas Italianas
(13, 'Fiat', 'Italy', 1899);

-- =============================================
-- INSERIR CARROS (cars.yml)
-- =============================================
INSERT INTO cars (id, model, brand_id, year, color, price, license_plate, mileage, fuel_type, transmission, available) VALUES
-- Toyota (brand_id: 1)
(1, 'Corolla', 1, 2024, 'Silver', 125000.00, 'ABC-1234', 0, 'Flex', 'Automatic', TRUE),
(2, 'Hilux', 1, 2024, 'White', 285000.00, 'DEF-5678', 15000, 'Diesel', 'Automatic', TRUE),
(3, 'Camry', 1, 2023, 'Black', 220000.00, 'GHI-9012', 8500, 'Hybrid', 'Automatic', FALSE),
(4, 'RAV4', 1, 2024, 'Red', 280000.00, 'JKL-3456', 0, 'Hybrid', 'Automatic', TRUE),

-- Honda (brand_id: 2)
(5, 'Civic', 2, 2024, 'Gray', 155000.00, 'MNO-7890', 5000, 'Flex', 'CVT', TRUE),
(6, 'HR-V', 2, 2024, 'Blue', 165000.00, 'PQR-1234', 0, 'Flex', 'CVT', TRUE),
(7, 'Accord', 2, 2023, 'White', 280000.00, 'STU-5678', 12000, 'Hybrid', 'Automatic', FALSE),
(8, 'City', 2, 2024, 'Silver', 115000.00, 'VWX-9012', 3000, 'Flex', 'CVT', TRUE),

-- Nissan (brand_id: 3)
(9, 'Sentra', 3, 2024, 'Black', 135000.00, 'YZA-3456', 0, 'Flex', 'CVT', TRUE),
(10, 'Kicks', 3, 2024, 'Orange', 125000.00, 'BCD-7890', 8000, 'Flex', 'CVT', TRUE),
(11, 'Frontier', 3, 2023, 'Gray', 250000.00, 'EFG-1234', 25000, 'Diesel', 'Automatic', TRUE),

-- Volkswagen (brand_id: 4)
(12, 'Golf', 4, 2024, 'White', 175000.00, 'HIJ-5678', 0, 'Gasoline', 'Automatic', TRUE),
(13, 'Jetta', 4, 2024, 'Black', 185000.00, 'KLM-9012', 6000, 'Gasoline', 'Automatic', TRUE),
(14, 'Tiguan', 4, 2023, 'Gray', 220000.00, 'NOP-3456', 18000, 'Gasoline', 'Automatic', FALSE),
(15, 'T-Cross', 4, 2024, 'Red', 145000.00, 'QRS-7890', 0, 'Flex', 'Automatic', TRUE),

-- BMW (brand_id: 5)
(16, '320i', 5, 2024, 'Black', 320000.00, 'TUV-1234', 0, 'Gasoline', 'Automatic', TRUE),
(17, 'X1', 5, 2024, 'White', 350000.00, 'WXY-5678', 5000, 'Gasoline', 'Automatic', TRUE),
(18, 'X3', 5, 2023, 'Blue', 420000.00, 'ZAB-9012', 15000, 'Diesel', 'Automatic', FALSE),
(19, '530e', 5, 2024, 'Silver', 550000.00, 'CDE-3456', 0, 'Hybrid', 'Automatic', TRUE),

-- Mercedes-Benz (brand_id: 6)
(20, 'C200', 6, 2024, 'Black', 380000.00, 'FGH-7890', 0, 'Gasoline', 'Automatic', TRUE),
(21, 'GLA 200', 6, 2024, 'White', 320000.00, 'IJK-1234', 8000, 'Gasoline', 'Automatic', TRUE),
(22, 'E300', 6, 2023, 'Silver', 520000.00, 'LMN-5678', 12000, 'Hybrid', 'Automatic', FALSE),

-- Audi (brand_id: 7)
(23, 'A3', 7, 2024, 'Gray', 280000.00, 'OPQ-9012', 0, 'Gasoline', 'Automatic', TRUE),
(24, 'A4', 7, 2024, 'Black', 350000.00, 'RST-3456', 5000, 'Gasoline', 'Automatic', TRUE),
(25, 'Q3', 7, 2023, 'White', 320000.00, 'UVW-7890', 20000, 'Gasoline', 'Automatic', TRUE),
(26, 'Q5', 7, 2024, 'Blue', 450000.00, 'XYZ-1234', 0, 'Hybrid', 'Automatic', TRUE),

-- Ford (brand_id: 8)
(27, 'Mustang', 8, 2024, 'Red', 450000.00, 'ABC-4567', 0, 'Gasoline', 'Manual', TRUE),
(28, 'Ranger', 8, 2024, 'Black', 280000.00, 'DEF-8901', 10000, 'Diesel', 'Automatic', TRUE),
(29, 'Bronco Sport', 8, 2024, 'Green', 250000.00, 'GHI-2345', 0, 'Gasoline', 'Automatic', TRUE),

-- Chevrolet (brand_id: 9)
(30, 'Onix', 9, 2024, 'White', 95000.00, 'JKL-6789', 0, 'Flex', 'Automatic', TRUE),
(31, 'Tracker', 9, 2024, 'Silver', 145000.00, 'MNO-0123', 5000, 'Flex', 'Automatic', TRUE),
(32, 'S10', 9, 2023, 'Gray', 250000.00, 'PQR-4567', 30000, 'Diesel', 'Automatic', FALSE),
(33, 'Camaro', 9, 2024, 'Yellow', 450000.00, 'STU-8901', 0, 'Gasoline', 'Automatic', TRUE),

-- Jeep (brand_id: 10)
(34, 'Renegade', 10, 2024, 'Orange', 145000.00, 'VWX-2345', 0, 'Flex', 'Automatic', TRUE),
(35, 'Compass', 10, 2024, 'Black', 195000.00, 'YZA-6789', 8000, 'Diesel', 'Automatic', TRUE),
(36, 'Commander', 10, 2024, 'White', 280000.00, 'BCD-0123', 0, 'Diesel', 'Automatic', TRUE),
(37, 'Wrangler', 10, 2023, 'Green', 380000.00, 'EFG-4567', 15000, 'Gasoline', 'Automatic', FALSE),

-- Hyundai (brand_id: 11)
(38, 'HB20', 11, 2024, 'Red', 95000.00, 'HIJ-8901', 0, 'Flex', 'Automatic', TRUE),
(39, 'Creta', 11, 2024, 'White', 145000.00, 'KLM-2345', 3000, 'Flex', 'Automatic', TRUE),
(40, 'Tucson', 11, 2024, 'Gray', 220000.00, 'NOP-6789', 0, 'Hybrid', 'Automatic', TRUE),
(41, 'Santa Fe', 11, 2023, 'Black', 320000.00, 'QRS-0123', 18000, 'Diesel', 'Automatic', TRUE),

-- Kia (brand_id: 12)
(42, 'Sportage', 12, 2024, 'Blue', 195000.00, 'TUV-4567', 0, 'Gasoline', 'Automatic', TRUE),
(43, 'Sorento', 12, 2024, 'Silver', 320000.00, 'WXY-8901', 5000, 'Diesel', 'Automatic', TRUE),
(44, 'Cerato', 12, 2024, 'White', 145000.00, 'ZAB-2345', 0, 'Flex', 'Automatic', TRUE),
(45, 'Carnival', 12, 2023, 'Black', 380000.00, 'CDE-6789', 12000, 'Diesel', 'Automatic', FALSE),

-- Fiat (brand_id: 13)
(46, 'Argo', 13, 2024, 'Red', 85000.00, 'FGH-0123', 0, 'Flex', 'Manual', TRUE),
(47, 'Pulse', 13, 2024, 'White', 115000.00, 'IJK-4567', 5000, 'Flex', 'Automatic', TRUE),
(48, 'Fastback', 13, 2024, 'Gray', 145000.00, 'LMN-8901', 0, 'Flex', 'Automatic', TRUE),
(49, 'Toro', 13, 2024, 'Black', 175000.00, 'OPQ-2345', 8000, 'Diesel', 'Automatic', TRUE),
(50, 'Strada', 13, 2024, 'Silver', 115000.00, 'RST-6789', 0, 'Flex', 'Manual', TRUE);

-- =============================================
-- VIEWS ÚTEIS
-- =============================================

-- View: Carros com informações da marca
CREATE OR REPLACE VIEW vw_cars_with_brand AS
SELECT
    c.id,
    c.model,
    b.name AS brand_name,
    b.country AS brand_country,
    c.year,
    c.color,
    c.price,
    c.license_plate,
    c.mileage,
    c.fuel_type,
    c.transmission,
    c.available,
    CONCAT(b.name, ' ', c.model) AS full_name
FROM cars c
INNER JOIN brands b ON c.brand_id = b.id;

-- View: Estatísticas por marca
CREATE OR REPLACE VIEW vw_brand_statistics AS
SELECT
    b.id,
    b.name,
    b.country,
    COUNT(c.id) AS total_cars,
    SUM(CASE WHEN c.available = TRUE THEN 1 ELSE 0 END) AS available_cars,
    AVG(c.price) AS avg_price,
    MIN(c.price) AS min_price,
    MAX(c.price) AS max_price
FROM brands b
LEFT JOIN cars c ON b.id = c.brand_id
GROUP BY b.id, b.name, b.country;

-- View: Carros disponíveis
CREATE OR REPLACE VIEW vw_available_cars AS
SELECT * FROM vw_cars_with_brand WHERE available = TRUE;

-- =============================================
-- STORED PROCEDURES
-- =============================================

-- Procedure: Autenticar usuário
DELIMITER //
CREATE PROCEDURE sp_authenticate_user(
    IN p_username VARCHAR(50),
    IN p_password VARCHAR(255),
    OUT p_user_id INT,
    OUT p_result VARCHAR(50)
)
BEGIN
    DECLARE v_user_id INT;
    DECLARE v_active BOOLEAN;

    -- Registrar tentativa de login
    INSERT INTO login_attempts (username, success) VALUES (p_username, FALSE);

    -- Buscar usuário
    SELECT id, active INTO v_user_id, v_active
    FROM users
    WHERE username = p_username AND password = SHA2(p_password, 256);

    IF v_user_id IS NOT NULL THEN
        IF v_active = TRUE THEN
            SET p_user_id = v_user_id;
            SET p_result = 'SUCCESS';

            -- Atualizar último login
            UPDATE users SET last_login = CURRENT_TIMESTAMP WHERE id = v_user_id;

            -- Atualizar tentativa de login como sucesso
            UPDATE login_attempts
            SET success = TRUE
            WHERE username = p_username
            ORDER BY attempted_at DESC
            LIMIT 1;
        ELSE
            SET p_user_id = NULL;
            SET p_result = 'USER_INACTIVE';
        END IF;
    ELSE
        SET p_user_id = NULL;
        SET p_result = 'INVALID_CREDENTIALS';
    END IF;
END //
DELIMITER ;

-- Procedure: Registrar novo usuário
DELIMITER //
CREATE PROCEDURE sp_register_user(
    IN p_username VARCHAR(50),
    IN p_password VARCHAR(255),
    IN p_email VARCHAR(100),
    IN p_full_name VARCHAR(100),
    OUT p_user_id INT,
    OUT p_result VARCHAR(50)
)
BEGIN
    DECLARE v_exists INT;

    -- Verificar se username já existe
    SELECT COUNT(*) INTO v_exists FROM users WHERE username = p_username;
    IF v_exists > 0 THEN
        SET p_user_id = NULL;
        SET p_result = 'USERNAME_EXISTS';
    ELSE
        -- Verificar se email já existe
        SELECT COUNT(*) INTO v_exists FROM users WHERE email = p_email;
        IF v_exists > 0 THEN
            SET p_user_id = NULL;
            SET p_result = 'EMAIL_EXISTS';
        ELSE
            -- Inserir novo usuário
            INSERT INTO users (username, password, email, full_name)
            VALUES (p_username, SHA2(p_password, 256), p_email, p_full_name);

            SET p_user_id = LAST_INSERT_ID();
            SET p_result = 'SUCCESS';
        END IF;
    END IF;
END //
DELIMITER ;

-- =============================================
-- ÍNDICES ADICIONAIS PARA PERFORMANCE
-- =============================================
CREATE INDEX idx_cars_brand_available ON cars(brand_id, available);
CREATE INDEX idx_cars_price_available ON cars(price, available);
CREATE INDEX idx_cars_year_brand ON cars(year, brand_id);

