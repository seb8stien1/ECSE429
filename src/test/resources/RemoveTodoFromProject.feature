Feature: Remove Todo from Project
  As a user, I want to remove a todo from a project when it's no longer relevant to that project.

  Background:
    Given the API server is running and available

    Given the following projects exist in the system:
      | projectTitle       | projectDescription | completed | active |
      | Marketing Campaign | New product ads    | false     | true   |

    Given the following todos exist in the system:
      | todoTitle       | todoDescription  | todoDoneStatus |
      | Design Brochure | Pamphlets        | false          |
      | Plan Event      | Book venue       | false          |
      | New Design      | Website homepage | false          |

    Given the following project and todo association exist in the system:
      | projectTitle       | todoTitle       |
      | Marketing Campaign | Design Brochure |
      | Marketing Campaign | Plan Event      |

  Scenario Outline: Normal Flow - Unlink a todo from a project
    When a user attempts to remove the todo "<todoTitle>" from the project "<projectTitle>"
    Then the todo "<todoTitle>" should no longer be linked to the project "<projectTitle>"

    Examples:
      | todoTitle       | projectTitle       |
      | Design Brochure | Marketing Campaign |
      | Plan Event      | Marketing Campaign |

  Scenario Outline: Alternate Flow - Unlink a todo that was not linked to the project
    When a user attempts to remove the todo "<todoTitle>" from the project "<projectTitle>"
    Then the status code returned by the API is "<statusCode>"

    Examples:
      | todoTitle   | projectTitle       | statusCode |
      | New Design  | Marketing Campaign | 404        |

  Scenario Outline: Error Flow - Try to unlink a todo from a non-existent project
    When a user attempts to remove the todo "<todoTitle>" from the non-existent project "<projectTitle>"
    And the status code returned by the API is "<statusCode>"

    Examples:
      | todoTitle       | projectTitle    | statusCode |
      | Design Brochure | Unknown Project | 400        |

  Scenario: Teardown
    Then the system is restored to the original state
