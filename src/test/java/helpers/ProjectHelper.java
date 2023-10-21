package helpers;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.entity.StringEntity;

import java.io.IOException;

import static helpers.ApiHelper.sendHttpRequest;

public class ProjectHelper {
    private final static String baseUrl = "http://localhost:4567/projects";

    /**
     * Sends an HTTP GET request to retrieve all projects.
     *
     * @param httpClient The HttpClient for making the request.
     * @return HttpResponse containing the response from the server.
     * @throws IOException If there's an I/O exception during the request.
     */
    public static HttpResponse getAllProjects(HttpClient httpClient) throws IOException {
        return sendHttpRequest("get", baseUrl, null, httpClient);
    }

    /**
     * Sends an HTTP DELETE request to delete a project by its ID.
     *
     * @param id The ID of the project to delete.
     * @param httpClient The HttpClient for making the request.
     * @return HttpResponse containing the response from the server.
     * @throws IOException If there's an I/O exception during the request.
     */
    public static HttpResponse deleteProject(String id, HttpClient httpClient) throws IOException {
        return sendHttpRequest("delete", String.format("%s/%s", baseUrl, id), null, httpClient);
    }

    /**
     * Sends an HTTP GET request to retrieve a project by its ID.
     *
     * @param id The ID of the project to retrieve.
     * @param httpClient The HttpClient for making the request.
     * @return HttpResponse containing the response from the server.
     * @throws IOException If there's an I/O exception during the request.
     */
    public static HttpResponse getProject(String id, HttpClient httpClient) throws IOException {
        return sendHttpRequest("get", String.format("%s/%s", baseUrl, id), null, httpClient);
    }

    /**
     * Sends an HTTP POST request to create a new project with the specified attributes.
     *
     * @param title The title of the project.
     * @param completed The completion status of the project (true or false).
     * @param active The active status of the project (true or false).
     * @param description The description of the project.
     * @param httpClient The HttpClient for making the request.
     * @return HttpResponse containing the response from the server.
     * @throws IOException If there's an I/O exception during the request.
     */
    public static HttpResponse createProject(String title, Object completed, Object active, String description, HttpClient httpClient) throws IOException {
        String bodyString = String.format("{\"title\":\"%s\", \"completed\":%s, \"active\": %s, \"description\":\"%s\"}",
                title, completed.toString(), active.toString(), description);
        StringEntity body = new StringEntity(bodyString);
        body.setContentType("application/json");
        return sendHttpRequest("post", baseUrl, body, httpClient);
    }

    /**
     * Sends an HTTP HEAD request to retrieve information about all projects.
     *
     * @param httpClient The HttpClient for making the request.
     * @return HttpResponse containing the response from the server.
     * @throws IOException If there's an I/O exception during the request.
     */
    public static HttpResponse headAllProjects(HttpClient httpClient) throws IOException {
        return sendHttpRequest("head", baseUrl, null, httpClient);
    }

    /**
     * Sends an HTTP HEAD request to retrieve information about a specific project by its ID.
     *
     * @param id The ID of the project to retrieve information about.
     * @param httpClient The HttpClient for making the request.
     * @return HttpResponse containing the response from the server.
     * @throws IOException If there's an I/O exception during the request.
     */
    public static HttpResponse headProject(String id, HttpClient httpClient) throws IOException {
        return sendHttpRequest("head", String.format("%s/%s", baseUrl, id), null, httpClient);
    }

    /**
     * Sends an HTTP PUT request to modify an existing project with the specified attributes.
     *
     * @param id The ID of the project to modify.
     * @param title The updated title of the project.
     * @param completed The updated completion status of the project (true or false).
     * @param active The updated active status of the project (true or false).
     * @param description The updated description of the project.
     * @param httpClient The HttpClient for making the request.
     * @return HttpResponse containing the response from the server.
     * @throws IOException If there's an I/O exception during the request.
     */
    public static HttpResponse modifyProjectPut(String id, String title, Object completed, Object active, String description, HttpClient httpClient) throws IOException {
        String bodyString = String.format("{\"title\":\"%s\", \"completed\":%s, \"active\": %s, \"description\":\"%s\"}",
                title, completed.toString(), active.toString(), description);
        StringEntity body = new StringEntity(bodyString);
        body.setContentType("application/json");
        return sendHttpRequest("put", String.format("%s/%s", baseUrl, id), body, httpClient);
    }

    /**
     * Sends an HTTP POST request to modify an existing project with the specified attributes.
     *
     * @param id The ID of the project to modify.
     * @param title The updated title of the project.
     * @param completed The updated completion status of the project (true or false).
     * @param active The updated active status of the project (true or false).
     * @param description The updated description of the project.
     * @param httpClient The HttpClient for making the request.
     * @return HttpResponse containing the response from the server.
     * @throws IOException If there's an I/O exception during the request.
     */
    public static HttpResponse modifyProjectPost(String id, String title, Object completed, Object active, String description, HttpClient httpClient) throws IOException {
        String bodyString = String.format("{\"title\":\"%s\", \"completed\":%s, \"active\": %s, \"description\":\"%s\"}",
                title, completed.toString(), active.toString(), description);
        StringEntity body = new StringEntity(bodyString);
        body.setContentType("application/json");
        return sendHttpRequest("post", String.format("%s/%s", baseUrl, id), body, httpClient);
    }

    /**
     * Creates an association between a project and an object of a specific type.
     *
     * @param associationType The type of association (e.g., "tasks").
     * @param projectId The ID of the project to create the association for.
     * @param objectId The ID of the object to associate with the project.
     * @param httpClient The HttpClient for making the request.
     * @return HttpResponse containing the response from the server.
     * @throws IOException If there's an I/O exception during the request.
     */
    public static HttpResponse createAssociation(String associationType, String projectId, String objectId, HttpClient httpClient) throws IOException {
        String bodyString = String.format("{\"id\":\"%s\"}", objectId);
        StringEntity body = new StringEntity(bodyString);
        body.setContentType("application/json");
        return sendHttpRequest("post", String.format("%s/%s/%s", baseUrl, projectId, associationType), body, httpClient);
    }

    /**
     * Retrieves information about the association between a project and objects of a specific type.
     *
     * @param associationType The type of association (e.g., "tasks").
     * @param projectId The ID of the project to retrieve associations for.
     * @param httpClient The HttpClient for making the request.
     * @return HttpResponse containing the response from the server.
     * @throws IOException If there's an I/O exception during the request.
     */
    public static HttpResponse getAssociation(String associationType, String projectId, HttpClient httpClient) throws IOException {
        return sendHttpRequest("get", String.format("%s/%s/%s", baseUrl, projectId, associationType), null, httpClient);
    }

    /**
     * Deletes an association between a project and an object of a specific type.
     *
     * @param associationType The type of association (e.g., "tasks").
     * @param projectId The ID of the project to delete the association from.
     * @param objectId The ID of the object to disassociate from the project.
     * @param httpClient The HttpClient for making the request.
     * @return HttpResponse containing the response from the server.
     * @throws IOException If there's an I/O exception during the request.
     */
    public static HttpResponse deleteAssociation(String associationType, String projectId, String objectId, HttpClient httpClient) throws IOException {
        return sendHttpRequest("delete", String.format("%s/%s/%s/%s", baseUrl, projectId, associationType, objectId), null, httpClient);
    }

}
