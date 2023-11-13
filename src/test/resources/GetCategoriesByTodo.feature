Feature: Get Categories By Todo
  As a user, I want to view the category a todo is associated with to understand its relevance to different areas.

  Background:
    Given the API server is running and available

    Given the following todos exist in the system:
      | todoTitle          | todoDescription       | todoDoneStatus      |
      | Schedule Meeting   | Add to calendar       | false               |
      | Update Software    | Computer needs repair | false               |
      | Complete Homework  | ECSE 415              | false               |

    Given the following categories exist in the system:
      | categoryTitle | categoryDescription   |
      | Work          | Stuff related to job  |
      | IT            | Stuff related to tech |

    Given the following todo and category association exist in the system:
      | todoTitle        | categoryTitle |
      | Schedule Meeting | Work          |
      | Update Software  | IT            |


  Scenario Outline: Normal Flow - Retrieve the category linked to a todo
    When a user retrieves the category for the todo "<todoTitle>"
    Then the category for the todo "<todoTitle>" is returned

    Examples:
      | todoTitle          |
      | Schedule Meeting   |
      | Update Software    |

  Scenario Outline: Alternate Flow - Retrieve the category for a todo with no associated categories
    When a user retrieves the category for the todo "<todoTitle>"
    Then the system should return an empty list indicating there are no categories for the given todo "<todoTitle>"

    Examples:
      | todoTitle          |
      | Complete Homework  |

  Scenario Outline: Error Flow - Attempt to retrieve categories for a non-existent todo
    When a user retrieves the category for the non-existent todo "<todoTitle>"
    Then the status code returned by the API is "<statusCode>"

    Examples:
      | todoTitle       | statusCode |
      | New Task        | 404        |

  Scenario: Teardown
    Then the system is restored to the original state