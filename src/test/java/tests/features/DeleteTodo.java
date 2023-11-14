package tests.features;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import lombok.AllArgsConstructor;
import org.apache.http.HttpResponse;
import org.apache.http.impl.client.CloseableHttpClient;
import response.Todo;
import response.TodoResponse;

import java.io.IOException;
import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;

import static org.junit.Assert.*;
import static helpers.ApiHelper.deserialize;
import static helpers.TodoHelper.deleteTodo;
import static helpers.TodoHelper.getAllTodos;

/**
 * Step definitions for deleting todos and verifying their deletion from the API.
 */
@AllArgsConstructor
public class DeleteTodo {

    private final TestContext testContext;

    /**
     * Attempts to delete a todo with the specified title from the system.
     *
     * @param todoTitle The title of the todo to be deleted.
     * @throws IOException if an I/O error occurs when sending or receiving the HTTP request.
     */
    @When("a user attempts to delete the todo with the title {string}")
    public void aUserAttemptsToDeleteTheTodoWithTheTitle(String todoTitle) throws IOException {
        HashMap<String, Todo> createdTodos = testContext.get("createdTodos", HashMap.class);
        CloseableHttpClient httpClient = testContext.get("httpClient", CloseableHttpClient.class);

        String todoID = createdTodos.get(todoTitle).getId();

        deleteTodo(todoID, httpClient);
    }

    /**
     * Verifies that the todo with the specified title has been removed from the system.
     *
     * @param todoTitle The title of the todo to verify removal.
     * @throws IOException if an I/O error occurs when sending or receiving the HTTP request.
     */
    @Then("the todo with the title {string} shall be removed from the system")
    public void theTodoWithTheTitleShallBeRemovedFromTheSystem(String todoTitle) throws IOException {
        HashMap<String, Todo> createdTodos = testContext.get("createdTodos", HashMap.class);
        CloseableHttpClient httpClient = testContext.get("httpClient", CloseableHttpClient.class);

        String todoID = createdTodos.get(todoTitle).getId();

        HttpResponse response = getAllTodos(httpClient);
        TodoResponse todoResponse = deserialize(response, TodoResponse.class);

        Optional<Todo> todoOptional = todoResponse.getTodos()
                .stream()
                .filter(todo -> todoID.equals(todo.getId()))
                .findFirst();
        assertFalse(todoOptional.isPresent());
    }

    /**
     * Attempts to delete a todo that has already been deleted, identified by its title.
     *
     * @param todoTitle The title of the already deleted todo.
     * @throws IOException if an I/O error occurs when sending or receiving the HTTP request.
     */
    @When("a user attempts to delete the already deleted todo with the title {string}")
    public void aUserAttemptsToDeleteTheAlreadyDeletedTodoWithTheTitle(String todoTitle) throws IOException {
        HashMap<String, Todo> createdTodos = testContext.get("createdTodos", HashMap.class);
        CloseableHttpClient httpClient = testContext.get("httpClient", CloseableHttpClient.class);

        Todo nonExistentTodo = createdTodos.get(todoTitle);
        assertNull(nonExistentTodo);
        String nonExistentTodoID = UUID.randomUUID().toString();

        HttpResponse response = deleteTodo(nonExistentTodoID, httpClient);

        int statusCode = response.getStatusLine().getStatusCode();
        testContext.set("statusCode", statusCode);
    }

    /**
     * Attempts to delete a todo using an invalid ID.
     *
     * @param invalidID The invalid ID to be used in the deletion attempt.
     * @throws IOException if an I/O error occurs when sending or receiving the HTTP request.
     */
    @When("a user attempts to delete the todo with an invalid ID {string}")
    public void aUserAttemptsToDeleteTheTodoWithAnInvalidID(String invalidID) throws IOException {
        CloseableHttpClient httpClient = testContext.get("httpClient", CloseableHttpClient.class);

        HttpResponse response = deleteTodo(invalidID, httpClient);

        int statusCode = response.getStatusLine().getStatusCode();
        testContext.set("statusCode", statusCode);
    }
}
