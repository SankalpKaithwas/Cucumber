@regression
Feature: PATCH Products

  Scenario: PATCH a product after creating a new product
    Given a product "Flashlight" priced 2.30 exists
    And the request body is a product with name "PieTorch" and price 5.36 with random suffix: true
    And the product is added to request body
    And the path parameter "id" is set to request
    When the client sends "PATCH" request to "/{id}"
    Then the response status code should be 200
    And the product "price" in the database should match
    And the product "name" in the database should match

  Scenario Outline: PATCHing Only a products_name should be Success
    Given a product "Cable" priced 5.99 exists
    And the request body is a product with only the name "PatchedCable" plus random suffix
    And the product is added to request body
    And the path parameter "id" is set to request
    When the client sends "PATCH" request to "/{id}"
    Then the response status code should be <StatusCode>
    And the product "price" in the database should match
    And the product "name" in the database should match
    Examples:
      | StatusCode |
      | 200        |

  Scenario Outline: PATCHing Only a products_price should be Success
    Given a product "Cable" priced 5.99 exists
    And the request body is a product with only the price 3.66
    And the product is added to request body
    And the path parameter "id" is set to request
    When the client sends "PATCH" request to "/{id}"
    Then the response status code should be <StatusCode>
    And the product "name" in the database should match
    And the product "price" in the database should match
    Examples:
      | StatusCode |
      | 200        |

  Scenario Outline: PATCH with a malformed body returns 400 Bad Request
    Given a product "Box" priced 0.99 exists
    And the empty product is created
    And the path parameter "id" is set to request
    When the client sends "PATCH" request to "/{id}"
    Then the response status code should be <StatusCode>
    And the response error for "Message" should contain "<Message>"
    And the response error for "Errors" should contain "<Errors>"
    Examples:
      | StatusCode | Message     | Errors                 |
      | 400        | Bad Request | Malformed request body |

  Scenario Outline: PATCH an existing product's data with empty request boody returns 400 Bad Request
    Given a product "Wallpaper" priced 45.78 exists
    And the empty product is created
    And the product is added to request body
    And the path parameter "id" is set to request
    When the client sends "PATCH" request to "/{id}"
    Then the response status code should be <StatusCode>
    And the response error for "Message" should contain "<Message>"
    And the response error for "Errors" should contain "<Errors>"
    Examples:
      | StatusCode | Message     | Errors                                             |
      | 400        | Bad Request | At least one of ‘name’ or ‘price’ must be provided |

  Scenario Outline: PATCH Only a products_name with Single character should return 400
    Given a product "Cable" priced 5.99 exists
    And the request body is a product with only the name "p"
    And the product is added to request body
    And the path parameter "id" is set to request
    When the client sends "PATCH" request to "/{id}"
    Then the response status code should be <StatusCode>
    And the response error for "Errors.Name" should contain "<Errors>"
    And the product "price" in the database should match
    And the product "name" in the database should match
    Examples:
      | StatusCode | Errors                                            |
      | 400        | Product name must be between 2 and 100 characters |

  Scenario Outline: PATCH Only a products_price with invalid price should return 400
    Given a product "Cable" priced 5.99 exists
    And the request body is a product with only the price <Price>
    And the product is added to request body
    And the path parameter "id" is set to request
    When the client sends "PATCH" request to "/{id}"
    Then the response status code should be 400
    And the response error for "Errors.Price" should contain "<Message>"
    And the product "name" in the database should match
    And the product "price" in the database should match
    Examples:
      | Price | Message                         |
      | 0.00  | Price must be greater than zero |
      | -1.00 | Price must be greater than zero |
      | -0.01 | Price must be greater than zero |

  Scenario: PATCH a product which doesnt exists fails with Not Found
    Given the request body is a product with name "Patch" and price 8.00 with random suffix: true
    And the product is added to request body
    And the path parameter id is set to "90123"
    When the client sends "PATCH" request to "/{id}"
    Then the response status code should be 404
    And the response "Message" should be "Not Found"
    And the response error for "Errors" should contain "Product not found with id: 90123"

  Scenario: PATCH to an existing product’s name returns 409
    Given a product "Bottle" priced 12.22 exists
    And an existing product is added to the request
    And the product is added to request body
    And the path parameter "id" is set to request
    When the client sends "PATCH" request to "/{id}"
    Then the response status code should be 409
    And the response error for "Message" should contain "Conflict"
    And the response error for "Errors" should contain "already exists"
    And the product "name" in the database should match
