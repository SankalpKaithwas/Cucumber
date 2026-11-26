@regression @get
Feature: GET Products

  Scenario: GET All Product
    When the client sends "GET" request to ""
    Then the response status code should be 200

  Scenario Outline: GET Product details using products ID
    Given a product "Adapter" priced <Price> exists
    And the path parameter "id" is set to request
    When the client sends "GET" request to "/{id}"
    Then the response status code should be 200
    And the response "Message" should be "Products fetched successfully"
    And the product "name" in the database should match
    And the response price should be <Price>
    Examples:
      | Price |
      | 7.78  |
      | 14.78 |

  Scenario Outline: GET all products detail within min and max price limit
    Given the query parameter "minimumPrice" is set to "<minimumPrice>"
    And the query parameter "maximumPrice" is set to "<maximumPrice>"
    When the client sends "GET" request to "/range"
    Then the response status code should be 200
    And every product price in the response should be between <minimumPrice> and <maximumPrice>
    Examples:
      | minimumPrice | maximumPrice |
      | 150.00       | 190.00       |

  Scenario Outline: GET all products detail within max price limit
    Given the query parameter "maxPrice" is set to "<MaxPrice>"
    When the client sends "GET" request to "/filter"
    Then the response status code should be 200
    And every product price in the response should be between 0.00 and <MaxPrice>
    Examples:
      | MaxPrice |
      | 20.00    |

  Scenario Outline: GET all products detail Using Pagination
    Given the query parameter "page" is set to "<Page>"
    And the query parameter "size" is set to "<Size>"
    When the client sends "GET" request to "/paged"
    Then the response status code should be 200
    And the response "Message" should be "<Message>"
    And the response "Size" should be "<Size>"
    Examples:
      | Page | Size | Message              |
      | 0    | 9    | Operation successful |

  Scenario Outline: GET an existing products detail by Products_name
    Given the query parameter "productName" is set to "<ProductName>"
    When the client sends "GET" request to "/search"
    Then the response status code should be 200
    And the response "Success" should be "<Success>"
    And every product name in the response Data should contain "<ProductName>"
    Examples:
      | Success | ProductName |
      | true    | Keyboard    |

    # ----------------------------- Validations --------------------------------

  @negative
  Scenario Outline: GET a non existing products detail by Products_name throws validation
    Given the query parameter "productName" is set to "adas"
    When the client sends "GET" request to "/search"
    Then the response status code should be 200
    And the response "Success" should be "<Success>"
    And the response "Message" should be "<Message>"
    Examples:
      | Success | Message                                     |
      | true    | No Product(s) Found matching keyword 'adas' |

  @negative
  Scenario Outline: GET when min price is greater than max price limit returns 400 Bad Request
    Given the query parameter "minimumPrice" is set to "<minimumPrice>"
    And the query parameter "maximumPrice" is set to "<maximumPrice>"
    When the client sends "GET" request to "/range"
    Then the response status code should be 400
    And the response error for "Message" should contain "<Message>"
    And the response error for "Errors" should contain "<Errors>"
    Examples:
      | minimumPrice | maximumPrice | Message     | Errors                                       |
      | 150          | 90           | Bad Request | Min price must not be greater than Max price |

  @negative
  Scenario Outline: GET when max price is not present when finding product within min and max price limit
    Given the query parameter "minimumPrice" is set to "<minimumPrice>"
    When the client sends "GET" request to "/range"
    Then the response status code should be <StatusCode>
    And the response "Message" should be "<Message>"
    And the response error for "Errors" should contain "<Errors>"
    Examples:
      | minimumPrice | StatusCode | Message     | Errors                                   |
      | 150          | 400        | Bad Request | Required parameter 'max' is not present. |

  @negative
  Scenario Outline: GET product details with String ID returns 400 Bad Request
    Given the path parameter id is set to "asd"
    When the client sends "GET" request to "/{id}"
    Then the response status code should be <StatusCode>
    And the response "Message" should be "<Message>"
    And the response "Errors" should be "<Errors>"
    Examples:
      | StatusCode | Message     | Errors                                   |
      | 400        | Bad Request | Failed to convert 'id' with value: 'asd' |

  @negative
  Scenario Outline: GET with Invalid Sort property foo returns 400 Bad Request
    When the client sends "GET" request to "/paged?sort=foo"
    Then the response status code should be <StatusCode>
    And the response "Message" should be "<Message>"
    And the response "Errors" should be "<Errors>"
    Examples:
      | StatusCode | Message     | Errors                     |
      | 400        | Bad Request | Invalid Sort property: foo |

  @negative
  Scenario Outline: GET with Invalid URI returns 404 Resource Not Found
    When the client sends "GET" request to "/paged/sort"
    Then the response status code should be <StatusCode>
    And the response "Message" should be "<Message>"
    And the response "Errors" should be "<Errors>"
    Examples:
      | StatusCode | Message            | Errors                                                                             |
      | 404        | Resource Not Found | No static resource api/products/paged/sort for request '/api/products/paged/sort'. |

  @negative
  Scenario Outline: Validation GET after DELETE using ID should fail with 404 Not Found
    Given a product "Delete" priced 23.12 exists
    And the path parameter "id" is set to request
    When the client sends "DELETE" request to "/{id}"
    Then the response status code should be 200
    And the path parameter "id" is set to request
    When the client sends "GET" request to "/{id}"
    Then the response status code should be <StatusCode>
    And the response error for "Errors" should contain "<Errors>"
    And the response error for "Message" should contain "<Message>"
    Examples:
      | StatusCode | Message   | Errors                    |
      | 404        | Not Found | Product not found with id |