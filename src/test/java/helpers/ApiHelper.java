package helpers;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.*;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

public class ApiHelper {

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
