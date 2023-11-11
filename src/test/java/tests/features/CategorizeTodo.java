package tests.features;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class CategorizeTodo {
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
}
