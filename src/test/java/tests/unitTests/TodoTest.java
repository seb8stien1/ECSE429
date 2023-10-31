package tests.unitTests;

import config.RandomOrderTestRunner;
import helpers.ApiHelper;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.http.HttpResponse;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.HttpStatus;
import org.junit.AfterClass;
import org.junit.runner.RunWith;
import response.ResponseError;
import response.Todo;
import response.TodoResponse;
import org.junit.Test;
import org.apache.http.impl.client.CloseableHttpClient;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import static helpers.TodoHelper.*;
import static helpers.ApiHelper.*;
import static org.junit.Assert.*;

@RunWith(RandomOrderTestRunner.class)
public class TodoTest {

    CloseableHttpClient httpClient;

    @AfterClass
    public static void teardown() {
        CloseableHttpClient httpClient= HttpClients.createDefault();
        try {
            ApiHelper.sendHttpRequest("get", "http://localhost:4567/shutdown", null, httpClient);
        } catch (Exception e) {
            assertEquals(HttpHostConnectException.class, e.getClass());
        }
    }

    @Test
    public void testCreateGetAllAndDeleteByIdTodo() throws IOException {
        // Define Todo properties
        String title = "testTodo";
        Boolean doneStatus = Boolean.FALSE;
        String description = "test description";

        // Create a new Todo
        HttpResponse response = createTodo(title, doneStatus, description, httpClient);

        // Check the HTTP status code to ensure successful creation
        int statusCode = response.getStatusLine().getStatusCode();
        assertEquals(HttpStatus.SC_CREATED, statusCode);

        // Retrieve all Todos to check if the newly created Todo exists
        response = getAllTodos(httpClient);
        TodoResponse todos = deserialize(response, TodoResponse.class);

        // Check the HTTP status code to ensure successful retrieval
        statusCode = response.getStatusLine().getStatusCode();
        assertEquals(HttpStatus.SC_OK, statusCode);

        // Filter and collect Todos based on criteria, then assert that the list is not empty
        List<Todo> todoList = todos.getTodos()
                .stream()
                .filter(todo -> title.equals(todo.getTitle())
                        && doneStatus.equals(todo.getDoneStatus())
                        && description.equals(todo.getDescription()))
                .collect(Collectors.toList());
        assertFalse(CollectionUtils.isEmpty(todoList));

        // Delete the created Todo(s)
        todoList.forEach(todo-> {
            try {
                deleteTodo(todo.getId(), httpClient);
            } catch (IOException e) {
                // Handle and re-throw any exceptions during deletion
                throw new RuntimeException(e);
            }
        });
    }

    // Test: HEAD request for all Todos
    @Test
    public void testHeadAllTodos() throws IOException {
        HttpResponse headResponse = headAllTodos(httpClient);
        HttpResponse getResponse = getAllTodos(httpClient);

        // Check that the HEAD response does not return anything in the body
        assertNull(headResponse.getEntity());

        // Compare the headers from the HEAD and GET responses (excluding the 'Date' attribute)
        assertEquals(headResponse.getAllHeaders().length, getResponse.getAllHeaders().length);

        // Compare corresponding elements in HEAD and GET responses (excluding 'Date')
        for (int i = 0; i < headResponse.getAllHeaders().length; i++) {
            if (!headResponse.getAllHeaders()[i].getName().equalsIgnoreCase("Date")) {
                assertEquals(headResponse.getAllHeaders()[i].getElements(), getResponse.getAllHeaders()[i].getElements());
            }
        }

    }

    // Test: HEAD request for a Todo by ID
    @Test
    public void testHeadTodoById() throws IOException {
        String title = "testTodo";
        Boolean doneStatus = Boolean.FALSE;
        String description = "test description";

        // Create a Todo to fetch its ID for the head request
        createTodo(title,doneStatus,description,httpClient);
        HttpResponse getAllResponse = getAllTodos(httpClient);
        TodoResponse projects = deserialize(getAllResponse, TodoResponse.class);
        String projectID = projects.getTodos().get(0).getId();

        // Making requests for HEAD and GET
        HttpResponse headResponse = headTodo(projectID, httpClient);
        HttpResponse getResponse = getTodo(projectID, httpClient);

        // Check that the HEAD response does not return anything in the body
        assertNull(headResponse.getEntity());

        // Compare the headers from the HEAD and GET responses (excluding the 'Date' attribute)
        assertEquals(headResponse.getAllHeaders().length, getResponse.getAllHeaders().length);

        // Compare corresponding elements in HEAD and GET responses (excluding 'Date')
        for (int i = 0; i < headResponse.getAllHeaders().length; i++) {
            if (!headResponse.getAllHeaders()[i].getName().equalsIgnoreCase("Date")) {
                assertEquals(headResponse.getAllHeaders()[i].getElements(), getResponse.getAllHeaders()[i].getElements());
            }
        }
    }

    // Test: Create and modify a Todo
    @Test
    public void testCreateAndModify() throws IOException {
        String title = "testTodo";
        Boolean doneStatus = Boolean.FALSE;
        String description = "test description";

        // Create a new Todo
        HttpResponse response = createTodo(title, doneStatus, description, httpClient);
        int statusCode = response.getStatusLine().getStatusCode();
        assertEquals(HttpStatus.SC_CREATED, statusCode);

        // Get Todo objects and match the one we just created
        response = getAllTodos(httpClient);
        TodoResponse todos = deserialize(response, TodoResponse.class);

        // Filter Todos based on criteria, collect them, and get the 'id' of the first matching Todo
        List<Todo> todoList = todos.getTodos()
                .stream()
                .filter(todo -> title.equals(todo.getTitle())
                        && doneStatus.equals(todo.getDoneStatus())
                        && description.equals(todo.getDescription()))
                .toList();
        String id = todoList.get(0).getId();

        // Modify the created Todo using PUT
        String newDescription = "new test description";
        response = modifyTodoPut(id, title, doneStatus, newDescription, httpClient);
        statusCode = response.getStatusLine().getStatusCode();
        assertEquals(HttpStatus.SC_OK, statusCode);

        // Get the created Todo by ID and verify the changes
        response = getTodo(id, httpClient);
        todos = deserialize(response, TodoResponse.class);
        Todo todo = todos.getTodos().get(0);
        assertEquals(title, todo.getTitle());
        assertEquals(doneStatus, todo.getDoneStatus());
        assertEquals(newDescription, todo.getDescription());

        // Modify the created Todo using POST
        Boolean newDoneStatus = Boolean.TRUE;
        response = modifyTodoPost(id, title, newDoneStatus, description, httpClient);
        statusCode = response.getStatusLine().getStatusCode();
        assertEquals(HttpStatus.SC_OK, statusCode);

        // Get the created Todo by ID and verify the changes
        response = getTodo(id, httpClient);
        todos = deserialize(response, TodoResponse.class);
        todo = todos.getTodos().get(0);        assertEquals(title, todo.getTitle());
        assertEquals(newDoneStatus, todo.getDoneStatus());
        assertEquals(description, todo.getDescription());

        // Delete the created Todo
        response = deleteTodo(todo.getId(), httpClient);
        statusCode = response.getStatusLine().getStatusCode();
        assertEquals(HttpStatus.SC_OK, statusCode);
    }

    // Test: Create a Todo with an empty title
    @Test
    public void testErrorCreate() throws IOException {
        String title = "testTodo";
        String doneStatus = "fals";
        String description = "test description";

        // Create a Todo with an empty title and expect an error
        HttpResponse response = createTodo(title, doneStatus, description, httpClient);

        // Verify that a bad request status is returned with an error message
        ResponseError e = deserialize(response, ResponseError.class);
        int statusCode = response.getStatusLine().getStatusCode();
        assertEquals(HttpStatus.SC_BAD_REQUEST, statusCode);
        assertEquals("Failed Validation: doneStatus should be BOOLEAN", e.getErrorMessages().get(0));
    }

    // Test: Retrieve a Todo by a non-existent ID
    @Test
    public void testGetByIdNonexistent() throws IOException {
        HttpResponse response = getTodo("-1", httpClient);
        // Verify that a not found status is returned
        int statusCode = response.getStatusLine().getStatusCode();
        assertEquals(HttpStatus.SC_NOT_FOUND, statusCode);
    }

    @Test
    public void testPutByNonexistentID() throws IOException {
        String title = "testTodo";
        Boolean doneStatus = Boolean.FALSE;
        String description = "test description";
        HttpResponse response = modifyTodoPut("-1", title, doneStatus, description, httpClient);
        int statusCode = response.getStatusLine().getStatusCode();
        assertEquals(HttpStatus.SC_NOT_FOUND, statusCode);
    }

    // Test: Modify a Todo with an empty title using PUT
    @Test
    public void testPutInvalidDoneStatus() throws IOException {
        String title = "testTodo";
        Boolean doneStatus = Boolean.FALSE;
        String description = "test description";

        // Create a Todo to get its ID
        createTodo(title, doneStatus, description, httpClient);
        HttpResponse response = getAllTodos(httpClient);
        TodoResponse todos = deserialize(response, TodoResponse.class);

        List<Todo> todoList = todos.getTodos()
                .stream()
                .filter(todo -> title.equals(todo.getTitle())
                        && doneStatus.equals(todo.getDoneStatus())
                        && description.equals(todo.getDescription()))
                .toList();
        String id = todoList.get(0).getId();

        String invalidDoneStatus = "fals";

        // Attempt to modify the Todo with an empty title using PUT
        response = modifyTodoPut(id, title, invalidDoneStatus
                , description, httpClient);

        // Verify that a bad request status is returned with an error message
        ResponseError e = deserialize(response, ResponseError.class);
        int statusCode = response.getStatusLine().getStatusCode();
        assertEquals(HttpStatus.SC_BAD_REQUEST, statusCode);
        assertEquals("Failed Validation: doneStatus should be BOOLEAN", e.getErrorMessages().get(0));

        // Delete the Todo created for the test
        response = deleteTodo(id, httpClient);
        statusCode = response.getStatusLine().getStatusCode();
        assertEquals(HttpStatus.SC_OK, statusCode);
    }

    @Test
    public void testPostNonexistentID() throws IOException {
        String title = "testTodo";
        Boolean doneStatus = Boolean.FALSE;
        String description = "test description";
        HttpResponse response = modifyTodoPost("-1", title, doneStatus, description, httpClient);
        int statusCode = response.getStatusLine().getStatusCode();
        assertEquals(HttpStatus.SC_NOT_FOUND, statusCode);
    }

    // Test: Modify a Todo with an empty title using POST
    @Test
    public void testPostInvalidDoneStatus() throws IOException {
        // Define Todo properties
        String title = "testTodo";
        Boolean doneStatus = Boolean.FALSE;
        String description = "test description";

        // Create a Todo to get its ID
        createTodo(title, doneStatus, description, httpClient);
        HttpResponse response = getAllTodos(httpClient);
        TodoResponse todos = deserialize(response, TodoResponse.class);

        List<Todo> todoList = todos.getTodos()
                .stream()
                .filter(todo -> title.equals(todo.getTitle())
                        && doneStatus.equals(todo.getDoneStatus())
                        && description.equals(todo.getDescription()))
                .toList();
        String id = todoList.get(0).getId();

        String invalidDoneStatus = "fals";

        // Attempt to modify the Todo with an empty title using POST
        response = modifyTodoPost(id, title, invalidDoneStatus
                , description, httpClient);

        // Verify that a bad request status is returned with an error message
        ResponseError e = deserialize(response, ResponseError.class);
        int statusCode = response.getStatusLine().getStatusCode();
        assertEquals(HttpStatus.SC_BAD_REQUEST, statusCode);
        assertEquals("Failed Validation: doneStatus should be BOOLEAN", e.getErrorMessages().get(0));

        // Delete the Todo created for the test
        response = deleteTodo(id, httpClient);
        statusCode = response.getStatusLine().getStatusCode();
        assertEquals(HttpStatus.SC_OK, statusCode);
    }
     // Test: Delete a Todo by a non-existent ID
    @Test
    public void testDeleteNonexistentID() throws IOException {
        // Attempt to delete a Todo with a non-existent ID
        HttpResponse response = deleteTodo("-1", httpClient);
        // Verify that a not found status is returned
        int statusCode = response.getStatusLine().getStatusCode();
        assertEquals(HttpStatus.SC_NOT_FOUND, statusCode);
    }
}
