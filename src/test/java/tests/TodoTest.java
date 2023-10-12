package tests;

import config.RandomOrderTestRunner;
import helpers.ApiHelper;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.http.HttpResponse;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.impl.client.HttpClients;
import org.junit.AfterClass;
import org.junit.runner.RunWith;
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
    public static void teardown() throws IOException {
        CloseableHttpClient httpClient= HttpClients.createDefault();
        try {
            ApiHelper.sendHttpRequest("get", "http://localhost:4567/shutdown", null, httpClient);
        } catch (Exception e) {
            assertEquals(HttpHostConnectException.class, e.getClass());
        }
    }

    @Test
    public void testCreateAndDeleteTodo() throws IOException {
        String title = "testTodo";
        Boolean doneStatus = Boolean.FALSE;
        String description = "test description";

//        create Todo object
        HttpResponse response = createTodo(title, doneStatus, description, httpClient);

        int statusCode = response.getStatusLine().getStatusCode();
        assertEquals(201, statusCode);

//        check todo object was created
        response = getAllTodos(httpClient);
        TodoResponse todos = deserialize(response, TodoResponse.class);

        statusCode = response.getStatusLine().getStatusCode();
        assertEquals(200, statusCode);

        List<Todo> todoList = todos.getTodos()
                .stream()
                .filter(todo -> title.equals(todo.getTitle())
                        && doneStatus.equals(todo.getDoneStatus()
                        && description.equals(todo.getDescription())))
                .collect(Collectors.toList());
        assertFalse(CollectionUtils.isEmpty(todoList));

//        delete each todo created, should be just one
        todoList.forEach(todo-> {
            try {
                deleteTodo(todo.getId(), httpClient);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Test
    public void testEmpty() throws IOException {}

    @Test
    public void testBlank() throws IOException {}

}
