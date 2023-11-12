package tests.features;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class GetCategoriesByTodo {
    @Given("the following project and category association exist in the system:")
    public void theFollowingProjectAndCategoryAssociationExistInTheSystem() {
        // todo
    }

    @Given("the following todos exist in the system:")
    public void theFollowingTodosExistInTheSystem() {
    }

    @Given("the following categories exist in the system:")
    public void theFollowingCategoriesExistInTheSystem() {
    }

    @Given("the following todo and category association exist in the system:")
    public void theFollowingTodoAndCategoryAssociationExistInTheSystem() {
    }

    @When("a user retrieves the category for the todo {string}")
    public void aUserRetrievesTheCategoryForTheTodo(String todoTitle) {
    }

    @Then("the category for the todo {string} is returned")
    public void theCategoryForTheTodoIsReturned(String todoTitle) {
    }

    @Then("the system is restored to the original state")
    public void theSystemIsRestoredToTheOriginalState() {
    }

    @Then("the system should return an empty list indicating there are no categories for the given todo")
    public void theSystemShouldReturnAnEmptyListIndicatingThereAreNoCategoriesForTheGivenTodo() {
    }

    @Then("an error should be raised")
    public void anErrorShouldBeRaised() {
    }

    @Then("the status code returned by the API is {string}")
    public void theStatusCodeReturnedByTheAPIIs(String statusCode) {
    }
}
