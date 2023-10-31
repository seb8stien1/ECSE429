Feature: Create a New Todo
  As a user, I wish to create a new todo in the system so that I can have all my todos in one place.

  Background:
    Given the following todos exist in the system:
      | todoTitle         | todoDescription        | doneStatus |
      | Complete Homework | Finish math assignment | false      |
      | Grocery Shopping  | Buy groceries          | false      |
      | Gym Workout       | Exercise at the gym    | true       |

  Scenario Outline: Normal Flow - A user creates a new todo with doneStatus as true
    When a user attempts to create a new todo with the title "<todoTitle>", description "<todoDescription>", and doneStatus "<doneStatus>"
    Then a new todo with the title "<todoTitle>", description "<todoDescription>", and doneStatus "<doneStatus>" shall be created
    Then the number of todos in the system is "<expectedTodoCount>"

    Examples:
      | todoTitle           | todoDescription           | expectedTodoCount |
      | Plan Vacation       | Prepare for upcoming trip | 4                 |
      | Study for Exam      | Review study materials    | 5                 |
      | Project Deadline    | Complete project tasks    | 6                 |

  Scenario Outline: Alternate Flow - A user creates a new todo with doneStatus as false
    When a user attempts to create a new todo with the title "<todoTitle>", description "<todoDescription>", and doneStatus "<doneStatus>"
    Then a new todo with the title "<todoTitle>", description "<todoDescription>", and doneStatus "false" shall be created
    Then the number of todos in the system is "<expectedTodoCount>"

    Examples:
      | todoTitle           | todoDescription           | expectedTodoCount |
      | Weekend Plans       | Prepare for weekend plans | 4                 |
      | Work Assignment     | Complete work assignment  | 5                 |
      | Family Gathering    | Plan family gathering     | 6                 |

  Scenario Outline: Error Flow - A user creates a new todo with an invalid doneStatus
    When a user attempts to create a new todo with the title "<todoTitle>", description "<todoDescription>", and doneStatus "<doneStatus>"
    Then the following "<error>" shall be raised
    And the status code returned by the API is "<statusCode>"
    Then the number of todos in the system is "<expectedTodoCount>"

    Examples:
      | todoTitle | todoDescription | error                                           | statusCode | expectedTodoCount |
      | Homework  | English Reading | Failed Validation: doneStatus should be BOOLEAN | 400        | 3                 |
