Feature: Delete Todo
  As a user, I want to delete a todo from my list so that I can keep the list up-to-date with only relevant tasks.

  Background:
    Given the API server is running and available

    Given the following todos exist in the system:
      | todoTitle            | todoDescription           | doneStatus |
      | Clean House          | Clean the entire house    | false      |
      | Call Abhigyan        | Call my friend John       | false      |
      | Pay Bills            | Pay electricity and water | true       |

  Scenario Outline: Normal Flow - A user deletes an existing todo
    When a user attempts to delete the todo with the title "<todoTitle>"
    Then the todo with the title "<todoTitle>" shall be removed from the system
    And the number of todos in the system is "<expectedTodoCount>"
    Then the system is restored to the original state

    Examples:
      | todoTitle   | expectedTodoCount |
      | Clean House | 2                 |
      | Call John   | 2                 |

  Scenario Outline: Alternate Flow - A user attempts to delete a todo that has already been deleted
    Given the todo with the title "<todoTitle>" is already deleted
    When a user attempts to delete the todo with the title "<todoTitle>"
    Then the status code returned by the API is "<statusCode>"
    Then the system is restored to the original state

    Examples:
      | todoTitle   | statusCode |
      | Clean House | 404        |
      | Call John   | 404        |

  Scenario Outline: Error Flow - A user attempts to delete a todo with an invalid ID
    When a user attempts to delete the todo with an invalid ID "<todoID>"
    Then an error should be raised
    And the status code returned by the API is "<statusCode>"
    Then the system is restored to the original state

    Examples:
      | todoID | statusCode |
      | abc123 | 400        |
      | 99999  | 400        |
