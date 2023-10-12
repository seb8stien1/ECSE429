package tests;

import config.RandomOrderTestRunner;
import helpers.ApiHelper;
import org.apache.http.HttpResponse;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(RandomOrderTestRunner.class)
public class AssociationTest {
    CloseableHttpClient httpClient;
    @BeforeClass
    public static void testOn() throws IOException {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpResponse response = ApiHelper.sendHttpRequest("get", "http://localhost:4567/", null, httpClient);
        int statusCode = response.getStatusLine().getStatusCode();
        assertEquals(200, statusCode);
    }
    @Test
    public void doNothing() {
        assertTrue(true);
    }

    @Test
    public void testEmpty() throws IOException {}

    @Test
    public void testBlank() throws IOException {}
}
