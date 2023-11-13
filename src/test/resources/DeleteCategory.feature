Feature: Delete Category
  As a user, I want to delete a category that is no longer needed to simplify my categorization system.

  Background:
    Given the API server is running and available

    Given the following categories exist in the system:
      | categoryTitle   | categoryDescription |
      | Old Course      | Completed Class     |
      | New Course      | Will do this class  |

  Scenario Outline: Normal Flow - Delete an existing category
    When a user deletes the category "<categoryTitle>"
    Then the category "<categoryTitle>" should be removed from the system
    Then the number of categories in the system is "<expectedCategoryCount>"

    Examples:
      | categoryTitle | expectedCategoryCount |
      | Old Course    | 1                     |

  Scenario Outline: Alternate Flow - A user attempts to delete a category that has already been deleted
    When a user deletes the already deleted category "<categoryTitle>"
    Then the status code returned by the API is "<statusCode>"

    Examples:
      | categoryTitle    | statusCode |
      | Old News         | 404        |

  Scenario Outline: Error Flow - Attempt to delete a category with an invalid ID
    When a user attempts to delete the category with an invalid ID "<categoryID>"
    Then the status code returned by the API is "<statusCode>"

    Examples:
      | categoryID     | statusCode |
      | SRK            | 404        |
      | Invalid        | 404        |

  Scenario: Teardown
    Then the system is restored to the original state