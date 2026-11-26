@regression @post
Feature: POST Products

  Scenario: Create a new Product using POST Request
    Given the request body is a product with name "Door Lock" and price 7.45 with random suffix: true
    And the product is added to request body
    When the client sends "POST" request to ""
    Then the response status code should be 201
    And the product Id is remembered
    And the new product should exist in the database

  Scenario: POST Request with non-ASCII product name return 201
    Given the request body is a product with name "Nescafé" and price 7.45 with random suffix: true
    And the product is added to request body
    When the client sends "POST" request to ""
    Then the response status code should be 201
    And the product Id is remembered
    And the new product should exist in the database


  Scenario Outline: POST Request with 2 characters and 100 characters product name return 201
    Given the request body is a product with name "<Name>" and price 7.45 with random suffix: true
    And the product is added to request body
    When the client sends "POST" request to ""
    Then the response status code should be 201
    And the product Id is remembered
    And the new product should exist in the database
    Examples:
      | Name                                                                                        |
      | bababadalgharaghtakamminarronnkonnbronntonnerronntuonnthunntrovarrhounawnskawntoohoohoorden |
      | bv                                                                                          |

    Scenario: POST request returns 201 SUCCESS response for role : admin
      Given I am authenticated as "Admin"
      And the request body is a product with name "Umbrella" and price 34.7 with random suffix: true
      And the product is added to request body
      When the client sends "POST" request to ""
      And the product Id is remembered
      Then the response status code should be 201
      And the product "name" in the database should match
      And the product "price" in the database should match

   # ----------------------------- Validations --------------------------------

  @negative
  Scenario: POST Reject a product with blank name and price
    Given the request body is a product with name "" and price 0.00
    And the product is added to request body
    When the client sends "POST" request to ""
    Then the response status code should be 400
    And the response error for "Errors.Name" should contain "Product name is required and cannot be blank"
    And the response error for "Errors.Name" should contain "Product name must be between 2 and 100 characters"
    And the response error for "Errors.Price" should contain "Price must be greater than zero"

  @negative
  Scenario: POST an already existing Products give 409 Conflict
    Given a product "Bottle" priced 12.22 exists
    And the product is added to request body
    When the client sends "POST" request to ""
    Then the response status code should be 409
    And the response error for "Message" should contain "Conflict"
    And the response error for "Errors" should contain "already exists."
    And the product "name" in the database should match
    And the product "price" in the database should match

  @negative
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

  @negative
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

  @negative
  Scenario Outline: POST with a malformed body returns 400 Bad Request
    Given the empty product is created
    When the client sends "POST" request to ""
    Then the response status code should be <StatusCode>
    And the response error for "Message" should contain "<Message>"
    And the response error for "Errors" should contain "<Errors>"
    Examples:
      | StatusCode | Message     | Errors                 |
      | 400        | Bad Request | Malformed request body |

  @negative
  Scenario Outline: POST Request with 1 character and 101 characters product name return 400 Validation failed
    Given the request body is a product with name "<Name>" and price 7.45 with random suffix: false
    And the product is added to request body
    When the client sends "POST" request to ""
    Then the response status code should be 400
    And the response error for "Errors.Name" should contain "<Errors>"
    Examples:
      | Name                                                                                                  | Errors                                            |
      | bababadalgharaghtakamminarronnkonnbronntonnerronntuonnthunntrovarrhounawnskawntoohoohoordenenthurnukT | Product name must be between 2 and 100 characters |
      | b                                                                                                     | Product name must be between 2 and 100 characters |