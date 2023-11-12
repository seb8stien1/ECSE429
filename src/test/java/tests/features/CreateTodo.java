package tests.features;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class CreateTodo {
    @When("a user attempts to create a new todo with the title {string}, description {string}, and doneStatus {string}")
    public void aUserAttemptsToCreateANewTodoWithTheTitleDescriptionAndDoneStatus(String title, String description, String doneStatus) {
        // todo
    }

    @Then("a new todo with the title {string}, description {string}, and doneStatus {string} shall be created")
    public void aNewTodoWithTheTitleDescriptionAndDoneStatusShallBeCreated(String title, String description, String doneStatus) {
        // todo
    }

    @Then("the system is restored to the original state")
    public void theSystemIsRestoredToTheOriginalState() {
        // todo
    }

}
