package general.scripts;

import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import stepDefinitions.ProductStepDefinitions;

public class StepScriptsTest {
public static String baseUrl = "http://localhost:8080";


    private ProductStepDefinitions steps;

    // Runs before every individual @Test method to give you a fresh instance
    @BeforeEach
    public void setup() {
        steps = new ProductStepDefinitions();
        steps.setupBaseRequest(baseUrl);
    }

    @Test
    public void testGetProductByIdXml() {
        steps.setHeader("Accept", "application/xml");
        Response response = steps.send_Get_Request_With_Id("/api/products/", 1);
        int statusCode = steps.the_Response_Status_Code_Should_Be(200);

        System.out.println("StatusCode = " + statusCode);
        //    String prettyString = response.asPrettyString();
        //    System.out.println("prettyString = " + prettyString);
        response.prettyPrint();
        // Both prettyPrint() and asPrettyString() prints JSON/XML in formatted way.
    }

    // ▶ Trigger: GET product returning JSON
    @Test
    public void testGetProductByIdJson() {
        steps.setHeader("Accept", "application/json");
        Response response = steps.send_Get_Request_With_Id("/api/products/", 1);
//        response.prettyPrint();
    }

    // ▶ Trigger: Search endpoint
    @Test
    public void testSearchEndpoint() {
        steps.setHeader("Accept", "application/json");
        steps.setQueryParam("productName", "mouse");
        steps.sendGetRequest("/api/products/search");
    }

    // ▶ Trigger: POST create product
    @Test
    public void testCreateProduct() {
        steps.setHeader("Content-Type", "application/json");
        steps.setHeader("Accept", "application/json");
        steps.setJsonRequestBody("""
            {
              "name": "Cloth Cabinet",
              "price": 295.50
            }
            """);
        steps.sendPostRequest("/api/products");
    }
}