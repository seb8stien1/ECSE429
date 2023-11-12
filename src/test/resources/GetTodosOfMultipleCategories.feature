Feature: Get Todos Linked to Multiple Categories
  As a user, I want to view all todos linked to multiple categories, so that I can understand how different tasks overlap across various aspects of my life.

  Background:
    Given the API server is running and available

    Given the following categories exist in the system:
      | categoryName     |
      | Work             |
      | Personal         |
      | Health           |

    Given the following todos exist in the system:
      | todoTitle        |
      | Call John        |
      | Pay Bills        |
      | Morning Jog      |

    Given the following todo and category association exist in the system:
      | todoTitle   | categoryName  |
      | Call John   | Work          |
      | Call John   | Personal      |
      | Pay Bills   | Personal      |
      | Morning Jog | Health        |

  Scenario Outline: Normal Flow - Viewing todos linked to multiple categories
    When a user retrieves todos linked to categories "<categoryName1>" and "<categoryName2>"
    Then the "<todoTitle>" linked to "<categoryName1>" and "<categoryName2>" should be the same
    Then the system is restored to the original state

    Examples:
      | categoryName1          | categoryName2 | todoTitle |
      | Work                   | Personal      | Call John |

  Scenario Outline: Alternate Flow - Viewing one todo linked to a category
    When a user retrieves the todo linked only to the category "<categoryName>"
    Then only the todo "<todoTitle>" linked to the category "<categoryName>" should be returned
    Then the system is restored to the original state

    Examples:
      | categoryName | todoTitle    |
      | Health       | Morning Jog  |

  Scenario Outline: Error Flow - Retrieving todos linked to non-existent categories
    When a user attempts to retrieve todos linked to a non-existent category "<nonExistentCategory>"
    And the status code returned by the API is "<statusCode>"
    Then the system is restored to the original state

    Examples:
      | nonExistentCategory | statusCode |
      | Fitness             | 404        |
