package me.pieralini.com.services;

import me.pieralini.com.objects.User;
import me.pieralini.com.utils.Database;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;

public class AuthService {

    private static AuthService instance;
    private User currentUser;

    private AuthService() {
    }

    public static AuthService getInstance() {
        if (instance == null) {
            instance = new AuthService();
        }
        return instance;
    }

    public AuthResult login(String username, String password) {
        if (username == null || username.trim().isEmpty()) {
            return new AuthResult(false, "Username is required", null);
        }
        if (password == null || password.trim().isEmpty()) {
            return new AuthResult(false, "Password is required", null);
        }

        String hashedPassword = hashPassword(password);

        String sql = "SELECT id, username, email, full_name, role, active, created_at, updated_at, last_login " +
                     "FROM users WHERE username = ? AND password = ?";

        try (Connection conn = Database.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            stmt.setString(2, hashedPassword);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                boolean active = rs.getBoolean("active");

                if (!active) {
                    logLoginAttempt(username, false);
                    return new AuthResult(false, "User account is inactive", null);
                }

                User user = mapResultSetToUser(rs);
                updateLastLogin(user.getId());
                logLoginAttempt(username, true);
                this.currentUser = user;

                System.out.println("[AuthService] User '" + username + "' logged in successfully");
                return new AuthResult(true, "Login successful", user);
            } else {
                logLoginAttempt(username, false);
                return new AuthResult(false, "Invalid username or password", null);
            }

        } catch (SQLException e) {
            System.err.println("[AuthService] Login error: " + e.getMessage());
            return new AuthResult(false, "Database error: " + e.getMessage(), null);
        }
    }

    public AuthResult register(String username, String password, String email, String fullName) {
        if (username == null || username.trim().isEmpty()) {
            return new AuthResult(false, "Username is required", null);
        }
        if (password == null || password.length() < 6) {
            return new AuthResult(false, "Password must be at least 6 characters", null);
        }
        if (email == null || !email.contains("@")) {
            return new AuthResult(false, "Valid email is required", null);
        }

        if (usernameExists(username)) {
            return new AuthResult(false, "Username already exists", null);
        }

        if (emailExists(email)) {
            return new AuthResult(false, "Email already exists", null);
        }

        String hashedPassword = hashPassword(password);

        String sql = "INSERT INTO users (username, password, email, full_name, role, active) " +
                     "VALUES (?, ?, ?, ?, 'USER', TRUE)";

        try (Connection conn = Database.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, username);
            stmt.setString(2, hashedPassword);
            stmt.setString(3, email);
            stmt.setString(4, fullName);

            int rows = stmt.executeUpdate();

            if (rows > 0) {
                ResultSet keys = stmt.getGeneratedKeys();
                if (keys.next()) {
                    User user = new User(username, null, email, fullName);
                    user.setId(keys.getInt(1));

                    System.out.println("[AuthService] User '" + username + "' registered successfully");
                    return new AuthResult(true, "Registration successful", user);
                }
            }

            return new AuthResult(false, "Failed to register user", null);

        } catch (SQLException e) {
            System.err.println("[AuthService] Registration error: " + e.getMessage());
            return new AuthResult(false, "Database error: " + e.getMessage(), null);
        }
    }

    public void logout() {
        if (currentUser != null) {
            System.out.println("[AuthService] User '" + currentUser.getUsername() + "' logged out");
            currentUser = null;
        }
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public boolean isLoggedIn() {
        return currentUser != null;
    }

    public boolean isAdmin() {
        return currentUser != null && currentUser.isAdmin();
    }

    public boolean changePassword(int userId, String oldPassword, String newPassword) {
        if (newPassword == null || newPassword.length() < 6) {
            System.err.println("[AuthService] New password must be at least 6 characters");
            return false;
        }

        String oldHash = hashPassword(oldPassword);
        String newHash = hashPassword(newPassword);

        String sql = "UPDATE users SET password = ? WHERE id = ? AND password = ?";

        try (Connection conn = Database.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, newHash);
            stmt.setInt(2, userId);
            stmt.setString(3, oldHash);

            int rows = stmt.executeUpdate();

            if (rows > 0) {
                System.out.println("[AuthService] Password changed successfully for user ID: " + userId);
                return true;
            } else {
                System.err.println("[AuthService] Invalid old password or user not found");
                return false;
            }

        } catch (SQLException e) {
            System.err.println("[AuthService] Password change error: " + e.getMessage());
            return false;
        }
    }

    public User getUserById(int userId) {
        String sql = "SELECT id, username, email, full_name, role, active, created_at, updated_at, last_login " +
                     "FROM users WHERE id = ?";

        try (Connection conn = Database.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToUser(rs);
            }

        } catch (SQLException e) {
            System.err.println("[AuthService] Error getting user: " + e.getMessage());
        }

        return null;
    }

    private String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not found", e);
        }
    }

    private boolean usernameExists(String username) {
        String sql = "SELECT COUNT(*) FROM users WHERE username = ?";

        try (Connection conn = Database.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }

        } catch (SQLException e) {
            System.err.println("[AuthService] Error checking username: " + e.getMessage());
        }

        return false;
    }

    private boolean emailExists(String email) {
        String sql = "SELECT COUNT(*) FROM users WHERE email = ?";

        try (Connection conn = Database.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }

        } catch (SQLException e) {
            System.err.println("[AuthService] Error checking email: " + e.getMessage());
        }

        return false;
    }

    private void updateLastLogin(int userId) {
        String sql = "UPDATE users SET last_login = CURRENT_TIMESTAMP WHERE id = ?";

        try (Connection conn = Database.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            stmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println("[AuthService] Error updating last login: " + e.getMessage());
        }
    }

    private void logLoginAttempt(String username, boolean success) {
        String sql = "INSERT INTO login_attempts (username, success) VALUES (?, ?)";

        try (Connection conn = Database.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            stmt.setBoolean(2, success);
            stmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println("[AuthService] Error logging attempt: " + e.getMessage());
        }
    }

    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getInt("id"));
        user.setUsername(rs.getString("username"));
        user.setEmail(rs.getString("email"));
        user.setFullName(rs.getString("full_name"));
        user.setRole(User.Role.valueOf(rs.getString("role")));
        user.setActive(rs.getBoolean("active"));
        user.setCreatedAt(rs.getTimestamp("created_at"));
        user.setUpdatedAt(rs.getTimestamp("updated_at"));
        user.setLastLogin(rs.getTimestamp("last_login"));
        return user;
    }

    public static class AuthResult {
        private final boolean success;
        private final String message;
        private final User user;

        public AuthResult(boolean success, String message, User user) {
            this.success = success;
            this.message = message;
            this.user = user;
        }

        public boolean isSuccess() {
            return success;
        }

        public String getMessage() {
            return message;
        }

        public User getUser() {
            return user;
        }

        @Override
        public String toString() {
            return "AuthResult{" +
                    "success=" + success +
                    ", message='" + message + '\'' +
                    ", user=" + (user != null ? user.getUsername() : "null") +
                    '}';
        }
    }
}
