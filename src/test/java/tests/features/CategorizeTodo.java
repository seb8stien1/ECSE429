package tests.features;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class CategorizeTodo {

    @Given("the following categories exist in the system:")
    public void theFollowingCategoriesExistInTheSystem() {
    // todo
    }

    @Given("the following todos exist in the system:")
    public void theFollowingTodosExistInTheSystem() {
    }

    @When("a user categorizes the todo {string} as {string}")
    public void aUserCategorizesTheTodoAs(String todoTitle, String categoryName) {
        // todo
    }

    @Then("the todo {string} should be categorized as {string}")
    public void theTodoShouldBeCategorizedAs(String todoTitle, String categoryName) {
        // todo
    }

    @Given("the todo {string} is already categorized as {string}")
    public void theTodoIsAlreadyCategorizedAs(String todoTitle, String categoryName) {
        // todo
    }

    @Then("the system is restored to the original state")
    public void theSystemIsRestoredToTheOriginalState() {
        // todo
    }

    @Then("an error should be raised")
    public void anErrorShouldBeRaised() {
        //todo
    }

    @And("the status code returned by the API is {string}")
    public void theStatusCodeReturnedByTheAPIIs(String statusCode) {
        //todo
    }
}
