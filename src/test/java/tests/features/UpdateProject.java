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
import static org.junit.Assert.assertEquals;

@AllArgsConstructor
public class UpdateProject {

    private final TestContext testContext;

    @When("a user updates the project {string} with new description {string} and new completed status {string}")
    public void aUserUpdatesTheProjectWithNewDescriptionAndNewCompletedStatus(String projectTitle, String newDescription, String newCompleted) throws IOException {
        HashMap<String, Project> createdProjects = testContext.get("createdProjects", HashMap.class);
        CloseableHttpClient httpClient = testContext.get("httpClient", CloseableHttpClient.class);

        Project project = createdProjects.get(projectTitle);
        String projectID = project.getId();
        String projectActive = project.getActive().toString();
        HttpResponse response = ProjectHelper.modifyProjectPut(projectID, projectTitle, newCompleted, projectActive, newDescription, httpClient);
        Project modifiedProject = deserialize(response, Project.class);
        createdProjects.put(projectTitle, modifiedProject);
        testContext.set("createdProjects", createdProjects);

        testContext.set("statusCode", response.getStatusLine().getStatusCode());
    }

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
