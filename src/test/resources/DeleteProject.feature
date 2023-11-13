Feature: Delete Project
  As a user, I want to delete a project that is no longer needed to keep my workspace clean.

  Background:
    Given the API server is running and available

    Given the following projects exist in the system:
      | projectTitle     | projectDescription   | completed | active |
      | Old Assignment   | Completed Assignment | true      | false  |
      | New Assignment   | Need to complete     | false     | true   |

  Scenario Outline: Normal Flow - A user deletes an existing project
    When a user deletes the project with title "<projectTitle>"
    Then the project with title "<projectTitle>" should be removed from the system
    And the number of projects in the system is "<expectedProjectCount>"

    Examples:
      | projectTitle    | expectedProjectCount |
      | Old Assignment  | 1                    |

  Scenario Outline: Alternate Flow - A user attempts to delete a project that has already been deleted
    When a user deletes the already deleted project with title "<projectTitle>"
    Then the status code returned by the API is "<statusCode>"

    Examples:
      | projectTitle    | statusCode |
      | Old News        | 404        |

  Scenario Outline: Error Flow - A user attempts to delete a project with an invalid ID
    When a user attempts to delete the project with an invalid ID "<projectID>"
    And the status code returned by the API is "<statusCode>"

    Examples:
      | projectID    | statusCode |
      | Invalid      | 404        |
      | Liverpool    | 404        |

  Scenario: Teardown
    Then the system is restored to the original state