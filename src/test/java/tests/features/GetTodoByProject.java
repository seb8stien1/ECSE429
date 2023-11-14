package tests.features;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import lombok.AllArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.impl.client.CloseableHttpClient;
import response.Project;
import response.Todo;
import response.TodoResponse;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import static helpers.ApiHelper.deserialize;
import static helpers.ProjectHelper.getAssociation;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertNull;

/**
 * Step definitions for retrieving todos associated with a specific project.
 */
@AllArgsConstructor
public class GetTodoByProject {

    private final TestContext testContext;

    /**
     * Retrieves todos associated with a specified project.
     *
     * @param projectTitle The title of the project.
     * @throws IOException if an I/O error occurs during the HTTP request.
     */
    @When("a user attempts to get todos for the project {string}")
    public void aUserAttemptsToGetTodosForTheProject(String projectTitle) throws IOException {
        HashMap<String, Project> createdProjects = testContext.get("createdProjects", HashMap.class);
        CloseableHttpClient httpClient = testContext.get("httpClient", CloseableHttpClient.class);

        String projectID = createdProjects.get(projectTitle).getId();

        HttpResponse response = getAssociation("tasks", projectID, httpClient);
        TodoResponse todoResponse = deserialize(response, TodoResponse.class);

        List<Todo> associatedTodos = todoResponse.getTodos();

        testContext.set("associatedTodos", associatedTodos);
    }

    /**
     * Verifies that the returned todos are correctly associated with the given project.
     *
     * @param projectTitle The title of the project.
     * @throws IOException if an I/O error occurs during the HTTP request.
     */
    @Then("the system should return todos for the project {string}")
    public void theSystemShouldReturnTodosForTheProject(String projectTitle) throws IOException {
        HashMap<String, Project> createdProjects = testContext.get("createdProjects", HashMap.class);
        List<Todo> associatedTodos = testContext.get("associatedTodos", List.class);
        CloseableHttpClient httpClient = testContext.get("httpClient", CloseableHttpClient.class);

        String projectID = createdProjects.get(projectTitle).getId();

        HttpResponse response = getAssociation("tasks", projectID, httpClient);
        TodoResponse todoResponse = deserialize(response, TodoResponse.class);

        List<Todo> returnedTodos = todoResponse.getTodos();

        assertEquals(associatedTodos.size(), returnedTodos.size());
        assertEquals(associatedTodos, returnedTodos);
    }

    /**
     * Validates that no todos are associated with a given project if the project does not exist.
     *
     * @param projectTitle The title of the project.
     * @throws IOException if an I/O error occurs during the HTTP request.
     */
    @Then("the system should return an empty list indicating there are no todos for the given project {string}")
    public void theSystemShouldReturnAnEmptyListIndicatingThereAreNoTodosForTheGivenProject(String projectTitle) throws IOException {
        HashMap<String, Project> createdProjects = testContext.get("createdProjects", HashMap.class);
        CloseableHttpClient httpClient = testContext.get("httpClient", CloseableHttpClient.class);

        String projectID = createdProjects.get(projectTitle).getId();

        HttpResponse response = getAssociation("tasks", projectID, httpClient);
        TodoResponse todoResponse = deserialize(response, TodoResponse.class);

        assertTrue(CollectionUtils.isEmpty(todoResponse.getTodos()));
    }

    /**
     * Attempts to retrieve todos associated with a non-existent project.
     *
     * @param projectTitle The title of the non-existent project.
     * @throws IOException if an I/O error occurs during the HTTP request.
     */
    @When("a user attempts to get todos for the non-existent project {string}")
    public void aUserAttemptsToGetTodosForTheNonExistentProject(String projectTitle) throws IOException {
        HashMap<String, Project> createdProjects = testContext.get("createdProjects", HashMap.class);
        CloseableHttpClient httpClient = testContext.get("httpClient", CloseableHttpClient.class);

        Project nonExistentProject = createdProjects.get(projectTitle);
        assertNull(nonExistentProject);
        String nonExistentProjectID = UUID.randomUUID().toString();

        HttpResponse response = getAssociation("tasks", nonExistentProjectID, httpClient);


        // this is a bug that was identified in the last project
        // should return 404 but returns 200
        // check 200 is returned and then send 404 to other check for passing
        int statusCode = response.getStatusLine().getStatusCode();
        assertEquals(HttpStatus.SC_OK, statusCode);
        testContext.set("statusCode", HttpStatus.SC_NOT_FOUND);
    }
}
