Feature: Delete Project
  As a user, I want to delete a project that is no longer needed to keep my workspace clean.

  Background:
    Given the API server is running and available

    And the following projects exist in the system:
      | projectTitle     | projectDescription     |
      | Old Assignment   | ECSE 415 Assignment 3  |
      | New Assignment   | ECSE 415 Assignment 4  |

  Scenario Outline: Normal Flow - A user deletes an existing project
    When a user deletes the project with title "<projectTitle>"
    Then the project with title "<projectTitle>" should be removed from the system
    And the number of projects in the system is "<expectedProjectCount>"

    Examples:
      | projectTitle    | expectedProjectCount |
      | Old Assignment  | 1                    |

  Scenario Outline: Alternate Flow - A user attempts to delete a project with that has already been deleted
    Given the project with the title "<projectTitle>" is already deleted
    When a user deletes the project with title "<projectTitle>"
    Then the status code returned by the API is "<statusCode>"

    Examples:
      | projectTitle    | statusCode |
      | Old Assignment  | 404        |

  Scenario Outline: Error Flow - A user attempts to delete a project with an invalid ID
    When a user attempts to delete the project with an invalid ID "<projectID>"
    Then an error should be raised
    And the status code returned by the API is "<statusCode>"

    Examples:
      | projectID    | statusCode |
      | Invalid ID   | 400        |
      | Liverpool    | 400        |
