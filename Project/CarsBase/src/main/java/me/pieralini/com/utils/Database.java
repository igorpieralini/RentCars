package me.pieralini.com.utils;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

public class Database {

    private static Database instance;
    private HikariDataSource dataSource;
    private boolean connected = false;
    private String dbType;

    private Database() {
        connect();
    }

    public static Database getInstance() {
        if (instance == null) {
            instance = new Database();
        }
        return instance;
    }

    private void connect() {
        try {
            Map<String, Object> dbConfig = LoadConfig.getSection("database");
            Map<String, Object> poolConfig = LoadConfig.getSection("database.pool");

            if (dbConfig == null) {
                throw new RuntimeException("Database configuration not found in config.yml!");
            }

            dbType = (String) dbConfig.get("type");
            String host = (String) dbConfig.get("host");
            Integer port = (Integer) dbConfig.get("port");
            String database = (String) dbConfig.get("name");
            String username = (String) dbConfig.get("username");
            String password = (String) dbConfig.get("password");

            if (dbType.equalsIgnoreCase("h2")) {
                Class.forName("org.h2.Driver");
            } else if (dbType.equalsIgnoreCase("mysql")) {
                Class.forName("com.mysql.cj.jdbc.Driver");
            }

            String jdbcUrl = buildJdbcUrl(dbType, host, port, database);

            HikariConfig config = new HikariConfig();
            config.setJdbcUrl(jdbcUrl);
            config.setUsername(username);
            config.setPassword(password);

            if (poolConfig != null) {
                Integer minimumIdle = (Integer) poolConfig.get("minimum-idle");
                Integer maximumPoolSize = (Integer) poolConfig.get("maximum-pool-size");
                Integer connectionTimeout = (Integer) poolConfig.get("connection-timeout");
                Integer idleTimeout = (Integer) poolConfig.get("idle-timeout");
                Integer maxLifetime = (Integer) poolConfig.get("max-lifetime");

                if (minimumIdle != null) config.setMinimumIdle(minimumIdle);
                if (maximumPoolSize != null) config.setMaximumPoolSize(maximumPoolSize);
                if (connectionTimeout != null) config.setConnectionTimeout(connectionTimeout);
                if (idleTimeout != null) config.setIdleTimeout(idleTimeout);
                if (maxLifetime != null) config.setMaxLifetime(maxLifetime);
            }

            config.addDataSourceProperty("cachePrepStmts", "true");
            config.addDataSourceProperty("prepStmtCacheSize", "250");
            config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

            config.setPoolName("CarsBase-HikariCP");

            dataSource = new HikariDataSource(config);
            connected = true;

            System.out.println("[Database] Connected to " + dbType + " database: " + database);


        } catch (Exception e) {
            connected = false;
            System.err.println("[Database] Failed to connect to database: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private String buildJdbcUrl(String type, String host, Integer port, String database) {
        String t = type.toLowerCase();
        if (t.equals("mysql")) {
            return String.format("jdbc:mysql://%s:%d/%s?useSSL=false&autoReconnect=true&characterEncoding=utf8", host, port, database);
        } else if (t.equals("postgresql")) {
            return String.format("jdbc:postgresql://%s:%d/%s", host, port, database);
        } else if (t.equals("mariadb")) {
            return String.format("jdbc:mariadb://%s:%d/%s", host, port, database);
        } else if (t.equals("sqlite")) {
            return String.format("jdbc:sqlite:%s", database);
        } else if (t.equals("h2")) {
            return "jdbc:h2:mem:" + database + ";DB_CLOSE_DELAY=-1;MODE=MySQL";
        } else {
            throw new IllegalArgumentException("Unsupported database type: " + type);
        }
    }


    public Connection getConnection() throws SQLException {
        if (!connected || dataSource == null) {
            throw new SQLException("Database is not connected!");
        }
        return dataSource.getConnection();
    }

    public ResultSet executeQuery(String sql, Object... params) {
        try {
            Connection conn = getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);

            for (int i = 0; i < params.length; i++) {
                stmt.setObject(i + 1, params[i]);
            }

            return stmt.executeQuery();

        } catch (SQLException e) {
            System.err.println("[Database] Error executing query: " + e.getMessage());
            return null;
        }
    }

    public int executeUpdate(String sql, Object... params) {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            for (int i = 0; i < params.length; i++) {
                stmt.setObject(i + 1, params[i]);
            }

            return stmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println("[Database] Error executing update: " + e.getMessage());
            return -1;
        }
    }

    public static void closeResultSet(ResultSet rs) {
        if (rs != null) {
            try {
                rs.getStatement().getConnection().close();
                rs.getStatement().close();
                rs.close();
            } catch (SQLException e) {
                System.err.println("[Database] Error closing ResultSet: " + e.getMessage());
            }
        }
    }

    public boolean isConnected() {
        return connected && dataSource != null && !dataSource.isClosed();
    }

    public void disconnect() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
            connected = false;
            System.out.println("[Database] Connection pool closed!");
        }
    }
}
