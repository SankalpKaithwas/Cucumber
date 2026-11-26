package api;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.config.EncoderConfig;
import io.restassured.config.HttpClientConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.filter.log.LogDetail;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.specification.RequestSpecification;
import utils.config.ConfigFileReader;

import java.nio.charset.StandardCharsets;

import static io.restassured.RestAssured.given;


/* ApiClient is the one class that knows how to talk to your API. The step
definitions say what to do (“create a product”), and ApiClient knows how (which URL, headers, verb).

RequestSpecification is REST Assured’s reusable “template” for a request. It holds the settings
every request shares, so you set them once.
final means the field is assigned once, in the constructor, and never replaced.*/


public class ApiClient {

    private final RequestSpecification spec;

    public ApiClient() {
        RequestSpecBuilder builder = new RequestSpecBuilder()
                .setBaseUri(ConfigFileReader.get("base.url"))
                .setContentType(ConfigFileReader.get("api.content.type"))
                .setAccept(ConfigFileReader.get("api.accept"))
                .setConfig(RestAssuredConfig.config().httpClient(HttpClientConfig.httpClientConfig()
                                .setParam("http.connection.timeout", 5_000)
                                .setParam("http.socket.timeout", 15_000))
                        .encoderConfig(EncoderConfig.encoderConfig()
                                .defaultContentCharset(StandardCharsets.UTF_8.name())));
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

    /* METHODS */
    //Generic
    /* Key point: preemptive means the Authorization header goes out with the first request.
     The default, non-preemptive mode sends the request without credentials first, waits for a 401
     with WWW-Authenticate, and only then retries. That doubles the traffic and, for write
     requests, adds a confusing extra round trip. With preemptive you we see exactly one request */

    public RequestSpecification newRequest(String role) {
        String normalizedRole = role.toLowerCase();
        RequestSpecification baseRequest = given().spec(spec);
        return switch (normalizedRole) {
            case "user", "admin" -> baseRequest.auth().preemptive().basic(
                    ConfigFileReader.get(normalizedRole + ".username"),
                    ConfigFileReader.get(normalizedRole + ".password"));

            case "invalid" -> baseRequest.auth().preemptive().basic(
                    ConfigFileReader.get("admin.username"),
                    "WrongPasswordXYZ");

            case "none" -> baseRequest;
            default -> throw new IllegalArgumentException("Unknown role: " + role);
        };
    }
}