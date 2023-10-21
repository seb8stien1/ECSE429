package helpers;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.entity.StringEntity;

import java.io.IOException;

import static helpers.ApiHelper.sendHttpRequest;

public class TodoHelper {
    private final static String baseUrl = "http://localhost:4567/todos";

    /**
     * Retrieves a list of all todos from the server.
     *
     * @param httpClient The HttpClient for making the request.
     * @return HttpResponse containing the response from the server.
     * @throws IOException If there's an I/O exception during the request.
     */
    public static HttpResponse getAllTodos(HttpClient httpClient) throws IOException {
        return sendHttpRequest("get", baseUrl, null, httpClient);
    }

    /**
     * Deletes a todo with the specified ID.
     *
     * @param id The ID of the todo to delete.
     * @param httpClient The HttpClient for making the request.
     * @return HttpResponse containing the response from the server.
     * @throws IOException If there's an I/O exception during the request.
     */
    public static HttpResponse deleteTodo(String id, HttpClient httpClient) throws IOException {
        return sendHttpRequest("delete", String.format("%s/%s", baseUrl, id), null, httpClient);
    }

    /**
     * Retrieves information about a specific todo by its ID.
     *
     * @param id The ID of the todo to retrieve.
     * @param httpClient The HttpClient for making the request.
     * @return HttpResponse containing the response from the server.
     * @throws IOException If there's an I/O exception during the request.
     */
    public static HttpResponse getTodo(String id, HttpClient httpClient) throws IOException {
        return sendHttpRequest("get", String.format("%s/%s", baseUrl, id), null, httpClient);
    }

    /**
     * Creates a new todo with the given title, done status, and description.
     *
     * @param title The title of the todo.
     * @param doneStatus The done status of the todo (true or false).
     * @param description The description of the todo.
     * @param httpClient The HttpClient for making the request.
     * @return HttpResponse containing the response from the server.
     * @throws IOException If there's an I/O exception during the request.
     */
    public static HttpResponse createTodo(String title, Object doneStatus, String description, HttpClient httpClient) throws IOException {
        // Construct an XML-formatted request body
        String bodyString = String.format("<todo>" +
                        "  <doneStatus>%s</doneStatus>" +
                        "  <description>%s</description>" +
                        "  <title>%s</title>" +
                        "</todo>",
                doneStatus.toString(), description, title);

        StringEntity body = new StringEntity(bodyString);
        body.setContentType("application/xml");
        return sendHttpRequest("post", baseUrl, body, httpClient);
    }

    /**
     * Retrieves headers of all todos from the server.
     *
     * @param httpClient The HttpClient for making the request.
     * @return HttpResponse containing the headers response from the server.
     * @throws IOException If there's an I/O exception during the request.
     */
    public static HttpResponse headAllTodos(HttpClient httpClient) throws IOException {
        return sendHttpRequest("head", baseUrl, null, httpClient);
    }

    /**
     * Retrieves headers of a specific todo by its ID.
     *
     * @param id The ID of the todo to retrieve headers for.
     * @param httpClient The HttpClient for making the request.
     * @return HttpResponse containing the headers response from the server.
     * @throws IOException If there's an I/O exception during the request.
     */
    public static HttpResponse headTodo(String id, HttpClient httpClient) throws IOException {
        return sendHttpRequest("head", String.format("%s/%s", baseUrl, id), null, httpClient);
    }

    /**
     * Modifies a todo with the specified ID using the HTTP PUT method.
     *
     * @param id The ID of the todo to modify.
     * @param title The updated title of the todo.
     * @param doneStatus The updated done status of the todo (true or false).
     * @param description The updated description of the todo.
     * @param httpClient The HttpClient for making the request.
     * @return HttpResponse containing the response from the server.
     * @throws IOException If there's an I/O exception during the request.
     */
    public static HttpResponse modifyTodoPut(String id, String title, Object doneStatus, String description, HttpClient httpClient) throws IOException {
        // Construct an XML-formatted request body
        String bodyString = String.format("<todo>" +
                        "  <doneStatus>%s</doneStatus>" +
                        "  <description>%s</description>" +
                        "  <title>%s</title>" +
                        "</todo>",
                doneStatus.toString(), description, title);
        StringEntity body = new StringEntity(bodyString);
        body.setContentType("application/xml");
        return sendHttpRequest("put", String.format("%s/%s", baseUrl, id), body, httpClient);
    }

    /**
     * Modifies a todo with the specified ID using the HTTP POST method.
     *
     * @param id The ID of the todo to modify.
     * @param title The updated title of the todo.
     * @param doneStatus The updated done status of the todo (true or false).
     * @param description The updated description of the todo.
     * @param httpClient The HttpClient for making the request.
     * @return HttpResponse containing the response from the server.
     * @throws IOException If there's an I/O exception during the request.
     */
    public static HttpResponse modifyTodoPost(String id, String title, Object doneStatus, String description, HttpClient httpClient) throws IOException {
        // Construct an XML-formatted request body
        String bodyString = String.format("<todo>" +
                        "  <doneStatus>%s</doneStatus>" +
                        "  <description>%s</description>" +
                        "  <title>%s</title>" +
                        "</todo>",
                doneStatus.toString(), description, title);
        StringEntity body = new StringEntity(bodyString);
        body.setContentType("application/xml");
        return sendHttpRequest("post", String.format("%s/%s", baseUrl, id), body, httpClient);
    }

    /**
     * Creates an association between a todo and another object.
     *
     * @param associationType The type of association.
     * @param todoId The ID of the todo.
     * @param objectId The ID of the associated object.
     * @param httpClient The HttpClient for making the request.
     * @return HttpResponse containing the response from the server.
     * @throws IOException If there's an I/O exception during the request.
     */
    public static HttpResponse createAssociation(String associationType, String todoId, String objectId, HttpClient httpClient) throws IOException {
        String bodyString = String.format("{\"id\":\"%s\"}", objectId);
        StringEntity body = new StringEntity(bodyString);
        body.setContentType("application/json");
        return sendHttpRequest("post", String.format("%s/%s/%s", baseUrl, todoId, associationType), body, httpClient);
    }

    /**
     * Retrieves an association between a todo and another object.
     *
     * @param associationType The type of association.
     * @param todoId The ID of the todo.
     * @param httpClient The HttpClient for making the request.
     * @return HttpResponse containing the response from the server.
     * @throws IOException If there's an I/O exception during the request.
     */
    public static HttpResponse getAssociation(String associationType, String todoId, HttpClient httpClient) throws IOException {
        return sendHttpRequest("get", String.format("%s/%s/%s", baseUrl, todoId, associationType), null, httpClient);
    }

    /**
     * Deletes an association between a todo and another object.
     *
     * @param associationType The type of association.
     * @param todoId The ID of the todo.
     * @param objectId The ID of the associated object.
     * @param httpClient The HttpClient for making the request.
     * @return HttpResponse containing the response from the server.
     * @throws IOException If there's an I/O exception during the request.
     */
    public static HttpResponse deleteAssociation(String associationType, String todoId, String objectId, HttpClient httpClient) throws IOException {
        return sendHttpRequest("delete", String.format("%s/%s/%s/%s", baseUrl, todoId, associationType, objectId), null, httpClient);
    }

}
