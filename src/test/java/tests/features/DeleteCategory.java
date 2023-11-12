package tests.features;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class DeleteCategory {
    @When("a user deletes the category {string}")
    public void aUserDeletesTheCategory(String categoryName) {
        // todo
    }

    @Then("the category {string} should be removed from the system")
    public void theCategoryShouldBeRemovedFromTheSystem(String categoryName) {
        // todo
    }

    @Given("the category {string} is already deleted")
    public void theCategoryIsAlreadyDeleted(String categoryName) {
        // todo
    }

    @When("a user attempts to delete the category with an invalid ID {string}")
    public void aUserAttemptsToDeleteTheCategoryWithAnInvalidID(String categoryID) {
        // todo
    }
}
