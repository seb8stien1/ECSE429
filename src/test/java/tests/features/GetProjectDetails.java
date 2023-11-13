package tests.features;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import lombok.AllArgsConstructor;
import org.apache.http.HttpResponse;
import org.apache.http.impl.client.CloseableHttpClient;
import response.Project;
import response.ProjectResponse;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import static helpers.ApiHelper.deserialize;
import static helpers.ProjectHelper.getProject;
import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

@AllArgsConstructor
public class GetProjectDetails {

    private final TestContext testContext;

    @When("a user retrieves details of the project with title {string}")
    public void aUserRetrievesDetailsOfTheProjectWithTitle(String projectTitle) throws IOException {
        HashMap<String, Project> createdProjects = testContext.get("createdProjects", HashMap.class);
        CloseableHttpClient httpClient = testContext.get("httpClient", CloseableHttpClient.class);

        String projectID = createdProjects.get(projectTitle).getId();

        HttpResponse response = getProject(projectID,httpClient);
        ProjectResponse projectResponse = deserialize(response, ProjectResponse.class);
        List<Project> retrievedProjects = projectResponse.getProjects();


        testContext.set("retrievedProjects", retrievedProjects);
        testContext.set("statusCode", response.getStatusLine().getStatusCode());
    }
    @Then("the project returned has description {string}, has completed status {string} and active status {string}")
    public void theProjectReturnedHasDescriptionHasCompletedStatusAndActiveStatus(String projectDescription, String completed, String active) {
        List<Project> retrievedProjects = testContext.get("retrievedProjects", List.class);
        Project retrievedProject = retrievedProjects.get(0);

        assertEquals(projectDescription, retrievedProject.getDescription());
        assertEquals(Boolean.valueOf(completed), retrievedProject.getCompleted());
        assertEquals(Boolean.valueOf(active), retrievedProject.getActive());
    }

    @Then("the project returned has blank description {string}")
    public void theProjectReturnedHasBlankDescription(String description) {
        List<Project> retrievedProjects = testContext.get("retrievedProjects", List.class);
        Project retrievedProject = retrievedProjects.get(0);

        assertEquals(description, retrievedProject.getDescription());
    }

    @When("a user retrieves details of the non-existent project with title {string}")
    public void aUserRetrievesDetailsOfTheNonExistentProjectWithTitle(String projectTitle) throws IOException {
        HashMap<String, Project> createdProjects = testContext.get("createdProjects", HashMap.class);
        CloseableHttpClient httpClient = testContext.get("httpClient", CloseableHttpClient.class);

        Project nonExistentProject = createdProjects.get(projectTitle);
        assertNull(nonExistentProject);
        String nonExistentProjectID = "103i20023i0309458934539";

        HttpResponse response = getProject(nonExistentProjectID,httpClient);

        testContext.set("response", response);
        testContext.set("statusCode", response.getStatusLine().getStatusCode());
    }

}
