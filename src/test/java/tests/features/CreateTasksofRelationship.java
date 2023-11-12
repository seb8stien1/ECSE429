package tests.features;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class CreateTasksofRelationship {

    @When("a user adds a task with title {string}, description {string} and done status {string} to project with title {string}")
    public void aUserAddsATaskWithTitleDescriptionAndDoneStatusToProjectWithTitle(String todoTitle, String todoDescription, String todoDoneStatus, String projectTitle) {
        // todo
    }

    @Then("this task should be contained as a task of the project")
    public void thisTaskShouldBeContainedAsATaskOfTheProject() {
        // todo
    }

    @When("a user adds a non-existent task with title {string} to project with title {string}")
    public void aUserAddsANonExistentTaskWithTitleToProjectWithTitle(String todoTitle, String projectTitle) {
        // todo
    }
}
