@regression @security
Feature: Security Feature

  Scenario: GET products returns 200 SUCCESS for valid role: user
    Given I am authenticated as "User"
    When the client sends "GET" request to ""
    Then the response status code should be 200

  @negative
  Scenario: GET products returns 401 Authentication required for role: none
    Given I am authenticated as "none"
    When the client sends "GET" request to ""
    Then the response status code should be 401
    And the response message should be "Authentication required"

  @negative
  Scenario: GET products with INVALID password returns 401 Authentication required for role: admin
    Given I am authenticated as "invalid"
    When the client sends "GET" request to ""
    Then the response status code should be 401
    And the response message should be "Authentication required"

  @negative
  Scenario: POST product returns 403 Access denied for role: user
    Given I am authenticated as "user"
    And the request body is a product with name "Cardboard" and price 12.22 with random suffix: true
    And the product is added to request body
    When the client sends "POST" request to ""
    Then the response status code should be 403
    And the response message should be "Access denied"
    And no product with the request name should exist in the database

  @negative
  Scenario: POST product returns 401 Authentication required for role: none
    Given I am authenticated as "none"
    And the request body is a product with name "Cardboard" and price 12.22 with random suffix: true
    And the product is added to request body
    When the client sends "POST" request to ""
    Then the response status code should be 401
    And the response message should be "Authentication required"
    And no product with the request name should exist in the database

  @negative
  Scenario: PUT product returns 403 Access denied for role: user
    Given I am authenticated as "user"
    And a product "Cardboard" priced 12.22 exists
    And the request body is a product with name "Card" and price 2.22 with random suffix: true
    And the product is added to request body
    And the path parameter "id" is set to request
    When the client sends "PUT" request to "/{id}"
    Then the response status code should be 403
    And the response message should be "Access denied"
    And the product "name" in the database should match
    And the product "price" in the database should match

  @negative
  Scenario: PATCH products returns 403 Access denied for role: user
    Given I am authenticated as "user"
    And a product "Cardboard" priced 12.22 exists
    And the request body is a product with only the name "CardboardPatch" plus random suffix
    And the product is added to request body
    And the path parameter "id" is set to request
    When the client sends "PATCH" request to "/{id}"
    Then the response status code should be 403
    And the response message should be "Access denied"
    And the product "name" in the database should match
    And the product "price" in the database should match

  @negative
  Scenario: PATCH products price returns 403 Access denied for role: user
    Given I am authenticated as "user"
    And a product "Cardboard" priced 12.22 exists
    And the request body is a product with only the price 4.44
    And the product is added to request body
    And the path parameter "id" is set to request
    When the client sends "PATCH" request to "/{id}"
    Then the response status code should be 403
    And the response message should be "Access denied"
    And the product "name" in the database should match
    And the product "price" in the database should match

  Scenario: DELETE a produc returns 403 Access denied for role: user
    Given I am authenticated as "user"
    And a product "Cardboard" priced 12.22 exists
    And the path parameter "id" is set to request
    When the client sends "DELETE" request to "/{id}"
    Then the response status code should be 403
    And the response message should be "Access denied"
    And the new product should exist in the database

    Scenario: DELETE a product using ID which doesn't exists returs 403 for role: user
      Given I am authenticated as "User"
      And a product "Cardboard" priced 16.56 exists
      And the path parameter id is set to "3999"
      When the client sends "DELETE" request to "/{id}"
      Then the response status code should be 403
      And the response message should be "Access denied"
      And the new product should exist in the database
      
