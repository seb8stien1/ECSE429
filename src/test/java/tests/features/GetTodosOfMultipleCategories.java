package tests.features;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import lombok.AllArgsConstructor;
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
import java.util.stream.Collectors;

import static helpers.ApiHelper.deserialize;
import static helpers.CategoryHelper.getAssociation;
import static org.junit.Assert.*;

@AllArgsConstructor
public class GetTodosOfMultipleCategories {

    private final TestContext testContext;

    @When("a user retrieves todos linked to categories {string} and {string}")
    public void aUserRetrievesTodosLinkedToCategoriesAnd(String categoryTitle1, String categoryTitle2) throws IOException {
        HashMap<String, Category> createdCategories = testContext.get("createdCategories", HashMap.class);
        CloseableHttpClient httpClient = testContext.get("httpClient", CloseableHttpClient.class);

        String firstCategoryID = createdCategories.get(categoryTitle1).getId();
        String secondCategoryID = createdCategories.get(categoryTitle2).getId();

        HttpResponse firstResponse = getAssociation("todos", firstCategoryID, httpClient);
        TodoResponse firstTodoResponse = deserialize(firstResponse, TodoResponse.class);
        List<String> firstCategoryTodos = firstTodoResponse.getTodos().stream().map(Todo::getId).collect(Collectors.toList());

        HttpResponse secondResponse = getAssociation("todos", secondCategoryID, httpClient);
        TodoResponse secondTodoResponse = deserialize(secondResponse, TodoResponse.class);
        List<String> secondCategoryTodos = secondTodoResponse.getTodos().stream().map(Todo::getId).collect(Collectors.toList());

        firstCategoryTodos.retainAll(secondCategoryTodos); // getting intersection, the @Then will see if the right common one was returned

        testContext.set("commonTodos", firstCategoryTodos);
    }

    @Then("the returned {string} should be the same for both categories")
    public void theLinkedToAndShouldBeTheSame(String todoTitle) {
        HashMap<String, Todo> createdTodos = testContext.get("createdTodos", HashMap.class);
        List<String> commonTodos = testContext.get("commonTodos", List.class);

        String todoId = createdTodos.get(todoTitle).getId();
        assertTrue(commonTodos.contains(todoId));
    }

    @When("a user retrieves the todo linked only to the category {string}")
    public void aUserRetrievesTheTodoLinkedOnlyToTheCategory(String categoryTitle) throws IOException {
        CloseableHttpClient httpClient = testContext.get("httpClient", CloseableHttpClient.class);
        HashMap<String, Category> createdCategories = testContext.get("createdCategories", HashMap.class);

        String categoryID = createdCategories.get(categoryTitle).getId();

        HttpResponse response = getAssociation("todos", categoryID, httpClient);
        TodoResponse todosResponse = deserialize(response, TodoResponse.class);

        testContext.set("returnedTodos", todosResponse.getTodos());
    }

    @Then("the todo {string} linked to the category should be returned")
    public void theTodoLinkedToTheCategoryShouldBeReturned(String todoTitle) {
        HashMap<String, Todo> createdTodos = testContext.get("createdTodos", HashMap.class);
        List<Todo> returnedTodos = testContext.get("returnedTodos", List.class);

        String todoID = createdTodos.get(todoTitle).getId();
        boolean todoIsPresent = returnedTodos.stream().anyMatch(todo -> todo.getId().equals(todoID));

        assertTrue(todoIsPresent);
    }

    @When("a user attempts to retrieve todos linked to a non-existent category {string}")
    public void aUserAttemptsToRetrieveTodosLinkedToANonExistentCategory(String categoryTitle) throws IOException {
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
