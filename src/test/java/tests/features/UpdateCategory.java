package tests.features;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class UpdateCategory {
    @When("a user updates the category {string} with new description {string}")
    public void aUserUpdatesTheCategoryWithNewDescription(String categoryTitle, String newDescription) {
        // todo
    }

    @Then("the category {string} should have description {string}")
    public void theCategoryShouldHaveDescription(String categoryTitle, String newDescription) {
        // todo
    }

    @When("a user updates the category {string} with new title {string}")
    public void aUserUpdatesTheCategoryWithNewTitle(String categoryTitle, String newTitle) {
        // todo
    }
}
