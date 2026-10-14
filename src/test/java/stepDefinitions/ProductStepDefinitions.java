package stepDefinitions;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.Assertions;
import utils.DatabaseHelper.DatabaseHelper;

import java.sql.SQLException;
import java.util.Map;

public class ProductStepDefinitions {
    // Instantiate your JDBC helper
    private final DatabaseHelper dbHelper = new DatabaseHelper();

//    // 1. Declare the specification and response fields
//    private String baseUrl;
//    private Response response;
//    private RequestSpecification request;
//    // RequestSpecification acts as a builder where you store all configuration
//    // for the outgoing HTTP call (headers, base URI, auth, query params, cookies, body)
//    // before actually firing the request.
//
//
//    // 2. Initialize with base configuration
//    @Given("the product API is available at {string}")
//    public void setBaseUrl(String url) {
//        this.baseUrl = url;
//    }
//
//    @When("the client requests product details with id {int}")
//    public void requestProduct(int id) {
//        response = RestAssured.given()
//                .baseUri(baseUrl)
//                .accept("application/xml")
//                .when()
//                .get("/api/products/" + id)
//                .then()
//                .log().all()   // <-- Prints entire response (status, headers, body) to console
//                .extract().response();
//    }
//
//    @Then("the response status code should be {int}")
//    public void verifyStatusCode(int expectedStatusCode) {
//        Assertions.assertEquals(expectedStatusCode, response.getStatusCode());
//    }
//
    @Then("the response XML should have:")
    public void verifyXmlContent(DataTable dataTable) {
        Map<String, String> expectedValues = dataTable.asMap(String.class, String.class);

        // Validating XML nodes directly via XML path
        expectedValues.forEach((key, expectedVal) -> {
            String actualVal = switch (key) {
                case "Success" -> response.xmlPath().getString("ApiResponse.Success");
                case "Message" -> response.xmlPath().getString("ApiResponse.Message");
                case "Id" -> response.xmlPath().getString("ApiResponse.Data.Id");
                case "Name" -> response.xmlPath().getString("ApiResponse.Data.Name");
                case "Price" -> response.xmlPath().getString("ApiResponse.Data.Price");
                default -> throw new IllegalArgumentException("Unknown field: " + key);
            };
            Assertions.assertEquals(expectedVal, actualVal, "Mismatch for field: " + key);
        });
    }

    @Then("the response JSON should have:")
    public void verifyJSONContent(DataTable dataTable) {
        Map<String, String> expectedValues = dataTable.asMap(String.class, String.class);

        // Validating XML nodes directly via XML path
        expectedValues.forEach((key, expectedVal) -> {
            String actualVal = switch (key) {
                case "Success" -> response.jsonPath().getString("Success");
                case "Message" -> response.jsonPath().getString("Message");
                case "Id" -> response.jsonPath().getString("Data.Id");
                case "Name" -> response.jsonPath().getString("Data.Name");
                case "Price" -> response.jsonPath().getString("Data.Price");
                default -> throw new IllegalArgumentException("Unknown field: " + key);
            };
            Assertions.assertEquals(expectedVal, actualVal, "Mismatch for field: " + key);
        });
    }


    /** If using RequestSpecification */

    // 1. Declare the specification and response fields
    private RequestSpecification request;
    private Response response;

    // 2. Initialize with base configuration
    @Given("the product API is available at {string}")
    public void setupBaseRequest(String baseUrl) {
        this.request = RestAssured.given()
                .baseUri(baseUrl)
                .log().all(); // Logs the outgoing request details(Logs request headers, method, URL, and body to the console)
    }

    // 3. Chain headers (like Accept: application/xml or application/json)
    @Given("the request header {string} is set to {string}")
    public void setHeader(String headerName, String headerValue) {
        this.request.header(headerName, headerValue);
    }

    // 4. Attach query parameters (e.g. ?category=electronics)
    @Given("the request query parameter {string} is {string}")
    public void setQueryParam(String paramName, String paramValue) {
        this.request.queryParam(paramName, paramValue);
    }

    // 5. Attach a JSON payload body (for POST / PUT calls)
    @Given("the request payload is:")
    public void setRequestBody(String jsonOrXmlPayload) {
        this.request.contentType(ContentType.JSON)
                .body(jsonOrXmlPayload);
    }

    // 6. Execute GET and store the resulting Response
    @When("the client sends GET request to {string}")
    public void sendGetRequest(String endpoint) {
        this.response = this.request
                .when()
                .get(endpoint);

        // Immediately log the response in the console
        this.response.then().log().all();
    }

    @When("the client sends PATCH request to {string}")
    public void sendPatchRequest(String endpoint) {
        this.response = this.request
                .when()
                .patch(endpoint);

        // Immediately log the response in the console
        this.response.then().log().all();
    }

    // 7. Execute POST and store the resulting Response
    @When("the client sends POST request to {string}")
    public void sendPostRequest(String endpoint) {
        this.response = this.request
                .when()
                .post(endpoint);

        this.response.then().log().all();
    }

    @When("the client sends GET request to {string} for {int}")
    public void sendGetRequestWithId(String endpoint, int productId) {
        this.response = this.request
                .when()
                .get(endpoint + productId);

        this.response.then().log().all();
    }

    @And("the path parameter {string} is set to {int}")
    public void thePathParameterIsSetTo(String paramName, int paramValue) {
            this.request.pathParam(paramName, paramValue);
        }

    @And("the returned product id should match the database record for product {string}")
    public void theReturnedProductIdShouldMatchTheDatabaseRecordForProduct(String productName) throws SQLException {
         // 1. Extract ID from API response (supports either JSON or XML)
            int responseId;
            String contentType = response.getContentType();

            if (contentType != null && contentType.contains("xml")) {
                responseId = response.xmlPath().getInt("ApiResponse.Data.Id");
            } else {
                responseId = response.jsonPath().getInt("Data.Id");
            }

            // 2. Fetch the corresponding ID directly from MySQL via plain JDBC
            Integer dbId = dbHelper.getLatestProductIdByName(productName);
            System.out.println("Product Id- " + dbId);
            // 3. Verify they match
            Assertions.assertNotNull(dbId, "No matching record found in MySQL for product: " + productName);
            Assertions.assertEquals(dbId, responseId, "API response ID does not match MySQL database ID!");
        }



}
