package tests.features;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class UpdateTodo {
    @Given("the following todos exist in the system:")
    public void theFollowingTodosExistInTheSystem() {
        // todo
    }

    @When("a user attempts to update the todo with the title {string} with new description {string} and same doneStatus {string}")
    public void aUserAttemptsToUpdateTheTodoWithTheTitleWithNewDescriptionAndSameDoneStatus(String title, String newDescription, String sameDoneStatus) {
        // todo
    }

    @Then("the todo with the title {string} shall be updated with new description {string} and same doneStatus {string}")
    public void theTodoWithTheTitleShallBeUpdatedWithNewDescriptionAndSameDoneStatus(String title, String newDescription, String sameDoneStatus) {
        // todo
    }

    @Then("the number of todos in the system is {string}")
    public void theNumberOfTodosInTheSystemIs(String expectedTodoCount) {
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

    @Then("the following {string} shall be raised")
    public void theFollowingErrorShallBeRaised(String error) {
        // todo
    }

    @And("the status code returned by the API is {string}")
    public void theStatusCodeReturnedByTheAPIIs(String statusCode) {
        // todo
    }
}
