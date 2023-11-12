package tests.features;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class CreateCategory {

    @Given("the following categories exist in the system:")
    public void theFollowingCategoriesExistInTheSystem() {

    }
    @When("a user creates a category with title {string} and description {string}")
    public void aUserCreatesACategoryWithTitleAndDescription(String categoryTitle, String categoryDescription) {
        // todo
    }

    @Then("a new category with title {string} and {string} should be created")
    public void aNewCategoryWithTitleAndShouldBeCreated(String categoryTitle, String categoryDescription) {
        // todo
    }

    @Then("the number of categories in the system is {string}")
    public void theNumberOfCategoriesInTheSystemIs(String expectedCategoryCount) {
        //todo
    }

    @Then("the system is restored to the original state")
    public void theSystemIsRestoredToTheOriginalState() {
        // todo
    }

    @Then("an error should be raised")
    public void anErrorShouldBeRaised() {
        //todo
    }

    @And("the status code returned by the API is {string}")
    public void theStatusCodeReturnedByTheAPIIs(String statusCode) {
        //todo
    }
}
