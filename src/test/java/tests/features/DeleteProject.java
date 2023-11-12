package tests.features;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class DeleteProject {

    @Given("the following projects exist in the system:")
    public void theFollowingProjectsExistInTheSystem() {
    }

    @When("a user deletes the project with title {string}")
    public void aUserDeletesTheProjectWithTitle(String projectTitle) {
        // todo
    }

    @Then("the project with title {string} should be removed from the system")
    public void theProjectWithTitleShouldBeRemovedFromTheSystem(String projectTitle) {
    }

    @And("the number of projects in the system is {string}")
    public void theNumberOfProjectsInTheSystemIs(String expectedProjectCount) {
    }

    @Given("the project with the title {string} is already deleted")
    public void theProjectWithTheTitleIsAlreadyDeleted(String projectTitle) {
    }

    @Then("the status code returned by the API is {string}")
    public void theStatusCodeReturnedByTheAPIIs(String statusCode) {
    }

    @Then("the system is restored to the original state")
    public void theSystemIsRestoredToTheOriginalState() {
        // todo
    }

    @When("a user attempts to delete the project with an invalid ID {string}")
    public void aUserAttemptsToDeleteTheProjectWithAnInvalidID(String projectID) {
    }

    @Then("an error should be raised")
    public void anErrorShouldBeRaised() {
    }
}
