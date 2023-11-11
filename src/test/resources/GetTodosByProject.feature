Feature: Get Todos of a Project
  As a user, I want to get all todos associated with a specific project to understand what needs to be done.

  Background:
    Given the API server is running and available

    Given the following projects exist in the system:
      | projectTitle     |
      | Website Redesign |
      | Year-End Audit   |

    Given the following todos are associated with the Website Redesign project:
      | todoTitle        | todoDescription            | todoDoneStatus |
      | Update Logo      | Redesign the company logo  | false          |
      | Revamp Home Page | Update the homepage layout | false          |

  Scenario Outline: Normal Flow - Retrieve all todos linked to a project
    When a user attempts to get todos for the project "<projectTitle>"
    Then the system should return todos for the project "<projectTitle>"

    Examples:
      | projectTitle      |
      | Website Redesign  |

  Scenario Outline: Alternate Flow - Retrieve todos from a project with no associated tasks
    When a user attempts to get todos for the project "<projectTitle>"
    Then the system should return an empty list indicating there are no todos for the given project

    Examples:
      | projectTitle    |
      | Year-End Audit  |

  Scenario Outline: Error Flow - Attempt to retrieve todos for a non-existent project
    When a user attempts to get todos for the project "<projectTitle>"
    Then an error should be raised
    Then the status code returned by the API is "<statusCode>"

    Examples:
      | projectTitle             | statusCode |
      | Non-existent Project     | 404        |
