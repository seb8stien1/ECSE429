Feature: Mark a Todo as Complete
  As a user, I want to mark a todo as complete so that I can track my progress.

  Background:
    Given the API server is running and available

    Given the following todos exist in the system:
      | todoTitle         | todoDescription        | doneStatus |
      | Complete Homework | Finish math assignment | false      |
      | Grocery Shopping  | Buy groceries          | false      |
      | Gym Workout       | Exercise at the gym    | true       |

  Scenario Outline: Normal Flow - A user marks an existing todo as complete
    When a user attempts to mark the todo titled "<todoTitle>" with doneStatus "<doneStatus>" as completed "<completed>"
    Then the todo titled "<todoTitle>" should have doneStatus as "<completed>"

    Examples:
      | todoTitle         | doneStatus  | completed |
      | Complete Homework | false       | true      |
      | Grocery Shopping  | false       | true      |

  Scenario Outline: Alternate Flow - A user marks an already completed todo as complete again
    When a user attempts to mark the todo titled "<todoTitle>" with doneStatus "<doneStatus>" as completed "<completed>"
    Then the todo titled "<todoTitle>" should have doneStatus as "<completed>"

    Examples:
      | todoTitle  | doneStatus |
      | Gym Workout| true       |

  Scenario Outline: Error Flow - A user tries to mark a non-existent todo as complete
    When a user attempts to mark the non-existent todo titled "<todoTitle>" with doneStatus as "<doneStatus>"
    Then an error should be raised
    Then the status code returned by the API is "<statusCode>"
    Then the number of todos in the system is "<expectedTodoCount>"

    Examples:
      | todoTitle            | doneStatus | statusCode | expectedTodoCount |
      | Nonexistent Todo     | true       | 404        | 3                 |
      | Another Invalid Todo | false      | 404        | 3                 |
