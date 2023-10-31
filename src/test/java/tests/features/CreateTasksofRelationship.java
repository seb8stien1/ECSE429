package tests.features;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class CreateTasksofRelationship {
    @Given("the following projects exist in the system:")
    public void theFollowingProjectsExistInTheSystem() {
        // todo
    }

    @Given("the following todos exist in the system:")
    public void theFollowingTodosExistInTheSystem() {
        // todo
    }

    @When("a user adds a task with title {string}, description {string} and done status {string} to project with title {string}")
    public void aUserAddsATaskWithTitleTodoTitleDescriptionTodoDescriptionAndDoneStatusTodoDoneStatusToProjectWithTitleProjectTitle(
            String todoTitle,
            String todoDescription,
            String todoDoneStatus,
            String projectTitle
    ) {
        // todo
    }

    @Then("this task should be contained as a task of the project")
    public void thisTaskShouldBeContainedAsATaskOfTheProject() {
        // todo
    }

    @And("the number of todos in the system is {string}")
    public void theNumberOfTodosInTheSystemIs(String expectedTodoCount) {
        // todo
    }

    @And("the number of projects in the system is {string}")
    public void theNumberOfProjectsInTheSystemIs(String expectedProjectCount) {
        // todo
    }

    @Then("the status code returned by the API is {string}")
    public void theStatusCodeReturnedByTheAPIIs(String statusCode) {
        // todo
    }
}
