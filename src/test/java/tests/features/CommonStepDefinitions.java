package tests.features;

import helpers.ApiHelper;
import helpers.TodoHelper;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import lombok.AllArgsConstructor;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import response.Category;
import response.Project;
import response.ResponseError;
import response.Todo;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static helpers.ApiHelper.deserialize;
import static helpers.CategoryHelper.*;
import static helpers.ProjectHelper.*;
import static helpers.TodoHelper.*;
import static org.junit.Assert.*;

/**
 * Common step definitions that will be used across multiple user stories
 */
@AllArgsConstructor
public class CommonStepDefinitions {

    private final TestContext testContext;

    /**
     * Verifies that the API server is running and available.
     *
     * @throws IOException if an I/O exception occurs.
     */
    @Given("the API server is running and available")
    public void theAPIServerIsRunningAndAvailable() throws IOException {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpResponse response = ApiHelper.sendHttpRequest("get", "http://localhost:4567/docs", null, httpClient);
        int statusCode = response.getStatusLine().getStatusCode();
        assertEquals(HttpStatus.SC_OK, statusCode);

        testContext.set("httpClient", httpClient);
    }

    /**
     * Sets up projects that exist in the system as defined in a DataTable.
     *
     * @param dataTable with project details.
     * @throws IOException if an I/O exception occurs.
     */
    @Given("the following projects exist in the system:")
    public void theFollowingProjectsExistInTheSystem(io.cucumber.datatable.DataTable dataTable) throws IOException {
        List<Map<String, String>> projects = dataTable.asMaps();
        HashMap<String, Project> createdProjects = new HashMap<>();

        CloseableHttpClient httpClient = testContext.get("httpClient", CloseableHttpClient.class);

        for (Map<String, String> project: projects) {
            String projectTitle = project.get("projectTitle");
            String projectDescription = project.get("projectDescription");
            Boolean projectCompleted = Boolean.valueOf(project.get("completed"));
            Boolean projectActive = Boolean.valueOf(project.get("active"));
            HttpResponse response = createProject(projectTitle, projectCompleted, projectActive, projectDescription, httpClient);
            Project createdProject = deserialize(response, Project.class);
            createdProjects.put(projectTitle, createdProject);
        }

        testContext.set("createdProjects", createdProjects);
    }

    /**
     * Sets up todos that exist in the system as defined in a DataTable.
     *
     * @param dataTable with todo details.
     * @throws IOException if an I/O exception occurs.
     */
    @Given("the following todos exist in the system:")
    public void theFollowingTodosExistInTheSystem(io.cucumber.datatable.DataTable dataTable) throws IOException {
        List<Map<String, String>> todos = dataTable.asMaps();
        HashMap<String, Todo> createdTodos = new HashMap<>();

        CloseableHttpClient httpClient = testContext.get("httpClient", CloseableHttpClient.class);

        for (Map<String, String> todo : todos) {
            String todoTitle = todo.get("todoTitle");
            String todoDescription = todo.get("todoDescription");
            Boolean todoDoneStatus = Boolean.valueOf(todo.get("todoDoneStatus"));
            HttpResponse response = createTodo(todoTitle, todoDoneStatus, todoDescription, httpClient);
            Todo createdTodo = deserialize(response, Todo.class);
            createdTodos.put(todoTitle, createdTodo);
        }

        testContext.set("createdTodos", createdTodos);
    }

    /**
     * Sets up categories that exist in the system as defined in a DataTable.
     *
     * @param dataTable with category details.
     * @throws IOException if an I/O exception occurs.
     */
    @Given("the following categories exist in the system:")
    public void theFollowingCategoriesExistInTheSystem(io.cucumber.datatable.DataTable dataTable) throws IOException {
        List<Map<String, String>> categories = dataTable.asMaps();
        HashMap<String, Category> createdCategories = new HashMap<>();

        CloseableHttpClient httpClient = testContext.get("httpClient", CloseableHttpClient.class);

        for (Map<String, String> category : categories) {
            String categoryTitle = category.get("categoryTitle");
            String categoryDescription = category.get("categoryDescription");
            HttpResponse response = createCategory(categoryTitle, categoryDescription, httpClient);
            Category createdCategory = deserialize(response, Category.class);
            createdCategories.put(categoryTitle, createdCategory);
        }

        testContext.set("createdCategories", createdCategories);
    }

    /**
     * Sets up associations between todos and categories in the system as defined in a DataTable.
     * This method associates existing todos with categories based on the provided data.
     *
     * @param dataTable DataTable containing associations between todos and categories.
     *                  The table should have columns for 'todoTitle' and 'categoryTitle'.
     * @throws IOException if an I/O exception occurs during the association process.
     */
    @Given("the following todo and category association exist in the system:")
    public void theFollowingTodoAndCategoryAssociationExistInTheSystem(io.cucumber.datatable.DataTable dataTable) throws IOException {
        List<Map<String, String>> associations = dataTable.asMaps();

        HashMap<String, Todo> createdTodos = testContext.get("createdTodos", HashMap.class);
        HashMap<String, Category> createdCategories = testContext.get("createdCategories", HashMap.class);

        CloseableHttpClient httpClient = testContext.get("httpClient", CloseableHttpClient.class);

        for (Map<String, String> association : associations) {
            String todoTitle = association.get("todoTitle");
            String categoryTitle = association.get("categoryTitle");

            String todoID = createdTodos.get(todoTitle).getId();
            String categoryID = createdCategories.get(categoryTitle).getId();

            TodoHelper.createAssociation("categories", todoID, categoryID, httpClient);
        }
    }


    /**
     * Verifies the number of todos in the system.
     *
     * @param expectedTodoCount Expected count of todos.
     */
    @Then("the number of todos in the system is {string}")
    public void theNumberOfTodosInTheSystemIs(String expectedTodoCount) {
        HashMap<String, Todo> createdTodos = testContext.get("createdTodos", HashMap.class);
        assertEquals(createdTodos.size(), Integer.parseInt(expectedTodoCount));
    }

    /**
     * Verifies the number of projects in the system.
     *
     * @param expectedProjectCount Expected count of projects.
     */
    @And("the number of projects in the system is {string}")
    public void theNumberOfProjectsInTheSystemIs(String expectedProjectCount) {
        HashMap<String, Project> createdProjects = testContext.get("createdProjects", HashMap.class);
        assertEquals(createdProjects.size(), Integer.parseInt(expectedProjectCount));
    }

    /**
     * Verifies the number of categories in the system.
     *
     * @param expectedCategoryCount Expected count of categories.
     */
    @Then("the number of categories in the system is {string}")
    public void theNumberOfCategoriesInTheSystemIs(String expectedCategoryCount) {
        HashMap<String, Category> createdCategories = testContext.get("createdCategories", HashMap.class);
        assertEquals(createdCategories.size(), Integer.parseInt(expectedCategoryCount));
    }

    /**
     * Verifies that a specific error has been raised.
     *
     * @param error Expected error message.
     * @throws IOException if an I/O exception occurs.
     */
    @Then("the following {string} shall be raised")
    public void theFollowingErrorShallBeRaised(String error) throws IOException {
        HttpResponse response = testContext.get("response", HttpResponse.class);
        ResponseError errorResponse = deserialize(response, ResponseError.class);

        assertNotNull(errorResponse);
        assertEquals(errorResponse.getErrorMessages().get(0), error);
    }

    /**
     * Verifies the status code returned by the API.
     *
     * @param statusCode Expected status code.
     */
    @Then("the status code returned by the API is {string}")
    public void theStatusCodeReturnedByTheAPIIs(String statusCode) {
        int actualStatusCode = testContext.get("statusCode", Integer.class);
        assertEquals(actualStatusCode, Integer.parseInt(statusCode));
    }

    /**
     * Restores the system to its original state by removing everything created in the test context.
     *
     * @throws IOException if an I/O exception occurs.
     */
    @Then("the system is restored to the original state")
    public void theSystemIsRestoredToTheOriginalState() throws IOException {
        HashMap<String, Todo> createdTodos = null;
        HashMap<String, Category> createdCategories = null;
        HashMap<String, Project> createdProjects = null;

        if (testContext.containsKey("createdTodos")) {
            createdTodos = testContext.get("createdTodos", HashMap.class);
        }

        if (testContext.containsKey("containsCategories")) {
            createdCategories = testContext.get("createdCategories", HashMap.class);
        }

        if (testContext.containsKey("containsProjects")) {
            createdProjects = testContext.get("createdProjects", HashMap.class);
        }

        CloseableHttpClient httpClient = testContext.get("httpClient", CloseableHttpClient.class);


        if (createdTodos != null) {
            for (Todo todo : createdTodos.values()) {
                deleteTodo(todo.getId(), httpClient);
            }
            testContext.remove("createdTodos");
        }

        if (createdCategories != null) {
            for (Category category : createdCategories.values()) {
                deleteCategory(category.getId(), httpClient);
            }
            testContext.remove("createdCategories");
        }

        if (createdProjects != null) {
            for (Project project : createdProjects.values()) {
                deleteProject(project.getId(), httpClient);
            }
            testContext.remove("createdProjects");
        }

        if (testContext.containsKey("response")) testContext.remove("response");
        if (testContext.containsKey("statusCode")) testContext.remove("statusCode");
        if (testContext.containsKey("httpClient")) testContext.remove("httpClient");
    }
}