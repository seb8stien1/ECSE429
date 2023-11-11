package tests.features;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class CreateProject {
    @When("a user attempts to create a project with title {string} and description {string}")
    public void aUserAttemptsToCreateAProjectWithTitleAndDescription(String projectTitle, String projectDescription) {
        // todo
    }

    @Then("a new project with title {string} and description {string} is created")
    public void aNewProjectWithTitleAndDescriptionIsCreated(String projectTitle, String projectDescription) {
        // todo
    }

    @When("a user attempts to create a project with an invalid title {string}")
    public void aUserAttemptsToCreateAProjectWithAnInvalidTitle(String invalidTitle) {
        // todo
    }
}
