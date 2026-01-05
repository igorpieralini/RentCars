# CarsBase 🚗

A car and automotive brand database management system.

## Description

CarsBase is a Java application that automatically creates and populates a MySQL database with car brands and models information. The system was designed to serve as a database for applications that need automotive data.

## Technologies Used

- **Java 25** - Programming language
- **Maven** - Dependency management and build
- **MySQL** - Relational database
- **HikariCP** - High-performance connection pool
- **SnakeYAML** - YAML parser for configurations
- **H2 Database** - In-memory database (for testing)

## Project Structure

```
CarsBase/
├── src/
│   ├── main/
│   │   ├── java/me/pieralini/com/
│   │   │   ├── Main.java                    # Application entry point
│   │   │   ├── objects/                     # System entities
│   │   │   │   ├── Brand.java               # Vehicle brand
│   │   │   │   ├── Car.java                 # Car
│   │   │   │   ├── CarModel.java            # Car model
│   │   │   │   ├── Color.java               # Color
│   │   │   │   ├── FuelType.java            # Fuel type
│   │   │   │   ├── Transmission.java        # Transmission type
│   │   │   │   └── User.java                # System user
│   │   │   ├── services/
│   │   │   │   ├── AuthService.java         # Authentication service
│   │   │   │   └── DataLoaderService.java   # Data loading service
│   │   │   └── utils/
│   │   │       ├── Database.java            # Database connection management
│   │   │       └── LoadConfig.java          # Configuration loading
│   │   └── resources/
│   │       ├── config.yml                   # Database settings
│   │       ├── brands.yml                   # Brand data
│   │       ├── cars.yml                     # Car data
│   │       ├── database.sql                 # Database SQL script
│   │       └── database_normalized.sql      # Normalized SQL script
│   └── test/java/                           # Unit tests
└── pom.xml                                  # Maven configuration
```

## Prerequisites

- Java 25 or higher
- Maven 3.6+
- MySQL 8.0+ (or H2 for testing)

## Configuration

### 1. Database

Configure the `src/main/resources/config.yml` file with your credentials:

```yaml
database:
  host: localhost
  port: 3306
  name: carsbase
  user: your_username
  password: your_password

timezone: America/Sao_Paulo
```

### 2. Data

Brand and car data are automatically loaded from YAML files:

- `brands.yml` - Automotive brands (Toyota, Ford, BMW, etc.)
- `cars.yml` - Car models with specifications

## Installation and Execution

### Compile the project

```bash
mvn clean compile
```

### Run the application

```bash
mvn exec:java -Dexec.mainClass="me.pieralini.com.Main"
```

### Or via command line

```bash
mvn clean package
java -jar target/carsbase.jar
```

## Features

- ✅ Automatic database table creation
- ✅ Automotive brand data population
- ✅ Car model registration with specifications
- ✅ Optimized connection pool with HikariCP
- ✅ Flexible configuration via YAML files
- ✅ Support for multiple fuel types
- ✅ Color and transmission management
- ✅ User authentication system

## Data Model

### Main Entities

| Entity | Description |
|----------|-----------|
| Brand | Vehicle brands (Toyota, Ford, etc.) |
| CarModel | Car models |
| Car | Individual vehicles |
| Color | Available colors |
| FuelType | Fuel types |
| Transmission | Transmission types |
| User | System users |

## Usage Example

```java
public class Main {
    public static void main(String[] args) {
        // Load configurations
        LoadConfig.getInstance();

        // Connect to database
        Database db = Database.getInstance();

        // Initialize and populate database
        DataLoaderService.getInstance().initializeDatabase();

        // Disconnect
        db.disconnect();
    }
}
```

## Contributing

1. Fork the project
2. Create your feature branch (`git checkout -b feature/new-feature`)
3. Commit your changes (`git commit -m 'Add new feature'`)
4. Push to the branch (`git push origin feature/new-feature`)
5. Open a Pull Request

## Author

**Igor Pieralini** - [@igorpieralini](https://github.com/igorpieralini)

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

---

⭐ If this project was helpful to you, consider giving it a star!

