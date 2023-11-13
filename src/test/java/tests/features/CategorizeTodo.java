package tests.features;

import helpers.CategoryHelper;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import lombok.AllArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.http.HttpResponse;
import org.apache.http.impl.client.CloseableHttpClient;
import response.Category;
import response.Todo;
import response.TodoResponse;

import java.io.IOException;
import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;

import static helpers.ApiHelper.deserialize;
import static org.junit.Assert.*;

/**
 * Step definitions for the 'Categorize Todo' feature.
 * These definitions handle the steps involved in categorizing a todo item under specific categories.
 */
@AllArgsConstructor
public class CategorizeTodo {

    private final TestContext testContext;

    /**
     * Categorizes a todo item with a given title under a specified category.
     * Retrieves the todo and category from the test context based on their titles and creates an association.
     *
     * @param todoTitle    The title of the todo to be categorized.
     * @param categoryTitle The title of the category to associate with the todo.
     * @throws IOException if an I/O exception occurs while making the association.
     */
    @When("a user categorizes the todo {string} as {string}")
    @Given("the todo {string} is categorized as {string}")
    public void aUserCategorizesTheTodoAs(String todoTitle, String categoryTitle) throws IOException {
        HashMap<String, Category> createdCategories = testContext.get("createdCategories", HashMap.class);
        HashMap<String, Todo> createdTodos = testContext.get("createdTodos", HashMap.class);
        CloseableHttpClient httpClient = testContext.get("httpClient", CloseableHttpClient.class);

        String categoryID = createdCategories.get(categoryTitle).getId();
        String todoID = createdTodos.get(todoTitle).getId();

        CategoryHelper.createAssociation("todos", categoryID, todoID, httpClient);
    }

    /**
     * Validates that a todo item is correctly categorized under a given category.
     * Ensures that the todo item is associated with the category and that their attributes match.
     *
     * @param todoTitle    The title of the categorized todo item.
     * @param categoryTitle The title of the category associated with the todo.
     * @throws IOException if an I/O exception occurs while retrieving the association.
     */
    @Then("the todo {string} should be categorized as {string}")
    public void theTodoShouldBeCategorizedAs(String todoTitle, String categoryTitle) throws IOException {
        HashMap<String, Category> createdCategories = testContext.get("createdCategories", HashMap.class);
        HashMap<String, Todo> createdTodos = testContext.get("createdTodos", HashMap.class);
        CloseableHttpClient httpClient = testContext.get("httpClient", CloseableHttpClient.class);

        String categoryID = createdCategories.get(categoryTitle).getId();
        String todoID = createdTodos.get(todoTitle).getId();

        HttpResponse response = CategoryHelper.getAssociation("todos", categoryID, httpClient);
        TodoResponse associatedTodos = deserialize(response, TodoResponse.class);

        assertFalse(CollectionUtils.isEmpty(associatedTodos.getTodos()));
        Optional<Todo> todoAssociationOptional = associatedTodos.getTodos()
                .stream()
                .filter(todo -> todoID.equals(todo.getId()))
                .findFirst();
        assertTrue(todoAssociationOptional.isPresent());
        Todo associatedTodo = todoAssociationOptional.get();

        // Check if the association's title, description, and done status match
        assertEquals(todoTitle, associatedTodo.getTitle());
        assertEquals(createdTodos.get(todoTitle).getDescription(), associatedTodo.getDescription());
        assertEquals(createdTodos.get(todoTitle).getDoneStatus(), associatedTodo.getDoneStatus());
    }

    /**
     * Attempts to categorize a todo item under a non-existent category.
     * This simulates a failure scenario to test the system's error handling.
     *
     * @param todoTitle    The title of the todo item to be categorized.
     * @param categoryTitle The title of a non-existent category.
     * @throws IOException if an I/O exception occurs while attempting the invalid association.
     */
    @When("a user categorizes the todo {string} to a non-existent {string}")
    public void aUserCategorizesTheTodoToANonExistent(String todoTitle, String categoryTitle) throws IOException {
        HashMap<String, Category> createdCategories = testContext.get("createdCategories", HashMap.class);
        HashMap<String, Todo> createdTodos = testContext.get("createdTodos", HashMap.class);
        CloseableHttpClient httpClient = testContext.get("httpClient", CloseableHttpClient.class);

        Category nonExistentCategory = createdCategories.get(categoryTitle);
        assertNull(nonExistentCategory);
        String nonExistentCategoryID = UUID.randomUUID().toString(); // Fabricating a non-existent category ID

        String todoID = createdTodos.get(todoTitle).getId();

        HttpResponse response = CategoryHelper.createAssociation("todos", todoID, nonExistentCategoryID, httpClient);

        int statusCode = response.getStatusLine().getStatusCode();
        testContext.set("statusCode", statusCode);

    }
}
