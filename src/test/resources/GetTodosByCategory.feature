Feature: Get Todos by Category
  As a user, I want to get my list of todos that have a certain category so that I can focus on important tasks first.

  Background:
    Given the following todos exist in the system:
      | todoTitle         | todoDescription        | todoDoneStatus | todoCategory |
      | Complete Homework | Finish math assignment | false          | School       |
      | Grocery Shopping  | Buy groceries          | false          | Personal     |
      | Gym Workout       | Exercise at the gym    | true           | Health       |
      | Work Presentation | Prepare slides         | false          | Work         |

    Given the following categories exist in the system:
      | category   | description                                  |
      | School     | Tasks related to academic activities         |
      | Personal   | Personal errands and chores                  |
      | Health     | Activities related to well-being and fitness |
      | Work       | Professional tasks and deadlines             |
      | Recreation | Fun and recreational activities              |
      | Travel     | Upcoming trips and travel plans              |

  Scenario Outline: Normal Flow - A user gets todos by a certain category
    When a user attempts to get todos with the category "<category>"
    Then the system should return todos with the category "<category>"

    Examples:
      | category |
      | School   |
      | Personal |
      | Health   |
      | Work     |

  Scenario Outline: Alternate Flow - A user gets todos by a category that has no todos assigned yet
    When a user attempts to get todos with the category "<category>"
    Then the system should return an empty list indicating there are no todos for the given category

    Examples:
      | category   |
      | Recreation |
      | Travel     |

  Scenario Outline: Error Flow - A user provides an invalid category filter
    When a user attempts to get todos with the category "<category>"
    Then the status code returned by the API is "<statusCode>"

    Examples:
      | category | statusCode |
      | &*^%$#@  | 400        |
      | 123456   | 400        |
