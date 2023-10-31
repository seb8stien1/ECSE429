package tests.features;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class GetTodoByCategory {
    @Given("the following todos exist in the system:")
    public void theFollowingTodosExistInTheSystem() {
        // todo
    }

    @Given("the following categories exist in the system:")
    public void theFollowingCategoriesExistInTheSystem() {
        // todo
    }

    @When("a user attempts to get todos with the category {string}")
    public void aUserAttemptsToGetTodosWithTheCategory(String categoryTitle) {
        // todo
    }

    @Then("the system should return todos with the category {string}")
    public void theSystemShouldReturnTodosWithTheCategory(String categoryTitle) {
        // todo
    }

    @Then("the system should return an empty list indicating there are no todos for the given category")
    public void theSystemShouldReturnAnEmptyListIndicatingThereAreNoTodosForTheGivenCategory() {
        // todo
    }

    @Then("the status code returned by the API is {string}")
    public void theStatusCodeReturnedByTheAPIIs(String statusCode) {
        // todo
    }
}
