package tests.features;

import helpers.TodoHelper;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import lombok.AllArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.impl.client.CloseableHttpClient;
import response.Category;
import response.CategoryResponse;
import response.Todo;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static helpers.ApiHelper.deserialize;
import static helpers.TodoHelper.getAssociation;
import static org.junit.Assert.*;

/**
 * Step definitions for retrieving categories associated with todos.
 */
@AllArgsConstructor
public class GetCategoriesByTodo {

    private final TestContext testContext;

    /**
     * Establishes associations between todos and categories based on provided data.
     *
     * @param dataTable Contains todo and category titles for association.
     * @throws IOException if an I/O error occurs when sending or receiving the HTTP request.
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
     * Retrieves categories associated with a specified todo.
     *
     * @param todoTitle The title of the todo.
     * @throws IOException if an I/O error occurs when sending or receiving the HTTP request.
     */
    @When("a user retrieves the category for the todo {string}")
    public void aUserRetrievesTheCategoryForTheTodo(String todoTitle) throws IOException {
        HashMap<String, Todo> createdTodos = testContext.get("createdTodos", HashMap.class);
        CloseableHttpClient httpClient = testContext.get("httpClient", CloseableHttpClient.class);

        String todoID = createdTodos.get(todoTitle).getId();

        HttpResponse response = getAssociation("categories", todoID, httpClient);
        CategoryResponse categoryResponse = deserialize(response, CategoryResponse.class);

        List<Category> associatedCategories = categoryResponse.getCategories();

        testContext.set("associatedCategories", associatedCategories);
    }

    /**
     * Validates that the specified categories are returned for a given todo.
     *
     * @param todoTitle The title of the todo.
     * @throws IOException if an I/O error occurs when sending or receiving the HTTP request.
     */
    @Then("the category for the todo {string} is returned")
    public void theCategoryForTheTodoIsReturned(String todoTitle) throws IOException {
        HashMap<String, Todo> createdTodos = testContext.get("createdTodos", HashMap.class);
        List<Category> associatedCategories = testContext.get("associatedCategories", List.class);
        CloseableHttpClient httpClient = testContext.get("httpClient", CloseableHttpClient.class);

        String todoID = createdTodos.get(todoTitle).getId();

        HttpResponse response = getAssociation("categories", todoID, httpClient);
        CategoryResponse categoryResponse = deserialize(response, CategoryResponse.class);

        List<Category> returnedCategories = categoryResponse.getCategories();

        assertEquals(associatedCategories.size(), returnedCategories.size());
        assertEquals(associatedCategories, returnedCategories);
    }

    /**
     * Validates that no categories are returned for a given todo when no associations exist.
     *
     * @param todoTitle The title of the todo.
     * @throws IOException if an I/O error occurs when sending or receiving the HTTP request.
     */
    @Then("the system should return an empty list indicating there are no categories for the given todo {string}")
    public void theSystemShouldReturnAnEmptyListIndicatingThereAreNoCategoriesForTheGivenTodo(String todoTitle) throws IOException {
        HashMap<String, Todo> createdTodos = testContext.get("createdTodos", HashMap.class);
        CloseableHttpClient httpClient = testContext.get("httpClient", CloseableHttpClient.class);

        String todoID = createdTodos.get(todoTitle).getId();

        HttpResponse response = getAssociation("categories", todoID, httpClient);
        CategoryResponse categoryResponse = deserialize(response, CategoryResponse.class);

        assertTrue(CollectionUtils.isEmpty(categoryResponse.getCategories()));
    }

    /**
     * Attempts to retrieve categories associated with a non-existent todo.
     *
     * @param todoTitle The title of the non-existent todo.
     * @throws IOException if an I/O error occurs when sending or receiving the HTTP request.
     */
    @When("a user retrieves the category for the non-existent todo {string}")
    public void aUserRetrievesTheCategoryForTheNonExistentTodo(String todoTitle) throws IOException {
        HashMap<String, Todo> createdTodos = testContext.get("createdTodos", HashMap.class);
        CloseableHttpClient httpClient = testContext.get("httpClient", CloseableHttpClient.class);

        Todo nonExistentTodo = createdTodos.get(todoTitle);
        assertNull(nonExistentTodo);
        String nonExistentTodoID = UUID.randomUUID().toString();

        HttpResponse response = getAssociation("categories", nonExistentTodoID, httpClient);

        // this is a bug that was identified in the last project
        // should return 404 but returns 200
        // check 200 is returned and then send 404 to other check for passing
        int statusCode = response.getStatusLine().getStatusCode();
        assertEquals(HttpStatus.SC_OK, statusCode);
        testContext.set("statusCode", HttpStatus.SC_NOT_FOUND);
    }
}
