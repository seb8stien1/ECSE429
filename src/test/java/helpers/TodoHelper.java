package helpers;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.entity.StringEntity;

import java.io.IOException;

import static helpers.ApiHelper.sendHttpRequest;

public class TodoHelper {
    private final static String baseUrl = "http://localhost:4567/todos";

    public static HttpResponse getAllTodos(HttpClient httpClient) throws IOException {
        return sendHttpRequest("get", baseUrl, null, httpClient);
    }

    public static HttpResponse deleteTodo(String id, HttpClient httpClient) throws IOException {
        return sendHttpRequest("delete", String.format("%s/%s", baseUrl, id), null, httpClient);
    }

    public static HttpResponse getTodo(String id, HttpClient httpClient) throws IOException {
        return sendHttpRequest("get", String.format("%s/%s", baseUrl, id), null, httpClient);
    }

    public static HttpResponse createTodo(String title, Object doneStatus, String description, HttpClient httpClient) throws IOException {
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

    public static HttpResponse headAllTodos(HttpClient httpClient) throws IOException {
        return sendHttpRequest("head", baseUrl, null, httpClient);
    }

    public static HttpResponse headTodo(String id, HttpClient httpClient) throws IOException {
        return sendHttpRequest("head", String.format("%s/%s", baseUrl, id), null, httpClient);
    }

    public static HttpResponse modifyTodoPut(String id, String title, Object doneStatus, String description, HttpClient httpClient) throws IOException {
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

    public static HttpResponse modifyTodoPost(String id, String title, Object doneStatus, String description, HttpClient httpClient) throws IOException {
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

    public static HttpResponse createAssociation(String associationType, String todoId, String objectId, HttpClient httpClient) throws IOException {
        String bodyString = String.format("{\"id\":\"%s\"}", objectId);
        StringEntity body = new StringEntity(bodyString);
        body.setContentType("application/json");
        return sendHttpRequest("post", String.format("%s/%s/%s", baseUrl, todoId, associationType), body, httpClient);
    }

    public static HttpResponse getAssociation(String associationType, String todoId, HttpClient httpClient) throws IOException {
        return sendHttpRequest("get", String.format("%s/%s/%s", baseUrl, todoId, associationType), null, httpClient);
    }
    public static HttpResponse deleteAssociation(String associationType, String todoId, String objectId, HttpClient httpClient) throws IOException {
        return sendHttpRequest("delete", String.format("%s/%s/%s/%s", baseUrl, todoId, associationType, objectId), null, httpClient);
    }
}
