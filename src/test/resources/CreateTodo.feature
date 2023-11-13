Feature: Create a New Todo
  As a user, I wish to create a new todo in the system so that I can have all my todos in one place.

  Background:
    Given the API server is running and available

    Given the following todos exist in the system:
      | todoTitle         | todoDescription        | todoDoneStatus |
      | Complete Homework | Finish math assignment | false          |
      | Grocery Shopping  | Buy groceries          | false          |
      | Gym Workout       | Exercise at the gym    | true           |

  Scenario Outline: Normal Flow - A user creates a new todo with doneStatus as true
    When a user attempts to create a new todo with the title "<todoTitle>", description "<todoDescription>", and doneStatus "<doneStatus>"
    Then a new todo with the title "<todoTitle>", description "<todoDescription>", and doneStatus "<doneStatus>" shall be created
    Then the number of todos in the system is "<expectedTodoCount>"

    Examples:
      | todoTitle           | todoDescription           | doneStatus | expectedTodoCount |
      | Plan Vacation       | Prepare for upcoming trip | true       | 4                 |
      | Study for Exam      | Review study materials    | true       | 4                 |
      | Project Deadline    | Complete project tasks    | true       | 4                 |

  Scenario Outline: Alternate Flow - A user creates a new todo with doneStatus as false
    When a user attempts to create a new todo with the title "<todoTitle>", description "<todoDescription>", and doneStatus "<doneStatus>"
    Then a new todo with the title "<todoTitle>", description "<todoDescription>", and doneStatus "false" shall be created
    Then the number of todos in the system is "<expectedTodoCount>"

    Examples:
      | todoTitle           | todoDescription           | doneStatus | expectedTodoCount |
      | Weekend Plans       | Prepare for weekend plans | false      | 4                 |
      | Work Assignment     | Complete work assignment  | false      | 4                 |
      | Family Gathering    | Plan family gathering     | false      | 4                 |

  Scenario Outline: Error Flow - A user creates a new todo with an invalid doneStatus
    When a user attempts to create a new todo with the title "<todoTitle>", description "<todoDescription>", and invalid doneStatus "<doneStatus>"
    Then the following "<error>" shall be raised
    Then the status code returned by the API is "<statusCode>"
    Then the number of todos in the system is "<expectedTodoCount>"

    Examples:
      | todoTitle | todoDescription | doneStatus | error                                           | statusCode | expectedTodoCount |
      | Homework  | English Reading | Arsenal    | Failed Validation: doneStatus should be BOOLEAN | 400        | 3                 |

  Scenario: Teardown
    Then the system is restored to the original state