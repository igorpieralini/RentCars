# AlugaCar

A **car rental system** built with **Java + MySQL**, featuring a **Swing-based UI** and **email-based password recovery**.

## 🚀 Quick Start

```shell
git clone https://github.com/your-username/AlugaCar.git
cd AlugaCar
mvn clean install
```

### Database Configuration

1. Edit the database credentials in `config.yml` (this file is not versioned).
2. Run the application:

   ```shell
   java -cp target/AlugaCar-1.0-SNAPSHOT.jar me.pieralini.com.Main
   ```

## 📁 Project Structure

```
src/
├── main/java/me/pieralini/com/
│   ├── Main.java                # Application entry point
│   ├── ui/                      # Screens and UI components
│   │   ├── LoginFrame.java      # Login screen
│   │   ├── view/                # Registration and main pages
│   │   ├── systems/             # Password recovery system
│   │   └── components/          # Custom fields, buttons, etc.
│   ├── util/                    # Utility classes
│   │   ├── Database.java        # MySQL connection
│   │   ├── ConfigLoader.java    # Configuration loader
│   │   ├── UIHelper.java        # UI helpers
│   │   └── email/               # Email utilities
│   │       ├── EmailConfig.java
│   │       ├── EmailService.java
│   │       └── EmailTemplate.java
├── resources/                   # Images and configuration files
```

## ⚙️ Features

* User authentication
* Car registration and listing
* Card-based car display
* Email-based password recovery
* MySQL database integration

## 🐛 Dependencies

* **MySQL Connector/J** – MySQL database connection
* **SnakeYAML** – Configuration file parsing
* **Jakarta Mail** – Email sending support

## 📏 License

MIT License 🔓
**Author:** Igor Pieralini
