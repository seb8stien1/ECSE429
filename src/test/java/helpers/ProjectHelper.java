package helpers;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;

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
}
