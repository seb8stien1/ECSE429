package tests.features;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class RemoveTodoFromProject {
    @Given("the following todos are associated with the Marketing Campaign project:")
    public void theFollowingTodosAreAssociatedWithTheMarketingCampaignProject() {
        // todo
    }

    @Given("the following projects exist in the system:")
    public void theFollowingProjectsExistInTheSystem() {
    }

    @Given("the following todos exist in the system:")
    public void theFollowingTodosExistInTheSystem() {
    }

    @When("a user attempts to remove the todo {string} from the project {string}")
    public void aUserAttemptsToRemoveTheTodoFromTheProject(String todoTitle, String projectTitle) {
    }

    @Then("the todo {string} should no longer be linked to the project {string}")
    public void theTodoShouldNoLongerBeLinkedToTheProject(String todoTitle, String projectTitle) {
    }

    @Then("the system is restored to the original state")
    public void theSystemIsRestoredToTheOriginalState() {
        // todo
    }

    @And("the status code returned by the API is {string}")
    public void theStatusCodeReturnedByTheAPIIs(String statusCode) {
    }
}
