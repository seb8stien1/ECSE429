package helpers;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;

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
}
