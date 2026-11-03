Feature: Product Feature

  Scenario: Get All Product by returning xml response
    When the client sends "GET" request to ""
    Then the response status code should be 200

  Scenario: Create a new Product using POST Request
    Given the request body is a product with name "Door Lock" and price 7.45
    And product is added to request body
    When the client sends "POST" request to ""
    And the product Id is remembered
    Then the response status code should be 201
    And the product with id of new product should exist in the database


  Scenario: Test get using ID
    When the path parameter "id" is set to "4"
    When the client sends "GET" request to "/{id}"
    Then the response status code should be 200

  Scenario: DB check
    When the client sends "GET" request to "/4"
    Then the response status code should be 200
    And the product with id 4 should exist in the database


  Scenario: Test get after deleting using ID should fail with 404 Not Found
    Given the path parameter "id" is set to "139"
    When the client sends "Delete" request to "/{id}"
    Then the response status code should be 200
    Given the path parameter "id" is set to "139"
    When the client sends "GET" request to "/{id}"
    Then the response status code should be 404


  Scenario: Delete a product after creating a new product
    Given a product "Flashlight" priced 2.30 exists
    When the client sends "DELETE" request to "/{id}"
    Then the response status code should be 200
    And the response message should be "Product Deleted Successfully"


  Scenario: Patch a product after creating a new product
    Given a product "Flashlight" priced 2.30 exists
    And the request body is a product with name "PieTorch1" and price 5.36
    And product is added to request body
    When the client sends "PATCH" request to "/{id}"
    Then the response status code should be 200
    And the product "price" in the database should be "5.36"
    And the product "name" in the database should be "PieTorch1"

  Scenario: Update a product Using PUT after creating a new product
    Given a product "Flashlight" priced 5.30 exists
    And the request body is a product with name "FlashlightPUT" and price 56.00
    And product is added to request body
    When the client sends "PUT" request to "/{id}"
    Then the response status code should be 200
    And the product "price" in the database should be "56.00"
    And the product "name" in the database should be "FlashlightPUT"



  Scenario: Cannot delete the whole collection
    When the client sends "DELETE" request to ""
    Then the response status code should be 405

  Scenario: Reject a product with blank name and price
    Given the request body is a product with name "" and price 0.00
    And product is added to request body
    When the client sends "POST" request to ""
    Then the response status code should be 400
    And the response error for "Errors.Name" should contain "Product name is required and cannot be blank"
    And the response error for "Errors.Price" should contain "Price must be greater than zero"