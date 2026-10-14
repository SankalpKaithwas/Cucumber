package Sample.Feature.File.Cucumber.Gherkin;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.PendingException;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SampleGherkinExpressionStepDefinitions {

    List<Map<String, String>> listOfItem = new ArrayList<Map<String, String>>();
    private int itemCount = 0;

    @Given("the order maximum limit is only {int}")
    public void theOrderMaximumLimitIsOnly(int arg0) {
        itemCount = arg0;
    }

    @Given("user {string} has {int} items in the cart")
    public void userHasItemsInTheCart(String username, int itemCount) {
        System.out.println("User: " + username + ", Count: " + itemCount);
    }

    // {double} or {float} matches decimal numbers
    @When("the item price is {double} with a tax rate of {double}")
    public void the_item_price_is_with_a_tax_rate_of(double price, double taxRate) {
        System.out.println("Price: " + price + ", Tax: " + taxRate);
    }

    // {boolean} matches true/false. you can use regex alternatives directly in your step definition to match specific words without a custom type:
    //Then the user is (admin|user)
//    @Then("^the active status should be either of value (true|false)$")
//    public void the_active_status_should_be(boolean isActive) {
//        System.out.println("Status: " + isActive);
//    }

    // OR
    // {booleanValue} matches true/false, Using custom parameter type - CustomTypeSteps.java
//    @Then("the active status should be {booleanValue}")
//    public void the_active_status_should_be_true_or_false(boolean isActive) {
//        System.out.println("Status: " + isActive);
//    }

    // OR
    // {booleanTwo} matches true/false, Using custom parameter type - CustomTypeSteps.java
    @Then("the active status should be {booleanTwo} using annotation")
    public void the_active_status_should_be_true_or_false_using_name_annotation(boolean isActive) {
        System.out.println("Status: " + isActive);
    }

    // Accept as {string} or {word} and Parse Manually
    @Then("the active status should be {string}")
    public void the_active_status_should_be(String status) {
        boolean isActive = Boolean.parseBoolean(status);
        System.out.println("Status: " + isActive);
    }

    // ^ and $ anchor the regex
    @Given("^user \"([^\"]*)\" has (\\d+) items priced at (\\d+\\.\\d+) rs as regular expression$")
    public void user_has_items(String name, int count, double price) {
        if (itemCount > count) {
            System.out.println("Name = " + name + " ,Count = " + count + " ,price = " + price);
        } else {
            System.out.println("Count should be equal To or smaller than - " + itemCount);
        }
    }


    @When("user adds the following product:")
    public void user_adds_product(DataTable dataTable) {
        List<Map<String, String>> rows = dataTable.asMaps(String.class, String.class);
        listOfItem.addAll(rows);
        for (Map<String, String> columns : rows) {
            String name = columns.get("name");
            double price = Double.parseDouble(columns.get("price"));
            int quantity = Integer.parseInt(columns.get("quantity"));
            boolean inStock = Boolean.parseBoolean(columns.get("inStock"));
//            System.out.println("----".repeat(10));
//            System.out.println("Name = " + name + " ,quantity = " + quantity + " ,price = " + price
//            + ",inStock = " + inStock);

        }
    }

    @Then("display list of data")
    public void displayListOfData() {
        for (Map<String, String> stringStringMap : listOfItem) {
            System.out.println("----".repeat(10));
            System.out.println(stringStringMap.get("name"));
            System.out.println(stringStringMap.get("quantity"));
            System.out.println(stringStringMap.get("price"));
            System.out.println(stringStringMap.get("inStock"));
        }

    }


}
