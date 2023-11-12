package tests.features;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class UpdateTodo {
    @When("a user attempts to update the todo with the title {string} with new description {string} and same doneStatus {string}")
    public void aUserAttemptsToUpdateTheTodoWithTheTitleWithNewDescriptionAndSameDoneStatus(String title, String newDescription, String sameDoneStatus) {
        // todo
    }

    @Then("the todo with the title {string} shall be updated with new description {string} and same doneStatus {string}")
    public void theTodoWithTheTitleShallBeUpdatedWithNewDescriptionAndSameDoneStatus(String title, String newDescription, String sameDoneStatus) {
        // todo
    }

    @When("a user attempts to update the todo with the title {string} with same description {string} and new doneStatus {string}")
    public void aUserAttemptsToUpdateTheTodoWithTheTitleWithSameDescriptionAndNewDoneStatus(String title, String sameDescription, String newDoneStatus) {
        // todo
    }

    @Then("the todo with the title {string} shall be updated with same description {string} and doneStatus {string}")
    public void theTodoWithTheTitleShallBeUpdatedWithSameDescriptionAndDoneStatus(String title, String sameDescription, String newDoneStatus) {
        // todo
    }

    @Then("the system is restored to the original state")
    public void theSystemIsRestoredToTheOriginalState() {
        // todo
    }
}
