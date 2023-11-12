package tests.features;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class MarkTodoComplete {

    @When("a user attempts to mark the todo titled {string} with doneStatus {string} as completed {string}")
    public void aUserAttemptsToMarkTheTodoTitledWithDoneStatusAsCompleted(String title, String doneStatus, String completed) {
        // todo
    }

    @Then("the todo titled {string} should have doneStatus as {string}")
    public void theTodoTitledShouldHaveDoneStatusAs(String todoTitle, String doneStatus) {
        // todo
    }

    @When("a user attempts to mark the non-existent todo titled {string} with doneStatus as {string}")
    public void aUserAttemptsToMarkTheNonExistentTodoTitledWithDoneStatusAs(String todoTitle, String doneStatus) {
        // todo
    }
}
