package tests.features;

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
import static helpers.ProjectHelper.createProject;
import static helpers.ProjectHelper.getProject;
import static org.junit.Assert.*;

/**
 * Step definitions for creating projects in the API.
 */
@AllArgsConstructor
public class CreateProject {

    private final TestContext testContext;

    /**
     * Attempts to create a project with the specified title and description.
     *
     * @param projectTitle The title of the project to be created.
     * @param projectDescription The description of the project to be created.
     * @throws IOException if an I/O error occurs when sending or receiving the HTTP request.
     */
    @When("a user attempts to create a project with title {string} and description {string}")
    public void aUserAttemptsToCreateAProjectWithTitleAndDescription(String projectTitle, String projectDescription) throws IOException {
        HashMap<String, Project> createdProjects = new HashMap<>();
        CloseableHttpClient httpClient = testContext.get("httpClient", CloseableHttpClient.class);

        HttpResponse response = createProject(projectTitle, Boolean.FALSE, Boolean.TRUE, projectDescription, httpClient);
        Project createdProject = deserialize(response, Project.class);
        createdProjects.put(projectTitle, createdProject);

        testContext.set("createdProjects", createdProjects);
    }

    /**
     * Verifies that a new project with the specified title and description has been created.
     *
     * @param projectTitle The title of the project to verify.
     * @param projectDescription The description of the project to verify.
     * @throws IOException if an I/O error occurs when sending or receiving the HTTP request.
     */
    @Then("a new project with title {string} and description {string} is created")
    public void aNewProjectWithTitleAndDescriptionIsCreated(String projectTitle, String projectDescription) throws IOException {
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

        assertNotNull(returnedProject);
        assertEquals(projectTitle, returnedProject.getTitle());
        assertEquals(projectDescription, returnedProject.getDescription());
        assertFalse(returnedProject.getCompleted());
        assertTrue(returnedProject.getActive());
    }

    /**
     * Attempts to create a project with an invalid 'completed' status.
     *
     * @param invalidCompleted The invalid 'completed' status used to create a project.
     * @throws IOException if an I/O error occurs when sending or receiving the HTTP request.
     */
    @When("a user attempts to create a project with an invalid completed {string}")
    public void aUserAttemptsToCreateAProjectWithAnInvalidComplete(String invalidCompleted) throws IOException {
        CloseableHttpClient httpClient = testContext.get("httpClient", CloseableHttpClient.class);

        HttpResponse response = createProject("projectTitle", invalidCompleted, Boolean.TRUE, "projectDescription", httpClient);

        testContext.set("response", response);
        testContext.set("statusCode", response.getStatusLine().getStatusCode());
    }
}
