package helpers;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.entity.StringEntity;

import java.io.IOException;

import static helpers.ApiHelper.sendHttpRequest;

/**
 * A helper class for making HTTP requests related to categories and associations.
 */
public class CategoryHelper {
    private final static String baseUrl = "http://localhost:4567/categories";

    /**
     * Send an HTTP request to retrieve all categories.
     *
     * @param httpClient The HttpClient to use for the request.
     * @return The HTTP response containing the list of all categories.
     * @throws IOException If there is an issue with the HTTP request or response.
     */
    public static HttpResponse getAllCategories(HttpClient httpClient) throws IOException {
        return sendHttpRequest("get", baseUrl, null, httpClient);
    }

    /**
     * Send an HTTP request to delete a category by ID.
     *
     * @param id         The ID of the category to delete.
     * @param httpClient The HttpClient to use for the request.
     * @return The HTTP response indicating the success or failure of the deletion.
     * @throws IOException If there is an issue with the HTTP request or response.
     */
    public static HttpResponse deleteCategory(String id, HttpClient httpClient) throws IOException {
        return sendHttpRequest("delete", String.format("%s/%s", baseUrl, id), null, httpClient);
    }

    /**
     * Send an HTTP request to retrieve a category by ID.
     *
     * @param id         The ID of the category to retrieve.
     * @param httpClient The HttpClient to use for the request.
     * @return The HTTP response containing the category with the specified ID.
     * @throws IOException If there is an issue with the HTTP request or response.
     */
    public static HttpResponse getCategory(String id, HttpClient httpClient) throws IOException {
        return sendHttpRequest("get", String.format("%s/%s", baseUrl, id), null, httpClient);
    }

    /**
     * Send an HTTP HEAD request to retrieve category information for all categories.
     *
     * @param httpClient The HttpClient to use for the request.
     * @return The HTTP response containing category information in the headers.
     * @throws IOException If there is an issue with the HTTP request or response.
     */
    public static HttpResponse headAllCategories(HttpClient httpClient) throws IOException {
        return sendHttpRequest("head", baseUrl, null, httpClient);
    }

    /**
     * Send an HTTP HEAD request to retrieve category information for a specific category by ID.
     *
     * @param id         The ID of the category to retrieve information for.
     * @param httpClient The HttpClient to use for the request.
     * @return The HTTP response containing category information in the headers.
     * @throws IOException If there is an issue with the HTTP request or response.
     */
    public static HttpResponse headCategory(String id, HttpClient httpClient) throws IOException {
        return sendHttpRequest("head", String.format("%s/%s", baseUrl, id), null, httpClient);
    }

    /**
     * Send an HTTP POST request to create a new category.
     *
     * @param title      The title of the new category.
     * @param description The description of the new category.
     * @param httpClient The HttpClient to use for the request.
     * @return The HTTP response indicating the success or failure of the category creation.
     * @throws IOException If there is an issue with the HTTP request or response.
     */
    public static HttpResponse createCategory(String title, String description, HttpClient httpClient) throws IOException {
        String bodyString = String.format("{\"title\":\"%s\", \"description\":\"%s\"}",
                title, description);
        StringEntity body = new StringEntity(bodyString);
        body.setContentType("application/json");
        return sendHttpRequest("post", baseUrl, body, httpClient);
    }

    /**
     * Send an HTTP PUT request to modify an existing category by ID.
     *
     * @param id         The ID of the category to modify.
     * @param title      The updated title for the category.
     * @param description The updated description for the category.
     * @param httpClient The HttpClient to use for the request.
     * @return The HTTP response indicating the success or failure of the category modification.
     * @throws IOException If there is an issue with the HTTP request or response.
     */
    public static HttpResponse modifyCategoryPut(String id, String title, String description, HttpClient httpClient) throws IOException {
        String bodyString = String.format("{\"title\":\"%s\", \"description\":\"%s\"}",
                title, description);
        StringEntity body = new StringEntity(bodyString);
        body.setContentType("application/json");
        return sendHttpRequest("put", String.format("%s/%s", baseUrl, id), body, httpClient);
    }

    /**
     * Send an HTTP POST request to modify an existing category by ID.
     *
     * @param id         The ID of the category to modify.
     * @param title      The updated title for the category.
     * @param description The updated description for the category.
     * @param httpClient The HttpClient to use for the request.
     * @return The HTTP response indicating the success or failure of the category modification.
     * @throws IOException If there is an issue with the HTTP request or response.
     */
    public static HttpResponse modifyCategoryPost(String id, String title, String description, HttpClient httpClient) throws IOException {
        String bodyString = String.format("{\"title\":\"%s\", \"description\":\"%s\"}",
                title, description);
        StringEntity body = new StringEntity(bodyString);
        body.setContentType("application/json");
        return sendHttpRequest("post", String.format("%s/%s", baseUrl, id), body, httpClient);
    }

    /**
     * Send an HTTP POST request to create an association between a category and an object.
     *
     * @param associationType The type of association.
     * @param categoryId      The ID of the category.
     * @param objectId        The ID of the associated object.
     * @param httpClient      The HttpClient to use for the request.
     * @return The HTTP response indicating the success or failure of the association creation.
     * @throws IOException If there is an issue with the HTTP request or response.
     */
    public static HttpResponse createAssociation(String associationType, String categoryId, String objectId, HttpClient httpClient) throws IOException {
        String bodyString = String.format("{\"id\":\"%s\"}", objectId);
        StringEntity body = new StringEntity(bodyString);
        body.setContentType("application/json");
        return sendHttpRequest("post", String.format("%s/%s/%s", baseUrl, categoryId, associationType), body, httpClient);
    }

    /**
     * Send an HTTP GET request to retrieve an association between a category and an object.
     *
     * @param associationType The type of association.
     * @param categoryId      The ID of the category.
     * @param httpClient      The HttpClient to use for the request.
     * @return The HTTP response containing information about the association.
     * @throws IOException If there is an issue with the HTTP request or response.
     */
    public static HttpResponse getAssociation(String associationType, String categoryId, HttpClient httpClient) throws IOException {
        return sendHttpRequest("get", String.format("%s/%s/%s", baseUrl, categoryId, associationType), null, httpClient);
    }

    /**
     * Send an HTTP DELETE request to delete an association between a category and an object.
     *
     * @param associationType The type of association.
     * @param categoryId      The ID of the category.
     * @param objectId        The ID of the associated object.
     * @param httpClient      The HttpClient to use for the request.
     * @return The HTTP response indicating the success or failure of the association deletion.
     * @throws IOException If there is an issue with the HTTP request or response.
     */
    public static HttpResponse deleteAssociation(String associationType, String categoryId, String objectId, HttpClient httpClient) throws IOException {
        return sendHttpRequest("delete", String.format("%s/%s/%s/%s", baseUrl, categoryId, associationType, objectId), null, httpClient);
    }
}
