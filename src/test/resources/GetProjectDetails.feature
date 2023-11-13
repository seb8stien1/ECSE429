Feature: Get Project Details
  As a user, I want to view details of a specific project to understand its scope and status.

  Background:
    Given the API server is running and available

    Given the following projects exist in the system:
      | projectTitle    | projectDescription       | completed | active |
      | Redesign Website| Website layout overhaul  | false     | true   |
      | Team Outing     | Organize a team outing   | false     | true   |
      | Finish Homework |                          | false     | true   |

  Scenario Outline: Normal Flow - A user views details of an existing project
    When a user retrieves details of the project with title "<projectTitle>"
    Then the project returned has description "<projectDescription>", has completed status "<completed>" and active status "<active>"

    Examples:
      | projectTitle     | projectDescription       | completed | active |
      | Redesign Website | Website layout overhaul  | false     | true   |
      | Team Outing      | Organize a team outing   | false     | true   |

  Scenario Outline: Alternate Flow - A user attempts to view details of a project with a blank description
    When a user retrieves details of the project with title "<projectTitle>"
    Then the project returned has blank description "<projectDescription>"

    Examples:
      | projectTitle     | projectDescription       |
      | Finish Homework  | null                     |

  Scenario Outline: Error Flow - A user attempts to retrieve details of a non-existent project
    When a user retrieves details of the non-existent project with title "<projectTitle>"
    And the status code returned by the API is "<statusCode>"

    Examples:
      | projectTitle    | statusCode |
      | Unknown Project | 404        |

  Scenario: Teardown
    Then the system is restored to the original state