Feature: Get Todo by Category
  As a user, I want to get a todo that has a certain category so that I can focus on important tasks first.

  Background:
    Given the API server is running and available

    Given the following categories exist in the system:
      | categoryTitle | categoryDescription                          |
      | School        | Tasks related to academic activities         |
      | Personal      | Personal errands and chores                  |

    Given the following todos exist in the system:
      | todoTitle         | todoDescription         | todoDoneStatus |
      | Complete Homework | Finish math assignment  | false          |
      | Complete Project  | Finish ECSE 429 Project | false          |

    Given the following category and todo association exist in the system:
      | categoryTitle | todoTitle         |
      | School        | Complete Homework |
      | School        | Complete Project  |

  Scenario Outline: Normal Flow - A user gets todos by a certain category
    When a user attempts to get todos with the category "<categoryTitle>"
    Then the system should return todos with the category "<categoryTitle>"

    Examples:
      | categoryTitle |
      | School        |

  Scenario Outline: Alternate Flow - A user gets todos by a category that has no todos assigned yet
    When a user attempts to get todos with the category "<categoryTitle>"
    Then the system should return an empty list indicating there are no todos for the given category "<categoryTitle>"

    Examples:
      | categoryTitle |
      | Personal      |

  Scenario Outline: Error Flow - A user provides an invalid category filter
    When a user attempts to get todos with the invalid category "<categoryTitle>"
    Then the status code returned by the API is "<statusCode>"

    Examples:
      | categoryTitle | statusCode |
      | Recreation    | 404        |
      | Travel        | 404        |

  Scenario: Teardown
    Then the system is restored to the original state
