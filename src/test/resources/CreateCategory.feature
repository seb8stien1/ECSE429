Feature: Create New Category
  As an organizer, I want to create a new category to classify my projects and todos.

  Background:
    Given the API server is running and available

    Given the following categories exist in the system:
      | categoryTitle | categoryDescription                          |
      | School        | Tasks related to academic activities         |
      | Work          | Professional tasks and deadlines             |
      | Recreation    | Fun and recreational activities              |
      | Travel        | Upcoming trips and travel plans              |


  Scenario Outline: Normal Flow - Creating a category with a title and description
    When a user creates a category with title "<categoryTitle>" and description "<categoryDescription>"
    Then a new category with title "<categoryTitle>" and "<categoryDescription>" should be created
    Then the number of categories in the system is "<expectedCategoryCount>"

    Examples:
      | categoryTitle | categoryDescription                          | expectedCategoryCount |
      | Personal      | Personal errands and chores                  | 5                     |
      | Health        | Activities related to well-being and fitness | 5                     |

  Scenario Outline: Alternate Flow - Creating a category with a blank description
    When a user creates a category with title "<categoryTitle>" and description "<categoryDescription>"
    Then a new category with title "<categoryTitle>" and "<categoryDescription>" should be created
    Then the number of categories in the system is "<expectedCategoryCount>"

    Examples:
      | categoryTitle | categoryDescription | expectedCategoryCount |
      | Office        |                     | 5                     |
      | Exercise      |                     | 5                     |

  Scenario Outline: Error Flow - Attempting to create a category with an invalid title
    When a user creates a category with an invalid title "<categoryTitle>"
    Then the following "<error>" shall be raised
    And the status code returned by the API is "<statusCode>"
    Then the number of categories in the system is "<expectedCategoryCount>"

    Examples:
      | categoryTitle | error                                                 | statusCode | expectedCategoryCount |
      |               | Failed Validation: title : can not be empty           | 400        | 4                     |

  Scenario: Teardown
    Then the system is restored to the original state