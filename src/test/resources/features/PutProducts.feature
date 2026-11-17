@regression @put
Feature: PUT Products

  Scenario Outline: Update a product Using PUT after creating a new product
    Given a product "Flashlight" priced 5.30 exists
    And the request body is a product with name "Flashlight" and price 56.00 with random suffix: true
    And the product is added to request body
    And the path parameter "id" is set to request
    When the client sends "PUT" request to "/{id}"
    Then the response status code should be 200
    Then the response "Success" should be "<Success>"
    Then the response "Message" should be "<Message>"
    And the product "price" in the database should match
    And the product "name" in the database should match
    Examples:
      | Success | Message              |
      | true    | Updated Successfully |

       # ----------------------------- Validations --------------------------------
  @negative
  Scenario: PUT and existing product with blank name returns 400
    Given a product "Flashlight" priced 5.30 exists
    And the request body is a product with name "" and price 5.00 with random suffix: false
    And the product is added to request body
    And the path parameter "id" is set to request
    When the client sends "PUT" request to "/{id}"
    Then the response status code should be 400
    And the response "Message" should be "Validation failed"
    And the product "price" in the database should match
    And the product "name" in the database should match

  @negative
  Scenario Outline: PUT an existing product with Invalid price returns 400
    Given a product "Flashlight" priced 5.30 exists
    And the request body is a product with name "PUT" and price <Price> with random suffix: true
    And the product is added to request body
    And the path parameter "id" is set to request
    When the client sends "PUT" request to "/{id}"
    Then the response status code should be 400
    And the response "Message" should be "Validation failed"
    And the response error for "Errors.Price" should contain "<Message>"
    And the product "name" in the database should match
    And the product "price" in the database should match
    Examples:
      | Price | Message                         |
      | 0.00  | Price must be greater than zero |
      | -1.00 | Price must be greater than zero |
      | -0.01 | Price must be greater than zero |

  @negative
  Scenario: PUT to an existing product’s name returns 409
    Given a product "Bottle" priced 12.22 exists
    And an existing product is added to the request
    And the product is added to request body
    And the path parameter "id" is set to request
    When the client sends "PUT" request to "/{id}"
    Then the response status code should be 409
    And the response error for "Message" should contain "Conflict"
    And the response error for "Errors" should contain "already exists."
    And the product "name" in the database should match
    And the product "price" in the database should match