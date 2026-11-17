package utils.DatabaseHelper;

import models.Product;
import utils.config.ConfigFileReader;

import java.math.BigDecimal;
import java.sql.*;


public class DatabaseHelper {

    private static final String DB_URL = ConfigFileReader.get("db.url");
    private static final String DB_USER = ConfigFileReader.get("db.user");
    private static final String DB_PASSWORD = ConfigFileReader.get("db.password");

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }

    // Method to query the newly created product ID by name
    public Long getLatestIdByProductName(String productName) {
        String query = "SELECT id FROM products WHERE name = ? ORDER BY id DESC LIMIT 1";

        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, productName);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong("id");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Database query failed while fetching product ID for: " + productName, e);
        }

        return null; // Return null if no matching record is found
    }

    // True if a row with this id is still in the products table
    public boolean isProductExists(Long id) {
        String sql = "SELECT 1 FROM products WHERE id = ?";

        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Database query failed while checking product id: " + id, e);
        }
    }

    // Returns null if the product does not exist
    public String getProductNameById(long id) {
        String sql = "SELECT name FROM products WHERE id = ?";

        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? rs.getString("name") : null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Database query failed while fetching name for id: " + id, e);
        }
    }

    // Returns null if the product does not exist
    public BigDecimal getProductPriceById(long id) {
        String sql = "SELECT price FROM products WHERE id = ?";

        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? rs.getBigDecimal("price") : null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Database query failed while fetching price for id: " + id, e);
        }
    }


    public Product getProductById(long id) {
        String sql = "SELECT NAME, PRICE FROM products WHERE id = ?";
        String name;
        BigDecimal price;
        Product product = new Product();
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    name = rs.getString("name");
                    price = rs.getBigDecimal("price");

                    product.setProductName(name);
                    product.setPrice(price);
                    product.setId(id);
                    return product;
                } else return null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Database query failed while fetching name for id: " + id, e);
        }
    }

    public void deleteProductById(long id) {
        try (Connection connection = getConnection();
           PreparedStatement preparedStatement =
                   connection.prepareStatement("DELETE FROM PRODUCTS WHERE ID = ?");) {
            preparedStatement.setLong(1, id);
            preparedStatement.executeUpdate();
    /* executeUpdate() is the one for DELETE, INSERT and UPDATE. executeQuery() is only for SELECT */
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Product getProductThatExists() {
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement =
                     connection.prepareStatement("SELECT * FROM products\n" +
                             "ORDER BY RAND()\n" +
                             "LIMIT 1;")) {
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return new Product(resultSet.getString("name"),
                        resultSet.getBigDecimal("price"));
            }
             return null;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
