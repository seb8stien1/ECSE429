package tests.features;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class GetTodoByProject {
    @Given("the following todos are associated with the Website Redesign project:")
    public void theFollowingTodosAreAssociatedWithTheWebsiteRedesignProject() {
        // todo
    }

    @When("a user attempts to get todos for the project {string}")
    public void aUserAttemptsToGetTodosForTheProject(String projectTitle) {
        // todo
    }

    @Then("the system should return todos for the project {string}")
    public void theSystemShouldReturnTodosForTheProject(String projectTitle) {
        // todo
    }

    @Then("the system should return an empty list indicating there are no todos for the given project")
    public void theSystemShouldReturnAnEmptyListIndicatingThereAreNoTodosForTheGivenProject() {
        // todo
    }

    @When("a user attempts to get todos for the non-existent project {string}")
    public void aUserAttemptsToGetTodosForTheNonExistentProject(String projectTitle) {
        // todo
    }
}
