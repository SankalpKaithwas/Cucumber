package utils.response.reader;

import io.restassured.response.Response;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static java.util.Map.entry;

public class ResponseReader {


    public record Paths(String xml, String json) {
    }

    private static final Map<String, Paths> FIELDS = Map.ofEntries(
            entry("Success", new Paths("ApiResponse.Success", "Success")),
            entry("Message", new Paths("ApiResponse.Message","Message" )),
            entry("StatusCode", new Paths("ApiResponse.StatusCode","StatusCode" )),
            entry("Id", new Paths("ApiResponse.Data.Id","Data.Id" )),
            entry("Name", new Paths("ApiResponse.Data.Name","Data.Name" )),
            entry("Price", new Paths("ApiResponse.Data.Price","Data.Price" )),
            entry("Errors", new Paths("ErrorResponse.Errors","Errors")),
            entry("Errors.Name", new Paths("ErrorResponse.Errors.Name","Errors.Name")),
            entry("Errors.Price", new Paths("ErrorResponse.Errors.Price","Errors.Price")));

    public static String readResponse(Response response, String field) {
        Paths path = lookup(field);
        return isXml(response)
                ? response.xmlPath().getString(path.xml())
                : response.jsonPath().getString(path.json());
    }

    public static List<String> readList(Response response, String field) {
        Paths paths = lookup(field);   // the same lookup-or-throw code that read() uses
        return isXml(response)
                ? response.xmlPath().getList(paths.xml())
                : response.jsonPath().getList(paths.json());
    }

    private static Paths lookup(String field) {
        Paths paths = FIELDS.get(field);
        if (paths == null) {
            throw new IllegalArgumentException("Unknown field: " + field);
        }
        return paths;
    }

    private static boolean isXml(Response response) {
        return response.getContentType().contains("xml");
    }

    // wrapper for getting price as BigDecimal
    public static BigDecimal getPrice(Response response) {
        return new BigDecimal(readResponse(response, "Price"));
    }
}
//getString for both formats. For JSON it avoids the ClassCastException you saw, and for
// XML the text is already a string. The cost is that a JSON price may come back as 140.0 rather
// than 140.00. Compare numbers with isEqualByComparingTo()