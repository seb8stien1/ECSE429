package helpers;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.*;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import response.CategoryResponse;
import response.Todo;
import response.TodoResponse;
import response.Category;
import response.ProjectResponse;
import response.Project;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class ApiHelper {

    public static void clearApiDb(HttpClient httpClient) throws IOException{
        HttpResponse response = TodoHelper.getAllTodos(httpClient);
        TodoResponse todos = deserialize(response, TodoResponse.class);
        for (Todo todo: todos.getTodos()) {
            TodoHelper.deleteTodo(todo.getId(), httpClient);
        }
        response = ProjectHelper.getAllProjects(httpClient);
        ProjectResponse projects = deserialize(response, ProjectResponse.class);
        for (Project project: projects.getProjects()) {
            ProjectHelper.deleteProject(project.getId(), httpClient);
        }
        response = CategoryHelper.getAllCategories(httpClient);
        CategoryResponse categories = deserialize(response, CategoryResponse.class);
        for (Category category: categories.getCategories()) {
            CategoryHelper.deleteCategory(category.getId(), httpClient);
        }
    }

    public static <T> T deserialize(HttpResponse response, Class<T> responseType) throws IOException {
        String responseBody = EntityUtils.toString(response.getEntity());
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(responseBody, responseType);
    }

    public static HttpResponse sendHttpRequest(String type, String url, StringEntity stringEntity, HttpClient httpClient) throws IOException {
        httpClient = HttpClients.createDefault();
        HttpResponse response = null;
        HttpRequestBase http = null;
        switch (type.toLowerCase()) {
            case "get" -> http = new HttpGet(url);
            case "post" -> {
                http = new HttpPost(url);
                http.setHeader("Content-Type", "application/json");
                ((HttpPost) http).setEntity(stringEntity);
            }
            case "delete" -> http = new HttpDelete(url);
            case "put" -> {
                http = new HttpPut(url);
                ((HttpPut) http).setEntity(stringEntity);
            }
            case "options" -> http = new HttpOptions(url);
            case "head" -> http = new HttpHead(url);
            default -> {
                throw new IOException(String.format("Incorrect request type: %s", type));
            }
        }
        response = httpClient.execute(http);
        return response;
    }
}
