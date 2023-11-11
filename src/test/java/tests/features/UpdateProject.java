package tests.features;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class UpdateProject {
    @When("a user updates the project {string} with new description {string} and new completed status {string}")
    public void aUserUpdatesTheProjectWithNewDescriptionAndNewCompletedStatus(String projectTitle, String newDescription, String newCompleted) {
        // todo
    }

    @Then("the project {string} should have description {string} and completed status {string}")
    public void theProjectShouldHaveDescriptionAndCompletedStatus(String projectTitle, String newDescription, String newCompleted) {
        // todo
    }

    @When("a user updates the project {string} with new active status {string}")
    public void aUserUpdatesTheProjectWithNewActiveStatus(String projectTitle, String newActive) {
        // todo
    }

    @Then("the project {string} should have active status {string}")
    public void theProjectShouldHaveActiveStatus(String projectTitle, String newActive) {
    }
}
