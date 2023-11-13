Feature: Get Todos of a Project
  As a user, I want to get all todos associated with a specific project to understand what needs to be done.

  Background:
    Given the API server is running and available

    Given the following projects exist in the system:
      | projectTitle     | projectDescription | completed | active |
      | Website Redesign | Homepage Revamp    | false     | true   |
      | Year-End Audit   | Report audits      | false     | true   |

    Given the following todos exist in the system:
      | todoTitle        | todoDescription            | todoDoneStatus |
      | Update Logo      | Redesign the company logo  | false          |
      | Revamp Home Page | Update the homepage layout | false          |

    Given the following project and todo association exist in the system:
      | projectTitle        | todoTitle        |
      | Website Redesign    | Update Logo      |
      | Website Redesign      | Revamp Home Page |

  Scenario Outline: Normal Flow - Retrieve all todos linked to a project
    When a user attempts to get todos for the project "<projectTitle>"
    Then the system should return todos for the project "<projectTitle>"

    Examples:
      | projectTitle      |
      | Website Redesign  |


