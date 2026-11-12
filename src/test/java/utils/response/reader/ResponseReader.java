package utils.response.reader;

import io.restassured.response.Response;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static java.util.Map.entry;

public class ResponseReader {


    public record Paths(String xml, String json, boolean multi) {
    }

    private static final Map<String, Paths> FIELDS = Map.ofEntries(
            entry("Success", new Paths("ApiResponse.Success", "Success", false)),
            entry("Message", new Paths("ApiResponse.Message","Message", false )),
            entry("StatusCode", new Paths("ApiResponse.StatusCode","StatusCode", false )),
            entry("Id", new Paths("ApiResponse.Data.Id","Data.Id" ,false)),
            entry("Name", new Paths("ApiResponse.Data.Name","Data.Name" , false)),
            entry("Price", new Paths("ApiResponse.Data.Price","Data.Price" ,false )),
            entry("Size", new Paths("ApiResponse.Data.Page.Size","Data.Page.Size" ,false )),
            entry("Errors", new Paths("ErrorResponse.Errors","Errors", false)),
            entry("Errors.Name", new Paths("ErrorResponse.Errors.Name","Errors.Name", true)),
            entry("Errors.Price", new Paths("ErrorResponse.Errors.Price","Errors.Price", true)));

    public static String readResponse(Response response, String field) {
        Paths path = lookup(field);
        return isXml(response)
                ? response.xmlPath().getString(path.xml())
                : response.jsonPath().getString(path.json());
    }

    public static List<String> readList(Response response, String field) {
        Paths paths = lookup(field);   // the same lookup-or-throw code that read() uses
        List<String> values = isXml(response)
                ? response.xmlPath().getList(paths.xml())
                : response.jsonPath().getList(paths.json());
        return values == null ? List.of() : values;

    }

    private static Paths lookup(String field) {
        Paths paths = FIELDS.get(field);
        if (paths == null) {
            throw new IllegalArgumentException("Unknown field: " + field);
        }
        return paths;
    }

    public static List<String> readAll(Response response, String field) {
        Paths paths = lookup(field);
        if (paths.multi()) {
            return readList(response, field);
        }
        String value = readResponse(response, field);
        return value == null ? List.of() : List.of(value);
    }

    private static boolean isXml(Response response) {
        String contentType = response.getContentType();
        if(contentType != null) {
            return contentType.contains("xml");
        }
        return false;
    }

    // wrapper for getting price as BigDecimal
    public static BigDecimal getPrice(Response response) {
        return new BigDecimal(readResponse(response, "Price"));
    }
}
//getString for both formats. For JSON it avoids the ClassCastException, and for
// XML the text is already a string. The cost is that a JSON price may come back as 140.0 rather
// than 140.00. Compare numbers with isEqualByComparingTo()