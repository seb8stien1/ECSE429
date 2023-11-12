package tests.features;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class UpdateProject {
    @When("a user updates the project {string} with new description {string} and new completed status {string}")
    public void aUserUpdatesTheProjectWithNewDescriptionAndNewCompletedStatus(String projectTitle, String newDescription, String newCompleted) {
        // todo
    }

    @Given("the following projects exist in the system:")
    public void theFollowingProjectsExistInTheSystem() {
    }

    @Then("the project {string} should have description {string} and completed status {string}")
    public void theProjectShouldHaveDescriptionAndCompletedStatus(String projectTitle, String newDescription, String newCompleted) {
    }

    @Then("the number of projects in the system is {string}")
    public void theNumberOfProjectsInTheSystemIs(String expectedProjectCount) {
    }

    @When("a user updates the project {string} with new active status {string}")
    public void aUserUpdatesTheProjectWithNewActiveStatus(String projectTitle, String newActive) {
    }

    @Then("the project {string} should have active status {string}")
    public void theProjectShouldHaveActiveStatus(String projectTitle, String newActive) {
    }

    @Then("the following {string} shall be raised")
    public void theFollowingShallBeRaised(String error) {
    }

    @Then("the status code returned by the API is {string}")
    public void theStatusCodeReturnedByTheAPIIs(String statusCode) {
    }
    @Then("the system is restored to the original state")
    public void theSystemIsRestoredToTheOriginalState() {
        // todo
    }
}
