Feature: Delete Todo
  As a user, I want to delete a todo from my list so that I can keep the list up-to-date with only relevant tasks.

  Background:
    Given the API server is running and available

    Given the following todos exist in the system:
      | todoTitle            | todoDescription           | todoDoneStatus |
      | Clean House          | Clean the entire house    | false          |
      | Call Abhigyan        | Call my friend John       | false          |
      | Pay Bills            | Pay electricity and water | true           |

  Scenario Outline: Normal Flow - A user deletes an existing todo
    When a user attempts to delete the todo with the title "<todoTitle>"
    Then the todo with the title "<todoTitle>" shall be removed from the system
    Then the number of todos in the system is "<expectedTodoCount>"

    Examples:
      | todoTitle       | expectedTodoCount |
      | Clean House     | 2                 |
      | Call Abhigyan   | 2                 |

  Scenario Outline: Alternate Flow - A user attempts to delete a todo that has already been deleted
    When a user attempts to delete the already deleted todo with the title "<todoTitle>"
    Then the status code returned by the API is "<statusCode>"

    Examples:
      | todoTitle    | statusCode |
      | Clean Office | 404        |
      | Call Seb     | 404        |

  Scenario Outline: Error Flow - A user attempts to delete a todo with an invalid ID
    When a user attempts to delete the todo with an invalid ID "<todoID>"
    And the status code returned by the API is "<statusCode>"

    Examples:
      | todoID | statusCode |
      | abc123 | 404        |
      | 99999  | 404        |

  Scenario: Teardown
    Then the system is restored to the original state