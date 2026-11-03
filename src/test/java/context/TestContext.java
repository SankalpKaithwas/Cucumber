package context;

import api.ApiClient;
import api.Endpoints;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import models.Product;
import utils.DatabaseHelper.DatabaseHelper;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*Today all the GIVEN WHEN THEN AND ETC of this lives as fields inside the one big ProductStepDefinitions
class (response, ids, original name for restore, and so on). That works while there is one class.
 The moment you split the steps into an API class and a DB class (which is the goal), they can’t
 see each other’s fields.
TestContext is the answer: one small object that holds the scenario’s state, and every step class
receives the same instance.
PicoContainer creates a fresh TestContext for every scenario and discards it afterwards.

context: A shared scenario context object (typically injected via dependency injection like
PicoContainer or Spring). It carries state between steps.*/

public class TestContext {

    private ApiClient apiClient;
    private DatabaseHelper databaseHelper;

    private Product product;
    private Response response;
    private Long productId;
    private final List<Long> createdIds = new ArrayList<>();

    private Long createdProductId;
    private Long existingProductId;
    private String originalName;
    private BigDecimal originalPrice;
    private final Map<String, String> queryParams = new HashMap<>();

    private final Map<String, String> productDetails = new HashMap<>();
    private RequestSpecification request;

    public RequestSpecification getRequest() {
        if (request == null) {
            request = getApiClient().newRequest();
        }
        return request;
    }

    public void resetRequest() {
        request = null;
    }

    /*Each scenario gets its own TestContext, so each gets its own ApiClient.
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

    public Response getResponse() {
        return response;
    }

    public void setResponse(Response response) {
        this.response = response;
    }

    public long getProductId() {
        return productId;
    }

    public void setProductId(long productId) {
        this.productId = productId;
    }

    public String getOriginalName() {
        return originalName;
    }

    public void setOriginalName(String originalName) {
        this.originalName = originalName;
    }

    public BigDecimal getOriginalPrice() {
        return originalPrice;
    }

    public void setOriginalPrice(BigDecimal originalPrice) {
        this.originalPrice = originalPrice;
    }

    public Long getCreatedProductId() {
        return createdProductId;
    }

    public void setCreatedProductId(Long createdProductId) {
        this.createdProductId = createdProductId;
    }

    public Long getExistingProductId() {
        return existingProductId;
    }

    public void setExistingProductId(Long existingProductId) {
        this.existingProductId = existingProductId;
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
                int status = getApiClient().newRequest()
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
        response = null;
        createdProductId = null;
        existingProductId = null;
        originalName = "";
        originalPrice = BigDecimal.ZERO;
        productDetails.clear();
        queryParams.clear();

    }
}
