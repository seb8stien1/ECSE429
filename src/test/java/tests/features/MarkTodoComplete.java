package tests.features;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class MarkTodoComplete {

    @Given("the following todos exist in the system:")
    public void theFollowingTodosExistInTheSystem() {
    }

    @When("a user attempts to mark the todo titled {string} with doneStatus {string} as completed {string}")
    public void aUserAttemptsToMarkTheTodoTitledWithDoneStatusAsCompleted(String title, String doneStatus, String completed) {
        // todo
    }

    @Then("the todo titled {string} should have doneStatus as {string}")
    public void theTodoTitledShouldHaveDoneStatusAs(String todoTitle, String doneStatus) {
    }

    @When("a user attempts to mark the non-existent todo titled {string} with doneStatus as {string}")
    public void aUserAttemptsToMarkTheNonExistentTodoTitledWithDoneStatusAs(String todoTitle, String doneStatus) {
    }

    @Then("an error should be raised")
    public void anErrorShouldBeRaised() {
    }

    @Then("the status code returned by the API is {string}")
    public void theStatusCodeReturnedByTheAPIIs(String statusCode) {
    }

    @Then("the system is restored to the original state")
    public void theSystemIsRestoredToTheOriginalState() {
        // todo
    }

    @Then("the number of todos in the system is {string}")
    public void theNumberOfTodosInTheSystemIs(String expectedTodoCount) {
    }
}
