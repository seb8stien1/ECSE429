Feature: Get Categories Linked to a Project
  As a project manager, I want to view all the categories associated with a specific project, so I can understand the project's diverse areas of focus.

  Background:
    Given the API server is running and available

    Given the following projects exist in the system:
      | projectTitle        | projectDescription | completed | active |
      | Website Revamp      | Homepage fixes     | false     | true   |
      | App Development     | Add OAuth          | false     | true   |

    Given the following categories exist in the system:
      | categoryTitle     | categoryDescription   |
      | Design            | Frontend Coding       |
      | Development       | Backend Coding        |
      | Marketing         | Non-technical aspects |

    Given the following project and category association exist in the system:
      | projectTitle     | categoryTitle  |
      | Website Revamp   | Design         |
      | Website Revamp   | Marketing      |
      | App Development  | Development    |

  Scenario Outline: Normal Flow - Viewing categories associated with a project
    When a user retrieves the categories associated with the project "<projectTitle>"
    Then the categories "<categoryTitle1>" and "<categoryTitle2>" associated with "<projectTitle>" should be returned

    Examples:
      | projectTitle    | categoryTitle1 | categoryTitle2 |
      | Website Revamp  | Design         | Marketing      |

  Scenario Outline: Alternate Flow - Viewing one single category of a project
    When a user retrieves the categories associated with the project "<projectTitle>"
    Then the category "<categoryTitle>" associated with the project "<projectTitle>" should be returned

    Examples:
      | projectTitle    | categoryTitle  |
      | App Development | Development    |

  Scenario Outline: Error Flow - Retrieving categories associated with a non-existent project
    When a user attempts to retrieve categories associated with a non-existent project "<nonExistentProject>"
    And the status code returned by the API is "<statusCode>"

    Examples:
      | nonExistentProject | statusCode |
      | New Launch         | 404        |

  Scenario: Teardown
    Then the system is restored to the original state