Feature: Product Feature

  // Background runs before each Scenario

  Background:
    Given the product API is available at "http://localhost:8080"

#  Scenario: Get Product by ID returning XML
#    Given the product API is available at "http://localhost:8080"
#    When the client requests product details with id 70
#    Then the response status code should be 200
#    And the response XML should have:
#      | Success | true                        |
#      | Message | Products fetched successfully |
#      | Id      | 70                          |
#      | Name    | Water bottle                |
#      | Price   | 5.99                        |


  Scenario Outline: Get Product by ID returning xml response
    Given the request header "Accept" is set to "application/xml"
#    When the client sends GET request to "/api/products/3"
    When the client sends GET request to "<endpoint>" for <id>
    Then the response status code should be 200
    And the response XML should have:
      | Success | true                          |
      | Message | Products fetched successfully |
      | Id      | 3                             |
      | Name    | Mechanical Keyboard           |
      | Price   | 110.0                         |
    Examples:
      | id |  | endpoint       |
      | 3  |  | /api/products/ |


  Scenario: Get Product by ID returning json response
    Given the request header "Accept" is set to "application/json"
    And the path parameter "id" is set to "1"
    When the client sends GET request to "/api/products/{id}"
    Then the response status code should be 200
    And the response JSON should have:
      | Success | true                          |
      | Message | Products fetched successfully |
      | Id      | 1                             |
      | Name    | Keyboard                      |
      | Price   | 45.0                          |


  Scenario: Add Product Request and response returning xml
    Given the request header "Content-Type" is set to "application/xml"
    Given the request header "Accept" is set to "application/xml"
    Given the xml request payload is:
    """
      <Product>
        <name>Oneplus 1</name>
        <price>699.99</price>
      </Product>
    """
    When the client sends POST request to "/api/products"
    Then the response status code should be 200
    And the returned product id should match the database record for product "Oneplus 1"
    And the response XML should have:
      | Success | true                         |
      | Message | Product Created Successfully |
      | Name    | Oneplus 1                    |
      | Price   | 699.99                       |

  Scenario: Add Product Request and response returning JSON
    Given the request header "Content-Type" is set to "application/json"
    Given the Json request payload is:
                                """
                                  {
                                    "name": "Carpet",
                                    "price": 2.50
                                  }
                                  """
    When the client sends POST request to "/api/products"
    Then the response status code should be 200
    And the returned product id should match the database record for product "Carpet"
    And the response JSON should have:
      | Success | true                         |
      | Message | Product Created Successfully |
      | Name    | Carpet                       |
      | Price   | 2.5                          |


  Scenario: Delete a product using Xml DELETE request after Post Request.(Full Workflow)
    Given the request header "Accept" is set to "application/xml"
    Given the xml request payload is:
                """
                  <Product>
                    <name>Oneplus 2</name>
                    <price>639.99</price>
                  </Product>
                """
    When the client sends POST request to "api/products"
    Then the response status code should be 201
    Then the product id is saved from the response
    Given the product API is available at "http://localhost:8080"
    Given the request header "Accept" is set to "application/xml"
    And the path parameter "id" is set to the created product id
    Given the client sends DELETE request to "api/products/{id}"
    Then the response status code should be 200
    And the response XML should have:
      | Success | true                         |
      | Message | Product Deleted Successfully |
    And the product should no longer exist in the database
# Why the second Given the product API is available at... is needed ? ->
# Your this.request object keeps everything set on it. Without that second Given,
# the DELETE would still carry the JSON/XML body and the Content-Type header from the POST.
# That step runs setupBaseRequest again, which builds a fresh request.
# Fixing it this way needs no new code.
#  Resolution is to create new request "Given A new request is prepared" (json delete)
# Or use both -
#    Given the product API is available at "http://localhost:8080"
#    Given the request header "Accept" is set to "application/xml"

  Scenario: Delete a product using Json DELETE request after Post Request.(Full Workflow)
    Given the Json request payload is:
                """
                  {
                    "name":"Oneplus 5",
                    "price":639.99
                  }
                """
    When the client sends POST request to "api/products"
    Then the response status code should be 201
    Then the product id is saved from the response
    Given A new request is prepared
    Given the request header "Accept" is set to "application/json"
    And the path parameter "id" is set to the created product id
    Given the client sends DELETE request to "api/products/{id}"
    Then the response status code should be 200
    And the response JSON should have:
      | Success | true                         |
      | Message | Product Deleted Successfully |
    And the product should no longer exist in the database

  Scenario: Delete an existing product using Xml request
    Given the request header "Accept" is set to "application/xml"
    And the path parameter "id" is set to "84"
    When the client sends DELETE request to "api/products/{id}"
    Then the response status code should be 200
    And the response XML should have:
      | Success | true                         |
      | Message | Product Deleted Successfully |

  Scenario: Delete an existing product using Json request
    And the path parameter "id" is set to "77"
    When the client sends DELETE request to "api/products/{id}"
    Then the response status code should be 200
    And the response JSON should have:
      | Success | true                         |
      | Message | Product Deleted Successfully |

  @restoreProduct
  Scenario: PATCH an existing product's name and price with new values using xml request.
    Given the existing product with id 76 is remembered
#    Given the request header "Accept" is set to "application/xml"
    And the path parameter "id" is set to "76"
    Given the xml request payload is:
                """
                  <Product>
                    <name>XMLPatched</name>
                    <Price>12</Price>
                  </Product>
                """
    When the client sends "PATCH" request to "api/products/{id}"
    Then the response status code should be 200
    And the product "name" in the database should be "XMLPatched"
    And the product "price" in the database should be "12"
#    And the product "price" in the database should be unchanged

  @restoreProduct
  Scenario: PATCH an existing product's name with new name using JSON request.
    Given the existing product with id 76 is remembered
#    Given the request header "Accept" is set to "application/xml"
    And the path parameter "id" is set to "76"
    Given the Json request payload is:
                """
                  {
                    "name":"JsonPatch"
                  }
                """
    When the client sends "PATCH" request to "api/products/{id}"
    Then the response status code should be 200
    And the product "name" in the database should be "JsonPatch"
    And the product "price" in the database should be unchanged


  Scenario Outline: Validation PATCH with a malformed JSON body returns 400,"Bad Request",
  "Malformed request body"
    Given the request header "Content-Type" is set to "application/json"
    And the path parameter "id" is set to "76"
    And the Json request payload is:
    """
    { "name": "as", "price": }
    """
    When the client sends "PATCH" request to "api/products/{id}"
    Then the response status code should be <StatusCode>
    Then the response Message should be "<Message>"
    Then the response Errors should be "<Errors>"
    Examples:
      | StatusCode | Message     | Errors                 |
      | 400        | Bad Request | Malformed request body |

  Scenario Outline: Validation PATCHing an existing product's data with empty request boody using JSON
  request returns 400,"Bad Request","At least one of ‘name’ or ‘price’ must be provided"
    Given the existing product with id 76 is remembered
    And the path parameter "id" is set to "76"
    Given the Json request payload is:
                """
                  {
                  }
                """
    When the client sends "PATCH" request to "api/products/{id}"
    Then the response status code should be <StatusCode>
    Then the response Message should be "<Message>"
    Then the response Errors should be "<Errors>"
    Examples:
      | StatusCode | Message     | Errors                                             |
      | 400        | Bad Request | At least one of ‘name’ or ‘price’ must be provided |


  Scenario: Validation Delete a product which doesnt exist - JSON
    And the path parameter "id" is set to "771231"
    When the client sends DELETE request to "api/products/{id}"
    Then the response status code should be 404
    And the response JSON should have:
      | Message | Not Found                                        |
      | Errors  | Cannot delete: Product not found with id: 771231 |

  Scenario: Validation Delete a product which doesnt exist - XML
    And the path parameter "id" is set to "771231"
    When the client sends DELETE request to "api/products/{id}"
    Then the response status code should be 404
    And the response XML should have:
      | Message | Not Found                                        |
      | Errors  | Cannot delete: Product not found with id: 771231 |

  Scenario Outline: Get an existing products detail by Products_name - JSON response
    Given the request header "Accept" is set to "application/json"
    And the query parameter "keyword" is set to "Monitor"
    When the client sends "GET" request to "api/products/search"
    Then the response status code should be 200
    And the response "Success" should be "<Success>"
#    And the response "Data" should be "<Data>"
#    And the response "Message" should be "<Message>"
    Examples:
      | Success | Data |
      | true    |  Monitor   |

  Scenario Outline: Get an existing products detail by Products_name - XML response
    Given the request header "Accept" is set to "application/xml"
    And the query parameter "keyword" is set to "Single"
    When the client sends "GET" request to "api/products/search"
    Then the response status code should be 200
    And the response "Success" should be "<Success>"
    And the response "Message" should be "<Message>"
    Examples:
      | Success | Message |
      | true    |      |

  Scenario Outline: Validation Get an non existing products detail by Products_name throws validation
    Given the request header "Accept" is set to "application/json"
    And the query parameter "keyword" is set to "adas"
    When the client sends "GET" request to "api/products/search"
    Then the response status code should be 200
    And the response "Success" should be "<Success>"
    And the response "Message" should be "<Message>"
    Examples:
      | Success | Message |
      | true    | No Product(s) Found matching keyword 'adas'|

  Scenario Outline: Get an products detail within min and max price limit
    Given the request header "Accept" is set to "application/json"
    And the query parameter "minPrice" is set to "<MinPrice>"
    And the query parameter "maxPrice" is set to "<MaxPrice>"
    When the client sends "GET" request to "api/products/range"
    Then the response status code should be 200
    Examples:
      | MinPrice | MaxPrice |
      | 150      | 200      |
#    And the response "Tags" should be "Value"


