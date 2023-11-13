package tests.features;


import helpers.ProjectHelper;
import helpers.TodoHelper;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import lombok.AllArgsConstructor;
import org.apache.http.HttpResponse;
import org.apache.http.impl.client.CloseableHttpClient;
import response.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static helpers.ApiHelper.deserialize;
import static helpers.ProjectHelper.getAssociation;
import static helpers.TodoHelper.createTodo;
import static org.junit.Assert.assertEquals;


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

           TodoHelper.createAssociation("projects", todoID, projectID, httpClient);
        }
    }

    @When("a user attempts to get todos for the project {string}")
    public void aUserAttemptsToGetTodosForTheProject(String projectTitle) throws IOException {
        HashMap<String, Project> createdProjects = testContext.get("createdProjects", HashMap.class);
        CloseableHttpClient httpClient = testContext.get("httpClient", CloseableHttpClient.class);

        String projectID = createdProjects.get(projectTitle).getId();

        HttpResponse response = getAssociation("todos", projectID, httpClient);
        TodoResponse todoResponse = deserialize(response, TodoResponse.class);

        List<Todo> associatedTodos = todoResponse.getTodos();

        testContext.set("associatedTodos", associatedTodos);
    }

    @Then("the system should return todos for the project {string}")
    public void theSystemShouldReturnTodosForTheProject(String projectTitle) throws IOException {
        HashMap<String, Todo> createdProjects = testContext.get("createdProjects", HashMap.class);
        List<Todo> associatedTodos = testContext.get("associatedTodos", List.class);
        CloseableHttpClient httpClient = testContext.get("httpClient", CloseableHttpClient.class);

        String todoID = createdProjects.get(projectTitle).getId();

        HttpResponse response = ProjectHelper.getAssociation("todos", todoID, httpClient);
        TodoResponse todoResponse = deserialize(response, TodoResponse.class);

        List<Todo> returnedTodos = todoResponse.getTodos();

        assertEquals(associatedTodos.size(), returnedTodos.size());
        assertEquals(associatedTodos, returnedTodos);
    }

    @Then("the system should return an empty list indicating there are no todos for the given project")
    public void theSystemShouldReturnAnEmptyListIndicatingThereAreNoTodosForTheGivenProject() {
        // todo
    }

    @When("a user attempts to get todos for the non-existent project {string}")
    public void aUserAttemptsToGetTodosForTheNonExistentProject(String projectTitle) {
        // todo
    }
}
