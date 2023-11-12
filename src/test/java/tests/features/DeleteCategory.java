package tests.features;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class DeleteCategory {
    @Given("the following categories exist in the system:")
    public void theFollowingCategoriesExistInTheSystem() {
    }

    @When("a user deletes the category {string}")
    public void aUserDeletesTheCategory(String categoryName) {
        // todo
    }

    @Then("the category {string} should be removed from the system")
    public void theCategoryShouldBeRemovedFromTheSystem(String categoryName) {
    }

    @And("the number of categories in the system is {string}")
    public void theNumberOfCategoriesInTheSystemIs(String expectedCategoryCount) {
    }

    @Given("the category {string} is already deleted")
    public void theCategoryIsAlreadyDeleted(String categoryName) {
    }

    @Then("the status code returned by the API is {string}")
    public void theStatusCodeReturnedByTheAPIIs(String statusCode) {
    }

    @Then("the system is restored to the original state")
    public void theSystemIsRestoredToTheOriginalState() {
        // todo
    }

    @When("a user attempts to delete the category with an invalid ID {string}")
    public void aUserAttemptsToDeleteTheCategoryWithAnInvalidID(String categoryID) {
    }

    @Then("an error should be raised")
    public void anErrorShouldBeRaised() {
    }
}
