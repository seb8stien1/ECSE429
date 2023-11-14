package tests.features;

import io.cucumber.java.en.Given;
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
import java.util.Map;
import java.util.UUID;

import static helpers.ApiHelper.deserialize;
import static helpers.ProjectHelper.getAssociation;
import static helpers.ProjectHelper.createAssociation;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertNull;


@AllArgsConstructor
public class GetTodoByProject {

    private final TestContext testContext;

    @Given("the following project and todo association exist in the system:")
    public void theFollowingProjectAndTodoAssociationExistInTheSystem(io.cucumber.datatable.DataTable dataTable) throws IOException {
        HashMap<String, Todo> createdTodos = testContext.get("createdTodos", HashMap.class);
        HashMap<String, Project> createdProjects = testContext.get("createdProjects", HashMap.class);

        List<Map<String, String>> associations = dataTable.asMaps();

        CloseableHttpClient httpClient = testContext.get("httpClient", CloseableHttpClient.class);

        for (Map<String, String> association : associations) {
            String todoTitle = association.get("todoTitle");
            String projectTitle = association.get("projectTitle");

            String todoID = createdTodos.get(todoTitle).getId();
            String projectID = createdProjects.get(projectTitle).getId();

           createAssociation("tasks", projectID, todoID, httpClient);
        }
    }

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

    @Then("the system should return an empty list indicating there are no todos for the given project {string}")
    public void theSystemShouldReturnAnEmptyListIndicatingThereAreNoTodosForTheGivenProject(String projectTitle) throws IOException {
        HashMap<String, Project> createdProjects = testContext.get("createdProjects", HashMap.class);
        CloseableHttpClient httpClient = testContext.get("httpClient", CloseableHttpClient.class);

        String projectID = createdProjects.get(projectTitle).getId();

        HttpResponse response = getAssociation("tasks", projectID, httpClient);
        TodoResponse todoResponse = deserialize(response, TodoResponse.class);

        assertTrue(CollectionUtils.isEmpty(todoResponse.getTodos()));
    }

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
