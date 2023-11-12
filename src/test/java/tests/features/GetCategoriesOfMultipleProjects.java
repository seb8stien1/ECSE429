package tests.features;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class GetCategoriesOfMultipleProjects {
    @Given("the following project and category association exist in the system:")
    public void theFollowingProjectAndCategoryAssociationExistInTheSystem() {
        // todo
    }

    @When("a user retrieves categories associated with the project {string}")
    public void aUserRetrievesCategoriesAssociatedWithTheProject(String projectTitle) {
        // todo
    }

    @Then("the categories {string} and {string} associated with {string} should be returned")
    public void theCategoriesAndAssociatedWithShouldBeReturned(String categoryName1, String categoryName2, String projectTitle) {
        // todo
    }

    @When("a user retrieves the categories associated with the project {string}")
    public void aUserRetrievesTheCategoriesAssociatedWithTheProject(String projectTitle) {
        // todo
    }

    @Then("only the category {string} associated with the project {string} should be returned")
    public void onlyTheCategoryAssociatedWithTheProjectShouldBeReturned(String categoryName, String projectTitle) {
        // todo
    }

    @When("a user attempts to retrieve categories associated with a non-existent project {string}")
    public void aUserAttemptsToRetrieveCategoriesAssociatedWithANonExistentProject(String nonExistentProject) {
        // todo
    }
}
