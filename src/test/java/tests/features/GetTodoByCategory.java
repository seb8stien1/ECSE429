package tests.features;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class GetTodoByCategory {
    @Given("the following todos exist in the system and are associated to the School category:")
    public void theFollowingTodosExistInTheSystemAndAreAssociatedToTheSchoolCategory() {
        // todo
    }

    @When("a user attempts to get todos with the category {string}")
    public void aUserAttemptsToGetTodosWithTheCategory(String categoryTitle) {
        // todo
    }

    @Then("the system should return todos with the category {string}")
    public void theSystemShouldReturnTodosWithTheCategory(String categoryTitle) {
        // todo
    }

    @Then("the system should return an empty list indicating there are no todos for the given category")
    public void theSystemShouldReturnAnEmptyListIndicatingThereAreNoTodosForTheGivenCategory() {
        // todo
    }

    @When("a user attempts to get todos with the invalid category {string}")
    public void aUserAttemptsToGetTodosWithTheInvalidCategory(String categoryTitle) {
        // todo
    }

}
