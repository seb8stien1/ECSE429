Feature: Update Project
  As a team leader, I want to update a project's details to reflect changes in scope or priorities.

  Background:
    Given the API server is running and available

    Given the following projects exist in the system:
      | projectTitle   | projectDescription      | completed | active |
      | Expansion Plan | Company expansion plans | false     | true   |

  Scenario Outline: Normal Flow - A user updates an existing project's description and completed status
    When a user updates the project "<projectTitle>" with new description "<newDescription>" and new completed status "<newCompleted>"
    Then the project "<projectTitle>" should have description "<newDescription>" and completed status "<newCompleted>"
    And the number of projects in the system is "<expectedProjectCount>"

    Examples:
      | projectTitle    | newDescription                | newCompleted | expectedProjectCount |
      | Expansion Plan  | Expanded company expansion    | true         | 1                    |

  Scenario Outline: Alternate Flow - A user updates an existing project's active status
    When a user updates the project "<projectTitle>" with new active status "<newActive>"
    Then the project "<projectTitle>" should have active status "<newActive>"
    And the number of projects in the system is "<expectedProjectCount>"

    Examples:
      | projectTitle    | newActive     | expectedProjectCount |
      | Expansion Plan  | false         | 1                    |

  Scenario Outline: Error Flow - A user attempts to update a project with an invalid active status
    When a user updates the project "<projectTitle>" with an invalid active status "<invalidActive>"
    Then the following "<error>" shall be raised
    Then the status code returned by the API is "<statusCode>"
    And the number of projects in the system is "<expectedTodoCount>"

    Examples:
      | projectTitle     | invalidActive | error                                         | statusCode | expectedTodoCount |
      | Expansion Plan   | Liverpool     | Failed Validation: active should be BOOLEAN   | 400        | 1                 |

  Scenario: Teardown
    Then the system is restored to the original state
