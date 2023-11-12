package tests.features;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class GetTodosOfMultipleCategories {

    @When("a user retrieves todos linked to categories {string} and {string}")
    public void aUserRetrievesTodosLinkedToCategoriesAnd(String categoryTitle1, String categoryTitle2) {
        // todo
    }

    @Then("the {string} linked to {string} and {string} should be the same")
    public void theLinkedToAndShouldBeTheSame(String todoTitle, String categoryTitle1, String categoryTitle2) {
        // todo
    }

    @When("a user retrieves the todo linked only to the category {string}")
    public void aUserRetrievesTheTodoLinkedOnlyToTheCategory(String categoryTitle) {
        // todo
    }

    @Then("only the todo {string} linked to the category {string} should be returned")
    public void onlyTheTodoLinkedToTheCategoryShouldBeReturned(String todoTitle, String categoryTitle) {
        // todo
    }

    @When("a user attempts to retrieve todos linked to a non-existent category {string}")
    public void aUserAttemptsToRetrieveTodosLinkedToANonExistentCategory(String nonExistentCategory) {
        // todo
    }
}
