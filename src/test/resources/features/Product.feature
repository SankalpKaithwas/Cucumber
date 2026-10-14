Feature: Product Feature

#  Scenario: Get Product by ID returning XML
#    Given the product API is available at "http://localhost:8080"
#    When the client requests product details with id {int}
#    Then the response status code should be 200
#    And the response XML should have:
#      | Success | true                        |
#      | Message | Products fetched successfully |
#      | Id      | 70                          |
#      | Name    | Water bottle                |
#      | Price   | 5.99                        |


  Scenario Outline: Get Product by ID returning xml response
    Given the product API is available at "http://localhost:8080"
    Given the request header "Accept" is set to "application/xml"
#    When the client sends GET request to "/api/products/3"
    When the client sends GET request to "<endpoint>" for <id>
    And the response XML should have:
      | Success | true                        |
      | Message | Products fetched successfully |
      | Id      | 3                          |
      | Name    | Mechanical Keyboard                |
      | Price   | 110.0                       |
    Examples:
      | id | |endpoint|
      | 3  | | /api/products/      |


  Scenario: Get Product by ID returning json response
    Given the product API is available at "http://localhost:8080"
    Given the request header "Accept" is set to "application/json"
    And the path parameter "id" is set to 1
    When the client sends GET request to "/api/products/{id}"
    And the response JSON should have:
      | Success | true                        |
      | Message | Products fetched successfully |
      | Id      | 1                          |
      | Name    | Keyboard                |
      | Price   | 45.0                       |

    # need to correct
  Scenario: Add Product Request and response returning xml
    Given the product API is available at "http://localhost:8080"
    Given the request header "Content-Type" is set to "application/xml"
    Given the request payload is:
    """
      <Product>
        <name>Sony Bravia</name>
        <price>199.99</price>
      </Product>
    """
    When the client sends POST request to "/api/products"
    And the response XML should have:
      | Success | true                        |
      | Message | Product Created Succesfully |
      | Name    | Sony Bravia              |
      | Price   |  199.99                      |

  Scenario: Add Product Request and response returning JSON
    Given the product API is available at "http://localhost:8080"
    Given the request header "Content-Type" is set to "application/json"
    Given the request payload is:
                                """
                                  {
                                    "name": "Black Belt",
                                    "price": 25.50
                                  }
                                  """
    When the client sends POST request to "/api/products"
    And the returned product id should match the database record for product "Black Belt"
    And the response JSON should have:
      | Success | true                        |
      | Message | Product Created Succesfully |
      | Name    | Black Belt                |
      | Price   | 25.5                       |
