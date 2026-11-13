package stepDefinitions;

import context.TestContext;
import io.cucumber.java.en.And;
import io.restassured.path.json.config.JsonPathConfig;
import io.restassured.response.Response;
import models.Product;
import org.junit.jupiter.api.Assertions;
import utils.DatabaseHelper.DatabaseHelper;
import utils.response.reader.ResponseReader;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ProductDbSteps {

    // Before each scenario, PicoContainer creates one TestContext and passes it to
// every class that asks for one. SO wherever a TestContext in passed in constructor, pico will pass the one
// already created by it. The constructor parameter is the only thing Pico looks at. No annotation
// is needed, but the class should have just one public constructor.

    private final TestContext context;

    public ProductDbSteps(TestContext context) {
        this.context = context;
    }


    private Product getProductDetails(Response response) {

        String name = ResponseReader.readResponse(response, "Name");
        BigDecimal price = ResponseReader.getPrice(response);
        return new Product(name, price);
    }

    private Product findRow(long id) {
        return context.getDatabaseHelper().getProductById(id);
    }

// Not being used. Substituted by getProductDetails(). Kept for reference
    private Product getProduct(Response response) {
        BigDecimal price = response.jsonPath()
                .using(JsonPathConfig.jsonPathConfig().numberReturnType(JsonPathConfig.NumberReturnType.BIG_DECIMAL))
                .getObject("Data.Price", BigDecimal.class);
        BigDecimal bigDecimal = BigDecimal.valueOf(response.jsonPath().getDouble("Data.Price"));
        return new Product(
                response.jsonPath().getString("Data.Name"), price);
    }

    @And("the product {string} in the database should be {string}")
    public void the_Product_In_The_Database_Should_Be(String column, String expected) {
        DatabaseHelper dbHelper = context.getDatabaseHelper();
        long id = context.requireCreatedProductId();
        switch (column.toLowerCase()) {
            case "name"  -> assertThat(dbHelper.getProductNameById(id))
                    .as("name column").isEqualTo(expected);
            case "price" -> assertThat(dbHelper.getProductPriceById(id))
                    .as("price column")
                    .isEqualByComparingTo(new BigDecimal(expected));
            default -> throw new IllegalArgumentException("Unknown product column: " + column);
        }
    }

    @And("the product with id of new product should exist in the database")
    public void theProductWithIdOfNewProductShouldExistInTheDatabase() {
        DatabaseHelper db = context.getDatabaseHelper();
        boolean productExists = db.isProductExists(context.requireCreatedProductId());
        assertThat(productExists).as("Product exists")
                .isTrue();
    }

    @And("the product {string} in the database should match")
    public void theProductInTheDatabaseShouldMatch(String column) {
        DatabaseHelper db = context.getDatabaseHelper();
        Product productById = db.getProductById(context.requireCreatedProductId());
        switch(column.toLowerCase()){
            case "name" -> Assertions.assertEquals(context.getProduct().getProductName(),productById.getProductName(),
                    "Product name should be equal");
            case "price" -> assertThat(context.getProduct().getPrice())
                    .as("price column")
                    .isEqualByComparingTo(productById.getPrice());
            default -> throw new IllegalArgumentException("Unknown product column: " + column);
        }
    }

    @And("the product should no longer exist in the database")
    public void theProductShouldNoLongerExistInTheDatabase() {
        DatabaseHelper db = context.getDatabaseHelper();
        Assertions.assertFalse(db.isProductExists(context.requireCreatedProductId()),
                "Product " + context.requireCreatedProductId() + " is still in the database after DELETE");
    }
}
