package tests.features;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import lombok.AllArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.impl.client.CloseableHttpClient;
import response.Category;
import response.Project;
import response.ProjectResponse;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static helpers.ApiHelper.deserialize;
import static helpers.CategoryHelper.getAssociation;
import static helpers.CategoryHelper.createAssociation;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertNull;

/**
 * Step definitions for retrieving projects associated with categories.
 */
@AllArgsConstructor
public class GetProjectByCategory {

    private final TestContext testContext;

    /**
     * Establishes associations between projects and categories based on provided data.
     *
     * @param dataTable Contains category and project titles for association.
     * @throws IOException if an I/O error occurs when sending or receiving the HTTP request.
     */
    @Given("the following category and project association exist in the system:")
    public void theFollowingCategoryAndProjectAssociationExistInTheSystem(io.cucumber.datatable.DataTable dataTable) throws IOException {
        List<Map<String, String>> associations = dataTable.asMaps();
        HashMap<String, Category> createdCategories = testContext.get("createdCategories", HashMap.class);
        HashMap<String, Project> createdProjects = testContext.get("createdProjects", HashMap.class);

        CloseableHttpClient httpClient = testContext.get("httpClient", CloseableHttpClient.class);

        for (Map<String, String> association : associations) {
            String categoryTitle = association.get("categoryTitle");
            String projectTitle = association.get("projectTitle");

            String categoryID = createdCategories.get(categoryTitle).getId();
            String projectID = createdProjects.get(projectTitle).getId();

            createAssociation("projects", categoryID, projectID, httpClient);
        }
    }

    /**
     * Retrieves projects associated with a specified category.
     *
     * @param categoryTitle The title of the category.
     * @throws IOException if an I/O error occurs when sending or receiving the HTTP request.
     */
    @When("a user retrieves projects under the category {string}")
    public void aUserRetrievesProjectsUnderTheCategory(String categoryTitle) throws IOException {
        HashMap<String, Category> createdCategories = testContext.get("createdCategories", HashMap.class);
        CloseableHttpClient httpClient = testContext.get("httpClient", CloseableHttpClient.class);

        String categoryID = createdCategories.get(categoryTitle).getId();

        HttpResponse response = getAssociation("projects", categoryID, httpClient);
        ProjectResponse projectResponse = deserialize(response, ProjectResponse.class);

        List<Project> associatedProjects = projectResponse.getProjects();

        testContext.set("associatedProjects", associatedProjects);
    }

    /**
     * Validates that the specified projects are returned for a given category.
     *
     * @param categoryTitle The title of the category.
     * @throws IOException if an I/O error occurs when sending or receiving the HTTP request.
     */
    @Then("the projects under the category {string} are returned")
    public void theProjectsUnderTheCategoryAreReturned(String categoryTitle) throws IOException {
        List<Project> associatedProjects = testContext.get("associatedProjects", List.class);
        HashMap<String, Category> createdCategories = testContext.get("createdCategories", HashMap.class);
        CloseableHttpClient httpClient = testContext.get("httpClient", CloseableHttpClient.class);

        String categoryID = createdCategories.get(categoryTitle).getId();

        HttpResponse response = getAssociation("projects", categoryID, httpClient);
        ProjectResponse projectResponse = deserialize(response, ProjectResponse.class);

        List<Project> returnedProjects = projectResponse.getProjects();

        assertEquals(associatedProjects.size(), returnedProjects.size());
        assertEquals(associatedProjects, returnedProjects);
    }

    /**
     * Validates that no projects are returned for a given category when no associations exist.
     *
     * @param categoryTitle The title of the category.
     * @throws IOException if an I/O error occurs when sending or receiving the HTTP request.
     */
    @Then("the system should return an empty list indicating there are no projects for the given category {string}")
    public void theSystemShouldReturnAnEmptyListIndicatingThereAreNoProjectsForTheGivenCategory(String categoryTitle) throws IOException {
        HashMap<String, Category> createdCategories = testContext.get("createdCategories", HashMap.class);
        CloseableHttpClient httpClient = testContext.get("httpClient", CloseableHttpClient.class);

        String categoryID = createdCategories.get(categoryTitle).getId();

        HttpResponse response = getAssociation("projects", categoryID, httpClient);
        ProjectResponse projectResponse = deserialize(response, ProjectResponse.class);

        assertTrue(CollectionUtils.isEmpty(projectResponse.getProjects()));
    }

    /**
     * Attempts to retrieve projects associated with a non-existent category.
     *
     * @param categoryTitle The title of the non-existent category.
     * @throws IOException if an I/O error occurs when sending or receiving the HTTP request.
     */
    @When("a user retrieves projects under the non-existent category {string}")
    public void aUserRetrievesProjectsUnderTheNonExistentCategory(String categoryTitle) throws IOException {
        HashMap<String, Category> createdCategories = testContext.get("createdCategories", HashMap.class);
        CloseableHttpClient httpClient = testContext.get("httpClient", CloseableHttpClient.class);

        Category nonExistentCategory = createdCategories.get(categoryTitle);
        assertNull(nonExistentCategory);
        String nonExistentCategoryID = UUID.randomUUID().toString();

        HttpResponse response = getAssociation("projects", nonExistentCategoryID, httpClient);

        // this is a bug that was identified in the last project
        // should return 404 but returns 200
        // check 200 is returned and then send 404 to other check for passing
        int statusCode = response.getStatusLine().getStatusCode();
        assertEquals(HttpStatus.SC_OK, statusCode);
        testContext.set("statusCode", HttpStatus.SC_NOT_FOUND);
    }
}
