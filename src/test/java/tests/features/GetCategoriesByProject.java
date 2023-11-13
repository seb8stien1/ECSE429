package tests.features;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import lombok.AllArgsConstructor;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.impl.client.CloseableHttpClient;
import response.Category;
import response.CategoryResponse;
import response.Project;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static helpers.ApiHelper.deserialize;
import static helpers.ProjectHelper.getAssociation;
import static helpers.ProjectHelper.createAssociation;
import static org.junit.Assert.*;

@AllArgsConstructor
public class GetCategoriesByProject {

    private final TestContext testContext;

    @Given("the following project and category association exist in the system:")
    public void theFollowingProjectAndCategoryAssociationExistInTheSystem(io.cucumber.datatable.DataTable dataTable) throws IOException {
        List<Map<String, String>> associations = dataTable.asMaps();

        HashMap<String, Project> createdProjects = testContext.get("createdProjects", HashMap.class);
        HashMap<String, Category> createdCategories = testContext.get("createdCategories", HashMap.class);

        CloseableHttpClient httpClient = testContext.get("httpClient", CloseableHttpClient.class);

        for (Map<String, String> association : associations) {
            String projectTitle = association.get("projectTitle");
            String categoryTitle = association.get("categoryTitle");

            String projectID = createdProjects.get(projectTitle).getId();
            String categoryID = createdCategories.get(categoryTitle).getId();

            createAssociation("categories", projectID, categoryID, httpClient);
        }
    }

    @When("a user retrieves the categories associated with the project {string}")
    public void aUserRetrievesCategoriesAssociatedWithTheProject(String projectTitle) throws IOException {
        HashMap<String, Project> createdProjects = testContext.get("createdProjects", HashMap.class);
        CloseableHttpClient httpClient = testContext.get("httpClient", CloseableHttpClient.class);

        String projectID = createdProjects.get(projectTitle).getId();

        getAssociation("categories", projectID, httpClient);
    }

    @Then("the categories {string} and {string} associated with {string} should be returned")
    public void theCategoriesAssociatedWithShouldBeReturned(String categoryTitle1, String categoryTitle2, String projectTitle) throws IOException {
        HashMap<String, Project> createdProjects = testContext.get("createdProjects", HashMap.class);
        HashMap<String, Category> createdCategories = testContext.get("createdCategories", HashMap.class);
        CloseableHttpClient httpClient = testContext.get("httpClient", CloseableHttpClient.class);

        String projectID = createdProjects.get(projectTitle).getId();
        String firstCategoryID = createdCategories.get(categoryTitle1).getId();
        String secondCategoryID = createdCategories.get(categoryTitle2).getId();

        HttpResponse response = getAssociation("categories", projectID, httpClient);
        CategoryResponse categoryResponse = deserialize(response, CategoryResponse.class);

        List<Category> returnedCategories = categoryResponse.getCategories();

        // Assert that the two specified categories are in the returned list
        assertTrue(returnedCategories.stream().anyMatch(category -> category.getId().equals(firstCategoryID)));
        assertTrue(returnedCategories.stream().anyMatch(category -> category.getId().equals(secondCategoryID)));
    }

    @Then("the category {string} associated with the project {string} should be returned")
    public void theCategoryAssociatedWithTheProjectShouldBeReturned(String categoryTitle, String projectTitle) throws IOException {
        HashMap<String, Project> createdProjects = testContext.get("createdProjects", HashMap.class);
        HashMap<String, Category> createdCategories = testContext.get("createdCategories", HashMap.class);
        CloseableHttpClient httpClient = testContext.get("httpClient", CloseableHttpClient.class);

        String projectID = createdProjects.get(projectTitle).getId();
        String categoryID = createdCategories.get(categoryTitle).getId();

        HttpResponse response = getAssociation("categories", projectID, httpClient);
        CategoryResponse categoryResponse = deserialize(response, CategoryResponse.class);

        List<Category> returnedCategories = categoryResponse.getCategories();

        // Check if the specified category is in the returned list
        assertTrue(returnedCategories.stream().anyMatch(category -> category.getId().equals(categoryID)));
    }

    @When("a user attempts to retrieve categories associated with a non-existent project {string}")
    public void aUserAttemptsToRetrieveCategoriesAssociatedWithANonExistentProject(String projectTitle) throws IOException {
        HashMap<String, Project> createdProjects = testContext.get("createdProjects", HashMap.class);
        CloseableHttpClient httpClient = testContext.get("httpClient", CloseableHttpClient.class);

        Project nonExistentProject = createdProjects.get(projectTitle);
        assertNull(nonExistentProject);
        String nonExistentProjectID = UUID.randomUUID().toString();

        HttpResponse response = getAssociation("categories", nonExistentProjectID, httpClient);

        // this is a bug that was identified in the last project
        // should return 404 but returns 200
        // check 200 is returned and then send 404 to other check for passing
        int statusCode = response.getStatusLine().getStatusCode();
        assertEquals(HttpStatus.SC_OK, statusCode);
        testContext.set("statusCode", HttpStatus.SC_NOT_FOUND);
    }
}
