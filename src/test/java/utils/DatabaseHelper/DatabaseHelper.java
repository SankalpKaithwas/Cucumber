package utils.DatabaseHelper;

import java.sql.*;

public class DatabaseHelper {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/product_db" +
            "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "root";

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }

    // Method to query the newly created product ID by name
    public Integer getLatestProductIdByName(String productName) throws SQLException {
        String query = "SELECT id FROM products WHERE name = ? ORDER BY id DESC LIMIT 1";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, productName);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Database query failed while fetching product ID for: " + productName, e);
        }

        return null; // Return null if no matching record is found
    }
}
