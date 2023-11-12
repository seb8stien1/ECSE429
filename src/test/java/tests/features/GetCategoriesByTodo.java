package tests.features;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class GetCategoriesByTodo {

    @When("a user retrieves the category for the todo {string}")
    public void aUserRetrievesTheCategoryForTheTodo(String todoTitle) {
        // todo
    }

    @Then("the category for the todo {string} is returned")
    public void theCategoryForTheTodoIsReturned(String todoTitle) {
        // todo
    }

    @Then("the system should return an empty list indicating there are no categories for the given todo")
    public void theSystemShouldReturnAnEmptyListIndicatingThereAreNoCategoriesForTheGivenTodo() {
        // todo
    }

    @When("a user retrieves the category for the non-existent todo {string}")
    public void aUserRetrievesTheCategoryForTheNonExistentTodo(String todoTitle) {
        // todo
    }
}
