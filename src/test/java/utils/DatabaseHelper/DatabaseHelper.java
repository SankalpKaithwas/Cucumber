package utils.DatabaseHelper;

import java.sql.*;

public class DatabaseHelper {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/product_db"
            + "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "root";

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }

    // Method to query the newly created product ID by name
    public Integer getLatestProductIdByName(String productName) throws SQLException {
        String query = "SELECT id FROM products WHERE name = ? ORDER BY id DESC LIMIT 1";

        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {

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

    // True if a row with this id is still in the products table
    public boolean productExists(int id) {
        String sql = "SELECT 1 FROM products WHERE id = ?";

        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Database query failed while checking product id: " + id, e);
        }
    }

    // Returns null if the product does not exist
    public String getProductNameById(int id) {
        String sql = "SELECT name FROM products WHERE id = ?";

        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? rs.getString("name") : null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Database query failed while fetching name for id: " + id, e);
        }
    }

    // Returns null if the product does not exist
    public Double getProductPriceById(int id) {
        String sql = "SELECT price FROM products WHERE id = ?";

        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? rs.getDouble("price") : null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Database query failed while fetching price for id: " + id, e);
        }
    }
}
