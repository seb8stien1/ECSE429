package tests.features;

import helpers.ApiHelper;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class CommonStepDefinitions {
    @Given("the API server is running and available")
    public void theAPIServerIsRunningAndAvailable() throws IOException {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpResponse response = ApiHelper.sendHttpRequest("get", "http://localhost:4567/docs", null, httpClient);
        int statusCode = response.getStatusLine().getStatusCode();
        assertEquals(HttpStatus.SC_OK, statusCode);
    }

    @Given("the following projects exist in the system:")
    public void theFollowingProjectsExistInTheSystem() {
        // todo
    }

    @Given("the following todos exist in the system:")
    public void theFollowingTodosExistInTheSystem() {
        // todo
    }

    @Given("the following categories exist in the system:")
    public void theFollowingCategoriesExistInTheSystem() {
        // todo
    }

    @And("the number of todos in the system is {string}")
    public void theNumberOfTodosInTheSystemIs(String expectedTodoCount) {
        // todo
    }

    @And("the number of projects in the system is {string}")
    public void theNumberOfProjectsInTheSystemIs(String expectedProjectCount) {
        // todo
    }

    @And("the number of categories in the system is {string}")
    public void theNumberOfCategoriesInTheSystemIs(String expectedCategoryCount) {
        // todo
    }

    @Then("an error should be raised")
    public void anErrorShouldBeRaised() {
        // todo
    }

    @Then("the following {string} shall be raised")
    public void theFollowingErrorShallBeRaised(String error) {
        // todo
    }

    @Then("the status code returned by the API is {string}")
    public void theStatusCodeReturnedByTheAPIIs(String statusCode) {
        // todo
    }

}
