package tests.features;

import helpers.TodoHelper;
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
import static helpers.TodoHelper.getTodo;
import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

@AllArgsConstructor
public class MarkTodoComplete {

    private final TestContext testContext;

    @When("a user attempts to mark the incomplete todo titled {string} as completed {string}")
    public void aUserAttemptsToMarkTheTodoTitledWithDoneStatusAsCompleted(String todoTitle, String completed) throws IOException {
        HashMap<String, Todo> createdTodos = testContext.get("createdTodos", HashMap.class);
        CloseableHttpClient httpClient = testContext.get("httpClient", CloseableHttpClient.class);

        Todo todo = createdTodos.get(todoTitle);
        String todoID = todo.getId();
        String todoDescription = createdTodos.get(todoTitle).getDescription();
        assertFalse(todo.getDoneStatus());
        HttpResponse response = TodoHelper.modifyTodoPut(todoID,todoTitle, completed,todoDescription, httpClient);
        Todo modifiedTodo = deserialize(response,Todo.class);
        createdTodos.put(todoTitle, modifiedTodo);
        testContext.set("createdTodos", createdTodos);

        testContext.set("statusCode", response.getStatusLine().getStatusCode());
    }

    @When("a user attempts to mark the completed todo titled {string} as completed {string}")
    public void aUserAttemptsToMarkTheCompletedTodoTitledAsCompleted(String todoTitle, String completed) throws IOException {
        HashMap<String, Todo> createdTodos = testContext.get("createdTodos", HashMap.class);
        CloseableHttpClient httpClient = testContext.get("httpClient", CloseableHttpClient.class);

        Todo todo = createdTodos.get(todoTitle);
        String todoID = todo.getId();
        String todoDescription = createdTodos.get(todoTitle).getDescription();
        assertTrue(todo.getDoneStatus());

        HttpResponse response = TodoHelper.modifyTodoPut(todoID,todoTitle, completed, todoDescription, httpClient);
        Todo modifiedTodo = deserialize(response,Todo.class);
        createdTodos.put(todoTitle, modifiedTodo);
        testContext.set("createdTodos", createdTodos);

        testContext.set("statusCode", response.getStatusLine().getStatusCode());
    }

    @Then("the todo titled {string} should have doneStatus as {string}")
    public void theTodoTitledShouldHaveDoneStatusAs(String todoTitle, String doneStatus) throws IOException {
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

        assertEquals(todoTitle, returnedTodo.getTitle());
        assertEquals(Boolean.valueOf(doneStatus), returnedTodo.getDoneStatus());

    }

    @When("a user attempts to mark the non-existent todo titled {string} with doneStatus as {string}")
    public void aUserAttemptsToMarkTheNonExistentTodoTitledWithDoneStatusAs(String todoTitle, String doneStatus) throws IOException {
        HashMap<String, Todo> createdTodos = testContext.get("createdTodos", HashMap.class);
        CloseableHttpClient httpClient = testContext.get("httpClient", CloseableHttpClient.class);

        Todo nonExistentTodo = createdTodos.get(todoTitle);
        assertNull(nonExistentTodo);
        String nonExistentTodoID = "103i20023i0309458934539";

        HttpResponse response = TodoHelper.modifyTodoPut(nonExistentTodoID, todoTitle, doneStatus,"todoDescription", httpClient);

        int statusCode = response.getStatusLine().getStatusCode();
        testContext.set("statusCode", statusCode);
    }
}
