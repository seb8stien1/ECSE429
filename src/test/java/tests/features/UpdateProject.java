package tests.features;

import helpers.ProjectHelper;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import lombok.AllArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.http.HttpResponse;
import org.apache.http.impl.client.CloseableHttpClient;
import response.Project;
import response.ProjectResponse;

import java.io.IOException;
import java.util.HashMap;
import java.util.Optional;

import static helpers.ApiHelper.deserialize;
import static helpers.ProjectHelper.getProject;
import static org.junit.Assert.*;

/**
 * Step definitions for updating project details in the system.
 */
@AllArgsConstructor
public class UpdateProject {

    private final TestContext testContext;

    /**
     * Updates a project's description and completed status.
     *
     * @param projectTitle   The title of the project to update.
     * @param newDescription The new description for the project.
     * @param newCompleted   The new completed status for the project.
     * @throws IOException if an I/O exception occurs.
     */
    @When("a user updates the project {string} with new description {string} and new completed status {string}")
    public void aUserUpdatesTheProjectWithNewDescriptionAndNewCompletedStatus(String projectTitle, String newDescription, String newCompleted) throws IOException {
        HashMap<String, Project> createdProjects = testContext.get("createdProjects", HashMap.class);
        CloseableHttpClient httpClient = testContext.get("httpClient", CloseableHttpClient.class);

        String projectID = createdProjects.get(projectTitle).getId();
        String projectActive = createdProjects.get(projectTitle).getActive().toString();
        HttpResponse response = ProjectHelper.modifyProjectPut(projectID, projectTitle, newCompleted, projectActive, newDescription, httpClient);
        Project modifiedProject = deserialize(response, Project.class);
        createdProjects.put(projectTitle, modifiedProject);
        testContext.set("createdProjects", createdProjects);

        testContext.set("statusCode", response.getStatusLine().getStatusCode());
    }

    /**
     * Validates that a project's description and completed status are updated.
     *
     * @param projectTitle   The title of the project.
     * @param newDescription The new description to verify.
     * @param newCompleted   The new completed status to verify.
     * @throws IOException if an I/O exception occurs.
     */
    @Then("the project {string} should have description {string} and completed status {string}")
    public void theProjectShouldHaveDescriptionAndCompletedStatus(String projectTitle, String newDescription, String newCompleted) throws IOException {
        HashMap<String, Project> createdProjects = testContext.get("createdProjects", HashMap.class);
        CloseableHttpClient httpClient = testContext.get("httpClient", CloseableHttpClient.class);

        String projectID = createdProjects.get(projectTitle).getId();

        HttpResponse response = getProject(projectID, httpClient);
        ProjectResponse projectResponse = deserialize(response, ProjectResponse.class);

        assertFalse(CollectionUtils.isEmpty(projectResponse.getProjects()));
        Optional<Project> projectOptional = projectResponse.getProjects()
                .stream()
                .filter(project -> projectID.equals(project.getId()))
                .findFirst();
        assertTrue(projectOptional.isPresent());
        Project returnedProject = projectOptional.get();

        assertEquals(projectTitle, returnedProject.getTitle());
        assertEquals(newDescription, returnedProject.getDescription());
        assertEquals(Boolean.valueOf(newCompleted), returnedProject.getCompleted());
    }

    /**
     * Updates a project's active status.
     *
     * @param projectTitle The title of the project to update.
     * @param newActive    The new active status for the project.
     * @throws IOException if an I/O exception occurs.
     */
    @When("a user updates the project {string} with new active status {string}")
    public void aUserUpdatesTheProjectWithNewActiveStatus(String projectTitle, String newActive) throws IOException {
        HashMap<String, Project> createdProjects = testContext.get("createdProjects", HashMap.class);
        CloseableHttpClient httpClient = testContext.get("httpClient", CloseableHttpClient.class);

        Project project = createdProjects.get(projectTitle);
        String projectID = project.getId();
        String projectCompleted = project.getCompleted().toString();
        String description = project.getDescription();
        HttpResponse response = ProjectHelper.modifyProjectPut(projectID, projectTitle, projectCompleted, newActive, description, httpClient);
        Project modifiedProject = deserialize(response, Project.class);
        createdProjects.put(projectTitle, modifiedProject);
        testContext.set("createdProjects", createdProjects);

        testContext.set("statusCode", response.getStatusLine().getStatusCode());
    }

    /**
     * Validates that a project's active status is updated.
     *
     * @param projectTitle The title of the project.
     * @param newActive    The new active status to verify.
     * @throws IOException if an I/O exception occurs.
     */
    @Then("the project {string} should have active status {string}")
    public void theProjectShouldHaveActiveStatus(String projectTitle, String newActive) throws IOException {
        HashMap<String, Project> createdProjects = testContext.get("createdProjects", HashMap.class);
        CloseableHttpClient httpClient = testContext.get("httpClient", CloseableHttpClient.class);

        String projectID = createdProjects.get(projectTitle).getId();

        HttpResponse response = getProject(projectID, httpClient);
        ProjectResponse projectResponse = deserialize(response, ProjectResponse.class);

        assertFalse(CollectionUtils.isEmpty(projectResponse.getProjects()));
        Optional<Project> projectOptional = projectResponse.getProjects()
                .stream()
                .filter(project -> projectID.equals(project.getId()))
                .findFirst();
        assertTrue(projectOptional.isPresent());
        Project returnedProject = projectOptional.get();

        assertEquals(projectTitle, returnedProject.getTitle());
        assertEquals(Boolean.valueOf(newActive), returnedProject.getActive());
    }

    /**
     * Updates a project with an invalid active status.
     *
     * @param projectTitle   The title of the project to update.
     * @param invalidActive  The invalid active status.
     * @throws IOException if an I/O exception occurs.
     */
    @When("a user updates the project {string} with an invalid active status {string}")
    public void aUserUpdatesTheProjectWithAnInvalidCompleteStatus(String projectTitle, String invalidActive) throws IOException {
        HashMap<String, Project> createdProjects = testContext.get("createdProjects", HashMap.class);
        CloseableHttpClient httpClient = testContext.get("httpClient", CloseableHttpClient.class);

        Project project = createdProjects.get(projectTitle);
        String projectID = project.getId();
        String projectDescription = project.getDescription();
        String projectComplete = project.getCompleted().toString();
        HttpResponse response = ProjectHelper.modifyProjectPut(projectID, projectTitle, projectComplete, invalidActive, projectDescription, httpClient);
        testContext.set("response", response);
        testContext.set("statusCode", response.getStatusLine().getStatusCode());
    }
}
