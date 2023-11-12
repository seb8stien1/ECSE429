package tests.features;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class DeleteTodo {
    @When("a user attempts to delete the todo with the title {string}")
    public void aUserAttemptsToDeleteTheTodoWithTheTitle(String todoTitle) {
        // TODO
    }

    @Then("the todo with the title {string} shall be removed from the system")
    public void theTodoWithTheTitleShallBeRemovedFromTheSystem(String todoTitle) {
        // TODO
    }

    @Given("the todo with the title {string} is already deleted")
    public void theTodoWithTheTitleIsAlreadyDeleted(String todoTitle) {
        // TODO
    }

    @When("a user attempts to delete the todo with an invalid ID {string}")
    public void aUserAttemptsToDeleteTheTodoWithAnInvalidID(String invalidID) {
        // TODO
    }

    @Then("the system is restored to the original state")
    public void theSystemIsRestoredToTheOriginalState() {
        // TODO
    }
}
