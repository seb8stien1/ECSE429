package tests.features;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import lombok.AllArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.http.HttpResponse;
import org.apache.http.impl.client.CloseableHttpClient;
import response.Todo;
import response.TodoResponse;

import java.io.IOException;
import java.util.HashMap;
import java.util.Optional;

import static helpers.ApiHelper.deserialize;
import static helpers.TodoHelper.createTodo;
import static helpers.TodoHelper.getTodo;
import static org.junit.Assert.*;

@AllArgsConstructor
public class CreateTodo {

    private final TestContext testContext;

    @When("a user attempts to create a new todo with the title {string}, description {string}, and doneStatus {string}")
    public void aUserAttemptsToCreateANewTodoWithTheTitleDescriptionAndDoneStatus(String todoTitle, String todoDescription, String todoDoneStatus) throws IOException {
        HashMap<String, Todo> createdTodos = new HashMap<>();
        CloseableHttpClient httpClient = testContext.get("httpClient", CloseableHttpClient.class);

        HttpResponse response = createTodo(todoTitle, todoDoneStatus, todoDescription, httpClient);
        Todo createdTodo = deserialize(response, Todo.class);
        createdTodos.put(todoTitle, createdTodo);

        testContext.set("createdTodos", createdTodos);
    }

    @Then("a new todo with the title {string}, description {string}, and doneStatus {string} shall be created")
    public void aNewTodoWithTheTitleDescriptionAndDoneStatusShallBeCreated(String todoTitle, String todoDescription, String doneStatus) throws IOException {
        HashMap<String, Todo> createdTodos = testContext.get("createdTodos", HashMap.class);
        CloseableHttpClient httpClient = testContext.get("httpClient", CloseableHttpClient.class);

        String todoID = createdTodos.get(todoTitle).getId();

        HttpResponse response = getTodo(todoID, httpClient);
        TodoResponse todoResponse = deserialize(response, TodoResponse.class);

        assertFalse(CollectionUtils.isEmpty(todoResponse.getTodos()));
        Optional<Todo> todoOptional = todoResponse.getTodos()
                .stream()
                .filter(todo -> todoID.equals(todo.getId()))
                .findFirst();
        assertTrue(todoOptional.isPresent());
        Todo returnedTodo = todoOptional.get();

        assertNotNull(returnedTodo);
        assertEquals(todoTitle, returnedTodo.getTitle());
        assertEquals(todoDescription, returnedTodo.getDescription());
        assertEquals(Boolean.valueOf(doneStatus), returnedTodo.getDoneStatus());
    }

    @When("a user attempts to create a new todo with the title {string}, description {string}, and invalid doneStatus {string}")
    public void aUserAttemptsToCreateANewTodoWithTheTitleDescriptionAndInvalidDoneStatus(String todoTitle, String todoDescription, String invalidDoneStatus) throws IOException {
        CloseableHttpClient httpClient = testContext.get("httpClient", CloseableHttpClient.class);

        HttpResponse response = createTodo(todoTitle, invalidDoneStatus, todoDescription, httpClient);

        testContext.set("response", response);
        testContext.set("statusCode", response.getStatusLine().getStatusCode());
    }
}
