package api;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.config.HttpClientConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.filter.log.LogDetail;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import utils.config.ConfigFileReader;

import static io.restassured.RestAssured.given;


/*ApiClient is the one class that knows how to talk to your API. The step
definitions say what to do (“create a product”), and ApiClient knows how (which URL, headers, verb).

RequestSpecification is REST Assured’s reusable “template” for a request. It holds the settings
every request shares, so you set them once.
final means the field is assigned once, in the constructor, and never replaced.*/


public class ApiClient {

    private final RequestSpecification spec;
//    private final String productsEndpoint = ConfigFileReader.get("products.endpoint");

    public ApiClient() {
        RequestSpecBuilder builder = new RequestSpecBuilder()
                .setBaseUri(ConfigFileReader.get("base.url"))
                .setContentType(ConfigFileReader.get("api.content.type"))
                .setAccept(ConfigFileReader.get("api.accept"))
                .setConfig(RestAssuredConfig.config().httpClient(HttpClientConfig.httpClientConfig()
                        .setParam("http.connection.timeout", 5_000)
                        .setParam("http.socket.timeout", 15_000)));
//These are timeouts in milliseconds. Without them, a hung API can freeze the test
// run for a long time. The first is the limit for connecting, and the second is the
// limit for waiting on a response. 5_000 is just 5000, and the underscore only helps readability.

        if (ConfigFileReader.getBoolean("api.log.enabled")) {
            builder.addFilter(new RequestLoggingFilter(LogDetail.ALL));
            builder.addFilter(new ResponseLoggingFilter(LogDetail.ALL));
        }
         /* Filters are hooks that run on every request. These two print the full request and response.
            It’s a config switch, so you set api.log.enabled=true when debugging and false
            otherwise, with no code change.   */

        // Authentication would go here, e.g. builder.addHeader("Authorization", "Bearer " + token);
        this.spec = builder.build();
        // .build() is what turns the builder into the finished spec.
    }

    /*------------------- Another way ---------------- */
    //     private final RequestSpecification spec = new RequestSpecBuilder()
    //            .setBaseUri(Config.get("api.baseUrl"))
    //            .setContentType(ContentType.JSON)
    //            .setAccept(ContentType.JSON).build();

    /* METHODS */
    //Generic
    public RequestSpecification newRequest() {
        return given().spec(spec);
    }


    /* NOTES ->
    1. given().spec(spec) starts a request that already includes everything from the template.
    2. .body(...) adds the payload for this call only.
    3. .when().get(...) is the actual call. get, post, put, patch and delete are the five methods.
    4. {id} with id as the second argument: REST Assured substitutes the placeholder, so you never concatenate URLs by
    hand. It also URL-encodes the value.
    5.Return type Response: the method does not assert anything. It hands the response back, and the step definition
    decides what to check. Keep it that way, because the same call can be correct in one scenario and wrong in
    another (a 404 is the expected result of “get a deleted product”).
    6. Object id accepts a Long, an int or a String, which is useful for negative tests like /api/products/abc.
     */

}
