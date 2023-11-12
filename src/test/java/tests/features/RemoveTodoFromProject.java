package tests.features;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class RemoveTodoFromProject {
    @Given("the following todos are associated with the Marketing Campaign project:")
    public void theFollowingTodosAreAssociatedWithTheMarketingCampaignProject() {
        // todo
    }

    @When("a user attempts to remove the todo {string} from the project {string}")
    public void aUserAttemptsToRemoveTheTodoFromTheProject(String todoTitle, String projectTitle) {
        // todo
    }

    @Then("the todo {string} should no longer be linked to the project {string}")
    public void theTodoShouldNoLongerBeLinkedToTheProject(String todoTitle, String projectTitle) {
        // todo
    }

    @Then("the system is restored to the original state")
    public void theSystemIsRestoredToTheOriginalState() {
        // todo
    }
}
