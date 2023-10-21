package helpers;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.*;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

/**
 * A utility class for making HTTP requests and handling JSON deserialization.
 */
public class ApiHelper {
    /**
     * Deserialize the JSON response from an HTTP request into an object of the specified type.
     *
     * @param response     The HTTP response to deserialize.
     * @param responseType  The class type to which the JSON response should be deserialized.
     * @param <T>          The type of the deserialized object.
     * @return An instance of the specified class representing the deserialized JSON response.
     * @throws IOException If there is an issue with reading the response entity or parsing JSON.
     */
    public static <T> T deserialize(HttpResponse response, Class<T> responseType) throws IOException {
        String responseBody = EntityUtils.toString(response.getEntity());
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(responseBody, responseType);
    }

        /**
     * Send an HTTP request of the specified type to the given URL with an optional request body.
     *
     * @param type          The type of HTTP request (e.g., "GET," "POST," "PUT," "DELETE," "HEAD").
     * @param url           The URL to send the request to.
     * @param stringEntity An optional request body as a StringEntity. Pass null for requests with no body.
     * @param httpClient    The HttpClient to use for sending the request.
     * @return The HTTP response from the server.
     * @throws IOException If there is an issue with the HTTP request or response.
     */
    public static HttpResponse sendHttpRequest(String type, String url, StringEntity stringEntity, HttpClient httpClient) throws IOException {
        httpClient = HttpClients.createDefault();
        HttpResponse response = null;
        HttpRequestBase http = null;
        switch (type.toLowerCase()) {
            case "get" -> http = new HttpGet(url);
            case "post" -> {
                http = new HttpPost(url);
                ((HttpPost) http).setEntity(stringEntity);
            }
            case "delete" -> http = new HttpDelete(url);
            case "put" -> {
                http = new HttpPut(url);
                ((HttpPut) http).setEntity(stringEntity);
            }
            case "head" -> http = new HttpHead(url);
            default -> {
                throw new IOException(String.format("Incorrect request type: %s", type));
            }
        }
        http.setHeader("Accept", "application/json");
        response = httpClient.execute(http);
        return response;
    }
}
