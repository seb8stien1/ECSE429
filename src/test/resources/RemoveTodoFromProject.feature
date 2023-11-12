Feature: Remove Todo from Project
  As a user, I want to remove a todo from a project when it's no longer relevant to that project.

  Background:
    Given the API server is running and available

    Given the following projects exist in the system:
      | projectTitle       |
      | Marketing Campaign |

    Given the following todos exist in the system:
      | todoTitle       |
      | Design Brochure |
      | Plan Event      |
      | New Design      |

    Given the following todos are associated with the Marketing Campaign project:
      | todoTitle       |
      | Design Brochure |
      | Plan Event      |

  Scenario Outline: Normal Flow - Unlink a todo from a project
    When a user attempts to remove the todo "<todoTitle>" from the project "<projectTitle>"
    Then the todo "<todoTitle>" should no longer be linked to the project "<projectTitle>"
    Then the system is restored to the original state

    Examples:
      | todoTitle       | projectTitle       |
      | Design Brochure | Marketing Campaign |
      | Plan Event      | Marketing Campaign |

  Scenario Outline: Alternate Flow - Unlink a todo that was not linked to the project
    When a user attempts to remove the todo "<todoTitle>" from the project "<projectTitle>"
    And the status code returned by the API is "<statusCode>"
    Then the system is restored to the original state

    Examples:
      | todoTitle   | statusCode |
      | New Design  | 400        |

  Scenario Outline: Error Flow - Try to unlink a todo from a non-existent project
    When a user attempts to remove the todo "<todoTitle>" from the project "<projectTitle>"
    And the status code returned by the API is "<statusCode>"
    Then the system is restored to the original state

    Examples:
      | todoTitle       | projectTitle    | statusCode |
      | Design Brochure | Unknown Project | 404        |
