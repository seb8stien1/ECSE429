package tests.features;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import lombok.AllArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.http.HttpResponse;
import org.apache.http.impl.client.CloseableHttpClient;
import response.Category;
import response.CategoryResponse;

import java.io.IOException;
import java.util.HashMap;
import java.util.Optional;

import static helpers.CategoryHelper.createCategory;
import static helpers.ApiHelper.deserialize;
import static helpers.CategoryHelper.getCategory;
import static org.junit.Assert.*;

/**
 * Step definitions for creating categories in the API.
 */
@AllArgsConstructor
public class CreateCategory {

    private final TestContext testContext;

    /**
     * Creates a category with the specified title and description.
     *
     * @param categoryTitle The title of the category to be created.
     * @param categoryDescription The description of the category to be created.
     * @throws IOException if an I/O error occurs when sending or receiving the HTTP request.
     */
    @When("a user creates a category with title {string} and description {string}")
    public void aUserCreatesACategoryWithTitleAndDescription(String categoryTitle, String categoryDescription) throws IOException {
        HashMap<String, Category> createdCategories = testContext.get("createdCategories", HashMap.class);
        CloseableHttpClient httpClient = testContext.get("httpClient", CloseableHttpClient.class);

        HttpResponse response = createCategory(categoryTitle, categoryDescription, httpClient);
        Category createdCategory = deserialize(response, Category.class);
        createdCategories.put(categoryTitle, createdCategory);

        testContext.set("createdCategories", createdCategories);
    }

    /**
     * Verifies that a new category with the specified title and description has been created.
     *
     * @param categoryTitle The title of the category to verify.
     * @param categoryDescription The description of the category to verify.
     * @throws IOException if an I/O error occurs when sending or receiving the HTTP request.
     */
    @Then("a new category with title {string} and {string} should be created")
    public void aNewCategoryWithTitleAndShouldBeCreated(String categoryTitle, String categoryDescription) throws IOException {
        HashMap<String, Category> createdCategories = testContext.get("createdCategories", HashMap.class);
        CloseableHttpClient httpClient = testContext.get("httpClient", CloseableHttpClient.class);

        String categoryID = createdCategories.get(categoryTitle).getId();

        HttpResponse response = getCategory(categoryID, httpClient);
        CategoryResponse categoryResponse = deserialize(response, CategoryResponse.class);

        assertFalse(CollectionUtils.isEmpty(categoryResponse.getCategories()));
        Optional<Category> categoryOptional = categoryResponse.getCategories()
                .stream()
                .filter(category -> categoryID.equals(category.getId()))
                .findFirst();
        assertTrue(categoryOptional.isPresent());
        Category returnedCategory = categoryOptional.get();

        assertNotNull(returnedCategory);
        assertEquals(categoryTitle, returnedCategory.getTitle());
        assertEquals(categoryDescription, returnedCategory.getDescription());
    }

    /**
     * Attempts to create a category with an invalid title.
     *
     * @param invalidTitle The invalid title used to create a category.
     * @throws IOException if an I/O error occurs when sending or receiving the HTTP request.
     */
    @When("a user creates a category with an invalid title {string}")
    public void aUserCreatesACategoryWithAnInvalidTitle(String invalidTitle) throws IOException {
        CloseableHttpClient httpClient = testContext.get("httpClient", CloseableHttpClient.class);

        HttpResponse response = createCategory(invalidTitle, "categoryDescription", httpClient);

        testContext.set("response", response);
        testContext.set("statusCode", response.getStatusLine().getStatusCode());
    }
}
