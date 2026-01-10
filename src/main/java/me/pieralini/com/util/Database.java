package me.pieralini.com.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

public final class Database {

    private static Connection connection;

    private static String host;
    private static String database;
    private static String user;
    private static String password;
    private static String port;

    private Database() {}

    public static void setup(Map<String, String> config) {
        host = config.get("host");
        database = config.get("database");
        user = config.get("user");
        password = config.get("password");
        port = config.getOrDefault("port", "3306");
    }

    public static boolean connect() {
        if (isConnected()) return true;
        try {
            String url = "jdbc:mysql://" + host + ":" + port + "/" + database + "?useSSL=false&autoReconnect=true";
            connection = DriverManager.getConnection(url, user, password);
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    public static void disconnect() {
        if (!isConnected()) return;
        try {
            connection.close();
        } catch (SQLException ignored) {}
        connection = null;
    }

    public static boolean reconnect() {
        disconnect();
        return connect();
    }

    public static boolean isConnected() {
        try {
            return connection != null && !connection.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }

    public static ResultSet query(String sql, Object... params) throws SQLException {
        ensureConnection();
        PreparedStatement statement = connection.prepareStatement(sql);
        bindParams(statement, params);
        return statement.executeQuery();
    }

    public static int execute(String sql, Object... params) throws SQLException {
        ensureConnection();
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            bindParams(statement, params);
            return statement.executeUpdate();
        }
    }

    private static void bindParams(PreparedStatement statement, Object... params) throws SQLException {
        for (int i = 0; i < params.length; i++) {
            statement.setObject(i + 1, params[i]);
        }
    }

    private static void ensureConnection() throws SQLException {
        if (!isConnected()) {
            throw new SQLException("Database not connected");
        }
    }
}
