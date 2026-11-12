@regression
Feature: Product Feature

  Scenario: Get All Product
    When the client sends "GET" request to ""
    Then the response status code should be 200

  Scenario: Create a new Product using POST Request
    Given the request body is a product with name "Door Lock" plus random suffix and price 7.45
    And product is added to request body
    When the client sends "POST" request to ""
    And the product Id is remembered
    Then the response status code should be 201
    And the product with id of new product should exist in the database

  Scenario: GET Product details using products ID
    When the path parameter id is set to "4"
    When the client sends "GET" request to "/{id}"
    Then the response status code should be 200
    And the response "Message" should be "Products fetched successfully"
    And the response "Name" should be "HD Monitor 24in"
    And the response price should be 140.00

  Scenario: DELETE a product after creating a new product
    Given a product "Flashlight" priced 2.30 exists
    And the path parameter "id" is set to request
    When the client sends "DELETE" request to "/{id}"
    Then the response status code should be 200
    And the response message should be "Product Deleted Successfully"
    And the product should no longer exist in the database

  Scenario: PATCH a product after creating a new product
    Given a product "Flashlight" priced 2.30 exists
    And the request body is a product with name "PieTorch" plus random suffix and price 5.36
    And product is added to request body
    And the path parameter "id" is set to request
    When the client sends "PATCH" request to "/{id}"
    Then the response status code should be 200
    And the product "price" in the database should match
    And the product "name" in the database should match

  Scenario Outline: Update a product Using PUT after creating a new product
    Given a product "Flashlight" priced 5.30 exists
    And the request body is a product with name "Flashlight" plus random suffix and price 56.00
    And product is added to request body
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

  Scenario Outline: Get all products detail within min and max price limit
    Given the query parameter "minimumPrice" is set to "<minimumPrice>"
    And the query parameter "maximumPrice" is set to "<maximumPrice>"
    When the client sends "GET" request to "/range"
    Then the response status code should be 200
    Examples:
      | minimumPrice | maximumPrice |
      | 150          | 200          |

  Scenario Outline: Get all products detail within max price limit
    And the query parameter "maxPrice" is set to "<MaxPrice>"
    When the client sends "GET" request to "/filter"
    Then the response status code should be 200
    Examples:
      | MaxPrice |
      | 20       |

  Scenario Outline: Get all products detail Using Pagination
    And the query parameter "page" is set to "<Page>"
    And the query parameter "size" is set to "<Size>"
    When the client sends "GET" request to "/paged"
    Then the response status code should be 200
    Then the response "Message" should be "<Message>"
    Then the response "Size" should be "<Size>"
    Examples:
      | Page | Size | Message              |
      | 0    | 9    | Operation successful |

  Scenario Outline: Get an existing products detail by Products_name
    And the query parameter "productName" is set to "Adjustable Aluminum Laptop Stand"
    When the client sends "GET" request to "/search"
    Then the response status code should be 200
    And the response "Success" should be "<Success>"
    And every product name in the response Data should contain "<ProductName>"
    Examples:
      | Success | ProductName                      |
      | true    | Adjustable Aluminum Laptop Stand |

    # ----------------------------- Validations --------------------------------
  Scenario Outline: Validation GET after deleting using ID should fail with 404 Not Found
    Given a product "Delete" priced 23.12 exists
    And the path parameter "id" is set to request
    When the client sends "DELETE" request to "/{id}"
    Then the response status code should be 200
    Given the path parameter "id" is set to request
    When the client sends "GET" request to "/{id}"
    Then the response status code should be <StatusCode>
    And the response error for "Errors" should contain "<Errors>"
    And the response error for "Message" should contain "<Message>"
    Examples:
      | StatusCode | Message   | Errors                    |
      | 404        | Not Found | Product not found with id |


  Scenario Outline: Cannot delete the whole collection
    When the client sends "DELETE" request to ""
    Then the response status code should be <StatusCode>
    And the response error for "Errors" should contain "<Errors>"
    And the response error for "Message" should contain "<Message>"
    Examples:
      | StatusCode | Message            | Errors                            |
      | 405        | Method Not Allowed | Method 'DELETE' is not supported. |


  Scenario: POST Reject a product with blank name and price
    Given the request body is a product with name "" and price 0.00
    And product is added to request body
    When the client sends "POST" request to ""
    Then the response status code should be 400
    And the response error for "Errors.Name" should contain "Product name is required and cannot be blank"
    And the response error for "Errors.Name" should contain "Product name must be between 2 and 100 characters"
    And the response error for "Errors.Price" should contain "Price must be greater than zero"


  Scenario: Adding a product that already exists give validation A product
  with name 'PRODUCT_NAME' already exists
    Given a product "Bottle" priced 12.22 exists
    And product is added to request body
    When the client sends "POST" request to ""
    Then the response status code should be 409
    Then the response error for "Message" should contain "Conflict"
    Then the response error for "Errors" should contain "already exists."


  Scenario Outline: Validation PATCH with a malformed body returns 400 Bad Request with
  Malformed request body
    Given the path parameter id is set to "76"
    When the client sends "PATCH" request to "/{id}"
    Then the response status code should be <StatusCode>
    And the response error for "Message" should contain "<Message>"
    And the response error for "Errors" should contain "<Errors>"
    Examples:
      | StatusCode | Message     | Errors                 |
      | 400        | Bad Request | Malformed request body |


  Scenario Outline: Validation PATCHing an existing product's data with empty request boody
  returns 400,"Bad Request","At least one of ‘name’ or ‘price’ must be provided"
    Given the path parameter id is set to "76"
    And the empty product is created
    And product is added to request body
    When the client sends "PATCH" request to "/{id}"
    Then the response status code should be <StatusCode>
    And the response error for "Message" should contain "<Message>"
    And the response error for "Errors" should contain "<Errors>"
    Examples:
      | StatusCode | Message     | Errors                                             |
      | 400        | Bad Request | At least one of ‘name’ or ‘price’ must be provided |


  Scenario Outline: Validation Creating a product's data with empty request body returns 400
    Given the empty product is created
    And product is added to request body
    When the client sends "POST" request to ""
    Then the response status code should be <StatusCode>
    And the response error for "Message" should contain "<Message>"
    And the response error for "Errors.Name" should contain "<ErrorsName>"
    And the response error for "Errors.Price" should contain "<ErrorsPrice>"
    Examples:
      | StatusCode | Message           | ErrorsName                                   | ErrorsPrice       |
      | 400        | Validation failed | Product name is required and cannot be blank | Price is required |


  Scenario Outline: Validation Delete a product which doesnt exist
    Given the path parameter id is set to "771231"
    When the client sends "DELETE" request to "/{id}"
    Then the response status code should be 404
    And the response error for "Message" should contain "<Message>"
    And the response error for "Errors" should contain "<Errors>"
    Examples:
      | Message   | Errors                                           |
      | Not Found | Cannot delete: Product not found with id: 771231 |

  Scenario Outline: Getting a non existing products detail by Products_name throws validation
    And the query parameter "productName" is set to "adas"
    When the client sends "GET" request to "/search"
    Then the response status code should be 200
    And the response "Success" should be "<Success>"
    And the response "Message" should be "<Message>"
    Examples:
      | Success | Message                                     |
      | true    | No Product(s) Found matching keyword 'adas' |


  Scenario Outline: Validation when min price is greater than max price limit
    And the query parameter "minimumPrice" is set to "<minimumPrice>"
    And the query parameter "maximumPrice" is set to "<maximumPrice>"
    When the client sends "GET" request to "/range"
    And the response error for "Message" should contain "<Message>"
    And the response error for "Errors" should contain "<Errors>"
    Examples:
      | minimumPrice | maximumPrice | Message     | Errors                                       |
      | 150          | 90           | Bad Request | Min price must not be greater than Max price |


  Scenario Outline: Validation when max price is not present when finding product within min and max price limit
    And the query parameter "minimumPrice" is set to "<minimumPrice>"
    When the client sends "GET" request to "/range"
    Then the response status code should be <StatusCode>
    And the response "Message" should be "<Message>"
    And the response error for "Message" should contain "<Message>"
    And the response error for "Errors" should contain "<Errors>"
    Examples:
      | minimumPrice | StatusCode | Message     | Errors                                   |
      | 150          | 400        | Bad Request | Required parameter 'max' is not present. |


  Scenario Outline: Validation Invalid - String ID
    Given the path parameter id is set to "asd"
    When the client sends "GET" request to "/{id}"
    Then the response status code should be <StatusCode>
    And the response "Message" should be "<Message>"
    And the response "Errors" should be "<Errors>"
    Examples:
      | StatusCode | Message     | Errors                                   |
      | 400        | Bad Request | Failed to convert 'id' with value: 'asd' |


  Scenario Outline: Validation Invalid - Sort property foo
    When the client sends "GET" request to "/paged?sort=foo"
    Then the response status code should be <StatusCode>
    And the response "Message" should be "<Message>"
    And the response "Errors" should be "<Errors>"
    Examples:
      | StatusCode | Message     | Errors                     |
      | 400        | Bad Request | Invalid Sort property: foo |

  Scenario Outline: Validation Invalid URI gives message Resource Not Found
    When the client sends "GET" request to "/paged/sort"
    Then the response status code should be <StatusCode>
    And the response "Message" should be "<Message>"
    And the response "Errors" should be "<Errors>"
    Examples:
      | StatusCode | Message            | Errors                                      |
      | 404        | Resource Not Found | No static resource api/products/paged/sort. |