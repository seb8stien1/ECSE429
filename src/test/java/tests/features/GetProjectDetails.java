package tests.features;

import io.cucumber.java.en.And;
import io.cucumber.java.en.When;

public class GetProjectDetails {
    @When("a user retrieves details of the project with title {string}")
    public void aUserRetrievesDetailsOfTheProjectWithTitle(String projectTitle) {
        // todo
    }

    @And("the project returned has description {string}, has completed status {string} and active status {string}")
    public void theProjectReturnedHasDescriptionHasCompletedStatusAndActiveStatus(String projectDescription, String completed, String active) {
        // todo
    }
}
