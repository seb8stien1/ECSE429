package tests.features;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import lombok.AllArgsConstructor;
import org.apache.http.HttpResponse;
import org.apache.http.impl.client.CloseableHttpClient;
import response.Category;
import response.CategoryResponse;

import java.io.IOException;
import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;

import static org.junit.Assert.*;
import static helpers.ApiHelper.deserialize;
import static helpers.CategoryHelper.deleteCategory;
import static helpers.CategoryHelper.getAllCategories;

/**
 * Step definitions for deleting categories and verifying their deletion in the API.
 */
@AllArgsConstructor
public class DeleteCategory {

    private final TestContext testContext;

    /**
     * Attempts to delete a category with the specified title from the system.
     *
     * @param categoryTitle The title of the category to be deleted.
     * @throws IOException if an I/O error occurs when sending or receiving the HTTP request.
     */
    @When("a user deletes the category {string}")
    public void aUserDeletesTheCategory(String categoryTitle) throws IOException {
        HashMap<String, Category> createdCategories = testContext.get("createdCategories", HashMap.class);
        CloseableHttpClient httpClient = testContext.get("httpClient", CloseableHttpClient.class);

        String categoryID = createdCategories.get(categoryTitle).getId();

        deleteCategory(categoryID, httpClient);
    }

    /**
     * Verifies that the category with the specified title has been removed from the system.
     *
     * @param categoryTitle The title of the category to verify removal.
     * @throws IOException if an I/O error occurs when sending or receiving the HTTP request.
     */
    @Then("the category {string} should be removed from the system")
    public void theCategoryShouldBeRemovedFromTheSystem(String categoryTitle) throws IOException {
        HashMap<String, Category> createdCategories = testContext.get("createdCategories", HashMap.class);
        CloseableHttpClient httpClient = testContext.get("httpClient", CloseableHttpClient.class);

        String categoryID = createdCategories.get(categoryTitle).getId();

        HttpResponse response = getAllCategories(httpClient);
        CategoryResponse categoryResponse = deserialize(response, CategoryResponse.class);

        Optional<Category> categoryOptional = categoryResponse.getCategories()
                .stream()
                .filter(category -> categoryID.equals(category.getId()))
                .findFirst();
        assertFalse(categoryOptional.isPresent());
    }

    /**
     * Attempts to delete a category that has already been deleted, identified by its title.
     *
     * @param categoryTitle The title of the already deleted category.
     * @throws IOException if an I/O error occurs when sending or receiving the HTTP request.
     */
    @When("a user deletes the already deleted category {string}")
    public void aUserDeletesTheAlreadyDeletedCategory(String categoryTitle) throws IOException {
        HashMap<String, Category> createdCategories = testContext.get("createdCategories", HashMap.class);
        CloseableHttpClient httpClient = testContext.get("httpClient", CloseableHttpClient.class);

        Category nonExistentCategory = createdCategories.get(categoryTitle);
        assertNull(nonExistentCategory);
        String nonExistentCategoryID = UUID.randomUUID().toString();

        HttpResponse response = deleteCategory(nonExistentCategoryID, httpClient);

        int statusCode = response.getStatusLine().getStatusCode();
        testContext.set("statusCode", statusCode);
    }

    /**
     * Attempts to delete a category using an invalid ID.
     *
     * @param invalidID The invalid ID to be used in the deletion attempt.
     * @throws IOException if an I/O error occurs when sending or receiving the HTTP request.
     */
    @When("a user attempts to delete the category with an invalid ID {string}")
    public void aUserAttemptsToDeleteTheCategoryWithAnInvalidID(String invalidID) throws IOException {
        CloseableHttpClient httpClient = testContext.get("httpClient", CloseableHttpClient.class);

        HttpResponse response = deleteCategory(invalidID, httpClient);

        int statusCode = response.getStatusLine().getStatusCode();
        testContext.set("statusCode", statusCode);
    }
}
