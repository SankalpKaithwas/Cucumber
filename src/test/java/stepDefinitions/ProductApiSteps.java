package stepDefinitions;

import api.Endpoints;
import context.TestContext;
import io.cucumber.java.PendingException;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.http.Method;
import io.restassured.response.Response;
import models.Product;
import org.junit.jupiter.api.Assertions;
import utils.response.reader.ResponseReader;
import utils.utility.Utilities;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static utils.response.reader.ResponseReader.readAll;

public class ProductApiSteps {

// Before each scenario, PicoContainer creates one TestContext and passes it to
// every class that asks for one. SO wherever a TestContext in passed in constructor, pico will pass the one
// already created by it. The constructor parameter is the only thing Pico looks at. No annotation
    // is needed, but the class should have just one public constructor.

    private final TestContext context;

    public ProductApiSteps(TestContext context) {
        this.context = context;
    }
    /*
    The step class declares in its constructor that it needs a TestContext. You never write
    new ProductApiSteps(...).
    The dependency cucumber-picocontainer is what makes it work. Before each
    scenario, it creates one TestContext, then creates every step and hook class and passes that same
    instance to each constructor.
    After the scenario, it throws everything away. The next scenario
    starts with a fresh, empty TestContext.

    So ProductApiSteps, ProductDbSteps and Hooks all hold the same object during one scenario,
    and a different one in the next.

    Two rules make this work:
    The constructor parameter is the only thing PicoContainer looks at. No annotation is needed.
    Don’t use static fields for scenario data. Static fields are shared across all scenarios, which
    brings back the data-leak problem this class exists to prevent.
    */

    @Then("the response status code should be {int}")
    public void the_Response_Status_Code_Should_Be(int expectedStatusCode) {
        int statusCode = context.getResponse().getStatusCode();
        assertThat(statusCode).as("HTTP Status. Body "
                + context.getResponse().asString()).isEqualTo(expectedStatusCode);
    }

    @When("the client sends {string} request to {string}")
    public void sendRequest(String method, String endpoint) {
        Response response = context.getRequest()
                .request(Method.valueOf(method.toUpperCase()), Endpoints.PRODUCTS + endpoint);
        context.setResponse(response);
        context.resetRequest();
    }

    @Given("the request body is a product with name {string} and price {bigdecimal}")
    public void theRequestBodyIsAProductWithNameAndPrice(String name, BigDecimal price) {
        context.setProduct(new Product(name, price));
    }

    @Given("the request body is a product with name {string} plus random suffix and price {bigdecimal}")
    public void theRequestBodyIsAProductWithNamePlusRandomSuffixAndPrice(String name, BigDecimal price) {
        String productName = Utilities.generateRandomName(name);
        context.setProduct(new Product(productName, price));
    }

    @And("the response error for {string} should contain {string}")
    public void theResponseErrorShouldContain(String error, String expected) {
        List<String> errors = readAll(context.getResponse(), error);
//        assertThat(errors).as("errors for " + error).contains(expected); exact match: one of the error entries equals the value

        // anyMatch - substring match: any error entry containing the value
        assertThat(errors).as("error values for " + error)
                .anyMatch(v -> v.contains(expected));
    }

    @And("product is added to request body")
    public void addTheProductToRequest() {
        context.getRequest().body(context.getProduct());
    }

    @Then("the product Id is remembered")
    public void theProductIdIsRemembered() {
        if (context.getResponse().getStatusCode() == 201) {
            String id = ResponseReader.readResponse(context.getResponse(), "Id");
            context.setCreatedProductId(Long.parseLong(id));
            context.trackForCleanup(Long.parseLong(id));
        }
    }


    @Given("the path parameter {string} is set to request")
    public void setPathParamToRequest(String paramName) {
        switch (paramName.toLowerCase()) {
            case "id":
                context.getRequest().pathParam(paramName, context.requireCreatedProductId());
                break;
            default:
                throw new IllegalArgumentException("Invalid path parameter " + paramName);
        }
    }

    @Then("the response message should be {string}")
    public void theResponseMessageShouldBe(String expected) {
        String message = ResponseReader.readResponse(context.getResponse(), "Message");
        Assertions.assertEquals(expected, message, "The response messsage and expected doesn't match.");
    }

    @Given("a product {string} priced {bigdecimal} exists")
    public void productExists(String name, BigDecimal price) {
        String productName = Utilities.generateRandomName(name);
        Product product = new Product(productName, price);
        Response response = context.getApiClient().newRequest()
                .body(product).post(Endpoints.PRODUCTS);
        /* getApiClient().newRequest() for the setup call. The setup uses its own request on
        purpose, so its body doesn’t leak into the request the scenario sends next.
        This is the case where bypassing getRequest() is right*/

        assertThat(response.statusCode()).as("setup: create product ststus code")
                .isEqualTo(201);

        Long id = Long.valueOf(ResponseReader.readResponse(response, "Id"));
        context.setCreatedProductId(id);
        context.setProduct(product);
        context.trackForCleanup(id);
    }

    @And("the query parameter {string} is set to {string}")
    public void theQueryParameterIsSetTo(String queryParam, String queryParamValue) {
        switch (queryParam) {
            case "productName" -> context.getRequest().queryParams("productName", queryParamValue);
            case "minimumPrice" -> context.getRequest().queryParams("min", queryParamValue);
            case "maximumPrice" -> context.getRequest().queryParams("max", queryParamValue);
            case "maxPrice" -> context.getRequest().queryParams("maxPrice", queryParamValue);
            case "page" -> context.getRequest().queryParams("page", queryParamValue);
            case "size" -> context.getRequest().queryParams("size", queryParamValue);
            default -> throw new IllegalStateException("Unexpected value: " + queryParam);
        }
        context.getQueryParams().put(queryParam, queryParamValue);
    }

    @And("the response {string} should be {string}")
    public void theResponseShouldBe(String tag, String value) {
        List<String> responseTag = readAll(context.getResponse(), tag);
        assertThat(responseTag).as("response " + tag).contains(value);
    }

    @And("every product name in the response Data should contain {string}")
    public void everyProductNameInTheResponseDataShouldContain(String productName) {
        List<String> products = ResponseReader.readAll(context.getResponse(), "Name");
        Assertions.assertFalse(products.isEmpty(), "No products found in response");
        products.forEach(n -> assertTrue(n.toLowerCase().contains(productName.toLowerCase()),
                n + " does not contain " + productName));
    }

    @And("the empty product is created")
    public void theEmptyProductIsCreated() {
        context.setProduct(new Product());
    }

    @Given("the path parameter id is set to {string}")
    public void thePathParameterIsSetTo(String parameter) {
        context.getRequest().pathParam("id", parameter);
    }

    @And("the response price should be {bigdecimal}")
    public void theResponsePriceShouldBe(BigDecimal expected) {
        assertThat(ResponseReader.getPrice(context.getResponse()))
                .as("price").isEqualByComparingTo(expected);
    }

    @And("every product price in the response should be between {bigdecimal} and {bigdecimal}")
    public void everyPriceShouldBeBetween(BigDecimal min, BigDecimal max) {
        List<String> prices = ResponseReader.readAll(context.getResponse(), "Price");
        assertThat(prices).as("prices in response").isNotEmpty();
        prices.forEach(p -> assertThat(new BigDecimal(p))
                .as("price " + p).isBetween(min, max));
    }

}
