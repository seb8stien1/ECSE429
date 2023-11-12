package tests.features;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class CreateTasksofRelationship {
    @When("a user adds a task with title {string}, description {string} and done status {string} to project with title {string}")
    public void aUserAddsATaskToProject(
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

    @Then("the system is restored to the original state")
    public void theSystemIsRestoredToTheOriginalState() {
        // todo
    }
}
