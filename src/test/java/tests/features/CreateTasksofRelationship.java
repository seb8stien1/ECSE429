package tests.features;

import helpers.TodoHelper;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import lombok.AllArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.http.HttpResponse;
import org.apache.http.impl.client.CloseableHttpClient;
import response.Project;
import response.ProjectResponse;
import response.Todo;

import java.io.IOException;
import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;

import static helpers.ApiHelper.deserialize;
import static org.junit.Assert.*;

/**
 * Step definitions for creating 'tasksof' relationships between todos and projects in the API.
 */
@AllArgsConstructor
public class CreateTasksofRelationship {

    private final TestContext testContext;

    /**
     * Adds a task to a project based on their titles.
     *
     * @param todoTitle The title of the task (todo).
     * @param projectTitle The title of the project.
     * @throws IOException if an I/O error occurs when sending or receiving the HTTP request.
     */
    @When("a user adds a task with title {string} to project with title {string}")
    public void aUserAddsATaskWithTitleDescriptionAndDoneStatusToProjectWithTitle(String todoTitle, String projectTitle) throws IOException {
        HashMap<String, Project> createdProjects = testContext.get("createdProjects", HashMap.class);
        HashMap<String, Todo> createdTodos = testContext.get("createdTodos", HashMap.class);
        CloseableHttpClient httpClient = testContext.get("httpClient", CloseableHttpClient.class);

        String projectID = createdProjects.get(projectTitle).getId();
        String todoID = createdTodos.get(todoTitle).getId();

        TodoHelper.createAssociation("tasksof", todoID, projectID, httpClient);
    }

    /**
     * Verifies that a task is associated with a specific project.
     *
     * @param todoTitle The title of the task (todo) to verify.
     * @param projectTitle The title of the project to verify.
     * @throws IOException if an I/O error occurs when sending or receiving the HTTP request.
     */
    @Then("the task {string} should be contained as a task of the project {string}")
    public void thisTaskShouldBeContainedAsATaskOfTheProject(String todoTitle, String projectTitle) throws IOException {
        HashMap<String, Project> createdProjects = testContext.get("createdProjects", HashMap.class);
        HashMap<String, Todo> createdTodos = testContext.get("createdTodos", HashMap.class);
        CloseableHttpClient httpClient = testContext.get("httpClient", CloseableHttpClient.class);

        String projectID = createdProjects.get(projectTitle).getId();
        String todoID = createdTodos.get(todoTitle).getId();

        HttpResponse response = TodoHelper.getAssociation("tasksof", todoID, httpClient);
        ProjectResponse associatedProjects = deserialize(response, ProjectResponse.class);

        assertFalse(CollectionUtils.isEmpty(associatedProjects.getProjects()));
        Optional<Project> projectAssociationOptional = associatedProjects.getProjects()
                .stream()
                .filter(project -> projectID.equals(project.getId()))
                .findFirst();
        assertTrue(projectAssociationOptional.isPresent());
        Project associatedProject = projectAssociationOptional.get();

        assertEquals(projectTitle, associatedProject.getTitle());
        assertEquals(createdProjects.get(projectTitle).getDescription(), associatedProject.getDescription());
        assertEquals(createdProjects.get(projectTitle).getCompleted(), associatedProject.getCompleted());
        assertEquals(createdProjects.get(projectTitle).getActive(), associatedProject.getActive());
    }

    /**
     * Tries to add a non-existent task to a project.
     *
     * @param todoTitle The title of the non-existent task.
     * @param projectTitle The title of the project.
     * @throws IOException if an I/O error occurs when sending or receiving the HTTP request.
     */
    @When("a user adds a non-existent task with title {string} to project with title {string}")
    public void aUserAddsANonExistentTaskWithTitleToProjectWithTitle(String todoTitle, String projectTitle) throws IOException {
        HashMap<String, Project> createdProjects = testContext.get("createdProjects", HashMap.class);
        HashMap<String, Todo> createdTodos = testContext.get("createdTodos", HashMap.class);
        CloseableHttpClient httpClient = testContext.get("httpClient", CloseableHttpClient.class);

        Todo nonExistentTodo = createdTodos.get(todoTitle);
        assertNull(nonExistentTodo);
        String nonExistentTodoID = UUID.randomUUID().toString();

        String projectID = createdProjects.get(projectTitle).getId();

        HttpResponse response = TodoHelper.createAssociation("tasksof", nonExistentTodoID, projectID, httpClient);

        int statusCode = response.getStatusLine().getStatusCode();
        testContext.set("statusCode", statusCode);
    }
}
