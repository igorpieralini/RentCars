package me.pieralini.com;

import me.pieralini.com.services.DataLoaderService;
import me.pieralini.com.utils.Database;
import me.pieralini.com.utils.LoadConfig;

public class Main {

    public static void main(String[] args) {
        // Load configuration
        LoadConfig.getInstance();

        // Connect to database
        Database db = Database.getInstance();

        if (!db.isConnected()) {
            System.err.println("Failed to connect to database. Exiting...");
            return;
        }

        // Initialize and populate database
        System.out.println("Initializing database...");
        DataLoaderService.getInstance().initializeDatabase();

        System.out.println("\n✓ Database created and populated successfully!");

        // Disconnect
        db.disconnect();

        System.out.println("\nCarsBase setup completed!");
    }
}
