package todos;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.http.HttpResponse;
import response.TodoResponse;
import org.apache.http.impl.client.HttpClients;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.apache.http.impl.client.CloseableHttpClient;

import java.io.IOException;

import static helpers.TodoHelper.*;
import static helpers.ApiHelper.*;
import static org.junit.Assert.*;

public class TodoTest {

    CloseableHttpClient httpClient;
    @Before
    public void before() throws IOException {
        clearApiDb(httpClient);
    }

    @Test
    public void testGetTodosNoneExpected() throws IOException {
        HttpResponse response = getAllTodos(httpClient);
        TodoResponse todos = deserialize(response, TodoResponse.class);

        int statusCode = response.getStatusLine().getStatusCode();
        assertEquals(200, statusCode);
        assertTrue(CollectionUtils.isEmpty(todos.getTodos()));
    }

    @Test
    public void testCreateTodo() throws IOException, InterruptedException {
        String title = "testTodo";
        Boolean doneStatus = Boolean.FALSE;
        String description = "test description";
        HttpResponse response = createTodo(title, doneStatus, description, httpClient);

        int statusCode = response.getStatusLine().getStatusCode();
        assertEquals(201, statusCode);

        response = getAllTodos(httpClient);
        TodoResponse todos = deserialize(response, TodoResponse.class);

        statusCode = response.getStatusLine().getStatusCode();
        assertEquals(200, statusCode);
        assertFalse(CollectionUtils.isEmpty(todos.getTodos()));
        assertEquals(title, todos.getTodos().get(0).getTitle());
        assertEquals(doneStatus, todos.getTodos().get(0).getDoneStatus());
        assertEquals(description, todos.getTodos().get(0).getDescription());
    }

}
