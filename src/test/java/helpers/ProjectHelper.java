package helpers;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.entity.StringEntity;

import java.io.IOException;

import static helpers.ApiHelper.sendHttpRequest;

public class ProjectHelper {
    private final static String baseUrl = "http://localhost:4567/projects";

    public static HttpResponse getAllProjects(HttpClient httpClient) throws IOException {
        return sendHttpRequest("get", baseUrl, null, httpClient);
    }

    public static HttpResponse deleteProject(String id, HttpClient httpClient) throws IOException {
        return sendHttpRequest("delete", String.format("%s/%s", baseUrl, id), null, httpClient);
    }
    public static HttpResponse getProject(String id, HttpClient httpClient) throws IOException {
        return sendHttpRequest("get", String.format("%s/%s", baseUrl, id), null, httpClient);
    }

    public static HttpResponse createProject(String title, Object completed, Object active, String description, HttpClient httpClient) throws IOException {
        String bodyString = String.format("{\"title\":\"%s\", \"completed\":%s, \"active\": %s, \"description\":\"%s\"}",
                title, completed.toString(), active.toString(), description);
        StringEntity body = new StringEntity(bodyString);
        body.setContentType("application/json");
        return sendHttpRequest("post", baseUrl, body, httpClient);
    }

    public static HttpResponse modifyProject1(String id, String title, Object completed, Object active, String description, HttpClient httpClient) throws IOException {
        String bodyString = String.format("{\"title\":\"%s\", \"completed\":%s, \"active\": %s, \"description\":\"%s\"}",
                title, completed.toString(), active.toString(), description);
        StringEntity body = new StringEntity(bodyString);
        body.setContentType("application/json");
        return sendHttpRequest("put", String.format("%s/%s", baseUrl, id), body, httpClient);
    }

    public static HttpResponse modifyProject2(String id, String title, Object completed, Object active, String description, HttpClient httpClient) throws IOException {
        String bodyString = String.format("{\"title\":\"%s\", \"completed\":%s, \"active\": %s, \"description\":\"%s\"}",
                title, completed.toString(), active.toString(), description);
        StringEntity body = new StringEntity(bodyString);
        body.setContentType("application/json");
        return sendHttpRequest("post", String.format("%s/%s", baseUrl, id), body, httpClient);
    }

    public static HttpResponse createCategoryAssociation(String projectId, String categoryId, HttpClient httpClient) throws IOException {
        String bodyString = String.format("{\"id\":\"%s\"}", categoryId);
        StringEntity body = new StringEntity(bodyString);
        body.setContentType("application/json");
        return sendHttpRequest("post", String.format("%s/%s/categories", baseUrl, projectId), body, httpClient);
    }

    public static HttpResponse getCategoryAssociation(String projectId, HttpClient httpClient) throws IOException {
        return sendHttpRequest("get", String.format("%s/%s/categories", baseUrl, projectId), null, httpClient);
    }
    public static HttpResponse deleteCategoryAssociation(String projectId, String categoryId, HttpClient httpClient) throws IOException {
        return sendHttpRequest("delete", String.format("%s/%s/categories/%s", baseUrl, projectId, categoryId), null, httpClient);
    }
}
