@regression
Feature: POST Products

  Scenario: Create a new Product using POST Request
    Given the request body is a product with name "Door Lock" and price 7.45 with random suffix: true
    And the product is added to request body
    When the client sends "POST" request to ""
    Then the response status code should be 201
    And the product Id is remembered
    And the product with id of new product should exist in the database

   # ----------------------------- Validations --------------------------------
  Scenario: POST Reject a product with blank name and price
    Given the request body is a product with name "" and price 0.00
    And the product is added to request body
    When the client sends "POST" request to ""
    Then the response status code should be 400
    And the response error for "Errors.Name" should contain "Product name is required and cannot be blank"
    And the response error for "Errors.Name" should contain "Product name must be between 2 and 100 characters"
    And the response error for "Errors.Price" should contain "Price must be greater than zero"

  Scenario: POST an already existing Products give 409 Conflict
    Given a product "Bottle" priced 12.22 exists
    And the product is added to request body
    When the client sends "POST" request to ""
    Then the response status code should be 409
    And the response error for "Message" should contain "Conflict"
    And the response error for "Errors" should contain "already exists."
    And the product "name" in the database should match
    And the product "price" in the database should match

  Scenario Outline: POST a product's data with empty request body returns 400 Validation failed
    Given the empty product is created
    And the product is added to request body
    When the client sends "POST" request to ""
    Then the response status code should be <StatusCode>
    And the response error for "Message" should contain "<Message>"
    And the response error for "Errors.Name" should contain "<ErrorsName>"
    And the response error for "Errors.Price" should contain "<ErrorsPrice>"
    Examples:
      | StatusCode | Message           | ErrorsName                                   | ErrorsPrice       |
      | 400        | Validation failed | Product name is required and cannot be blank | Price is required |

  Scenario Outline: POST with an invalid price returns 400
    Given the request body is a product with name "Valid" and price <Price> with random suffix: true
    And the product is added to request body
    When the client sends "POST" request to ""
    Then the response status code should be 400
    And the response error for "Errors.Price" should contain "<Message>"
    Examples:
      | Price | Message                         |
      | 0.00  | Price must be greater than zero |
      | -1.00 | Price must be greater than zero |
      | -0.01 | Price must be greater than zero |

  Scenario Outline: POST with a malformed body returns 400 Bad Request
    Given the empty product is created
    When the client sends "POST" request to ""
    Then the response status code should be <StatusCode>
    And the response error for "Message" should contain "<Message>"
    And the response error for "Errors" should contain "<Errors>"
    Examples:
      | StatusCode | Message     | Errors                 |
      | 400        | Bad Request | Malformed request body |