package tests.features;

import helpers.ApiHelper;
import helpers.CategoryHelper;
import helpers.ProjectHelper;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import lombok.AllArgsConstructor;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import response.*;

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
     * Once that is verified, it checks the number of each type of object that pre-exists in the database
     * This will help in asserting the right amount of objects are created during these feature tests
     *
     * @throws IOException if an I/O exception occurs.
     */
    @Before
    @Given("the API server is running and available")
    public void theAPIServerIsRunningAndAvailable() throws IOException {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpResponse response = ApiHelper.sendHttpRequest("get", "http://localhost:4567/docs", null, httpClient);
        int statusCode = response.getStatusLine().getStatusCode();
        assertEquals(HttpStatus.SC_OK, statusCode);

        testContext.set("httpClient", httpClient);

        ProjectResponse allProjects = deserialize(getAllProjects(httpClient), ProjectResponse.class);
        int numProjectsPredefined = allProjects.getProjects().size();
        testContext.set("numProjectsPredefined", numProjectsPredefined);

        TodoResponse allTodos = deserialize(getAllTodos(httpClient), TodoResponse.class);
        int numTodosPredefined = allTodos.getTodos().size();
        testContext.set("numTodosPredefined", numTodosPredefined);

        CategoryResponse allCategories = deserialize(getAllCategories(httpClient), CategoryResponse.class);
        int numCategoriesPredefined = allCategories.getCategories().size();
        testContext.set("numCategoriesPredefined", numCategoriesPredefined);

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
     * Sets up associations between categories and todos in the system as defined in a DataTable.
     * This method associates existing categories with todos based on the provided data.
     *
     * @param dataTable DataTable containing associations between categories and todo.
     * @throws IOException if an I/O exception occurs during the association process.
     */
    @Given("the following category and todo association exist in the system:")
    public void theFollowingCategoryAndTodoAssociationExistInTheSystem(io.cucumber.datatable.DataTable dataTable) throws IOException {
        HashMap<String, Category> createdCategories = testContext.get("createdCategories", HashMap.class);
        HashMap<String, Todo> createdTodos = testContext.get("createdTodos", HashMap.class);

        List<Map<String, String>> associations = dataTable.asMaps();

        CloseableHttpClient httpClient = testContext.get("httpClient", CloseableHttpClient.class);

        for (Map<String, String> association : associations) {
            String categoryTitle = association.get("categoryTitle");
            String todoTitle = association.get("todoTitle");

            String categoryID = createdCategories.get(categoryTitle).getId();
            String todoID = createdTodos.get(todoTitle).getId();

            CategoryHelper.createAssociation("todos", categoryID, todoID, httpClient);
        }
    }

    /**
     * Sets up associations between projects and todos in the system as defined in a DataTable.
     * This method associates existing projects with todos based on the provided data.
     *
     * @param dataTable DataTable containing associations between projects and todo.
     * @throws IOException if an I/O exception occurs during the association process.
     */
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

            ProjectHelper.createAssociation("tasks", projectID, todoID, httpClient);
        }
    }

    /**
     * Verifies the number of todos in the system.
     *
     * @param expectedTodoCount Expected count of todos.
     *
     * @throws IOException if an I/O exception occurs during the association process.
     */
    @Then("the number of todos in the system is {string}")
    public void theNumberOfTodosInTheSystemIs(String expectedTodoCount) throws IOException {
        CloseableHttpClient httpClient = testContext.get("httpClient", CloseableHttpClient.class);
        int numTodosPredefined = testContext.get("numTodosPredefined", Integer.class);

        TodoResponse allTodos = deserialize(getAllTodos(httpClient), TodoResponse.class);

        int numNewlyCreatedTodos = allTodos.getTodos().size() - numTodosPredefined;

        assertEquals(Integer.parseInt(expectedTodoCount), numNewlyCreatedTodos);
    }


    /**
     * Verifies the number of projects in the system.
     *
     * @param expectedProjectCount Expected count of projects.
     *
     * @throws IOException if an I/O exception occurs during the association process.
     */
    @And("the number of projects in the system is {string}")
    public void theNumberOfProjectsInTheSystemIs(String expectedProjectCount) throws IOException {
        CloseableHttpClient httpClient = testContext.get("httpClient", CloseableHttpClient.class);
        int numProjectsPredefined = testContext.get("numProjectsPredefined", Integer.class);

        ProjectResponse allProjects = deserialize(getAllProjects(httpClient), ProjectResponse.class);

        int numNewlyCreatedProjects = allProjects.getProjects().size() - numProjectsPredefined;

        assertEquals(Integer.parseInt(expectedProjectCount), numNewlyCreatedProjects);
    }

    /**
     * Verifies the number of categories in the system.
     *
     * @param expectedCategoryCount Expected count of categories.
     *
     * @throws IOException if an I/O exception occurs during the association process.
     */
    @Then("the number of categories in the system is {string}")
    public void theNumberOfCategoriesInTheSystemIs(String expectedCategoryCount) throws IOException {
        CloseableHttpClient httpClient = testContext.get("httpClient", CloseableHttpClient.class);
        int numCategoriesPredefined = testContext.get("numCategoriesPredefined", Integer.class);

        CategoryResponse allCategories = deserialize(getAllCategories(httpClient), CategoryResponse.class);

        int numNewlyCreatedCategories = allCategories.getCategories().size() - numCategoriesPredefined;

        assertEquals(Integer.parseInt(expectedCategoryCount), numNewlyCreatedCategories);
    }

    /**
     * Verifies that a specific error has been raised.
     *
     * @param expectedErrorMessage Expected error message.
     * @throws IOException if an I/O exception occurs.
     */
    @Then("the following {string} shall be raised")
    public void theFollowingErrorShallBeRaised(String expectedErrorMessage) throws IOException {
        HttpResponse response = testContext.get("response", HttpResponse.class);
        ResponseError errorResponse = deserialize(response, ResponseError.class);

        assertNotNull(errorResponse);
        String actualErrorMessage = errorResponse.getErrorMessages().get(0);
        assertTrue(actualErrorMessage.startsWith(expectedErrorMessage));
    }

    /**
     * Verifies the status code returned by the API.
     *
     * @param expectedStatusCode Expected status code.
     */
    @Then("the status code returned by the API is {string}")
    public void theStatusCodeReturnedByTheAPIIs(String expectedStatusCode) {
        int actualStatusCode = testContext.get("statusCode", Integer.class);
        assertEquals(Integer.parseInt(expectedStatusCode), actualStatusCode);
    }

    /**
     * Restores the system to its original state by removing everything created in the test context.
     *
     * @throws IOException if an I/O exception occurs.
     */
    @After
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
        testContext.remove("httpClient");
    }
}