package helpers;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;

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

    public static HttpResponse createTodo(String title, Boolean doneStatus, String description, HttpClient httpClient) throws IOException {
        String bodyString = String.format("{\"title\":\"%s\", \"doneStatus\":%s, \"description\":\"%s\"}",
                title, doneStatus.toString(), description);
        StringEntity body = new StringEntity(bodyString);
        body.setContentType("application/json");
        return sendHttpRequest("post", baseUrl, body, httpClient);
    }

    public static HttpResponse modifyTodo(String id, String title, Boolean doneStatus, String description, HttpClient httpClient) throws IOException {
        String bodyString = String.format("{\"title\":\"%s\", \"doneStatus\":\"%s\", \"description\":\"%s\"}",
                title, doneStatus.toString(), description);
        System.out.println(bodyString);
        StringEntity body = new StringEntity(bodyString);
        body.setContentType("application/json");
        return sendHttpRequest("post", String.format("%s/%s", baseUrl, id), body, httpClient);
    }
}
