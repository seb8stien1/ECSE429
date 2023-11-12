Feature: Update Todo Details
  As a user, I wish to update the details of a todo in the system to fix and update any mistakes I may have made earlier.

  Background:
    Given the API server is running and available

    Given the following todos exist in the system:
      | todoTitle         | todoDescription        | todoDoneStatus |
      | Complete Homework | Finish math assignment | false          |
      | Grocery Shopping  | Buy groceries          | false          |
      | Gym Workout       | Exercise at the gym    | true           |

  Scenario Outline: Normal Flow - A user updates a todo with a new description
    When a user attempts to update the todo with the title "<todoTitle>" with description "<newDescription>" and doneStatus "<sameDoneStatus>"
    Then the todo with the title "<todoTitle>" shall be updated with description "<newDescription>" and doneStatus "<sameDoneStatus>"
    Then the number of todos in the system is "<expectedTodoCount>"

    Examples:
      | todoTitle           | newDescription          | sameDoneStatus | expectedTodoCount |
      | Complete Homework   | Complete math homework  | false          | 3                 |
      | Grocery Shopping    | Buy organic groceries   | false          | 3                 |
      | Gym Workout         | Cardio at the gym       | true           | 3                 |

  Scenario Outline: Alternate Flow - A user attempts to update a todo with a new done status
    When a user attempts to update the todo with the title "<todoTitle>" with description "<sameDescription>" and doneStatus "<newDoneStatus>"
    Then the todo with the title "<todoTitle>" shall be updated with description "<sameDescription>" and doneStatus "<newDoneStatus>"
    Then the number of todos in the system is "<expectedTodoCount>"

    Examples:
      | todoTitle           | sameDescription          | newDoneStatus  | expectedTodoCount |
      | Complete Homework   | Finish math assignment   | true           | 3                 |
      | Grocery Shopping    | Buy groceries            | true           | 3                 |
      | Gym Workout         | Exercise at the gym      | false          | 3                 |

  Scenario Outline: Error Flow - A user attempts to update a todo with an invalid doneStatus
    When a user attempts to update the todo with the title "<todoTitle>" with description "<sameDescription>" and invalid doneStatus "<newDoneStatus>"
    Then the following "<error>" shall be raised
    Then the status code returned by the API is "<statusCode>"
    Then the number of todos in the system is "<expectedTodoCount>"

    Examples:
      | todoTitle           | sameDescription          | newDoneStatus  | error                                           | statusCode | expectedTodoCount |
      | Complete Homework   | Finish math homework     | invalidStatus  | Failed Validation: doneStatus should be BOOLEAN | 400        | 3                 |
      | Grocery Shopping    | Buy groceries            | notDone        | Failed Validation: doneStatus should be BOOLEAN | 400        | 3                 |
      | Gym Workout         | Exercise at the gym      | newStatus      | Failed Validation: doneStatus should be BOOLEAN | 400        | 3                 |

  Scenario: Teardown
    Then the system is restored to the original state
