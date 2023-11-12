package tests.features;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class UpdateCategory {
    @Given("the following categories exist in the system:")
    public void theFollowingCategoriesExistInTheSystem() {
    }

    @When("a user updates the category {string} with new description {string}")
    public void aUserUpdatesTheCategoryWithNewDescription(String categoryTitle, String newDescription) {
        // todo
    }

    @Then("the category {string} should have description {string}")
    public void theCategoryShouldHaveDescription(String categoryName, String newDescription) {
    }

    @Then("the number of categories in the system is {string}")
    public void theNumberOfCategoriesInTheSystemIs(String expectedCategoryCount) {
    }

    @When("a user updates the category {string} with new title {string}")
    public void aUserUpdatesTheCategoryWithNewTitle(String categoryName, String newTitle) {
    }

    @Then("an error should be raised")
    public void anErrorShouldBeRaised() {
    }

    @Then("the system is restored to the original state")
    public void theSystemIsRestoredToTheOriginalState() {
        // todo
    }

    @Then("the status code returned by the API is {string}")
    public void theStatusCodeReturnedByTheAPIIs(String statusCode) {
    }
}
