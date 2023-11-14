package tests.features;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import lombok.AllArgsConstructor;
import org.apache.http.HttpResponse;
import org.apache.http.impl.client.CloseableHttpClient;
import response.Project;
import response.Todo;
import response.TodoResponse;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import static helpers.ApiHelper.deserialize;
import static helpers.ProjectHelper.deleteAssociation;
import static helpers.ProjectHelper.getAssociation;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;

/**
 * Step definitions for removing a todo from a project.
 */
@AllArgsConstructor
public class RemoveTodoFromProject {

    private final TestContext testContext;

    /**
     * Attempts to remove a todo from a project.
     *
     * @param todoTitle The title of the todo to be removed.
     * @param projectTitle The title of the project from which the todo is to be removed.
     * @throws IOException if an I/O error occurs during the HTTP request.
     */
    @When("a user attempts to remove the todo {string} from the project {string}")
    public void aUserAttemptsToRemoveTheTodoFromTheProject(String todoTitle, String projectTitle) throws IOException {
        CloseableHttpClient httpClient = testContext.get("httpClient", CloseableHttpClient.class);
        HashMap<String, Todo> createdTodos = testContext.get("createdTodos", HashMap.class);
        HashMap<String, Project> createdProjects = testContext.get("createdProjects", HashMap.class);

        String todoID = createdTodos.get(todoTitle).getId();
        String projectID = createdProjects.get(projectTitle).getId();

        HttpResponse response = deleteAssociation("tasks", projectID, todoID, httpClient);
        testContext.set("statusCode", response.getStatusLine().getStatusCode());
    }

    /**
     * Validates that a todo is no longer linked to a project.
     *
     * @param todoTitle The title of the todo.
     * @param projectTitle The title of the project.
     * @throws IOException if an I/O error occurs during the HTTP request.
     */
    @Then("the todo {string} should no longer be linked to the project {string}")
    public void theTodoShouldNoLongerBeLinkedToTheProject(String todoTitle, String projectTitle) throws IOException {
        CloseableHttpClient httpClient = testContext.get("httpClient", CloseableHttpClient.class);
        HashMap<String, Project> createdProjects = testContext.get("createdProjects", HashMap.class);
        HashMap<String, Todo> createdTodos = testContext.get("createdTodos", HashMap.class);

        String projectID = createdProjects.get(projectTitle).getId();

        HttpResponse response = getAssociation("tasks", projectID, httpClient);
        TodoResponse todoResponse = deserialize(response, TodoResponse.class);

        List<String> associatedTodoIds = todoResponse.getTodos().stream()
                .map(Todo::getId)
                .toList();

        String todoID = createdTodos.get(todoTitle).getId();

        assertFalse("The Todo should not be linked to the Project anymore", associatedTodoIds.contains(todoID));
    }

    /**
     * Attempts to remove a todo from a non-existent project.
     *
     * @param todoTitle The title of the todo.
     * @param projectTitle The title of the non-existent project.
     * @throws IOException if an I/O error occurs during the HTTP request.
     */
    @When("a user attempts to remove the todo {string} from the non-existent project {string}")
    public void aUserAttemptsToRemoveTheTodoFromTheNonExistentProject(String todoTitle, String projectTitle) throws IOException {
        CloseableHttpClient httpClient = testContext.get("httpClient", CloseableHttpClient.class);
        HashMap<String, Todo> createdTodos = testContext.get("createdTodos", HashMap.class);
        HashMap<String, Project> createdProjects = testContext.get("createdProjects", HashMap.class);

        Project project = createdProjects.get(projectTitle);
        assertNull("Project should not exist", project);
        String nonExistentProjectID = UUID.randomUUID().toString();

        String todoID = createdTodos.get(todoTitle).getId();

        HttpResponse response = deleteAssociation("tasks", nonExistentProjectID, todoID, httpClient);
        testContext.set("statusCode", response.getStatusLine().getStatusCode());
    }
}
