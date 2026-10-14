Feature: Sample Feature file showing how to use Cucumber Expressions with Gherkin

  Background:
    Given the order maximum limit is only 10

  Scenario: Placing an order with different data types #1
    Given user "Hawkeye" has 3 items in the cart
    When the item price is 499.50 with a tax rate of 18.0
#    Then the active status should be either of value true
    Then the active status should be true using annotation


  Scenario: Placing an order with different data types #2
    Given user "Black Widow" has 6 items in the cart
    When the item price is 4996.50 with a tax rate of 168.0
#    Then the active status should be true
    Then the active status should be true using annotation


  Scenario: Placing an order with different data types #3
    Given user "Loki" has 90 items priced at 55.09 rs as regular expression
    When the item price is 46.50 with a tax rate of 68.0
#    Then the active status should be true
    Then the active status should be true using annotation


  Scenario: Placing an order with different data types using DataTable #4
    When user adds the following product:
      | name     | price  | quantity | inStock |
      | Wireless Mouse | 749.00 | 2        | true    |
      | Mac Mouse | 23349.00 | 6        | false    |
    Then display list of data