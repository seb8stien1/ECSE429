Feature: Get Projects By Category
  As a user, I want to get all projects under a specific category to see related projects.

  Background:
    Given the API server is running and available

    Given the following categories exist in the system:
      | categoryName  |
      | Development   |
      | Marketing     |

    Given the following projects are categorized under Development:
      | projectTitle      |
      | Software Upgrade  |
      | Database Migration|

  Scenario Outline: Normal Flow - Retrieve all projects under a given category
    When a user retrieves projects under the category "<categoryName>"
    Then the projects under the category "<categoryName>" are returned
    Then the system is restored to the original state

    Examples:
      | categoryName  |
      | Development   |

  Scenario Outline: Alternate Flow - Retrieve projects under a category with no associated projects
    When a user retrieves projects under the category "<categoryName>"
    Then the system should return an empty list indicating there are no projects for the given category
    Then the system is restored to the original state

    Examples:
      | categoryName |
      | Marketing    |

  Scenario Outline: Error Flow - Attempt to retrieve projects for a non-existent category
    When a user retrieves projects under the category "<categoryName>"
    Then an error should be raised
    Then the status code returned by the API is "<statusCode>"
    Then the system is restored to the original state

    Examples:
      | categoryName  | statusCode |
      | Unknown       | 404        |
