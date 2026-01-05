package me.pieralini.com.services;

import me.pieralini.com.utils.Database;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataLoaderService {

    private static DataLoaderService instance;
    private Map<String, Integer> colorMap = new HashMap<>();
    private Map<String, Integer> fuelTypeMap = new HashMap<>();
    private Map<String, Integer> transmissionMap = new HashMap<>();
    private Map<String, Integer> carModelMap = new HashMap<>();

    private DataLoaderService() {
    }

    public static DataLoaderService getInstance() {
        if (instance == null) {
            instance = new DataLoaderService();
        }
        return instance;
    }

    public void initializeDatabase() {
        createTables();
        loadReferenceData();
        loadBrandsFromYml();
        loadCarsFromYml();
    }

    private void createTables() {
        String[] createTables = {
            "CREATE TABLE IF NOT EXISTS colors (id INT AUTO_INCREMENT PRIMARY KEY, name VARCHAR(30) NOT NULL UNIQUE)",
            "CREATE TABLE IF NOT EXISTS fuel_types (id INT AUTO_INCREMENT PRIMARY KEY, name VARCHAR(30) NOT NULL UNIQUE)",
            "CREATE TABLE IF NOT EXISTS transmissions (id INT AUTO_INCREMENT PRIMARY KEY, name VARCHAR(30) NOT NULL UNIQUE)",
            "CREATE TABLE IF NOT EXISTS users (id INT AUTO_INCREMENT PRIMARY KEY, username VARCHAR(50) NOT NULL UNIQUE, password VARCHAR(255) NOT NULL, email VARCHAR(100) NOT NULL UNIQUE, full_name VARCHAR(100), role VARCHAR(20) DEFAULT 'USER', active BOOLEAN DEFAULT TRUE, created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, last_login TIMESTAMP NULL)",
            "CREATE TABLE IF NOT EXISTS brands (id INT AUTO_INCREMENT PRIMARY KEY, name VARCHAR(50) NOT NULL UNIQUE, country VARCHAR(50), founded_year INT)",
            "CREATE TABLE IF NOT EXISTS car_models (id INT AUTO_INCREMENT PRIMARY KEY, name VARCHAR(100) NOT NULL, brand_id INT NOT NULL, UNIQUE KEY uk_model_brand (name, brand_id))",
            "CREATE TABLE IF NOT EXISTS cars (id INT AUTO_INCREMENT PRIMARY KEY, car_model_id INT NOT NULL, year INT NOT NULL, color_id INT, fuel_type_id INT, transmission_id INT, price DECIMAL(12,2) NOT NULL, license_plate VARCHAR(20) UNIQUE, mileage INT DEFAULT 0, available BOOLEAN DEFAULT TRUE)",
            "CREATE TABLE IF NOT EXISTS login_attempts (id INT AUTO_INCREMENT PRIMARY KEY, username VARCHAR(50) NOT NULL, ip_address VARCHAR(45), success BOOLEAN DEFAULT FALSE, attempted_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP)"
        };

        try (Connection conn = Database.getInstance().getConnection()) {
            for (String sql : createTables) {
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.executeUpdate();
                }
            }
            System.out.println("[DataLoader] Tables created successfully!");
            insertDefaultUsers(conn);
        } catch (SQLException e) {
            System.err.println("[DataLoader] Error creating tables: " + e.getMessage());
        }
    }

    private void loadReferenceData() {
        String[] colors = {"Black", "White", "Silver", "Gray", "Red", "Blue", "Green", "Yellow", "Orange", "Brown", "Purple", "Pink"};
        String[] fuelTypes = {"Flex", "Gasoline", "Diesel", "Hybrid", "Electric"};
        String[] transmissions = {"Manual", "Automatic", "CVT", "Semi-Automatic"};

        try (Connection conn = Database.getInstance().getConnection()) {
            for (String color : colors) {
                insertIfNotExists(conn, "colors", color);
            }
            for (String fuel : fuelTypes) {
                insertIfNotExists(conn, "fuel_types", fuel);
            }
            for (String trans : transmissions) {
                insertIfNotExists(conn, "transmissions", trans);
            }

            loadMaps(conn);
            System.out.println("[DataLoader] Reference data loaded!");
        } catch (SQLException e) {
            System.err.println("[DataLoader] Error loading reference data: " + e.getMessage());
        }
    }

    private void insertIfNotExists(Connection conn, String table, String name) throws SQLException {
        String checkSql = "SELECT COUNT(*) FROM " + table + " WHERE name = ?";
        String insertSql = "INSERT INTO " + table + " (name) VALUES (?)";

        try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
            checkStmt.setString(1, name);
            ResultSet rs = checkStmt.executeQuery();
            if (rs.next() && rs.getInt(1) == 0) {
                try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                    insertStmt.setString(1, name);
                    insertStmt.executeUpdate();
                }
            }
        }
    }

    private void loadMaps(Connection conn) throws SQLException {
        loadMap(conn, "colors", colorMap);
        loadMap(conn, "fuel_types", fuelTypeMap);
        loadMap(conn, "transmissions", transmissionMap);
    }

    private void loadMap(Connection conn, String table, Map<String, Integer> map) throws SQLException {
        String sql = "SELECT id, name FROM " + table;
        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                map.put(rs.getString("name"), rs.getInt("id"));
            }
        }
    }

    private void insertDefaultUsers(Connection conn) {
        String checkSql = "SELECT COUNT(*) FROM users";
        String insertSql = "INSERT INTO users (username, password, email, full_name, role) VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement checkStmt = conn.prepareStatement(checkSql);
             ResultSet rs = checkStmt.executeQuery()) {

            if (rs.next() && rs.getInt(1) == 0) {
                try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                    insertStmt.setString(1, "admin");
                    insertStmt.setString(2, "240be518fabd2724ddb6f04eeb9d5b07d35b796efa6e28b7535c5e29c0e0f5e7d");
                    insertStmt.setString(3, "admin@carsbase.com");
                    insertStmt.setString(4, "Administrador");
                    insertStmt.setString(5, "ADMIN");
                    insertStmt.executeUpdate();
                }
                System.out.println("[DataLoader] Default admin user created!");
            }
        } catch (SQLException e) {
            System.err.println("[DataLoader] Error inserting default users: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    private void loadBrandsFromYml() {
        try {
            Yaml yaml = new Yaml();
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream("brands.yml");

            if (inputStream == null) {
                System.err.println("[DataLoader] brands.yml not found!");
                return;
            }

            Map<String, Object> data = yaml.load(inputStream);
            List<Map<String, Object>> brands = (List<Map<String, Object>>) data.get("brands");

            if (brands == null || brands.isEmpty()) {
                System.out.println("[DataLoader] No brands found in brands.yml");
                return;
            }

            String checkSql = "SELECT COUNT(*) FROM brands WHERE id = ? OR name = ?";
            String insertSql = "INSERT INTO brands (id, name, country, founded_year) VALUES (?, ?, ?, ?)";

            try (Connection conn = Database.getInstance().getConnection()) {
                int inserted = 0;

                for (Map<String, Object> brand : brands) {
                    int id = (int) brand.get("id");
                    String name = (String) brand.get("name");

                    try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
                        checkStmt.setInt(1, id);
                        checkStmt.setString(2, name);
                        ResultSet rs = checkStmt.executeQuery();

                        if (rs.next() && rs.getInt(1) == 0) {
                            try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                                insertStmt.setInt(1, id);
                                insertStmt.setString(2, (String) brand.get("name"));
                                insertStmt.setString(3, (String) brand.get("country"));
                                insertStmt.setInt(4, (int) brand.get("foundedYear"));
                                insertStmt.executeUpdate();
                                inserted++;
                            }
                        }
                    }
                }

                System.out.println("[DataLoader] Loaded " + inserted + " brands from brands.yml");
            }

            inputStream.close();

        } catch (Exception e) {
            System.err.println("[DataLoader] Error loading brands: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    private void loadCarsFromYml() {
        try {
            Yaml yaml = new Yaml();
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream("cars.yml");

            if (inputStream == null) {
                System.err.println("[DataLoader] cars.yml not found!");
                return;
            }

            Map<String, Object> data = yaml.load(inputStream);
            List<Map<String, Object>> cars = (List<Map<String, Object>>) data.get("cars");

            if (cars == null || cars.isEmpty()) {
                System.out.println("[DataLoader] No cars found in cars.yml");
                return;
            }

            try (Connection conn = Database.getInstance().getConnection()) {
                int modelsInserted = 0;
                int carsInserted = 0;

                for (Map<String, Object> car : cars) {
                    String model = (String) car.get("model");
                    int brandId = (int) car.get("brandId");

                    int carModelId = getOrCreateCarModel(conn, model, brandId);
                    if (carModelId == -1) continue;

                    String licensePlate = (String) car.get("licensePlate");
                    if (!carExists(conn, licensePlate)) {
                        insertCar(conn, car, carModelId);
                        carsInserted++;
                    }
                }

                System.out.println("[DataLoader] Loaded " + carsInserted + " cars from cars.yml");
            }

            inputStream.close();

        } catch (Exception e) {
            System.err.println("[DataLoader] Error loading cars: " + e.getMessage());
        }
    }

    private int getOrCreateCarModel(Connection conn, String modelName, int brandId) throws SQLException {
        String key = modelName + "_" + brandId;

        if (carModelMap.containsKey(key)) {
            return carModelMap.get(key);
        }

        String checkSql = "SELECT id FROM car_models WHERE name = ? AND brand_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(checkSql)) {
            stmt.setString(1, modelName);
            stmt.setInt(2, brandId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                int id = rs.getInt("id");
                carModelMap.put(key, id);
                return id;
            }
        }

        String insertSql = "INSERT INTO car_models (name, brand_id) VALUES (?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(insertSql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, modelName);
            stmt.setInt(2, brandId);
            stmt.executeUpdate();
            ResultSet keys = stmt.getGeneratedKeys();
            if (keys.next()) {
                int id = keys.getInt(1);
                carModelMap.put(key, id);
                return id;
            }
        }

        return -1;
    }

    private boolean carExists(Connection conn, String licensePlate) throws SQLException {
        String sql = "SELECT COUNT(*) FROM cars WHERE license_plate = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, licensePlate);
            ResultSet rs = stmt.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        }
    }

    private void insertCar(Connection conn, Map<String, Object> car, int carModelId) throws SQLException {
        String sql = "INSERT INTO cars (car_model_id, year, color_id, fuel_type_id, transmission_id, price, license_plate, mileage, available) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, carModelId);
            stmt.setInt(2, ((Number) car.get("year")).intValue());

            String colorName = String.valueOf(car.get("color"));
            String fuelTypeName = String.valueOf(car.get("fuelType"));
            String transmissionName = String.valueOf(car.get("transmission"));

            stmt.setObject(3, colorMap.get(colorName));
            stmt.setObject(4, fuelTypeMap.get(fuelTypeName));
            stmt.setObject(5, transmissionMap.get(transmissionName));
            stmt.setDouble(6, ((Number) car.get("price")).doubleValue());
            stmt.setString(7, String.valueOf(car.get("licensePlate")));
            stmt.setInt(8, ((Number) car.get("mileage")).intValue());
            stmt.setBoolean(9, (boolean) car.get("available"));
            stmt.executeUpdate();
        }
    }
}
