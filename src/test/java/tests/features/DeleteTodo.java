package tests.features;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class DeleteTodo {
    @Given("the following projects exist in the system:")
    public void theFollowingProjectsExistInTheSystem() {
    }

    @Given("the following todos exist in the system:")
    public void theFollowingTodosExistInTheSystem() {
    }

    @When("a user attempts to delete the todo with the title {string}")
    public void aUserAttemptsToDeleteTheTodoWithTheTitle(String todoTitle) {
    }

    @Then("the todo with the title {string} shall be removed from the system")
    public void theTodoWithTheTitleShallBeRemovedFromTheSystem(String todoTitle) {
    }

    @And("the number of todos in the system is {string}")
    public void theNumberOfTodosInTheSystemIs(String expectedTodoCount) {
    }

    @Given("the todo with the title {string} is already deleted")
    public void theTodoWithTheTitleIsAlreadyDeleted(String todoTitle) {
    }

    @Then("the status code returned by the API is {string}")
    public void theStatusCodeReturnedByTheAPIIs(String statusCode) {
    }

    @Then("the system is restored to the original state")
    public void theSystemIsRestoredToTheOriginalState() {
        // todo
    }

    @When("a user attempts to delete the todo with an invalid ID {string}")
    public void aUserAttemptsToDeleteTheTodoWithAnInvalidID(String todoID) {
    }

    @Then("an error should be raised")
    public void anErrorShouldBeRaised() {
    }
}
