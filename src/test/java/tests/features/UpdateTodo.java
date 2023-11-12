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

@AllArgsConstructor
public class UpdateTodo {

    private final TestContext testContext;

    @When("a user attempts to update the todo with the title {string} with description {string} and doneStatus {string}")
    public void aUserAttemptsToUpdateTheTodoWithTheTitleWithDescriptionAndDoneStatus(String todoTitle, String todoDescription, String todoDoneStatus) throws IOException {
        HashMap<String, Todo> createdTodos = testContext.get("createdTodos", HashMap.class);
        CloseableHttpClient httpClient = testContext.get("httpClient", CloseableHttpClient.class);

        String todoID = createdTodos.get(todoTitle).getId();
        HttpResponse response = TodoHelper.modifyTodoPut(todoID,todoTitle, todoDoneStatus,todoDescription, httpClient);
        Todo modifiedTodo = deserialize(response,Todo.class);
        createdTodos.put(todoTitle, modifiedTodo);
        testContext.set("createdTodos", createdTodos);

        testContext.set("statusCode", response.getStatusLine().getStatusCode());
    }
    @Then("the todo with the title {string} shall be updated with description {string} and doneStatus {string}")
    public void theTodoWithTheTitleShallBeUpdatedWithDescriptionAndDoneStatus(String todoTitle, String todoDescription, String todoDoneStatus) throws IOException {
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
        assertEquals(todoDescription, returnedTodo.getDescription());
        assertEquals(Boolean.valueOf(todoDoneStatus), returnedTodo.getDoneStatus());
    }


    @When("a user attempts to update the todo with the title {string} with description {string} and invalid doneStatus {string}")
    public void aUserAttemptsToUpdateTheTodoWithTheTitleWithDescriptionAndInvalidDoneStatus(String todoTitle, String todoDescription, String invalidDoneStatus) throws IOException {
        HashMap<String, Todo> createdTodos = testContext.get("createdTodos", HashMap.class);
        CloseableHttpClient httpClient = testContext.get("httpClient", CloseableHttpClient.class);

        String todoID = createdTodos.get(todoTitle).getId();
        HttpResponse response = TodoHelper.modifyTodoPut(todoID,todoTitle, invalidDoneStatus,todoDescription, httpClient);


        testContext.set("response", response);
        testContext.set("statusCode", response.getStatusLine().getStatusCode());
    }
}
