package tests.features;

import helpers.ApiHelper;
import io.cucumber.java.en.Given;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class CommonStepDefinitions {
    @Given("the API server is running and available")
    public void theAPIServerIsRunningAndAvailable() throws IOException {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpResponse response = ApiHelper.sendHttpRequest("get", "http://localhost:4567/docs", null, httpClient);
        int statusCode = response.getStatusLine().getStatusCode();
        assertEquals(HttpStatus.SC_OK, statusCode);
    }

}
