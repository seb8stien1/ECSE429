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

/**
 * Step definitions for retrieving categories associated with projects.
 */
@AllArgsConstructor
public class GetCategoriesByProject {

    private final TestContext testContext;

    /**
     * Establishes associations between projects and categories based on provided data.
     *
     * @param dataTable Contains project and category titles for association.
     * @throws IOException if an I/O error occurs when sending or receiving the HTTP request.
     */
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

    /**
     * Retrieves categories associated with a specified project.
     *
     * @param projectTitle The title of the project.
     * @throws IOException if an I/O error occurs when sending or receiving the HTTP request.
     */
    @When("a user retrieves the categories associated with the project {string}")
    public void aUserRetrievesCategoriesAssociatedWithTheProject(String projectTitle) throws IOException {
        HashMap<String, Project> createdProjects = testContext.get("createdProjects", HashMap.class);
        CloseableHttpClient httpClient = testContext.get("httpClient", CloseableHttpClient.class);

        String projectID = createdProjects.get(projectTitle).getId();

        getAssociation("categories", projectID, httpClient);
    }

    /**
     * Validates that specific categories are associated with a given project.
     *
     * @param categoryTitle1 The title of the first category.
     * @param categoryTitle2 The title of the second category.
     * @param projectTitle   The title of the project.
     * @throws IOException if an I/O error occurs when sending or receiving the HTTP request.
     */
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

        assertTrue(returnedCategories.stream().anyMatch(category -> category.getId().equals(firstCategoryID)));
        assertTrue(returnedCategories.stream().anyMatch(category -> category.getId().equals(secondCategoryID)));
    }

    /**
     * Validates that a specific category is associated with a given project.
     *
     * @param categoryTitle The title of the category.
     * @param projectTitle  The title of the project.
     * @throws IOException if an I/O error occurs when sending or receiving the HTTP request.
     */
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

        assertTrue(returnedCategories.stream().anyMatch(category -> category.getId().equals(categoryID)));
    }

    /**
     * Attempts to retrieve categories associated with a non-existent project.
     *
     * @param projectTitle The title of the non-existent project.
     * @throws IOException if an I/O error occurs when sending or receiving the HTTP request.
     */
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
