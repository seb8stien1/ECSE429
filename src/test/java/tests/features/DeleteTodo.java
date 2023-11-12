package tests.features;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class DeleteTodo {

    @When("a user attempts to delete the todo with the title {string}")
    public void aUserAttemptsToDeleteTheTodoWithTheTitle(String todoTitle) {
        // todo
    }

    @Then("the todo with the title {string} shall be removed from the system")
    public void theTodoWithTheTitleShallBeRemovedFromTheSystem(String todoTitle) {
        // todo
    }

    @When("a user attempts to delete the todo with an invalid ID {string}")
    public void aUserAttemptsToDeleteTheTodoWithAnInvalidID(String todoID) {
        // todo
    }

    @Then("an error should be raised")
    public void anErrorShouldBeRaised() {
        // todo
    }
}
