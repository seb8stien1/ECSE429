package tests.features;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class DeleteProject {

    @When("a user deletes the project with title {string}")
    public void aUserDeletesTheProjectWithTitle(String projectTitle) {
        // todo
    }

    @Then("the project with title {string} should be removed from the system")
    public void theProjectWithTitleShouldBeRemovedFromTheSystem(String projectTitle) {
        // todo
    }

    @When("a user attempts to delete the project with an invalid ID {string}")
    public void aUserAttemptsToDeleteTheProjectWithAnInvalidID(String projectID) {
        // todo
    }
}
