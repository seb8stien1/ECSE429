Feature: Categorize Todo
  As a user, I want to categorize a todo to better manage and prioritize my tasks.

  Background:
    Given the API server is running and available

    Given the following todos exist in the system:
      | todoTitle       | todoDescription        | todoDoneStatus |
      | Laundry         | Wash clothes           | false          |
      | Email Client    | Send an email to client| false          |

    Given the following categories exist in the system:
      | categoryTitle  | categoryDescription   |
      | Home           | Stuff related to home |
      | Work           | Stuff related to work |

  Scenario Outline: Normal Flow - Assigning a category to a todo
    When a user categorizes the todo "<todoTitle>" as "<categoryTitle>"
    Then the todo "<todoTitle>" should be categorized as "<categoryTitle>"

    Examples:
      | todoTitle    | categoryTitle |
      | Laundry      | Home          |
      | Email Client | Work          |

  Scenario Outline: Alternate Flow - Changing the category of an existing todo
    Given the todo "<todoTitle>" is categorized as "<categoryTitle>"
    When a user categorizes the todo "<todoTitle>" as "<newCategoryTitle>"
    Then the todo "<todoTitle>" should be categorized as "<newCategoryTitle>"

    Examples:
      | todoTitle    | categoryTitle | newCategoryTitle |
      | Laundry      | Home          | Work             |
      | Email Client | Work          | Home             |

  Scenario Outline: Error Flow - Assigning a non-existent category to a todo
    When a user categorizes the todo "<todoTitle>" to a non-existent "<categoryTitle>"
    Then the status code returned by the API is "<statusCode>"

    Examples:
      | todoTitle    | categoryTitle | statusCode |
      | Laundry      | Outdoor       | 404        |
      | Email Client | Personal      | 404        |

  Scenario: Teardown
    Then the system is restored to the original state