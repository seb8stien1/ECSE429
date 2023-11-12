package tests.features;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class GetProjectDetails {

    @When("a user retrieves details of the project with title {string}")
    public void aUserRetrievesDetailsOfTheProjectWithTitle(String projectTitle) {
        // todo
    }
    @Then("the project returned has description {string}, has completed status {string} and active status {string}")
    public void theProjectReturnedHasDescriptionHasCompletedStatusAndActiveStatus(String projectDescription, String completed, String active) {
        // todo
    }

    @Then("the project returned has blank description {string}")
    public void theProjectReturnedHasBlankDescription(String description) {
        // todo
    }

    @When("a user retrieves details of the non-existent project with title {string}")
    public void aUserRetrievesDetailsOfTheNonExistentProjectWithTitle(String projectTitle) {
        // todo
    }

}
