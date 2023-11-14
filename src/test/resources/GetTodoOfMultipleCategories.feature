Feature: Get Todo Linked to Multiple Categories
  As a user, I want to view a todo linked to multiple categories, so that I can understand how different tasks overlap across various aspects of my life.

  Background:
    Given the API server is running and available

    Given the following categories exist in the system:
      | categoryTitle     | categoryDescription      |
      | Work              | Stuff related to work    |
      | Personal          | Stuff related to home    |
      | Health            | Stuff related to fitness |

    Given the following todos exist in the system:
      | todoTitle        | todoDescription      | todoDoneStatus |
      | Call John        | Book meeting         | false          |
      | Pay Bills        | Electricity          | false          |
      | Morning Jog      | Routine life         | false          |

    Given the following category and todo association exist in the system:
      | todoTitle   | categoryTitle  |
      | Call John   | Work           |
      | Call John   | Personal       |
      | Pay Bills   | Personal       |
      | Morning Jog | Health         |

  Scenario Outline: Normal Flow - Viewing todos linked to multiple categories
    When a user retrieves todos linked to categories "<categoryTitle1>" and "<categoryTitle2>"
    Then the returned "<todoTitle>" should be the same for both categories

    Examples:
      | categoryTitle1          | categoryTitle2 | todoTitle |
      | Work                    | Personal       | Call John |

  Scenario Outline: Alternate Flow - Viewing one todo linked to a category
    When a user retrieves the todo linked only to the category "<categoryTitle>"
    Then the todo "<todoTitle>" linked to the category should be returned

    Examples:
      | categoryTitle | todoTitle     |
      | Health        | Morning Jog   |

  Scenario Outline: Error Flow - Retrieving todos linked to non-existent categories
    When a user attempts to retrieve todos linked to a non-existent category "<nonExistentCategory>"
    And the status code returned by the API is "<statusCode>"

    Examples:
      | nonExistentCategory | statusCode |
      | Fitness             | 404        |

  Scenario: Teardown
    Then the system is restored to the original state
