package tests.features;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class GetProjectDetails {

    @Given("the following projects exist in the system:")
    public void theFollowingProjectsExistInTheSystem() {
    }
    @When("a user retrieves details of the project with title {string}")
    public void aUserRetrievesDetailsOfTheProjectWithTitle(String projectTitle) {
        // todo
    }
    @Then("the project returned has description {string}, has completed status {string} and active status {string}")
    public void theProjectReturnedHasDescriptionHasCompletedStatusAndActiveStatus(String projectDescription, String completed, String active) {
    }

    @Then("the system is restored to the original state")
    public void theSystemIsRestoredToTheOriginalState() {
        // todo
    }

    @Then("an error should be raised")
    public void anErrorShouldBeRaised() {
    }

    @And("the status code returned by the API is {string}")
    public void theStatusCodeReturnedByTheAPIIs(String statusCode) {
    }
}
