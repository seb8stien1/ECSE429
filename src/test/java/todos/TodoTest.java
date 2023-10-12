package todos;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.http.HttpResponse;
import response.Todo;
import response.TodoResponse;
import org.apache.http.impl.client.HttpClients;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.apache.http.impl.client.CloseableHttpClient;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import static helpers.TodoHelper.*;
import static helpers.ApiHelper.*;
import static org.junit.Assert.*;

public class TodoTest {

    CloseableHttpClient httpClient;

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

}
