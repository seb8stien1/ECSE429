Feature: Create New Project
  As a project manager, I want to create a new project so that I can organize related tasks under it.

  Background:
    Given the API server is running and available

  Scenario Outline: Normal Flow - A user creates a project with valid details
    When a user attempts to create a project with title "<projectTitle>" and description "<projectDescription>"
    Then a new project with title "<projectTitle>" and description "<projectDescription>" is created
    And the number of projects in the system is "<expectedProjectCount>"
    Then the system is restored to the original state

    Examples:
      | projectTitle   | projectDescription           | expectedProjectCount |
      | Website Launch | Launch the new company site  | 1                    |
      | Marketing Plan | Plan for Q3 marketing        | 1                    |

  Scenario Outline: Alternate Flow - A user creates a project with a blank description
    When a user attempts to create a project with title "<projectTitle>" and description "<projectDescription>"
    Then a new project with title "<projectTitle>" and description "<projectDescription>" is created
    And the number of projects in the system is "<expectedProjectCount>"
    Then the system is restored to the original state

    Examples:
      | projectTitle   |projectDescription | expectedProjectCount |
      | Website Launch |                   | 1                    |
      | Marketing Plan |                   | 1                    |

  Scenario Outline: Error Flow - A user attempts to create a project with an invalid title
    When a user attempts to create a project with an invalid title "<invalidTitle>"
    Then an error should be raised
    And the status code returned by the API is "<statusCode>"
    Then the system is restored to the original state

    Examples:
      | invalidTitle   | statusCode |
      |                | 400        |

