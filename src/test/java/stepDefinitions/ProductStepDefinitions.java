package stepDefinitions;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.After;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.jupiter.api.Assertions;
import utils.DatabaseHelper.DatabaseHelper;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class ProductStepDefinitions {
    private static final Log log = LogFactory.getLog(ProductStepDefinitions.class);
    // Instantiate your JDBC helper
    private final DatabaseHelper dbHelper = new DatabaseHelper();

    // Id of a product seeded straight into MySQL by the scenario (null if none)
    private Integer createdProductId;

    private String baseUrlUpdated;
    private Integer existingProductId;
    private String originalName;
    private BigDecimal originalPrice;

    private static final Map<String, String> FIELD_PATHS = Map.of(
            "Success", "Success",
            "Message", "Message",
            "Size", "Data.page.Size"
            // add new tags here, e.g. "Total", "Data.Id"
    );

//    private static final Map<String, String> REQUESTTYPE_ENDPOINTS = Map.of(
//            "GET", "api/products",
//            "POST", "api/products",
//            "PATCH","api/products"
//    );

    private final Map<String, String> queryParams = new HashMap<>();

    private final Map<String, String> productDetails = new HashMap<>();


    //    // 1. Declare the specification and response fields
//    private String baseUrl;
//    private Response response;
//    private RequestSpecification request;
//    // RequestSpecification acts as a builder where you store all configuration
//    // for the outgoing HTTP call (headers, base URI, auth, query params, cookies, body)
//    // before actually firing the request.
//
//
    // 2. Initialize with base configuration
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
                case "StatusCode" -> response.xmlPath().getString("ApiResponse.StatusCode");
                case "Success" -> response.xmlPath().getString("ApiResponse.Success");
                case "Message" -> response.xmlPath().getString("ApiResponse.Message");
                case "Id" -> response.xmlPath().getString("ApiResponse.Data.Id");
                case "Errors" -> response.xmlPath().getString("ErrorResponse.Errors");
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
                case "StatusCode" -> response.jsonPath().getString("StatusCode");
                case "Success" -> response.jsonPath().getString("Success");
                case "Message" -> response.jsonPath().getString("Message");
                case "Errors" -> response.jsonPath().getString("Errors");
                case "Id" -> response.jsonPath().getString("Data.Id");
                case "Name" -> response.jsonPath().getString("Data.Name");
                case "Price" -> response.jsonPath().getString("Data.Price");
                default -> throw new IllegalArgumentException("Unknown field: " + key);
            };
            Assertions.assertEquals(expectedVal, actualVal, "Mismatch for field: " + key);
        });
    }


    /**
     * If using RequestSpecification
     */

    // 1. Declare the specification and response fields
    private RequestSpecification request;
    private Response response;

    // 2. Initialize with base configuration
    @Given("the product API is available at {string}")
    public String setupBaseRequest(String baseUrl) {
        this.request = RestAssured.given()
                .baseUri(baseUrl)
                .log().all(); // Logs the outgoing request details(Logs request headers, method, URL, and body to the console)
        return this.baseUrlUpdated = baseUrl;
    }

    // {string} automatically matches text inside double quotes ("...") or single quotes ('...')
    // {int} matches integer values
    // {double} or {float} matches decimal numbers
    // {boolean} matches true/false
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
    @Given("the Json request payload is:")
    public void setJsonRequestBody(String jsonPayload) {
        this.request.contentType(ContentType.JSON)
                .body(jsonPayload);
    }

    // Attach a XML payload body (for POST / PUT calls)
    @Given("the xml request payload is:")
    public void set_Xml_Request_Body(String xmlPayload) {
        this.request.contentType(ContentType.XML)
                .body(xmlPayload);
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

    @When("the client sends {string} request to {string}")
    public void the_client_sends_Request(String requestType, String endpoint) {
        if (requestType.equalsIgnoreCase("GET")) {
            this.response = this.request
                    .when()
                    .get(endpoint);

            // Immediately log the response in the console
            this.response.then().log().all();
        } else if (requestType.equalsIgnoreCase("PATCH")) {
            this.response = this.request
                    .when()
                    .patch(endpoint);

            // Immediately log the response in the console
            this.response.then().log().all();

        } else if (requestType.equalsIgnoreCase("PUT")) {
            this.response = this.request
                    .when()
                    .put(endpoint);
            // Immediately log the response in the console
            this.response.then().log().all();
        }
    }

//    public void sendRequest(String requestType, String endpoint) {
//
//        if (requestType.equalsIgnoreCase("GET")) {
//            this.response = this.request
//                    .when()
//                    .get(endpoint);
//
//            // Immediately log the response in the console
//            this.response.then().log().all();
//        } else if (requestType.equalsIgnoreCase("PATCH")) {
//            this.response = this.request
//                    .when()
//                    .patch(endpoint);
//
//            // Immediately log the response in the console
//            this.response.then().log().all();
//        }
//    }


    // 7. Execute POST and store the resulting Response
    @When("the client sends POST request to {string}")
    public void sendPostRequest(String endpoint) {
        this.response = this.request
                .when()
                .post(endpoint);

        this.response.then().log().all();
    }

    @When("the client sends GET request to {string} for {int}")
    public Response send_Get_Request_With_Id(String endpoint, int productId) {
        this.response = this.request
                .when()
                .get(endpoint + productId);

        this.response.then().log().all();
        return response;
    }

    @And("the path parameter {string} is set to {string}")
    public void the_Path_Parameter_Is_Set_To(String paramName, String paramValue) {
        switch (paramName) {
            case "id":
                this.request.pathParam(paramName, Integer.parseInt(paramValue));
                break;
            case "anything":
                this.request.pathParam(paramName, "anything");
                break;
            default:
                this.request.pathParam(paramName, "default");
                break;
        }
    }

    @And("the returned product id should match the database record for product {string}")
    public void the_Returned_Product_Id_Should_Match_The_Database_Record_For_Product(
            String productName) {
        // 1. Fetch the corresponding ID directly from MySQL via plain JDBC
        Integer dbId = dbHelper.getLatestProductIdByName(productName);
        log.info("Product Id- " + dbId);
        // 2. Verify they match
        Assertions.assertNotNull(dbId, "No matching record found in MySQL for product: " + productName);
        Assertions.assertEquals(dbId, createdProductId, "API response ID does not match MySQL database ID!");
    }


    @Then("the product id is saved from the response")
    public void the_ProductId_Is_Saved_From_The_Response() {
        String contentType = response.contentType();
        if (contentType != null && contentType.contains("xml")) {
            createdProductId = response.getBody().xmlPath().getInt("ApiResponse.Data.Id");
        } else {
            createdProductId = response.jsonPath().getInt("Data.Id");
        }
        log.info("CreatedProductId - " + createdProductId);
    }

    @Then("the response status code should be {int}")
    public int the_Response_Status_Code_Should_Be(int expectedStatusCode) {
        int statusCode = response.getStatusCode();
        Assertions.assertEquals(expectedStatusCode, statusCode, "Unexpected HTTP status code");
        return statusCode;
    }

    @Then("the response Message should be {string}")
    public void the_Response_Message_Should_Be(String expectedMessage) {
        String message = response.getBody().jsonPath().get("Message");
        Assertions.assertEquals(expectedMessage, message, "Unexpected HTTP status code");
    }

    @Then("the response Errors should be {string}")
    public void the_Response_Errors_Should_Be(String expectedErrors) {
        String errors = response.getBody().jsonPath().get("Errors");
        Assertions.assertEquals(expectedErrors, errors, "Unexpected HTTP status code");
    }

    @Then("the client sends DELETE request to {string}")
    public void the_Client_Sends_DELETE_Request_To(String endPoint) {
        this.response = this.request
                .when()
                .delete(endPoint);
        this.response.then().log().all();
    }

    @Then("the product should no longer exist in the database")
    public void the_Product_Should_No_Longer_Exist_In_The_Database() {
        Assertions.assertNotNull(createdProductId, "No seeded product id to check");
        Assertions.assertFalse(dbHelper.productExists(createdProductId),
                "Product " + createdProductId + " is still in the database after DELETE");
    }

    @And("the path parameter {string} is set to the created product id")
    public void the_PathParameter_Is_Set_To_The_CreatedProductId(String id) {
        this.request.pathParams(id, createdProductId);
    }

    @Given("A new request is prepared")
    public void a_New_Request_Is_Prepared() {
        setupBaseRequest(baseUrlUpdated);
    }

    @Given("the existing product with id {int} is remembered")
    public void the_Existing_Product_With_Id_Int_Is_Remembered(int id) {
        existingProductId = id;
        originalName = dbHelper.getProductNameById(id);
        originalPrice = dbHelper.getProductPriceById(id);
        Assertions.assertNotNull(originalName, "Product " + id
                + "does not exist. This Scneario needs it to be there.");
    }

    @Given("Get existing product details using id {int}")
    public void get_Existing_Product_Details_Using_Id(int id) {
        response = this.request.when().get("api/products/" + id);
        if(response.getBody()!= null) {
            int productId = response.getBody().jsonPath().get("Data.Id");
            String productName = response.getBody().jsonPath().get("Data.Name");
            double productprice = response.getBody().jsonPath().get("Data.Price");
            productDetails.put("id", String.valueOf(productId));
            productDetails.put("name", productName);
            productDetails.put("price", String.valueOf(productprice));
        }
        log.info("ProductDetails " + productDetails);
    }

    // Put the original values back through the PATCH endpoint, like the other cleanup
    @After("@restoreProduct")
    public void restore_Existing_Product() {
        var body = "{\"name\":\"" + originalName + "\",\"price\":" + originalPrice + "}";
//        String body = "<Product><name>" + originalName + "</name><price>" + originalPrice + "</price></Product>";

        if (existingProductId != null && baseUrlUpdated != null) {
            Response restored = RestAssured.given()
                    .baseUri(baseUrlUpdated)
                    .contentType(ContentType.JSON)
//                    .contentType(ContentType.XML)
                    .body(body).log().all().patch("/api/products/" + existingProductId).then()
                    .log().all()   // <-- Prints entire response (status, headers, body) to console
                    .extract().response();
            existingProductId = null;
            Assertions.assertEquals(200, restored.getStatusCode(),
                    "RESTORE FAILED - product data was left modified: " + restored.asString());
        }
    }

    @And("the product {string} in the database should be {string}")
    public void the_Product_In_The_Database_Should_Be(String name, String value) {
        if (name.equalsIgnoreCase("name")) {
            Assertions.assertEquals(value, dbHelper.getProductNameById(existingProductId),
                    "Product name - " + name + " matches the updated value with - " + value);
        } else if (name.equalsIgnoreCase("price")) {
            Assertions.assertEquals(new BigDecimal(value), dbHelper.getProductPriceById(existingProductId),
                    "Product name - " + name + " matches the updated value with - " + value);
        }
    }

    @Then("the product {string} in the database should be unchanged")
    public void the_Product_Price_Should_Be_Unchanged(String product_check) {
        if (Objects.equals(product_check, "price")) {
            Assertions.assertEquals(originalPrice, dbHelper.getProductPriceById(existingProductId));
        }
    }

//    @And("the response {string} should be {string}")
//    public void theResponseShouldBe(String tag, String value) {
//        String tagValue = "";
//        List<String> names = new ArrayList<>();
//        if (response.getContentType().contains("json")) {
//            switch (tag) {
//                case "Success" -> {
//                    tagValue = response.getBody().jsonPath().getString("Success");
//                    Assertions.assertEquals(value, tagValue, "The given value does not match.");
//                }
//                case "Data" -> {
//                    names = response.getBody().jsonPath().getList("Data.Name");
////                    System.out.println("Data = " + names);
//                    Assertions.assertFalse(names.isEmpty(), "No products found in response");
//                    names.forEach(n -> assertTrue(n.toLowerCase().contains(value.toLowerCase()),
//                            n + " does not contain " + value));
//                }
//                case "Message" -> {
//                    tagValue = response.getBody().jsonPath().getString("Message");
//                    Assertions.assertEquals(value, tagValue, "The given value does not match.");
//                }
//                default -> throw new IllegalArgumentException("Unsupported tag: [" + tag + "]");
//            }
//        } else if (response.getContentType().contains("xml")) {
//            switch (tag) {
//                case "Success" -> {
//                    tagValue = response.getBody().xmlPath().getString("ApiResponse.Success");
//                    Assertions.assertEquals(value, tagValue, "The given value does not match.");
//                }
//                case "Data" -> {
//                    names = response.getBody().xmlPath().getList("ApiResponse.Data.Name");

    /// /                    System.out.println("Data = " + names);
//                    Assertions.assertFalse(names.isEmpty(), "No products found in response");
//                    names.forEach(n -> assertTrue(n.toLowerCase().contains(value.toLowerCase()),
//                            n + " does not contain " + value));
//                }
//
//                case "Message" -> {
//                    tagValue = response.getBody().xmlPath().getString("ApiResponse.Message");
//                    Assertions.assertEquals(value, tagValue, "The given value does not match.");
//                }
//                default -> throw new IllegalArgumentException("Unsupported tag: [" + tag + "]");
//            }
//        }
//    }

//Can also do like below =>

//    @And("the response {string} should be {string}")
//    public void theResponseShouldBe(String tag, String value) {
//        boolean isXml = response.getContentType().contains("xml");
//        String prefix = isXml ? "ApiResponse." : "";
//
//        switch (tag) {
//            case "Success", "Message" -> {
//                String actual = isXml
//                        ? response.xmlPath().getString(prefix + tag)
//                        : response.jsonPath().getString(tag);
//                Assertions.assertEquals(value, actual, tag + " value does not match.");
//            }
//            case "Data" -> {
//                List<String> names = isXml
//                        ? response.xmlPath().getList(prefix + "Data.Name", String.class)
//                        : response.jsonPath().getList("Data.Name", String.class);
//                Assertions.assertFalse(names.isEmpty(), "No products found in response");
//                names.forEach(n -> assertTrue(n.toLowerCase().contains(value.toLowerCase()),
//                        n + " does not contain " + value));
//            }
//            default -> throw new IllegalArgumentException("Unsupported tag: [" + tag + "]");
//        }
//    }

    // OR BETTER -
    private String extract(String path) {
        return isXml()
                ? response.xmlPath().getString("ApiResponse." + path)
                : response.jsonPath().getString(path);
    }

    private boolean isXml() {
        return response.getContentType().contains("xml");
    }

    @And("the response {string} should be {string}")
    public void theResponseShouldBe(String tag, String value) {
        String path = FIELD_PATHS.get(tag);
        if (path == null) {
            throw new IllegalArgumentException("Unsupported tag: [" + tag + "]");
        }
        boolean isPaginated = queryParams.containsKey("page") || queryParams.containsKey("size");
        if (isPaginated) {
//            Assertions.assertEquals(value, extract("Data.NumberOfElements"), tag + " value does not match.");
            Assertions.assertEquals(value, extract(path), tag + " value does not match.");
        } else {
            Assertions.assertEquals(value, extract(path), tag + " value does not match.");
        }
    }

    @And("the query parameter {string} is set to {string}")
    public void theQueryParameterIsSetTo(String queryParam, String queryParamValue) {
        switch (queryParam) {
            case "productName" -> request.queryParams("productName", queryParamValue);
            case "minimumPrice" -> request.queryParams("min", queryParamValue);
            case "maximumPrice" -> request.queryParams("max", queryParamValue);
            case "maxPrice" -> request.queryParams("maxPrice", queryParamValue);
            case "page" -> request.queryParams("page", queryParamValue);
            case "size" -> request.queryParams("size", queryParamValue);
            default -> throw new IllegalStateException("Unexpected value: " + queryParam);
        }
        queryParams.put(queryParam, queryParamValue);
    }


    @And("every product name in the response Data should contain {string}")
    public void everyProductNameInTheResponseDataShouldContain(String productName) {
        List<String> names = isXml()
                ? response.body().xmlPath().getList("ApiResponse.Data.Name")
                : response.body().jsonPath().getList("Data.Name");
        Assertions.assertFalse(names.isEmpty(), "No products found in response");
        names.forEach(n -> assertTrue(n.toLowerCase().contains(productName.toLowerCase()),
                n + " does not contain " + productName));
    }
}
