package stepDefinitions;

import api.Endpoints;
import context.TestContext;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.http.Method;
import io.restassured.response.Response;
import models.Product;
import org.junit.jupiter.api.Assertions;
import utils.response.reader.ResponseReader;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static utils.response.reader.ResponseReader.readList;

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

    @When("get the product using id {long}")
    public void getTheProductUsingID(long id) {
        Response response = context.getRequest().get(Endpoints.PRODUCT_BY_ID, id);
        context.setResponse(response);
        Assertions.assertEquals(200, response.statusCode());
    }

    @Then("the response status code should be {int}")
    public int the_Response_Status_Code_Should_Be(int expectedStatusCode) {
        int statusCode = context.getResponse().getStatusCode();
        Assertions.assertEquals(expectedStatusCode, statusCode, "Unexpected HTTP status code");
        return statusCode;
    }

    @When("the client sends {string} request to {string}")
    public void sendRequest(String method, String endpoint) {
        Response response = context.getRequest()
                .request(Method.valueOf(method.toUpperCase()), Endpoints.PRODUCTS + endpoint);
        context.setResponse(response);
        context.resetRequest();
    }

    @Given("the path parameter {string} is set to {string}")
    public void setPathParam(String paramName, String paramValue) {
        switch (paramName) {
            case "id":
                context.getRequest().pathParam(paramName, Long.parseLong(paramValue));
                break;
            case "anything":
                context.getRequest().pathParam(paramName, "anything");
                break;
            default:
                context.getRequest().pathParam(paramName, "default");
                break;
        }
    }

    @Given("the request body is a product with name {string} and price {bigdecimal}")
    public void theRequestBodyIsAProductWithNameAndPrice(String name, BigDecimal price) {
        context.setProduct(new Product(name, price));
//        context.setProduct(new Product(name
//                + "_" + UUID.randomUUID().toString().substring(0,3), price));
    }

    @And("the response error for {string} should contain {string}")
    public void theResponseErrorForShouldContain(String error, String value) {
        List<String> strings = readList(context.getResponse(), error);
        assertTrue(strings.contains(value));

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
        switch (paramName) {
            case "id":
                context.getRequest().pathParam(paramName, context.getCreatedProductId());
                break;
            case "anything":
                context.getRequest().pathParam(paramName, "anything");
                break;
            default:
                context.getRequest().pathParam(paramName, "default");
                break;
        }
    }

    @Then("the response message should be {string}")
    public void theResponseMessageShouldBe(String expected) {
        String message = ResponseReader.readResponse(context.getResponse(), "Message");
        Assertions.assertEquals(expected, message, "The response messsage and expected doesn't match.");
    }

    @Given("a product {string} priced {bigdecimal} exists")
    public void productExists(String name, BigDecimal price) {
        Product product = new Product(name
                + "_" + UUID.randomUUID().toString().substring(0, 3), price);
        Response response = context.getApiClient().newRequest()
                .body(product).post(Endpoints.PRODUCTS);
        /* getApiClient().newRequest() for the setup call. The setup uses its own request on
        purpose, so its body doesn’t leak into the request the scenario sends next.
        This is the case where bypassing getRequest() is right*/

        assertThat(response.statusCode()).as("setup: create product")
                .isEqualTo(201);

        Long id = Long.valueOf(ResponseReader.readResponse(response, "Id"));
        context.setCreatedProductId(id);
        context.getRequest().pathParam("id", id);
        context.trackForCleanup(id);
    }
}
