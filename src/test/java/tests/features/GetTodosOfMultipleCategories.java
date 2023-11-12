package tests.features;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class GetTodosOfMultipleCategories {
    @Given("the following todos exist in the system:")
    public void theFollowingTodosExistInTheSystem() {
    }

    @Given("the following todo and category association exist in the system:")
    public void theFollowingTodoAndCategoryAssociationExistInTheSystem() {
    }

    @When("a user retrieves todos linked to categories {string} and {string}")
    public void aUserRetrievesTodosLinkedToCategoriesAnd(String categoryName1, String categoryName2) {
        // todo
    }

    @Then("the {string} linked to {string} and {string} should be the same")
    public void theLinkedToAndShouldBeTheSame(String todoTitle, String categoryName1, String categoryName2) {
    }

    @When("a user retrieves the todo linked only to the category {string}")
    public void aUserRetrievesTheTodoLinkedOnlyToTheCategory(String categoryName) {
    }

    @Then("only the todo {string} linked to the category {string} should be returned")
    public void onlyTheTodoLinkedToTheCategoryShouldBeReturned(String todoTitle, String categoryName) {
    }

    @Then("the system is restored to the original state")
    public void theSystemIsRestoredToTheOriginalState() {
        // todo
    }

    @When("a user attempts to retrieve todos linked to a non-existent category {string}")
    public void aUserAttemptsToRetrieveTodosLinkedToANonExistentCategory(String nonExistentCategory) {
    }

    @Then("an error should be raised")
    public void anErrorShouldBeRaised() {
    }

    @And("the status code returned by the API is {string}")
    public void theStatusCodeReturnedByTheAPIIs(String statusCode) {
    }
}
