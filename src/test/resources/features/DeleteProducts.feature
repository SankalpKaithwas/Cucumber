@regression
Feature: DELETE Products

  Scenario: DELETE a product after creating a new product
    Given a product "Flashlight" priced 2.30 exists
    And the path parameter "id" is set to request
    When the client sends "DELETE" request to "/{id}"
    Then the response status code should be 200
    And the response message should be "Product Deleted Successfully"
    And the product should no longer exist in the database

   # ----------------------------- Validations --------------------------------

  Scenario Outline: Cannot delete all the products
    When the client sends "DELETE" request to ""
    Then the response status code should be <StatusCode>
    And the response error for "Errors" should contain "<Errors>"
    And the response error for "Message" should contain "<Message>"
    Examples:
      | StatusCode | Message            | Errors                            |
      | 405        | Method Not Allowed | Method 'DELETE' is not supported. |

  Scenario Outline: DELETE a product which doesnt exist returns 404 Not Found
    Given the path parameter id is set to "771231"
    When the client sends "DELETE" request to "/{id}"
    Then the response status code should be 404
    And the response error for "Message" should contain "<Message>"
    And the response error for "Errors" should contain "<Errors>"
    Examples:
      | Message   | Errors                                           |
      | Not Found | Cannot delete: Product not found with id: 771231 |