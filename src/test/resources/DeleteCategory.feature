Feature: Delete Category
  As a user, I want to delete a category that is no longer needed to simplify my categorization system.

  Background:
    Given the API server is running and available

    Given the following categories exist in the system:
      | categoryName   |
      | Old Course     |
      | New Course     |

  Scenario Outline: Normal Flow - Delete an existing category
    When a user deletes the category "<categoryName>"
    Then the category "<categoryName>" should be removed from the system
    And the number of categories in the system is "<expectedCategoryCount>"
    Then the system is restored to the original state

    Examples:
      | categoryName | expectedCategoryCount |
      | Old Course   | 1                     |

  Scenario Outline: Alternate Flow - A user attempts to delete a category that has already been deleted
    Given the category "<categoryName>" is already deleted
    When a user deletes the category "<categoryName>"
    Then the status code returned by the API is "<statusCode>"
    Then the system is restored to the original state

    Examples:
      | categoryName    | statusCode |
      | Old Course      | 404        |

  Scenario Outline: Error Flow - Attempt to delete a category with an invalid ID
    When a user attempts to delete the category with an invalid ID "<categoryID>"
    Then an error should be raised
    And the status code returned by the API is "<statusCode>"
    Then the system is restored to the original state

    Examples:
      | categoryID     | statusCode |
      | Shah Rukh Khan | 404        |
      | Invalid ID     | 404        |
