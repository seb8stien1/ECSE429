Feature: Categorize Todo
  As a user, I want to categorize a todo to organize my tasks better.

  Background:
    Given the API server is running and available

    Given the following todos exist in the system:
      | todoTitle       | todoDescription        |
      | Laundry         | Wash clothes           |
      | Email Client    | Send an email to client|

    Given the following categories exist in the system:
      | categoryName  |
      | Home          |
      | Work          |

  Scenario Outline: Normal Flow - Assigning a category to a todo
    When a user categorizes the todo "<todoTitle>" as "<categoryName>"
    Then the todo "<todoTitle>" should be categorized as "<categoryName>"
    Then the system is restored to the original state

    Examples:
      | todoTitle    | categoryName |
      | Laundry      | Home         |
      | Email Client | Work         |

  Scenario Outline: Alternate Flow - Changing the category of an existing todo
    Given the todo "<todoTitle>" is already categorized as "<oldCategoryName>"
    When a user categorizes the todo "<todoTitle>" as "<newCategoryName>"
    Then the todo "<todoTitle>" should be categorized as "<newCategoryName>"
    Then the system is restored to the original state

    Examples:
      | todoTitle    | oldCategoryName | newCategoryName |
      | Laundry      | Home            | Work            |
      | Email Client | Work            | Home            |

  Scenario Outline: Error Flow - Assigning a non-existent category to a todo
    When a user categorizes the todo "<todoTitle>" as "<categoryName>"
    Then an error should be raised
    And the status code returned by the API is "<statusCode>"
    Then the system is restored to the original state

    Examples:
      | todoTitle    | categoryName | statusCode |
      | Laundry      | Outdoor      | 404        |
      | Email Client | Personal     | 404        |
