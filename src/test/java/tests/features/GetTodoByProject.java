package tests.features;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class GetTodoByProject {
    @Given("the following todos are associated with the Website Redesign project:")
    public void theFollowingTodosAreAssociatedWithTheWebsiteRedesignProject() {
        // todo
    }

    @Given("the following projects exist in the system:")
    public void theFollowingProjectsExistInTheSystem() {
    }

    @When("a user attempts to get todos for the project {string}")
    public void aUserAttemptsToGetTodosForTheProject(String projectTitle) {
    }

    @Then("the system should return todos for the project {string}")
    public void theSystemShouldReturnTodosForTheProject(String projectTitle) {
    }

    @Then("the system should return an empty list indicating there are no todos for the given project")
    public void theSystemShouldReturnAnEmptyListIndicatingThereAreNoTodosForTheGivenProject() {
    }

    @Then("the system is restored to the original state")
    public void theSystemIsRestoredToTheOriginalState() {
        // todo
    }
    @Then("an error should be raised")
    public void anErrorShouldBeRaised() {
    }

    @Then("the status code returned by the API is {string}")
    public void theStatusCodeReturnedByTheAPIIs(String statusCode) {
    }
}
