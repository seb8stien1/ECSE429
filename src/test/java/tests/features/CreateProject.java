package tests.features;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class CreateProject {
    @When("a user attempts to create a project with title {string} and description {string}")
    public void aUserAttemptsToCreateAProjectWithTitleAndDescription(String projectTitle, String projectDescription) {
        // todo
    }

    @Then("a new project with title {string} and description {string} is created")
    public void aNewProjectWithTitleAndDescriptionIsCreated(String projectTitle, String projectDescription) {
    }

    @And("the number of projects in the system is {string}")
    public void theNumberOfProjectsInTheSystemIs(String expectedProjectCount) {
    }

    @When("a user attempts to create a project with an invalid title {string}")
    public void aUserAttemptsToCreateAProjectWithAnInvalidTitle(String invalidTitle) {
    }

    @Then("the system is restored to the original state")
    public void theSystemIsRestoredToTheOriginalState() {
        // todo
    }

    @Then("an error should be raised")
    public void anErrorShouldBeRaised() {
    }

    @And("the status code returned by the API is {string}")
    public void theStatusCodeReturnedByTheAPIIs(String statusCode) {
    }
}
