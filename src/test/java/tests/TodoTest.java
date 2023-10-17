package tests;

import config.RandomOrderTestRunner;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.http.HttpResponse;
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

//    @AfterClass
//    public static void teardown() {
//        CloseableHttpClient httpClient= HttpClients.createDefault();
//        try {
//            ApiHelper.sendHttpRequest("get", "http://localhost:4567/shutdown", null, httpClient);
//        } catch (Exception e) {
//            assertEquals(HttpHostConnectException.class, e.getClass());
//        }
//    }

    @Test
    public void testCreateGetAllAndDeleteByIdTodo() throws IOException {
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
    public void testGetPostPutById() throws IOException {
        String title = "testTodo";
        Boolean doneStatus = Boolean.FALSE;
        String description = "test description";

//        create Todo object
        HttpResponse response = createTodo(title, doneStatus, description, httpClient);
        int statusCode = response.getStatusLine().getStatusCode();
        assertEquals(201, statusCode);

//        get todo objects and match to the one we just created
        response = getAllTodos(httpClient);
        TodoResponse todos = deserialize(response, TodoResponse.class);

        List<Todo> todoList = todos.getTodos()
                .stream()
                .filter(todo -> title.equals(todo.getTitle())
                        && doneStatus.equals(todo.getDoneStatus()
                        && description.equals(todo.getDescription())))
                .toList();
        String id = todoList.get(0).getId();

//        modify the created todo using put
        String newDescription = "new test description";
        response = modifyTodo1(id, title, doneStatus, newDescription, httpClient);
        statusCode = response.getStatusLine().getStatusCode();
        assertEquals(200, statusCode);

//        get the created todo by id
        response = getTodo(id, httpClient);
        todos = deserialize(response, TodoResponse.class);
        Todo todo = todos.getTodos().get(0);
        assertEquals(title, todo.getTitle());
        assertEquals(doneStatus, todo.getDoneStatus());
        assertEquals(newDescription, todo.getDescription());

//        modify the created todo using post
        Boolean newDoneStatus = Boolean.TRUE;
        response = modifyTodo1(id, title, newDoneStatus, description, httpClient);
        statusCode = response.getStatusLine().getStatusCode();
        assertEquals(200, statusCode);

//        get the created todo by id
        response = getTodo(id, httpClient);
        todos = deserialize(response, TodoResponse.class);
        todo = todos.getTodos().get(0);        assertEquals(title, todo.getTitle());
        assertEquals(newDoneStatus, todo.getDoneStatus());
        assertEquals(description, todo.getDescription());

//        delete the created todo
        response = deleteTodo(todo.getId(), httpClient);
        statusCode = response.getStatusLine().getStatusCode();
        assertEquals(200, statusCode);
    }

    @Test
    public void testErrorCreate() throws IOException {
        String title = "testTodo";
        String doneStatus = "fals";
        String description = "test description";

//        create Todo object
        HttpResponse response = createTodo(title, doneStatus, description, httpClient);

        ResponseError e = deserialize(response, ResponseError.class);
        int statusCode = response.getStatusLine().getStatusCode();
        assertEquals(400, statusCode);
        assertEquals("Failed Validation: doneStatus should be BOOLEAN", e.getErrorMessages().get(0));
    }

    @Test
    public void test404GetById() throws IOException {
        HttpResponse response = getTodo("-1", httpClient);
        int statusCode = response.getStatusLine().getStatusCode();
        assertEquals(404, statusCode);
    }

    @Test
    public void test404Put() throws IOException {
        String title = "testTodo";
        Boolean doneStatus = Boolean.FALSE;
        String description = "test description";
        HttpResponse response = modifyTodo1("-1", title, doneStatus, description, httpClient);
        int statusCode = response.getStatusLine().getStatusCode();
        assertEquals(404, statusCode);
    }

    @Test
    public void test400Put() throws IOException {
        String title = "testTodo";
        Boolean doneStatus = Boolean.FALSE;
        String description = "test description";

        createTodo(title, doneStatus, description, httpClient);
        HttpResponse response = getAllTodos(httpClient);
        TodoResponse todos = deserialize(response, TodoResponse.class);

        List<Todo> todoList = todos.getTodos()
                .stream()
                .filter(todo -> title.equals(todo.getTitle())
                        && doneStatus.equals(todo.getDoneStatus()
                        && description.equals(todo.getDescription())))
                .toList();
        String id = todoList.get(0).getId();

        String invalidDoneStatus = "fals";

        response = modifyTodo1(id, title, invalidDoneStatus
                , description, httpClient);
        ResponseError e = deserialize(response, ResponseError.class);
        int statusCode = response.getStatusLine().getStatusCode();
        assertEquals(400, statusCode);
        assertEquals("Failed Validation: doneStatus should be BOOLEAN", e.getErrorMessages().get(0));

        deleteTodo(id, httpClient);
    }

    @Test
    public void test404Post() throws IOException {
        String title = "testTodo";
        Boolean doneStatus = Boolean.FALSE;
        String description = "test description";
        HttpResponse response = modifyTodo2("-1", title, doneStatus, description, httpClient);
        int statusCode = response.getStatusLine().getStatusCode();
        assertEquals(404, statusCode);
    }

    @Test
    public void test400Post() throws IOException {
        String title = "testTodo";
        Boolean doneStatus = Boolean.FALSE;
        String description = "test description";

        createTodo(title, doneStatus, description, httpClient);
        HttpResponse response = getAllTodos(httpClient);
        TodoResponse todos = deserialize(response, TodoResponse.class);

        List<Todo> todoList = todos.getTodos()
                .stream()
                .filter(todo -> title.equals(todo.getTitle())
                        && doneStatus.equals(todo.getDoneStatus()
                        && description.equals(todo.getDescription())))
                .toList();
        String id = todoList.get(0).getId();

        String invalidDoneStatus = "fals";

        response = modifyTodo2(id, title, invalidDoneStatus
                , description, httpClient);
        ResponseError e = deserialize(response, ResponseError.class);
        int statusCode = response.getStatusLine().getStatusCode();
        assertEquals(400, statusCode);
        assertEquals("Failed Validation: doneStatus should be BOOLEAN", e.getErrorMessages().get(0));

        deleteTodo(id, httpClient);
    }

    @Test
    public void test404Delete() throws IOException {
        HttpResponse response = deleteTodo("-1", httpClient);
        int statusCode = response.getStatusLine().getStatusCode();
        assertEquals(404, statusCode);
    }
}
