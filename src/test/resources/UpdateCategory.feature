Feature: Update Category
  As a user, I want to update the details of a category to correct or modify its information.

  Background:
    Given the API server is running and available

    Given the following categories exist in the system:
      | categoryTitle  | categoryDescription       |
      | Urgent         | Tasks that are urgent     |
      | Personal       | Personal related tasks    |

  Scenario Outline: Normal Flow - Update an existing category's details
    When a user updates the category "<categoryTitle>" with new description "<newDescription>"
    Then the category "<categoryTitle>" should have description "<newDescription>"
    Then the number of categories in the system is "<expectedCategoryCount>"

    Examples:
      | categoryTitle | newDescription                       | expectedCategoryCount |
      | Urgent        | Tasks that need immediate attention  | 2                     |
      | Personal      | Personal and private tasks           | 2                     |

  Scenario Outline: Alternate Flow - Update an existing category with a blank description
    When a user updates the category "<categoryTitle>" with new description "<newDescription>"
    Then the category "<categoryTitle>" should have description "<newDescription>"
    Then the number of categories in the system is "<expectedCategoryCount>"

    Examples:
      | categoryTitle | newDescription  | expectedCategoryCount |
      | Urgent        |                 | 2                     |
      | Personal      |                 | 2                     |

  Scenario Outline: Error Flow - Update an existing category with an invalid title
    When a user updates the category "<categoryTitle>" with new title "<newTitle>"
    Then the status code returned by the API is "<statusCode>"
    Then the following "<error>" shall be raised
    Then the number of categories in the system is "<expectedCategoryCount>"

    Examples:
      | categoryTitle   | newTitle | statusCode | error                                        | expectedCategoryCount |
      | Urgent          |          | 400        | Failed Validation: title : can not be empty  | 2                     |

  Scenario: Teardown
    Then the system is restored to the original state