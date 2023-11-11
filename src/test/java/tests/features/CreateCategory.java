package tests.features;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class CreateCategory {
    @When("a user creates a category with title {string} and description {string}")
    public void aUserCreatesACategoryWithTitleAndDescription(String categoryTitle, String categoryDescription) {
        // todo
    }

    @Then("a new category with title {string} and {string} should be created")
    public void aNewCategoryWithTitleAndShouldBeCreated(String categoryTitle, String categoryDescription) {
        // todo
    }
}
