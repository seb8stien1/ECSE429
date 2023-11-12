Feature: Get Categories Linked to a Project
  As a project manager, I want to view all the categories associated with a specific project, so I can understand the project's diverse areas of focus.

  Background:
    Given the API server is running and available

    Given the following projects exist in the system:
      | projectTitle        |
      | Website Revamp      |
      | App Development     |

    Given the following categories exist in the system:
      | categoryName     |
      | Design           |
      | Development      |
      | Marketing        |

    Given the following project and category association exist in the system:
      | projectTitle     | categoryName  |
      | Website Revamp   | Design        |
      | Website Revamp   | Marketing     |
      | App Development  | Development   |

  Scenario Outline: Normal Flow - Viewing categories associated with a project
    When a user retrieves categories associated with the project "<projectTitle>"
    Then the categories "<categoryName1>" and "<categoryName2>" associated with "<projectTitle>" should be returned
    Then the system is restored to the original state

    Examples:
      | projectTitle    | categoryName1 | categoryName2 |
      | Website Revamp  | Design        | Marketing     |

  Scenario Outline: Alternate Flow - Viewing one single category of a project
    When a user retrieves the categories associated with the project "<projectTitle>"
    Then only the category "<categoryName>" associated with the project "<projectTitle>" should be returned
    Then the system is restored to the original state

    Examples:
      | projectTitle    | categoryName  |
      | App Development | Development   |

  Scenario Outline: Error Flow - Retrieving categories associated with a non-existent project
    When a user attempts to retrieve categories associated with a non-existent project "<nonExistentProject>"
    And the status code returned by the API is "<statusCode>"
    Then the system is restored to the original state

    Examples:
      | nonExistentProject | statusCode |
      | New Launch         | 404        |
