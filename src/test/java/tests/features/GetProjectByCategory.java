package tests.features;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class GetProjectByCategory {
    @Given("the following projects are categorized under Development:")
    public void theFollowingProjectsAreCategorizedUnderDevelopment() {
        // todo
    }

    @When("a user retrieves projects under the category {string}")
    public void aUserRetrievesProjectsUnderTheCategory(String categoryTitle) {
        // todo
    }

    @Then("the projects under the category {string} are returned")
    public void theProjectsUnderTheCategoryAreReturned(String categoryTitle) {
        // todo
    }

    @Then("the system should return an empty list indicating there are no projects for the given category")
    public void theSystemShouldReturnAnEmptyListIndicatingThereAreNoProjectsForTheGivenCategory() {
        // todo
    }

    @When("a user retrieves projects under the non-existent category {string}")
    public void aUserRetrievesProjectsUnderTheNonExistentCategory(String  categoryTitle) {
        // todo
    }
}
