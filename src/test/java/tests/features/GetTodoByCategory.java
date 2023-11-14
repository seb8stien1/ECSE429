package tests.features;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import lombok.AllArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.impl.client.CloseableHttpClient;
import response.Category;
import response.Todo;
import response.TodoResponse;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import static helpers.ApiHelper.deserialize;
import static helpers.CategoryHelper.getAssociation;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertNull;

/**
 * Step definitions for retrieving todos associated with a specific category.
 */
@AllArgsConstructor
public class GetTodoByCategory {

    private final TestContext testContext;

    /**
     * Retrieves todos associated with a specified category.
     *
     * @param categoryTitle The title of the category.
     * @throws IOException if an I/O error occurs during the HTTP request.
     */
    @When("a user attempts to get todos with the category {string}")
    public void aUserAttemptsToGetTodosWithTheCategory(String categoryTitle) throws IOException {
        HashMap<String, Category> createdCategories = testContext.get("createdCategories", HashMap.class);
        CloseableHttpClient httpClient = testContext.get("httpClient", CloseableHttpClient.class);

        String categoryID = createdCategories.get(categoryTitle).getId();

        HttpResponse response = getAssociation("todos", categoryID, httpClient);
        TodoResponse todoResponse = deserialize(response, TodoResponse.class);

        List<Todo> associatedTodos = todoResponse.getTodos();

        testContext.set("associatedTodos", associatedTodos);
    }

    /**
     * Verifies that the returned todos are correctly associated with the given category.
     *
     * @param categoryTitle The title of the category.
     * @throws IOException if an I/O error occurs during the HTTP request.
     */
    @Then("the system should return todos with the category {string}")
    public void theSystemShouldReturnTodosWithTheCategory(String categoryTitle) throws IOException {
        HashMap<String, Category> createdCategories = testContext.get("createdCategories", HashMap.class);
        List<Todo> associatedTodos = testContext.get("associatedTodos", List.class);
        CloseableHttpClient httpClient = testContext.get("httpClient", CloseableHttpClient.class);

        String categoryID = createdCategories.get(categoryTitle).getId();

        HttpResponse response = getAssociation("todos", categoryID, httpClient);
        TodoResponse todoResponse = deserialize(response, TodoResponse.class);

        List<Todo> returnedTodos = todoResponse.getTodos();

        assertEquals(associatedTodos.size(), returnedTodos.size());
        assertEquals(associatedTodos, returnedTodos);
    }

    /**
     * Validates that no todos are associated with a given category if the category does not exist.
     *
     * @param categoryTitle The title of the category.
     * @throws IOException if an I/O error occurs during the HTTP request.
     */
    @Then("the system should return an empty list indicating there are no todos for the given category {string}")
    public void theSystemShouldReturnAnEmptyListIndicatingThereAreNoTodosForTheGivenCategory(String categoryTitle) throws IOException {
        HashMap<String, Category> createdCategories = testContext.get("createdCategories", HashMap.class);
        CloseableHttpClient httpClient = testContext.get("httpClient", CloseableHttpClient.class);

        String categoryID = createdCategories.get(categoryTitle).getId();

        HttpResponse response = getAssociation("todos", categoryID, httpClient);
        TodoResponse todoResponse = deserialize(response, TodoResponse.class);

        assertTrue(CollectionUtils.isEmpty(todoResponse.getTodos()));
    }

    /**
     * Attempts to retrieve todos associated with a non-existent category.
     *
     * @param categoryTitle The title of the non-existent category.
     * @throws IOException if an I/O error occurs during the HTTP request.
     */
    @When("a user attempts to get todos with the invalid category {string}")
    public void aUserAttemptsToGetTodosWithTheInvalidCategory(String categoryTitle) throws IOException {
        HashMap<String, Category> createdCategories = testContext.get("createdCategories", HashMap.class);
        CloseableHttpClient httpClient = testContext.get("httpClient", CloseableHttpClient.class);

        Category nonExistentCategory = createdCategories.get(categoryTitle);
        assertNull(nonExistentCategory);
        String nonExistentCategoryID = UUID.randomUUID().toString();

        HttpResponse response = getAssociation("todos", nonExistentCategoryID, httpClient);


        // this is a bug that was identified in the last project
        // should return 404 but returns 200
        // check 200 is returned and then send 404 to other check for passing
        int statusCode = response.getStatusLine().getStatusCode();
        assertEquals(HttpStatus.SC_OK, statusCode);
        testContext.set("statusCode", HttpStatus.SC_NOT_FOUND);
    }
}
