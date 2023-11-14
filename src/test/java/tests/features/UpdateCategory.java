package tests.features;

import helpers.CategoryHelper;
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

import static helpers.ApiHelper.deserialize;
import static helpers.CategoryHelper.getCategory;
import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

@AllArgsConstructor
public class UpdateCategory {

    private final TestContext testContext;

    @When("a user updates the category {string} with new description {string}")
    public void aUserUpdatesTheCategoryWithNewDescription(String categoryTitle, String newDescription) throws IOException {
        HashMap<String, Category> createdCategories = testContext.get("createdCategories", HashMap.class);
        CloseableHttpClient httpClient = testContext.get("httpClient", CloseableHttpClient.class);

        String categoryID = createdCategories.get(categoryTitle).getId();
        HttpResponse response = CategoryHelper.modifyCategoryPut(categoryID, categoryTitle, newDescription, httpClient);
        Category modifiedCategory = deserialize(response,Category.class);
        createdCategories.put(categoryTitle, modifiedCategory);
        testContext.set("createdTodos", createdCategories);

        testContext.set("statusCode", response.getStatusLine().getStatusCode());
    }

    @Then("the category {string} should have description {string}")
    public void theCategoryShouldHaveDescription(String categoryTitle, String newDescription) throws IOException {
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
        assertEquals(newDescription, returnedCategory.getDescription());
    }

    @When("a user updates the category {string} with new title {string}")
    public void aUserUpdatesTheCategoryWithNewTitle(String categoryTitle, String newTitle) throws IOException {
        HashMap<String, Category> createdCategories = testContext.get("createdCategories", HashMap.class);
        CloseableHttpClient httpClient = testContext.get("httpClient", CloseableHttpClient.class);

        String categoryID = createdCategories.get(categoryTitle).getId();
        String categoryDescription = createdCategories.get(categoryTitle).getDescription();
        HttpResponse response = CategoryHelper.modifyCategoryPut(categoryID, newTitle, categoryDescription, httpClient);


        testContext.set("response", response);
        testContext.set("statusCode", response.getStatusLine().getStatusCode());
    }
}
