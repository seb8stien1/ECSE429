package tests.features;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class GetProjectByCategory {
    @Given("the following projects are categorized under Development:")
    public void theFollowingProjectsAreCategorizedUnderDevelopment() {
        // todo
    }

    @Given("the following categories exist in the system:")
    public void theFollowingCategoriesExistInTheSystem() {
    }

    @When("a user retrieves projects under the category {string}")
    public void aUserRetrievesProjectsUnderTheCategory(String categoryName) {
    }

    @Then("the projects under the category {string} are returned")
    public void theProjectsUnderTheCategoryAreReturned(String categoryName) {
    }

    @Then("the system should return an empty list indicating there are no projects for the given category")
    public void theSystemShouldReturnAnEmptyListIndicatingThereAreNoProjectsForTheGivenCategory() {
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
