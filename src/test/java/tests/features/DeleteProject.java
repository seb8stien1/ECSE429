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
import java.util.Optional;
import java.util.UUID;

import static org.junit.Assert.*;
import static helpers.ApiHelper.deserialize;
import static helpers.ProjectHelper.deleteProject;
import static helpers.ProjectHelper.getAllProjects;

/**
 * Step definitions for deleting projects and verifying their deletion in the API.
 */
@AllArgsConstructor
public class DeleteProject {

    private final TestContext testContext;

    /**
     * Attempts to delete a project with the specified title from the system.
     *
     * @param projectTitle The title of the project to be deleted.
     * @throws IOException if an I/O error occurs when sending or receiving the HTTP request.
     */
    @When("a user deletes the project with title {string}")
    public void aUserDeletesTheProjectWithTitle(String projectTitle) throws IOException {
        HashMap<String, Project> createdProjects = testContext.get("createdProjects", HashMap.class);
        CloseableHttpClient httpClient = testContext.get("httpClient", CloseableHttpClient.class);

        String projectID = createdProjects.get(projectTitle).getId();

        deleteProject(projectID, httpClient);
    }

    /**
     * Verifies that the project with the specified title has been removed from the system.
     *
     * @param projectTitle The title of the project to verify removal.
     * @throws IOException if an I/O error occurs when sending or receiving the HTTP request.
     */
    @Then("the project with title {string} should be removed from the system")
    public void theProjectWithTitleShouldBeRemovedFromTheSystem(String projectTitle) throws IOException {
        HashMap<String, Project> createdProjects = testContext.get("createdProjects", HashMap.class);
        CloseableHttpClient httpClient = testContext.get("httpClient", CloseableHttpClient.class);

        String projectID = createdProjects.get(projectTitle).getId();

        HttpResponse response = getAllProjects(httpClient);
        ProjectResponse projectResponse = deserialize(response, ProjectResponse.class);

        Optional<Project> projectOptional = projectResponse.getProjects()
                .stream()
                .filter(project -> projectID.equals(project.getId()))
                .findFirst();
        assertFalse(projectOptional.isPresent());
    }

    /**
     * Attempts to delete a project that has already been deleted, identified by its title.
     *
     * @param projectTitle The title of the already deleted project.
     * @throws IOException if an I/O error occurs when sending or receiving the HTTP request.
     */
    @When("a user deletes the already deleted project with title {string}")
    public void aUserDeletesTheAlreadyDeletedProjectWithTitle(String projectTitle) throws IOException {
        HashMap<String, Project> createdProjects = testContext.get("createdProjects", HashMap.class);
        CloseableHttpClient httpClient = testContext.get("httpClient", CloseableHttpClient.class);

        Project nonExistentProject = createdProjects.get(projectTitle);
        assertNull(nonExistentProject);
        String nonExistentProjectID = UUID.randomUUID().toString();

        HttpResponse response = deleteProject(nonExistentProjectID, httpClient);

        int statusCode = response.getStatusLine().getStatusCode();
        testContext.set("statusCode", statusCode);
    }

    /**
     * Attempts to delete a project using an invalid ID.
     *
     * @param invalidID The invalid ID to be used in the deletion attempt.
     * @throws IOException if an I/O error occurs when sending or receiving the HTTP request.
     */
    @When("a user attempts to delete the project with an invalid ID {string}")
    public void aUserAttemptsToDeleteTheProjectWithAnInvalidID(String invalidID) throws IOException {
        CloseableHttpClient httpClient = testContext.get("httpClient", CloseableHttpClient.class);

        HttpResponse response = deleteProject(invalidID, httpClient);

        int statusCode = response.getStatusLine().getStatusCode();
        testContext.set("statusCode", statusCode);
    }
}
