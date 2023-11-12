Feature: Create a tasksof relationship between a task and a project

  As a user, I want to add a task to a project, so that I can remember to complete it during the project.

  Background:
    Given the API server is running and available

    Given the following projects exist in the system:
      | projectTitle   | completed         | active         | projectDescription    |
      | Project A      | false             | true           | Unit Tests            |
      | Project B      | false             | true           | Story Tests           |
      | Fitness        | false             | true           | Work on losing weight |

    Given the following todos exist in the system:
      | todoTitle               | todoDescription         | todoDoneStatus |
      | Complete Feature Files  | Write 20 files          | false          |
      | Complete Project Report | Summarize in a word doc | false          |
      | Gym Workout             | Exercise at the gym     | true           |

  Scenario Outline: Add a New incomplete Task to Project - Normal Flow
    When a user adds a task with title "<todoTitle>", description "<todoDescription>" and done status "<todoDoneStatus>" to project with title "<projectTitle>"
    Then this task should be contained as a task of the project
    Then the number of todos in the system is "<expectedTodoCount>"
    And the number of projects in the system is "<expectedProjectCount>"

    Examples:
      | todoTitle                | todoDescription         | todoDoneStatus   | projectTitle | expectedTodoCount | expectedProjectCount |
      | Completed Project Report | Summarize in a word doc | false            | Project A    | 3                 | 3                    |
      | Complete Feature Files   | Write 20 files          | false            | Project B    | 3                 | 3                    |

  Scenario Outline: Add a Completed Task to a Project - Alternate Flow
    When a user adds a task with title "<todoTitle>", description "<todoDescription>" and done status "<todoDoneStatus>" to project with title "<projectTitle>"
    Then this task should be contained as a task of the project
    Then the number of todos in the system is "<expectedTodoCount>"
    And the number of projects in the system is "<expectedProjectCount>"

    Examples:
      | todoTitle   | todoDescription     | todoDoneStatus  | projectTitle | expectedTodoCount | expectedProjectCount |
      | Gym Workout | Exercise at the gym | true            | Fitness      | 3                 | 3                    |

  Scenario Outline: Attempt to Add a non-existent Task to a Project - Error Flow
    When a user adds a non-existent task with title "<todoTitle>" to project with title "<projectTitle>"
    Then the status code returned by the API is "<statusCode>"
    Then the number of todos in the system is "<expectedTodoCount>"
    And the number of projects in the system is "<expectedProjectCount>"

    Examples:
      | todoTitle           | projectTitle | statusCode | expectedTodoCount | expectedProjectCount |
      | Complete Comments   | Project A    | 400        | 3                 | 3                    |

  Scenario: Teardown
    Then the system is restored to the original state