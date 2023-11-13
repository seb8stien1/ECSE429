Feature: Get Projects By Category
  As a user, I want to get all projects under a specific category to see related projects.

  Background:
    Given the API server is running and available

    Given the following categories exist in the system:
      | categoryTitle  | categoryDescription  |
      | Development    | Coding               |
      | Marketing      | Non-technical aspect |

    Given the following projects exist in the system:
      | projectTitle       | projectDescription  | completed | active |
      | Software Upgrade   | Update to version 7 | false     | true   |
      | Database Migration | Add new table       | false     | true   |

    Given the following category and project association exist in the system:
      | categoryTitle | projectTitle       |
      | Development   | Software Upgrade   |
      | Development   | Database Migration |

  Scenario Outline: Normal Flow - Retrieve all projects under a given category
    When a user retrieves projects under the category "<categoryTitle>"
    Then the projects under the category "<categoryTitle>" are returned

    Examples:
      | categoryTitle  |
      | Development    |

  Scenario Outline: Alternate Flow - Retrieve projects under a category with no associated projects
    When a user retrieves projects under the category "<categoryTitle>"
    Then the system should return an empty list indicating there are no projects for the given category "<categoryTitle>"

    Examples:
      | categoryTitle |
      | Marketing     |

  Scenario Outline: Error Flow - Attempt to retrieve projects for a non-existent category
    When a user retrieves projects under the non-existent category "<categoryTitle>"
    Then the status code returned by the API is "<statusCode>"

    Examples:
      | categoryTitle  | statusCode |
      | Unknown        | 404        |

  Scenario: Teardown
    Then the system is restored to the original state
