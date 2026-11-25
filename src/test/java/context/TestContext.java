package context;

import api.ApiClient;
import api.Endpoints;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import models.Product;
import utils.DatabaseHelper.DatabaseHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
TestContext is one small object that holds the scenario’s state, and every step class
receives the same instance.
PicoContainer creates a fresh TestContext for every scenario and discards it afterwards.

context: A shared scenario context object (typically injected via dependency injection like
PicoContainer or Spring). It carries state between steps.*/

public class TestContext {

    private ApiClient apiClient;
    private DatabaseHelper databaseHelper;
    private Product product;
    private Response response;
    private final List<Long> createdIds = new ArrayList<>();
    private Product originalProduct;
    private Long createdProductId;
    private String role = "admin";

    private final Map<String, String> queryParams = new HashMap<>();

    private RequestSpecification request;

    public RequestSpecification getRequest() {

        if (request == null) {
            request = getApiClient().newRequest(role);
        }
        return request;
    }

    public void resetRequest() {
        request = null;
    }

    /* Each scenario gets its own TestContext, so each gets its own ApiClient.
    That’s intentional: nothing leaks from one scenario into the next.
     */
    public ApiClient getApiClient() {
        if (apiClient == null) {
            apiClient = new ApiClient();
        }
        return apiClient;
    }

    public DatabaseHelper getDatabaseHelper() {
        if (databaseHelper == null) {
            databaseHelper = new DatabaseHelper();
        }
        return databaseHelper;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public void setOriginalProduct(Product originalProduct) {
        this.originalProduct = originalProduct;
    }

    public Product getOriginalProduct() {
        return originalProduct;
    }

    public Response getResponse() {
        return response;
    }

    public void setResponse(Response response) {
        this.response = response;
    }

    public void setCreatedProductId(Long createdProductId) {
        this.createdProductId = createdProductId;
    }

    public Map<String, String> getQueryParams() {
        return queryParams;
    }

    public void setRole(String role) {
        if(request != null){
            throw new IllegalStateException("Set the role before the request is built");
        }
        this.role = role;
    }

    public long requireCreatedProductId() {
        if (createdProductId == null) {
            throw new IllegalStateException(
                    "No product id captured in this scenario. Create one first with "
                            + "'a product ... exists', or use 'the product Id is remembered'"
                            + " after a successful POST.");
        }
        return createdProductId;
    }

    /**
     * Remember an id so the @After hook can delete the test data again.
     */
    public void trackForCleanup(Long id) {
        if (id != null && !createdIds.contains(id)) {
            createdIds.add(id);
        }
    }

    /* Whenever a scenario creates a product, its id goes into createdIds.
    cleanup() deletes them all afterwards.
    The try/catch is deliberate. If the scenario already deleted the product, the cleanup delete fails
    with a 404, and that must not fail the test.*/
    public void cleanUp() {
        for (Long id : createdIds) {
            try {
                int status = getApiClient().newRequest("admin")
                        .pathParam("id", id)
                        .delete(Endpoints.PRODUCT_BY_ID)
                        .statusCode();
                if (status != 200 && status != 404) {
                    System.out.println("WARNING: cleanup of product " + id + " returned " + status);
                }
            } catch (RuntimeException e) {
                System.out.println("WARNING: cleanup of product " + id + " failed: " + e.getMessage());
            }
        }
        createdIds.clear();
//PicoContainer discards the whole context after the scenario, so
// nulling response and createdProductId achieves nothing.
    }
}
