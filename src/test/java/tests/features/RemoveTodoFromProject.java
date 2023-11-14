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

@AllArgsConstructor
public class RemoveTodoFromProject {

    private final TestContext testContext;

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

        assertFalse(associatedTodoIds.contains(todoID));
    }

    @When("a user attempts to remove the todo {string} from the non-existent project {string}")
    public void aUserAttemptsToRemoveTheTodoFromTheNonExistentProject(String todoTitle, String projectTitle) throws IOException {
        CloseableHttpClient httpClient = testContext.get("httpClient", CloseableHttpClient.class);
        HashMap<String, Todo> createdTodos = testContext.get("createdTodos", HashMap.class);
        HashMap<String, Project> createdProjects = testContext.get("createdProjects", HashMap.class);

        Project project = createdProjects.get(projectTitle);
        assertNull(project);
        String nonExistentProjectID = UUID.randomUUID().toString();

        String todoID = createdTodos.get(todoTitle).getId();

        HttpResponse response = deleteAssociation("tasks", nonExistentProjectID, todoID, httpClient);
        testContext.set("statusCode", response.getStatusLine().getStatusCode());
    }
}
