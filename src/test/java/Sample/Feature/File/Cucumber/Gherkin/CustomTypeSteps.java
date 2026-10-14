package Sample.Feature.File.Cucumber.Gherkin;

import io.cucumber.java.ParameterType;

public class CustomTypeSteps {


    // Define the custom parameter type
    @ParameterType("true|false")
    public boolean booleanValue(String value){
        return Boolean.parseBoolean(value);
    }

    // Note: If you name the method boolean (which is a reserved keyword in Java), use an
    // explicit name in the annotation parameter:
    // But Choose one only
    @ParameterType(name = "booleanTwo", value = "true|false")
    public Boolean booleanType(String value) {
        return Boolean.parseBoolean(value);
    }
}
