package tests.features;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class CreateTodo {


    @Given("the following todos exist in the system:")
    public void theFollowingTodosExistInTheSystem() {
    }

    @When("a user attempts to create a new todo with the title {string}, description {string}, and doneStatus {string}")
    public void aUserAttemptsToCreateANewTodoWithTheTitleDescriptionAndDoneStatus(String title, String description, String doneStatus) {
    }

    @Then("a new todo with the title {string}, description {string}, and doneStatus {string} shall be created")
    public void aNewTodoWithTheTitleDescriptionAndDoneStatusShallBeCreated(String title, String description, String doneStatus) {
    }

    @Then("the number of todos in the system is {string}")
    public void theNumberOfTodosInTheSystemIs(String expectedTodoCount) {
    }

    @Then("the following {string} shall be raised")
    public void theFollowingErrorShallBeRaised(String error) {
    }

    @And("the status code returned by the API is {string}")
    public void theStatusCodeReturnedByTheAPIIs(String statusCode) {
    }

}
