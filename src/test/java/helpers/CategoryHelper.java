package helpers;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.entity.StringEntity;

import java.io.IOException;

import static helpers.ApiHelper.sendHttpRequest;

public class CategoryHelper {
    private final static String baseUrl = "http://localhost:4567/categories";

    public static HttpResponse getAllCategories(HttpClient httpClient) throws IOException {
        return sendHttpRequest("get", baseUrl, null, httpClient);
    }

    public static HttpResponse deleteCategory(String id, HttpClient httpClient) throws IOException {
        return sendHttpRequest("delete", String.format("%s/%s", baseUrl, id), null, httpClient);
    }
    public static HttpResponse getCategory(String id, HttpClient httpClient) throws IOException {
        return sendHttpRequest("get", String.format("%s/%s", baseUrl, id), null, httpClient);
    }

    public static HttpResponse createCategory(String title, String description, HttpClient httpClient) throws IOException {
        String bodyString = String.format("{\"title\":\"%s\", \"description\":\"%s\"}",
                title, description);
        StringEntity body = new StringEntity(bodyString);
        body.setContentType("application/json");
        return sendHttpRequest("post", baseUrl, body, httpClient);
    }

    public static HttpResponse modifyCategoryPut(String id, String title, String description, HttpClient httpClient) throws IOException {
        String bodyString = String.format("{\"title\":\"%s\", \"description\":\"%s\"}",
                title, description);
        StringEntity body = new StringEntity(bodyString);
        body.setContentType("application/json");
        return sendHttpRequest("put", String.format("%s/%s", baseUrl, id), body, httpClient);
    }

    public static HttpResponse modifyCategoryPost(String id, String title, String description, HttpClient httpClient) throws IOException {
        String bodyString = String.format("{\"title\":\"%s\", \"description\":\"%s\"}",
                title, description);
        StringEntity body = new StringEntity(bodyString);
        body.setContentType("application/json");
        return sendHttpRequest("post", String.format("%s/%s", baseUrl, id), body, httpClient);
    }

    public static HttpResponse createProjectAssociation(String categoryId, String projectId, HttpClient httpClient) throws IOException {
        String bodyString = String.format("{\"id\":\"%s\"}", projectId);
        StringEntity body = new StringEntity(bodyString);
        body.setContentType("application/json");
        return sendHttpRequest("post", String.format("%s/%s/projects", baseUrl, categoryId), body, httpClient);
    }

    public static HttpResponse getProjectAssociation(String categoryId, HttpClient httpClient) throws IOException {
        return sendHttpRequest("get", String.format("%s/%s/projects", baseUrl, categoryId), null, httpClient);
    }
    public static HttpResponse deleteProjectAssociation(String categoryId, String projectId, HttpClient httpClient) throws IOException {
        return sendHttpRequest("delete", String.format("%s/%s/projects/%s", baseUrl, categoryId, projectId), null, httpClient);
    }
}
