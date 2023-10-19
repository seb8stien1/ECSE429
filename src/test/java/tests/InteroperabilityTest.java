package tests;

import config.RandomOrderTestRunner;
import helpers.ApiHelper;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import response.Category;
import response.CategoryResponse;
import response.Project;
import response.ProjectResponse;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static helpers.ApiHelper.deserialize;
import static helpers.CategoryHelper.*;
import static helpers.ProjectHelper.*;
import static org.junit.Assert.*;

@RunWith(RandomOrderTestRunner.class)
public class InteroperabilityTest {
    CloseableHttpClient httpClient;
    @BeforeClass
    public static void testOn() throws IOException {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpResponse response = ApiHelper.sendHttpRequest("get", "http://localhost:4567/", null, httpClient);
        int statusCode = response.getStatusLine().getStatusCode();
        assertEquals(HttpStatus.SC_OK, statusCode);
    }

    @Test
    public void testCreateGetDeleteProjectsOfCategoryRelationship() throws IOException {
        String categoryTitle = "category title";
        String categoryDescription = "category description";
        String projectTitle = "project title";
        String projectDescription = "project description";

        createCategory(categoryTitle, categoryDescription, httpClient);
        createProject(projectTitle, false, false, projectDescription, httpClient);

        HttpResponse response = getAllCategories(httpClient);
        CategoryResponse categories = deserialize(response, CategoryResponse.class);

        List<Category> categoryList = categories.getCategories()
                .stream()
                .filter(category -> categoryTitle.equals(category.getTitle())
                        && categoryDescription.equals(category.getDescription()))
                .toList();
        String categoryId = categoryList.get(0).getId();

        response = getAllProjects(httpClient);
        ProjectResponse projects = deserialize(response, ProjectResponse.class);

        List<Project> projectList = projects.getProjects()
                .stream()
                .filter(project -> projectTitle.equals(project.getTitle())
                        && Boolean.FALSE.equals(project.getActive()
                        && Boolean.FALSE.equals(project.getCompleted())
                        && projectDescription.equals(project.getDescription())))
                .toList();
        String projectId = projectList.get(0).getId();

        response = getProjectAssociation(categoryId, httpClient);
        ProjectResponse projectAssociations = deserialize(response, ProjectResponse.class);
        assertTrue(CollectionUtils.isEmpty(projectAssociations.getProjects()));

        response = createProjectAssociation(categoryId, projectId, httpClient);
        int statusCode = response.getStatusLine().getStatusCode();
        assertEquals(HttpStatus.SC_CREATED, statusCode);

        response = getProjectAssociation(categoryId, httpClient);
        projectAssociations = deserialize(response, ProjectResponse.class);
        assertFalse(CollectionUtils.isEmpty(projectAssociations.getProjects()));
        Optional<Project> projectAssociationOptional = projectAssociations.getProjects()
                .stream()
                .filter(project -> projectId.equals(project.getId()))
                .findFirst();
        assertTrue(projectAssociationOptional.isPresent());
        Project projectAssociation = projectAssociationOptional.get();
        assertEquals(projectTitle, projectAssociation.getTitle());
        assertEquals(projectDescription, projectAssociation.getDescription());
        assertEquals(Boolean.FALSE, projectAssociation.getActive());
        assertEquals(Boolean.FALSE, projectAssociation.getCompleted());

        response = deleteProjectAssociation(categoryId, projectId, httpClient);
        statusCode = response.getStatusLine().getStatusCode();
        assertEquals(HttpStatus.SC_OK, statusCode);

        response = getProjectAssociation(categoryId, httpClient);
        projectAssociations = deserialize(response, ProjectResponse.class);
        assertTrue(CollectionUtils.isEmpty(projectAssociations.getProjects()));

        response = deleteCategory(categoryId, httpClient);
        statusCode = response.getStatusLine().getStatusCode();
        assertEquals(HttpStatus.SC_OK, statusCode);

        response = deleteProject(projectId, httpClient);
        statusCode = response.getStatusLine().getStatusCode();
        assertEquals(HttpStatus.SC_OK, statusCode);
    }

    @Test
    public void test404CreateProjectsOfCategoriesNoProject() throws IOException {
        String categoryTitle = "category title";
        String categoryDescription = "category description";
        createCategory(categoryTitle, categoryDescription, httpClient);

        HttpResponse response = getAllCategories(httpClient);
        CategoryResponse categories = deserialize(response, CategoryResponse.class);

        List<Category> categoryList = categories.getCategories()
                .stream()
                .filter(category -> categoryTitle.equals(category.getTitle())
                        && categoryDescription.equals(category.getDescription()))
                .toList();
        String categoryId = categoryList.get(0).getId();

        response = createProjectAssociation(categoryId, "-1", httpClient);
        int statusCode = response.getStatusLine().getStatusCode();
        assertEquals(404, statusCode);

        deleteCategory(categoryId, httpClient);
    }

    @Test
    public void test404CreateProjectsOfCategoriesNoCategory() throws IOException {
        String projectTitle = "project title";
        String projectDescription = "project description";

        createProject(projectTitle, false, false, projectDescription, httpClient);

        HttpResponse response = getAllProjects(httpClient);
        ProjectResponse projects = deserialize(response, ProjectResponse.class);

        List<Project> projectList = projects.getProjects()
                .stream()
                .filter(project -> projectTitle.equals(project.getTitle())
                        && Boolean.FALSE.equals(project.getActive()
                        && Boolean.FALSE.equals(project.getCompleted())
                        && projectDescription.equals(project.getDescription())))
                .toList();
        String projectId = projectList.get(0).getId();

        response = createProjectAssociation("-1", projectId, httpClient);
        int statusCode = response.getStatusLine().getStatusCode();
        assertEquals(404, statusCode);

        deleteProject(projectId, httpClient);
    }

    @Test
    public void test404GetProjectsOfCategoryExpectedResult() throws IOException {
        HttpResponse response = getProjectAssociation("-1", httpClient);
        int statusCode = response.getStatusLine().getStatusCode();
        assertEquals(404, statusCode);
    }

    @Test
    public void test404GetCategoriesOfProjectExpectedResult() throws IOException {
        HttpResponse response = getCategoryAssociation("-1", httpClient);
        int statusCode = response.getStatusLine().getStatusCode();
        assertEquals(404, statusCode);
    }

    @Test
    public void test404GetProjectsOfCategoryWorks() throws IOException {
        HttpResponse response = getProjectAssociation("-1", httpClient);
        int statusCode = response.getStatusLine().getStatusCode();
        assertEquals(200, statusCode);
    }

    @Test
    public void test404GetCategoriesOfProjectWorks() throws IOException {
        HttpResponse response = getCategoryAssociation("-1", httpClient);
        int statusCode = response.getStatusLine().getStatusCode();
        assertEquals(200, statusCode);
    }

    @Test
    public void testCreateGetDeleteCategoriesOfProjectRelationship() throws IOException {
        String categoryTitle = "category title";
        String categoryDescription = "category description";
        String projectTitle = "project title";
        String projectDescription = "project description";

        createCategory(categoryTitle, categoryDescription, httpClient);
        createProject(projectTitle, false, false, projectDescription, httpClient);

        HttpResponse response = getAllCategories(httpClient);
        CategoryResponse categories = deserialize(response, CategoryResponse.class);

        List<Category> categoryList = categories.getCategories()
                .stream()
                .filter(category -> categoryTitle.equals(category.getTitle())
                        && categoryDescription.equals(category.getDescription()))
                .toList();
        String categoryId = categoryList.get(0).getId();

        response = getAllProjects(httpClient);
        ProjectResponse projects = deserialize(response, ProjectResponse.class);

        List<Project> projectList = projects.getProjects()
                .stream()
                .filter(project -> projectTitle.equals(project.getTitle())
                        && Boolean.FALSE.equals(project.getActive()
                        && Boolean.FALSE.equals(project.getCompleted())
                        && projectDescription.equals(project.getDescription())))
                .toList();
        String projectId = projectList.get(0).getId();



        response = getCategoryAssociation(projectId, httpClient);
        CategoryResponse categoryAssociations = deserialize(response, CategoryResponse.class);
        assertTrue(CollectionUtils.isEmpty(categoryAssociations.getCategories()));

        response = createCategoryAssociation(projectId, categoryId, httpClient);
        int statusCode = response.getStatusLine().getStatusCode();
        assertEquals(HttpStatus.SC_CREATED, statusCode);

        response = getCategoryAssociation(projectId, httpClient);
        categoryAssociations = deserialize(response, CategoryResponse.class);
        assertFalse(CollectionUtils.isEmpty(categoryAssociations.getCategories()));
        Optional<Category> categoryAssociationOptional = categoryAssociations.getCategories()
                .stream()
                .filter(category -> categoryId.equals(category.getId()))
                .findFirst();
        assertTrue(categoryAssociationOptional.isPresent());
        Category categoryAssociation = categoryAssociationOptional.get();
        assertEquals(categoryTitle, categoryAssociation.getTitle());
        assertEquals(categoryDescription, categoryAssociation.getDescription());

        response = deleteCategoryAssociation(projectId, categoryId, httpClient);
        statusCode = response.getStatusLine().getStatusCode();
        assertEquals(HttpStatus.SC_OK, statusCode);

        response = getCategoryAssociation(projectId, httpClient);
        categoryAssociations = deserialize(response, CategoryResponse.class);
        assertTrue(CollectionUtils.isEmpty(categoryAssociations.getCategories()));

        response = deleteCategory(categoryId, httpClient);
        statusCode = response.getStatusLine().getStatusCode();
        assertEquals(HttpStatus.SC_OK, statusCode);

        response = deleteProject(projectId, httpClient);
        statusCode = response.getStatusLine().getStatusCode();
        assertEquals(HttpStatus.SC_OK, statusCode);
    }
}
