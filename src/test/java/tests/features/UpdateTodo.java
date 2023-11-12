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
import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

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
        createdTodos.put(todoID,modifiedTodo);
        testContext.set("createdTodos",createdTodos);

        testContext.set("statusCode", response.getStatusLine().getStatusCode());
    }
    @Then("the todo with the title {string} shall be updated with description {string} and doneStatus {string}")
    public void theTodoWithTheTitleShallBeUpdatedWithDescriptionAndDoneStatus(String todoTitle, String newDescription, String sameDoneStatus) throws IOException {
        HashMap<String, Todo> createdTodos = testContext.get("createdTodos", HashMap.class);
        CloseableHttpClient httpClient = testContext.get("httpClient", CloseableHttpClient.class);

        String todoID = createdTodos.get(todoTitle).getId();

        HttpResponse response = TodoHelper.getTodo(todoID, httpClient);
        TodoResponse todoResponse = deserialize(response, TodoResponse.class);

        assertFalse(CollectionUtils.isEmpty(todoResponse.getTodos()));
        Optional<Todo> todoAssociationOptional = todoResponse.getTodos()
                .stream()
                .filter(todo -> todoID.equals(todo.getId()))
                .findFirst();
        assertTrue(todoAssociationOptional.isPresent());
        Todo associatedTodo = todoAssociationOptional.get();

        assertEquals(todoTitle, associatedTodo.getTitle());
        assertEquals(createdTodos.get(todoTitle).getDescription(), associatedTodo.getDescription());
        assertEquals(createdTodos.get(todoTitle).getDoneStatus(), associatedTodo.getDoneStatus());
    }


    @When("a user attempts to update the todo with the title {string} with description {string} and invalid doneStatus {string}")
    public void aUserAttemptsToUpdateTheTodoWithTheTitleWithDescriptionAndInvalidDoneStatus(String todoTitle, String todoDoneStatus, String todoDescription) throws IOException {
        HashMap<String, Todo> createdTodos = testContext.get("createdTodos", HashMap.class);
        CloseableHttpClient httpClient = testContext.get("httpClient", CloseableHttpClient.class);

        String todoID = createdTodos.get(todoTitle).getId();
        HttpResponse response = TodoHelper.modifyTodoPut(todoID,todoTitle, todoDoneStatus,todoDescription, httpClient);


        testContext.set("statusCode", response.getStatusLine().getStatusCode());
    }
}
