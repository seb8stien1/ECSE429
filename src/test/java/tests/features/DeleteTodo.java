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

@AllArgsConstructor
public class DeleteTodo {

    private final TestContext testContext;

    @When("a user attempts to delete the todo with the title {string}")
    public void aUserAttemptsToDeleteTheTodoWithTheTitle(String todoTitle) throws IOException {
        HashMap<String, Todo> createdTodos = testContext.get("createdTodos", HashMap.class);
        CloseableHttpClient httpClient = testContext.get("httpClient", CloseableHttpClient.class);

        String todoID = createdTodos.get(todoTitle).getId();

        deleteTodo(todoID, httpClient);
    }

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

    @When("a user attempts to delete the todo with an invalid ID {string}")
    public void aUserAttemptsToDeleteTheTodoWithAnInvalidID(String invalidID) throws IOException {
        CloseableHttpClient httpClient = testContext.get("httpClient", CloseableHttpClient.class);

        HttpResponse response = deleteTodo(invalidID, httpClient);

        int statusCode = response.getStatusLine().getStatusCode();
        testContext.set("statusCode", statusCode);
    }
}
