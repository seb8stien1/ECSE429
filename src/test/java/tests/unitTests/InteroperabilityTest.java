package tests.unitTests;

import config.RandomOrderTestRunner;
import helpers.ApiHelper;
import helpers.CategoryHelper;
import helpers.ProjectHelper;
import helpers.TodoHelper;
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
import response.Todo;
import response.TodoResponse;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static helpers.ApiHelper.deserialize;
import static helpers.CategoryHelper.*;
import static helpers.TodoHelper.*;
import static helpers.ProjectHelper.*;
import static org.junit.Assert.*;

@RunWith(RandomOrderTestRunner.class)
public class InteroperabilityTest {
    CloseableHttpClient httpClient;
    @BeforeClass
    public static void testServiceOn() throws IOException {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpResponse response = ApiHelper.sendHttpRequest("get", "http://localhost:4567/docs", null, httpClient);
        int statusCode = response.getStatusLine().getStatusCode();
        assertEquals(HttpStatus.SC_OK, statusCode);
    }

    /**
     * This method is a test case for creating, retrieving, and deleting projects associated with a category.
     * It verifies the functionality of creating, retrieving, and deleting project-category relationships.
     *
     * @throws IOException If there's an I/O exception during the test.
     */
    @Test
    public void testCreateGetDeleteProjectsOfCategoryRelationship() throws IOException {
        // Define test data
        String categoryTitle = "category title";
        String categoryDescription = "category description";
        String projectTitle = "project title";
        String projectDescription = "project description";

        // Create a new category
        createCategory(categoryTitle, categoryDescription, httpClient);

        // Create a new project
        createProject(projectTitle, false, false, projectDescription, httpClient);

        // Retrieve all categories
        HttpResponse response = getAllCategories(httpClient);
        CategoryResponse categories = deserialize(response, CategoryResponse.class);

        // Filter categories to find the one we just created
        List<Category> categoryList = categories.getCategories()
                .stream()
                .filter(category -> categoryTitle.equals(category.getTitle())
                        && categoryDescription.equals(category.getDescription()))
                .toList();
        String categoryId = categoryList.get(0).getId();

        // Retrieve all projects
        response = getAllProjects(httpClient);
        ProjectResponse projects = deserialize(response, ProjectResponse.class);

        // Filter projects to find the one we just created
        List<Project> projectList = projects.getProjects()
                .stream()
                .filter(project -> projectTitle.equals(project.getTitle())
                        && Boolean.FALSE.equals(project.getActive())
                        && Boolean.FALSE.equals(project.getCompleted())
                        && projectDescription.equals(project.getDescription()))
                .toList();
        String projectId = projectList.get(0).getId();

        // Retrieve project associations with the category and ensure it's empty
        response = CategoryHelper.getAssociation("projects", categoryId, httpClient);
        ProjectResponse projectAssociations = deserialize(response, ProjectResponse.class);
        assertTrue(CollectionUtils.isEmpty(projectAssociations.getProjects()));

        // Create an association between the category and the project
        response = CategoryHelper.createAssociation("projects", categoryId, projectId, httpClient);
        int statusCode = response.getStatusLine().getStatusCode();
        assertEquals(HttpStatus.SC_CREATED, statusCode);

        // Retrieve project associations with the category and verify the association
        response = CategoryHelper.getAssociation("projects", categoryId, httpClient);
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

        // Delete the association between the category and the project
        response = CategoryHelper.deleteAssociation("projects", categoryId, projectId, httpClient);
        statusCode = response.getStatusLine().getStatusCode();
        assertEquals(HttpStatus.SC_OK, statusCode);

        // Verify that project associations with the category are now empty
        response = CategoryHelper.getAssociation("projects", categoryId, httpClient);
        projectAssociations = deserialize(response, ProjectResponse.class);
        assertTrue(CollectionUtils.isEmpty(projectAssociations.getProjects()));

        // Delete the category
        response = deleteCategory(categoryId, httpClient);
        statusCode = response.getStatusLine().getStatusCode();
        assertEquals(HttpStatus.SC_OK, statusCode);

        // Delete the project
        response = deleteProject(projectId, httpClient);
        statusCode = response.getStatusLine().getStatusCode();
        assertEquals(HttpStatus.SC_OK, statusCode);
    }


    /**
     * This test case checks if creating a project-category association with an invalid project ID
     * returns the expected status code (404 - Not Found).
     *
     * @throws IOException If there's an I/O exception during the test.
     */
    @Test
    public void testCreateProjectsOfCategoriesInvalidProject() throws IOException {
        // Define test data
        String categoryTitle = "category title";
        String categoryDescription = "category description";

        // Create a new category
        createCategory(categoryTitle, categoryDescription, httpClient);

        // Retrieve all categories
        HttpResponse response = getAllCategories(httpClient);
        CategoryResponse categories = deserialize(response, CategoryResponse.class);

        // Filter categories to find the one we just created
        List<Category> categoryList = categories.getCategories()
                .stream()
                .filter(category -> categoryTitle.equals(category.getTitle())
                        && categoryDescription.equals(category.getDescription()))
                .toList();
        String categoryId = categoryList.get(0).getId();

        // Attempt to create an association with an invalid project ID
        response = CategoryHelper.createAssociation("projects", categoryId, "-1", httpClient);
        int statusCode = response.getStatusLine().getStatusCode();

        // Verify that the status code is as expected (404 - Not Found)
        assertEquals(HttpStatus.SC_NOT_FOUND, statusCode);

        // Delete the category to clean up the test data
        response = deleteCategory(categoryId, httpClient);
        statusCode = response.getStatusLine().getStatusCode();

        // Verify that the category was successfully deleted
        assertEquals(HttpStatus.SC_OK, statusCode);
    }

    /**
     * This test case checks if creating a project-category association with an invalid category ID
     * returns the expected status code (404 - Not Found).
     *
     * @throws IOException If there's an I/O exception during the test.
     */
    @Test
    public void testCreateProjectsOfCategoriesInvalidCategory() throws IOException {
        // Define test data
        String projectTitle = "project title";
        String projectDescription = "project description";

        // Create a new project
        createProject(projectTitle, false, false, projectDescription, httpClient);

        // Retrieve all projects
        HttpResponse response = getAllProjects(httpClient);
        ProjectResponse projects = deserialize(response, ProjectResponse.class);

        // Filter projects to find the one we just created
        List<Project> projectList = projects.getProjects()
                .stream()
                .filter(project -> projectTitle.equals(project.getTitle())
                        && Boolean.FALSE.equals(project.getActive())
                        && Boolean.FALSE.equals(project.getCompleted())
                        && projectDescription.equals(project.getDescription()))
                .toList();
        String projectId = projectList.get(0).getId();

        // Attempt to create an association with an invalid category ID
        response = CategoryHelper.createAssociation("projects", "-1", projectId, httpClient);
        int statusCode = response.getStatusLine().getStatusCode();

        // Verify that the status code is as expected (404 - Not Found)
        assertEquals(HttpStatus.SC_NOT_FOUND, statusCode);

        // Delete the project to clean up the test data
        response = deleteProject(projectId, httpClient);
        statusCode = response.getStatusLine().getStatusCode();

        // Verify that the project was successfully deleted
        assertEquals(HttpStatus.SC_OK, statusCode);
    }


    /**
     * This test case checks if attempting to retrieve projects associated with a nonexistent category
     * returns the expected result, which is failing (HTTP status code 404 - Not Found).
     *
     * @throws IOException If there's an I/O exception during the test.
     */
    @Test
    public void testGetProjectsOfNonexistentCategoryExpectedResultFailing() throws IOException {
        // Attempt to retrieve projects associated with a nonexistent category
        HttpResponse response = CategoryHelper.getAssociation("projects", "-1", httpClient);
        int statusCode = response.getStatusLine().getStatusCode();

        // Assert that the status code is not equal to the expected value (HTTP status code 404 - Not Found)
        assertNotEquals(String.format("The API behavior for this test is different than expected. Expected %s but got %s.", HttpStatus.SC_NOT_FOUND, statusCode), HttpStatus.SC_OK, statusCode);
    }

    /**
     * This test case checks the actual behavior when attempting to retrieve projects associated with a
     * nonexistent category. It expects that the API behavior is different from the expected result.
     *
     * @throws IOException If there's an I/O exception during the test.
     */
    @Test
    public void testGetProjectsOfNonexistentCategoryActualBehaviour() throws IOException {
        // Attempt to retrieve projects associated with a nonexistent category
        HttpResponse response = CategoryHelper.getAssociation("projects", "-1", httpClient);
        int statusCode = response.getStatusLine().getStatusCode();

        // Verify that the status code is equal to the expected value (HTTP status code 200 - OK)
        assertEquals(HttpStatus.SC_OK, statusCode);
    }

    /**
     * This test case verifies the creation, retrieval, and deletion of relationships between
     * categories and projects. It ensures that categories can be associated with projects and
     * subsequently disassociated.
     *
     * @throws IOException If there's an I/O exception during the test.
     */
    @Test
    public void testCreateGetDeleteCategoriesOfProjectRelationship() throws IOException {
        // Define test data
        String categoryTitle = "category title";
        String categoryDescription = "category description";
        String projectTitle = "project title";
        String projectDescription = "project description";

        // Create a new category
        createCategory(categoryTitle, categoryDescription, httpClient);

        // Create a new project
        createProject(projectTitle, false, false, projectDescription, httpClient);

        // Retrieve all categories
        HttpResponse response = getAllCategories(httpClient);
        CategoryResponse categories = deserialize(response, CategoryResponse.class);

        // Filter categories to find the one we just created
        List<Category> categoryList = categories.getCategories()
                .stream()
                .filter(category -> categoryTitle.equals(category.getTitle())
                        && categoryDescription.equals(category.getDescription()))
                .toList();
        String categoryId = categoryList.get(0).getId();

        // Retrieve all projects
        response = getAllProjects(httpClient);
        ProjectResponse projects = deserialize(response, ProjectResponse.class);

        // Filter projects to find the one we just created
        List<Project> projectList = projects.getProjects()
                .stream()
                .filter(project -> projectTitle.equals(project.getTitle())
                        && Boolean.FALSE.equals(project.getActive())
                        && Boolean.FALSE.equals(project.getCompleted())
                        && projectDescription.equals(project.getDescription()))
                .toList();
        String projectId = projectList.get(0).getId();

        // Retrieve category associations with the project and ensure it's empty
        response = ProjectHelper.getAssociation("categories", projectId, httpClient);
        CategoryResponse categoryAssociations = deserialize(response, CategoryResponse.class);
        assertTrue(CollectionUtils.isEmpty(categoryAssociations.getCategories()));

        // Create an association between the project and the category
        response = ProjectHelper.createAssociation("categories", projectId, categoryId, httpClient);
        int statusCode = response.getStatusLine().getStatusCode();

        // Verify that the association was successfully created (HTTP status code 201 - Created)
        assertEquals(HttpStatus.SC_CREATED, statusCode);

        // Retrieve category associations with the project and verify the association
        response = ProjectHelper.getAssociation("categories", projectId, httpClient);
        categoryAssociations = deserialize(response, CategoryResponse.class);
        assertFalse(CollectionUtils.isEmpty(categoryAssociations.getCategories()));
        Optional<Category> categoryAssociationOptional = categoryAssociations.getCategories()
                .stream()
                .filter(category -> categoryId.equals(category.getId()))
                .findFirst();
        assertTrue(categoryAssociationOptional.isPresent());
        Category categoryAssociation = categoryAssociationOptional.get();

        // Check if the associated category's title and description match
        assertEquals(categoryTitle, categoryAssociation.getTitle());
        assertEquals(categoryDescription, categoryAssociation.getDescription());

        // Delete the association between the project and the category
        response = ProjectHelper.deleteAssociation("categories", projectId, categoryId, httpClient);
        statusCode = response.getStatusLine().getStatusCode();

        // Verify that the association was successfully deleted (HTTP status code 200 - OK)
        assertEquals(HttpStatus.SC_OK, statusCode);

        // Retrieve category associations with the project and ensure it's empty
        response = ProjectHelper.getAssociation("categories", projectId, httpClient);
        categoryAssociations = deserialize(response, CategoryResponse.class);
        assertTrue(CollectionUtils.isEmpty(categoryAssociations.getCategories()));

        // Delete the category to clean up the test data
        response = deleteCategory(categoryId, httpClient);
        statusCode = response.getStatusLine().getStatusCode();

        // Verify that the category was successfully deleted
        assertEquals(HttpStatus.SC_OK, statusCode);

        // Delete the project to clean up the test data
        response = deleteProject(projectId, httpClient);
        statusCode = response.getStatusLine().getStatusCode();

        // Verify that the project was successfully deleted
        assertEquals(HttpStatus.SC_OK, statusCode);
    }

    /**
     * This test case checks if creating a project-category association with an invalid category ID
     * returns the expected status code (404 - Not Found).
     *
     * @throws IOException If there's an I/O exception during the test.
     */
    @Test
    public void testCreateCategoriesOfProjectInvalidCategory() throws IOException {
        // Define test data
        String projectTitle = "project title";
        String projectDescription = "project description";

        // Create a new project
        createProject(projectTitle, false, false, projectDescription, httpClient);

        // Retrieve all projects
        HttpResponse response = getAllProjects(httpClient);
        ProjectResponse projects = deserialize(response, ProjectResponse.class);

        // Filter projects to find the one we just created
        List<Project> projectList = projects.getProjects()
                .stream()
                .filter(project -> projectTitle.equals(project.getTitle())
                        && Boolean.FALSE.equals(project.getCompleted())
                        && Boolean.FALSE.equals(project.getActive())
                        && projectDescription.equals(project.getDescription()))
                .toList();
        String projectId = projectList.get(0).getId();

        // Attempt to create an association with an invalid category ID
        response = ProjectHelper.createAssociation("categories", projectId, "-1", httpClient);
        int statusCode = response.getStatusLine().getStatusCode();

        // Verify that the status code is as expected (404 - Not Found)
        assertEquals(HttpStatus.SC_NOT_FOUND, statusCode);

        // Delete the project to clean up the test data
        response = deleteProject(projectId, httpClient);
        statusCode = response.getStatusLine().getStatusCode();

        // Verify that the project was successfully deleted
        assertEquals(HttpStatus.SC_OK, statusCode);
    }

    /**
     * This test case checks if creating a project-category association with an invalid project ID
     * returns the expected status code (404 - Not Found).
     *
     * @throws IOException If there's an I/O exception during the test.
     */
    @Test
    public void testCreateCategoriesOfProjectInvalidProject() throws IOException {
        // Define test data
        String categoryTitle = "category title";
        String categoryDescription = "category description";

        // Create a new category
        createCategory(categoryTitle, categoryDescription, httpClient);

        // Retrieve all categories
        HttpResponse response = getAllCategories(httpClient);
        CategoryResponse categories = deserialize(response, CategoryResponse.class);

        // Filter categories to find the one we just created
        List<Category> categoryList = categories.getCategories()
                .stream()
                .filter(category -> categoryTitle.equals(category.getTitle())
                        && categoryDescription.equals(category.getDescription()))
                .toList();
        String categoryId = categoryList.get(0).getId();

        // Attempt to create an association with an invalid project ID
        response = ProjectHelper.createAssociation("categories", "-1", categoryId, httpClient);
        int statusCode = response.getStatusLine().getStatusCode();

        // Verify that the status code is as expected (404 - Not Found)
        assertEquals(HttpStatus.SC_NOT_FOUND, statusCode);

        // Delete the category to clean up the test data
        response = deleteCategory(categoryId, httpClient);
        statusCode = response.getStatusLine().getStatusCode();

        // Verify that the category was successfully deleted
        assertEquals(HttpStatus.SC_OK, statusCode);
    }

    /**
     * This test case checks if attempting to retrieve categories associated with a nonexistent project
     * returns the expected result, which is failing (HTTP status code 404 - Not Found).
     *
     * @throws IOException If there's an I/O exception during the test.
     */
    @Test
    public void testGetCategoriesOfNonexistentProjectExpectedResultFailing() throws IOException {
        // Attempt to retrieve categories associated with a nonexistent project
        HttpResponse response = ProjectHelper.getAssociation("categories", "-1", httpClient);
        int statusCode = response.getStatusLine().getStatusCode();

        // Assert that the status code is not equal to the expected value (HTTP status code 404 - Not Found)
        assertNotEquals(String.format("The API behavior for this test is different than expected. Expected %s but got %s.", HttpStatus.SC_NOT_FOUND, statusCode), HttpStatus.SC_OK, statusCode);
    }

    /**
     * This test case checks the actual behavior when attempting to retrieve categories associated with a
     * nonexistent project. It expects that the API behavior is different from the expected result.
     *
     * @throws IOException If there's an I/O exception during the test.
     */
    @Test
    public void testGetCategoriesOfNonexistentProjectActualBehaviour() throws IOException {
        // Attempt to retrieve categories associated with a nonexistent project
        HttpResponse response = ProjectHelper.getAssociation("categories", "-1", httpClient);
        int statusCode = response.getStatusLine().getStatusCode();

        // Verify that the status code is equal to the expected value (HTTP status code 200 - OK)
        assertEquals(HttpStatus.SC_OK, statusCode);
    }


    /**
     * This test case verifies the creation, retrieval, and deletion of relationships between
     * todos and projects. It ensures that projects can be associated with todos and
     * subsequently disassociated.
     *
     * @throws IOException If there's an I/O exception during the test.
     */
    @Test
    public void testCreateGetDeleteProjectsOfTodoRelationship() throws IOException {
        // Define test data
        String todoTitle = "todo title";
        String todoDescription = "todo description";
        String projectTitle = "project title";
        String projectDescription = "project description";

        // Create a new todo
        createTodo(todoTitle, false, todoDescription, httpClient);

        // Create a new project
        createProject(projectTitle, false, false, projectDescription, httpClient);

        // Retrieve all todos
        HttpResponse response = getAllTodos(httpClient);
        TodoResponse todos = deserialize(response, TodoResponse.class);

        // Filter todos to find the one we just created
        List<Todo> todoList = todos.getTodos()
                .stream()
                .filter(todo -> todoTitle.equals(todo.getTitle())
                        && todoDescription.equals(todo.getDescription())
                        && Boolean.FALSE.equals(todo.getDoneStatus()))
                .toList();
        String todoId = todoList.get(0).getId();

        // Retrieve all projects
        response = getAllProjects(httpClient);
        ProjectResponse projects = deserialize(response, ProjectResponse.class);

        // Filter projects to find the one we just created
        List<Project> projectList = projects.getProjects()
                .stream()
                .filter(project -> projectTitle.equals(project.getTitle())
                        && Boolean.FALSE.equals(project.getActive())
                        && Boolean.FALSE.equals(project.getCompleted())
                        && projectDescription.equals(project.getDescription()))
                .toList();
        String projectId = projectList.get(0).getId();

        // Retrieve project associations with the todo and ensure it's empty
        response = TodoHelper.getAssociation("tasksof", todoId, httpClient);
        ProjectResponse projectAssociations = deserialize(response, ProjectResponse.class);
        assertTrue(CollectionUtils.isEmpty(projectAssociations.getProjects()));

        // Create an association between the todo and the project
        response = TodoHelper.createAssociation("tasksof", todoId, projectId, httpClient);
        int statusCode = response.getStatusLine().getStatusCode();

        // Verify that the association was successfully created (HTTP status code 201 - Created)
        assertEquals(HttpStatus.SC_CREATED, statusCode);

        // Retrieve project associations with the todo and verify the association
        response = TodoHelper.getAssociation("tasksof", todoId, httpClient);
        projectAssociations = deserialize(response, ProjectResponse.class);
        assertFalse(CollectionUtils.isEmpty(projectAssociations.getProjects()));
        Optional<Project> projectAssociationOptional = projectAssociations.getProjects()
                .stream()
                .filter(project -> projectId.equals(project.getId()))
                .findFirst();
        assertTrue(projectAssociationOptional.isPresent());
        Project projectAssociation = projectAssociationOptional.get();

        // Check if the associated project's title, description, and status match
        assertEquals(projectTitle, projectAssociation.getTitle());
        assertEquals(projectDescription, projectAssociation.getDescription());
        assertEquals(Boolean.FALSE, projectAssociation.getActive());
        assertEquals(Boolean.FALSE, projectAssociation.getCompleted());

        // Delete the association between the todo and the project
        response = TodoHelper.deleteAssociation("tasksof", todoId, projectId, httpClient);
        statusCode = response.getStatusLine().getStatusCode();

        // Verify that the association was successfully deleted (HTTP status code 200 - OK)
        assertEquals(HttpStatus.SC_OK, statusCode);

        // Retrieve project associations with the todo and ensure it's empty
        response = TodoHelper.getAssociation("tasksof", todoId, httpClient);
        projectAssociations = deserialize(response, ProjectResponse.class);
        assertTrue(CollectionUtils.isEmpty(projectAssociations.getProjects()));

        // Delete the todo to clean up the test data
        response = deleteTodo(todoId, httpClient);
        statusCode = response.getStatusLine().getStatusCode();

        // Verify that the todo was successfully deleted
        assertEquals(HttpStatus.SC_OK, statusCode);

        // Delete the project to clean up the test data
        response = deleteProject(projectId, httpClient);
        statusCode = response.getStatusLine().getStatusCode();

        // Verify that the project was successfully deleted
        assertEquals(HttpStatus.SC_OK, statusCode);
    }

    /**
     * This test case checks if creating a project-todo association with an invalid project ID
     * returns the expected status code (404 - Not Found).
     *
     * @throws IOException If there's an I/O exception during the test.
     */
    @Test
    public void testCreateProjectsOfTodoInvalidProject() throws IOException {
        // Define test data
        String todoTitle = "category title";
        String todoDescription = "category description";

        // Create a new todo
        createTodo(todoTitle, false, todoDescription, httpClient);

        // Retrieve all todos
        HttpResponse response = getAllTodos(httpClient);
        TodoResponse todos = deserialize(response, TodoResponse.class);

        // Filter todos to find the one we just created
        List<Todo> todoList = todos.getTodos()
                .stream()
                .filter(todo -> todoTitle.equals(todo.getTitle())
                        && todoDescription.equals(todo.getDescription())
                        && Boolean.FALSE.equals(todo.getDoneStatus()))
                .toList();
        String todoId = todoList.get(0).getId();

        // Attempt to create an association with an invalid project ID
        response = TodoHelper.createAssociation("tasksof", todoId, "-1", httpClient);
        int statusCode = response.getStatusLine().getStatusCode();

        // Verify that the status code is equal to the expected value (HTTP status code 404 - Not Found)
        assertEquals(HttpStatus.SC_NOT_FOUND, statusCode);

        // Delete the todo to clean up the test data
        response = deleteTodo(todoId, httpClient);
        statusCode = response.getStatusLine().getStatusCode();

        // Verify that the todo was successfully deleted
        assertEquals(HttpStatus.SC_OK, statusCode);
    }

    /**
     * This test case checks if creating a project-todo association with an invalid todo ID
     * returns the expected status code (404 - Not Found).
     *
     * @throws IOException If there's an I/O exception during the test.
     */
    @Test
    public void testCreateProjectsOfTodoInvalidTodo() throws IOException {
        // Define test data
        String projectTitle = "project title";
        String projectDescription = "project description";

        // Create a new project
        createProject(projectTitle, false, false, projectDescription, httpClient);

        // Retrieve all projects
        HttpResponse response = getAllProjects(httpClient);
        ProjectResponse projects = deserialize(response, ProjectResponse.class);

        // Filter projects to find the one we just created
        List<Project> projectList = projects.getProjects()
                .stream()
                .filter(project -> projectTitle.equals(project.getTitle())
                        && Boolean.FALSE.equals(project.getActive()
                        && Boolean.FALSE.equals(project.getCompleted())
                        && projectDescription.equals(project.getDescription())))
                .toList();
        String projectId = projectList.get(0).getId();

        // Attempt to create an association with an invalid todo ID
        response = TodoHelper.createAssociation("tasksof", "-1", projectId, httpClient);
        int statusCode = response.getStatusLine().getStatusCode();

        // Verify that the status code is equal to the expected value (HTTP status code 404 - Not Found)
        assertEquals(HttpStatus.SC_NOT_FOUND, statusCode);

        // Delete the project to clean up the test data
        response = deleteProject(projectId, httpClient);
        statusCode = response.getStatusLine().getStatusCode();

        // Verify that the project was successfully deleted
        assertEquals(HttpStatus.SC_OK, statusCode);
    }


    /**
     * This test case verifies the behavior when attempting to get projects associated with a
     * nonexistent todo. The expected behavior is that the HTTP status code is not equal to
     * HttpStatus.SC_OK (200).
     *
     * @throws IOException If there's an I/O exception during the test.
     */
    @Test
    public void testGetProjectsOfNonexistentTodoExpectedResultFailing() throws IOException {
        // Attempt to retrieve projects associated with a nonexistent todo (-1)
        HttpResponse response = TodoHelper.getAssociation("tasksof", "-1", httpClient);
        int statusCode = response.getStatusLine().getStatusCode();

        // Verify that the status code is not equal to HttpStatus.SC_OK
        assertNotEquals(
                String.format(
                        "The API behavior for this test is different than expected. Expected %s but got %s.",
                        HttpStatus.SC_NOT_FOUND, statusCode),
                HttpStatus.SC_OK, statusCode);
    }

    /**
     * This test case verifies the actual behavior when attempting to get projects associated
     * with a nonexistent todo. The expected behavior is that the HTTP status code is
     * HttpStatus.SC_OK (200).
     *
     * @throws IOException If there's an I/O exception during the test.
     */
    @Test
    public void testGetProjectsOfNonexistentTodoActualBehaviour() throws IOException {
        // Attempt to retrieve projects associated with a nonexistent todo (-1)
        HttpResponse response = TodoHelper.getAssociation("tasksof", "-1", httpClient);
        int statusCode = response.getStatusLine().getStatusCode();

        // Verify that the status code is equal to HttpStatus.SC_OK
        assertEquals(HttpStatus.SC_OK, statusCode);
    }

    /**
     * This test case checks the creation, retrieval, and deletion of relationships between
     * todos and projects. It ensures that todos can be associated with projects and
     * subsequently disassociated.
     *
     * @throws IOException If there's an I/O exception during the test.
     */
    @Test
    public void testCreateGetDeleteTodoOfProjectRelationship() throws IOException {
        // Define test data
        String todoTitle = "todo title";
        String todoDescription = "todo description";
        String projectTitle = "project title";
        String projectDescription = "project description";

        // Create a new todo
        createTodo(todoTitle, false, todoDescription, httpClient);

        // Create a new project
        createProject(projectTitle, false, false, projectDescription, httpClient);

        // Retrieve all todos
        HttpResponse response = getAllTodos(httpClient);
        TodoResponse todos = deserialize(response, TodoResponse.class);

        // Filter todos to find the one we just created
        List<Todo> todoList = todos.getTodos()
                .stream()
                .filter(todo -> todoTitle.equals(todo.getTitle())
                        && todoDescription.equals(todo.getDescription())
                        && Boolean.FALSE.equals(todo.getDoneStatus()))
                .toList();
        String todoId = todoList.get(0).getId();

        // Retrieve all projects
        response = getAllProjects(httpClient);
        ProjectResponse projects = deserialize(response, ProjectResponse.class);

        // Filter projects to find the one we just created
        List<Project> projectList = projects.getProjects()
                .stream()
                .filter(project -> projectTitle.equals(project.getTitle())
                        && Boolean.FALSE.equals(project.getActive())
                        && Boolean.FALSE.equals(project.getCompleted())
                        && projectDescription.equals(project.getDescription()))
                .toList();
        String projectId = projectList.get(0).getId();

        // Retrieve todo associations with the project and ensure it's empty
        response = ProjectHelper.getAssociation("tasks", projectId, httpClient);
        TodoResponse todoAssociations = deserialize(response, TodoResponse.class);
        assertTrue(CollectionUtils.isEmpty(todoAssociations.getTodos()));

        // Create an association between the todo and the project
        response = ProjectHelper.createAssociation("tasks", projectId, todoId, httpClient);
        int statusCode = response.getStatusLine().getStatusCode();

        // Verify that the association was successfully created (HTTP status code 201 - Created)
        assertEquals(HttpStatus.SC_CREATED, statusCode);

        // Retrieve todo associations with the project and verify the association
        response = ProjectHelper.getAssociation("tasks", projectId, httpClient);
        todoAssociations = deserialize(response, TodoResponse.class);
        assertFalse(CollectionUtils.isEmpty(todoAssociations.getTodos()));
        Optional<Todo> categoryAssociationOptional = todoAssociations.getTodos()
                .stream()
                .filter(todo -> todoId.equals(todo.getId()))
                .findFirst();
        assertTrue(categoryAssociationOptional.isPresent());
        Todo todoAssociation = categoryAssociationOptional.get();

        // Check if the associated todo's title and description match
        assertEquals(todoTitle, todoAssociation.getTitle());
        assertEquals(todoDescription, todoAssociation.getDescription());

        // Delete the association between the todo and the project
        response = ProjectHelper.deleteAssociation("tasks", projectId, todoId, httpClient);
        statusCode = response.getStatusLine().getStatusCode();

        // Verify that the association was successfully deleted (HTTP status code 200 - OK)
        assertEquals(HttpStatus.SC_OK, statusCode);

        // Retrieve todo associations with the project and ensure it's empty
        response = ProjectHelper.getAssociation("tasks", projectId, httpClient);
        todoAssociations = deserialize(response, TodoResponse.class);
        assertTrue(CollectionUtils.isEmpty(todoAssociations.getTodos()));

        // Delete the todo to clean up the test data
        response = deleteTodo(todoId, httpClient);
        statusCode = response.getStatusLine().getStatusCode();

        // Verify that the todo was successfully deleted
        assertEquals(HttpStatus.SC_OK, statusCode);

        // Delete the project to clean up the test data
        response = deleteProject(projectId, httpClient);
        statusCode = response.getStatusLine().getStatusCode();

        // Verify that the project was successfully deleted
        assertEquals(HttpStatus.SC_OK, statusCode);
    }

    /**
     * This test case checks if creating a todo-project association with an invalid todo ID
     * returns the expected status code (404 - Not Found).
     *
     * @throws IOException If there's an I/O exception during the test.
     */
    @Test
    public void testCreateTodoOfProjectInvalidTodo() throws IOException {
        // Define test data
        String projectTitle = "project title";
        String projectDescription = "project description";

        // Create a new project
        createProject(projectTitle, false, false, projectDescription, httpClient);

        // Retrieve all projects
        HttpResponse response = getAllProjects(httpClient);
        ProjectResponse projects = deserialize(response, ProjectResponse.class);

        // Filter projects to find the one we just created
        List<Project> projectList = projects.getProjects()
                .stream()
                .filter(project -> projectTitle.equals(project.getTitle())
                        && Boolean.FALSE.equals(project.getCompleted())
                        && Boolean.FALSE.equals(project.getActive())
                        && projectDescription.equals(project.getDescription()))
                .toList();
        String projectId = projectList.get(0).getId();

        // Attempt to create an association with an invalid todo ID
        response = ProjectHelper.createAssociation("tasks", projectId, "-1", httpClient);
        int statusCode = response.getStatusLine().getStatusCode();

        // Verify that the status code is equal to the expected value (HTTP status code 404 - Not Found)

        assertEquals(HttpStatus.SC_NOT_FOUND, statusCode);

        // Delete the project to clean up the test data
        response = deleteProject(projectId, httpClient);
        statusCode = response.getStatusLine().getStatusCode();

        // Verify that the project was successfully deleted
        assertEquals(HttpStatus.SC_OK, statusCode);
    }

    /**
     * This test case checks if creating a todo-project association with an invalid project ID
     * returns the expected status code (404 - Not Found).
     *
     * @throws IOException If there's an I/O exception during the test.
     */
    @Test
    public void testCreateTodoOfProjectInvalidProject() throws IOException {
        // Define test data
        String todoTitle = "todo title";
        String todoDescription = "todo description";

        // Create a new todo
        createTodo(todoTitle, false, todoDescription, httpClient);

        // Retrieve all todos
        HttpResponse response = getAllTodos(httpClient);
        TodoResponse todos = deserialize(response, TodoResponse.class);

        // Filter todos to find the one we just created
        List<Todo> todoList = todos.getTodos()
                .stream()
                .filter(todo -> todoTitle.equals(todo.getTitle())
                        && todoDescription.equals(todo.getDescription())
                        && Boolean.FALSE.equals(todo.getDoneStatus()))
                .toList();
        String todoId = todoList.get(0).getId();

        // Attempt to create an association with an invalid project ID
        response = ProjectHelper.createAssociation("tasks", "-1", todoId, httpClient);
        int statusCode = response.getStatusLine().getStatusCode();

        // Verify that the status code is equal to the expected value (HTTP status code 404 - Not Found)

        assertEquals(HttpStatus.SC_NOT_FOUND, statusCode);

        // Delete the todo to clean up the test data
        response = deleteTodo(todoId, httpClient);
        statusCode = response.getStatusLine().getStatusCode();

        // Verify that the todo was successfully deleted
        assertEquals(HttpStatus.SC_OK, statusCode);
    }

    /**
     * This test case checks the behavior when attempting to get todos associated with a
     * nonexistent project. The expected behavior is that the HTTP status code is not equal
     * to HttpStatus.SC_OK (200).
     *
     * @throws IOException If there's an I/O exception during the test.
     */
    @Test
    public void testGetTodosOfNonexistentProjectExpectedResultFailing() throws IOException {
        // Attempt to retrieve todos associated with a nonexistent project (-1)
        HttpResponse response = ProjectHelper.getAssociation("tasks", "-1", httpClient);
        int statusCode = response.getStatusLine().getStatusCode();

        // Verify that the status code is not equal to HttpStatus.SC_OK
        assertNotEquals(
                String.format(
                        "The API behavior for this test is different than expected. Expected %s but got %s.",
                        HttpStatus.SC_NOT_FOUND, statusCode),
                HttpStatus.SC_OK, statusCode);
    }

    /**
     * This test case checks the actual behavior when attempting to get todos associated with a
     * nonexistent project. The expected behavior is that the HTTP status code is equal to
     * HttpStatus.SC_OK (200).
     *
     * @throws IOException If there's an I/O exception during the test.
     */
    @Test
    public void testGetTodosOfNonexistentProjectActualBehaviour() throws IOException {
        // Attempt to retrieve todos associated with a nonexistent project (-1)
        HttpResponse response = ProjectHelper.getAssociation("tasks", "-1", httpClient);
        int statusCode = response.getStatusLine().getStatusCode();

        // Verify that the status code is equal to HttpStatus.SC_OK
        assertEquals(HttpStatus.SC_OK, statusCode);
    }


    /**
     * This test case verifies the creation, retrieval, and deletion of relationships between
     * categories and todos. It ensures that todos can be associated with categories and
     * subsequently disassociated.
     *
     * @throws IOException If there's an I/O exception during the test.
     */
    @Test
    public void testCreateGetDeleteTodosOfCategoryRelationship() throws IOException {
        // Define test data
        String categoryTitle = "category title";
        String categoryDescription = "category description";
        String todoTitle = "todo title";
        String todoDescription = "todo description";

        // Create a new category
        createCategory(categoryTitle, categoryDescription, httpClient);

        // Create a new todo
        createTodo(todoTitle, false, todoDescription, httpClient);

        // Retrieve all categories
        HttpResponse response = getAllCategories(httpClient);
        CategoryResponse categories = deserialize(response, CategoryResponse.class);

        // Filter categories to find the one we just created
        List<Category> categoryList = categories.getCategories()
                .stream()
                .filter(category -> categoryTitle.equals(category.getTitle())
                        && categoryDescription.equals(category.getDescription()))
                .toList();
        String categoryId = categoryList.get(0).getId();

        // Retrieve all todos
        response = getAllTodos(httpClient);
        TodoResponse todos = deserialize(response, TodoResponse.class);

        // Filter todos to find the one we just created
        List<Todo> todoList = todos.getTodos()
                .stream()
                .filter(todo -> todoTitle.equals(todo.getTitle())
                        && Boolean.FALSE.equals(todo.getDoneStatus())
                        && todoDescription.equals(todo.getDescription()))
                .toList();
        String todoId = todoList.get(0).getId();

        // Retrieve todo associations with the category and ensure it's empty
        response = CategoryHelper.getAssociation("todos", categoryId, httpClient);
        TodoResponse todoAssociations = deserialize(response, TodoResponse.class);
        assertTrue(CollectionUtils.isEmpty(todoAssociations.getTodos()));

        // Create an association between the todo and the category
        response = CategoryHelper.createAssociation("todos", categoryId, todoId, httpClient);
        int statusCode = response.getStatusLine().getStatusCode();

        // Verify that the association was successfully created (HTTP status code 201 - Created)
        assertEquals(HttpStatus.SC_CREATED, statusCode);

        // Retrieve todo associations with the category and verify the association
        response = CategoryHelper.getAssociation("todos", categoryId, httpClient);
        todoAssociations = deserialize(response, TodoResponse.class);
        assertFalse(CollectionUtils.isEmpty(todoAssociations.getTodos()));
        Optional<Todo> todoAssociationOptional = todoAssociations.getTodos()
                .stream()
                .filter(todo -> todoId.equals(todo.getId()))
                .findFirst();
        assertTrue(todoAssociationOptional.isPresent());
        Todo todoAssociation = todoAssociationOptional.get();

        // Check if the associated todo's title, description, and done status match
        assertEquals(todoTitle, todoAssociation.getTitle());
        assertEquals(todoDescription, todoAssociation.getDescription());
        assertEquals(Boolean.FALSE, todoAssociation.getDoneStatus());

        // Delete the association between the todo and the category
        response = CategoryHelper.deleteAssociation("todos", categoryId, todoId, httpClient);
        statusCode = response.getStatusLine().getStatusCode();

        // Verify that the association was successfully deleted (HTTP status code 200 - OK)
        assertEquals(HttpStatus.SC_OK, statusCode);

        // Retrieve todo associations with the category and ensure it's empty
        response = CategoryHelper.getAssociation("todos", categoryId, httpClient);
        todoAssociations = deserialize(response, TodoResponse.class);
        assertTrue(CollectionUtils.isEmpty(todoAssociations.getTodos()));

        // Delete the category to clean up the test data
        response = deleteCategory(categoryId, httpClient);
        statusCode = response.getStatusLine().getStatusCode();

        // Verify that the category was successfully deleted
        assertEquals(HttpStatus.SC_OK, statusCode);

        // Delete the todo to clean up the test data
        response = deleteTodo(todoId, httpClient);
        statusCode = response.getStatusLine().getStatusCode();

        // Verify that the todo was successfully deleted
        assertEquals(HttpStatus.SC_OK, statusCode);
    }

    /**
     * This test case checks if creating a category-todo association with an invalid todo ID
     * returns the expected status code (404 - Not Found).
     *
     * @throws IOException If there's an I/O exception during the test.
     */
    @Test
    public void testCreateTodosOfCategoriesInvalidTodo() throws IOException {
        // Define test data
        String categoryTitle = "category title";
        String categoryDescription = "category description";

        // Create a new category
        createCategory(categoryTitle, categoryDescription, httpClient);

        // Retrieve all categories
        HttpResponse response = getAllCategories(httpClient);
        CategoryResponse categories = deserialize(response, CategoryResponse.class);

        // Filter categories to find the one we just created
        List<Category> categoryList = categories.getCategories()
                .stream()
                .filter(category -> categoryTitle.equals(category.getTitle())
                        && categoryDescription.equals(category.getDescription()))
                .toList();
        String categoryId = categoryList.get(0).getId();

        // Attempt to create an association with an invalid todo ID
        response = CategoryHelper.createAssociation("todos", categoryId, "-1", httpClient);
        int statusCode = response.getStatusLine().getStatusCode();

        // Verify that the status code is as expected (404 - Not Found)
        assertEquals(HttpStatus.SC_NOT_FOUND, statusCode);

        // Delete the category to clean up the test data
        response = deleteCategory(categoryId, httpClient);
        statusCode = response.getStatusLine().getStatusCode();

        // Verify that the category was successfully deleted
        assertEquals(HttpStatus.SC_OK, statusCode);
    }

    /**
     * This test case checks if creating a category-todo association with an invalid category ID
     * returns the expected status code (404 - Not Found).
     *
     * @throws IOException If there's an I/O exception during the test.
     */
    @Test
    public void testCreateTodosOfCategoriesInvalidCategory() throws IOException {
        // Define test data
        String todoTitle = "todo title";
        String todoDescription = "todo description";

        // Create a new todo
        createTodo(todoTitle, false, todoDescription, httpClient);

        // Retrieve all todos
        HttpResponse response = getAllTodos(httpClient);
        TodoResponse projects = deserialize(response, TodoResponse.class);

        // Filter todos to find the one we just created
        List<Todo> todoList = projects.getTodos()
                .stream()
                .filter(todo -> todoTitle.equals(todo.getTitle())
                        && Boolean.FALSE.equals(todo.getDoneStatus())
                        && todoDescription.equals(todo.getDescription()))
                .toList();
        String todoId = todoList.get(0).getId();

        // Attempt to create an association with an invalid category ID
        response = CategoryHelper.createAssociation("todos", "-1", todoId, httpClient);
        int statusCode = response.getStatusLine().getStatusCode();

        // Verify that the status code is as expected (404 - Not Found)
        assertEquals(HttpStatus.SC_NOT_FOUND, statusCode);

        // Delete the todo to clean up the test data
        response = deleteTodo(todoId, httpClient);
        statusCode = response.getStatusLine().getStatusCode();

        // Verify that the todo was successfully deleted
        assertEquals(HttpStatus.SC_OK, statusCode);
    }

    /**
     * This test case checks if attempting to retrieve todos associated with a nonexistent category
     * returns the expected result, which is failing (HTTP status code 404 - Not Found).
     *
     * @throws IOException If there's an I/O exception during the test.
     */
    @Test
    public void testGetTodosOfNonexistentCategoryExpectedResultFailing() throws IOException {
        // Attempt to retrieve todos associated with a nonexistent category
        HttpResponse response = CategoryHelper.getAssociation("todos", "-1", httpClient);
        int statusCode = response.getStatusLine().getStatusCode();

        // Assert that the status code is not equal to the expected value (HTTP status code 404 - Not Found)
        assertNotEquals(String.format("The API behavior for this test is different than expected. Expected %s but got %s.", HttpStatus.SC_NOT_FOUND, statusCode), HttpStatus.SC_OK, statusCode);
    }

    /**
     * This test case checks the actual behavior when attempting to retrieve todos associated with a
     * nonexistent category. It expects that the API behavior is different from the expected result.
     *
     * @throws IOException If there's an I/O exception during the test.
     */
    @Test
    public void testGetTodosOfNonexistentCategoryActualBehaviour() throws IOException {
        // Attempt to retrieve todos associated with a nonexistent category
        HttpResponse response = CategoryHelper.getAssociation("todos", "-1", httpClient);
        int statusCode = response.getStatusLine().getStatusCode();

        // Verify that the status code is equal to the expected value (HTTP status code 200 - OK)
        assertEquals(HttpStatus.SC_OK, statusCode);
    }


    /**
     * This test case verifies the creation, retrieval, and deletion of relationships between
     * categories and todos. It ensures that categories can be associated with todos and
     * subsequently disassociated.
     *
     * @throws IOException If there's an I/O exception during the test.
     */
    @Test
    public void testCreateGetDeleteCategoriesOfTodoRelationship() throws IOException {
        // Define test data
        String categoryTitle = "category title";
        String categoryDescription = "category description";
        String todoTitle = "todo title";
        String todoDescription = "todo description";

        // Create a new category
        createCategory(categoryTitle, categoryDescription, httpClient);

        // Create a new todo
        createTodo(todoTitle, false, todoDescription, httpClient);

        // Retrieve all categories
        HttpResponse response = getAllCategories(httpClient);
        CategoryResponse categories = deserialize(response, CategoryResponse.class);

        // Filter categories to find the one we just created
        List<Category> categoryList = categories.getCategories()
                .stream()
                .filter(category -> categoryTitle.equals(category.getTitle())
                        && categoryDescription.equals(category.getDescription()))
                .toList();
        String categoryId = categoryList.get(0).getId();

        // Retrieve all todos
        response = getAllTodos(httpClient);
        TodoResponse todos = deserialize(response, TodoResponse.class);

        // Filter todos to find the one we just created
        List<Todo> todoList = todos.getTodos()
                .stream()
                .filter(todo -> todoTitle.equals(todo.getTitle())
                        && Boolean.FALSE.equals(todo.getDoneStatus())
                        && todoDescription.equals(todo.getDescription()))
                .toList();
        String todoId = todoList.get(0).getId();

        // Retrieve category associations with the todo and ensure it's empty
        response = TodoHelper.getAssociation("categories", todoId, httpClient);
        CategoryResponse categoryAssociations = deserialize(response, CategoryResponse.class);
        assertTrue(CollectionUtils.isEmpty(categoryAssociations.getCategories()));

        // Create an association between the todo and the category
        response = TodoHelper.createAssociation("categories", todoId, categoryId, httpClient);
        int statusCode = response.getStatusLine().getStatusCode();

        // Verify that the association was successfully created (HTTP status code 201 - Created)
        assertEquals(HttpStatus.SC_CREATED, statusCode);

        // Retrieve category associations with the todo and verify the association
        response = TodoHelper.getAssociation("categories", todoId, httpClient);
        categoryAssociations = deserialize(response, CategoryResponse.class);
        assertFalse(CollectionUtils.isEmpty(categoryAssociations.getCategories()));
        Optional<Category> categoryAssociationOptional = categoryAssociations.getCategories()
                .stream()
                .filter(category -> categoryId.equals(category.getId()))
                .findFirst();
        assertTrue(categoryAssociationOptional.isPresent());
        Category categoryAssociation = categoryAssociationOptional.get();

        // Check if the associated category's title and description match
        assertEquals(categoryTitle, categoryAssociation.getTitle());
        assertEquals(categoryDescription, categoryAssociation.getDescription());

        // Delete the association between the todo and the category
        response = TodoHelper.deleteAssociation("categories", todoId, categoryId, httpClient);
        statusCode = response.getStatusLine().getStatusCode();

        // Verify that the association was successfully deleted (HTTP status code 200 - OK)
        assertEquals(HttpStatus.SC_OK, statusCode);

        // Retrieve category associations with the todo and ensure it's empty
        response = TodoHelper.getAssociation("categories", todoId, httpClient);
        categoryAssociations = deserialize(response, CategoryResponse.class);
        assertTrue(CollectionUtils.isEmpty(categoryAssociations.getCategories()));

        // Delete the category to clean up the test data
        response = deleteCategory(categoryId, httpClient);
        statusCode = response.getStatusLine().getStatusCode();

        // Verify that the category was successfully deleted
        assertEquals(HttpStatus.SC_OK, statusCode);

        // Delete the todo to clean up the test data
        response = deleteTodo(todoId, httpClient);
        statusCode = response.getStatusLine().getStatusCode();

        // Verify that the todo was successfully deleted
        assertEquals(HttpStatus.SC_OK, statusCode);
    }

    /**
     * This test case checks if creating a todo-category association with an invalid category ID
     * returns the expected status code (404 - Not Found).
     *
     * @throws IOException If there's an I/O exception during the test.
     */
    @Test
    public void testCreateCategoriesOfTodoInvalidCategory() throws IOException {
        // Define test data
        String todoTitle = "todo title";
        String todoDescription = "todo description";

        // Create a new todo
        createTodo(todoTitle, false, todoDescription, httpClient);

        // Retrieve all todos
        HttpResponse response = getAllTodos(httpClient);
        TodoResponse todos = deserialize(response, TodoResponse.class);

        // Filter todos to find the one we just created
        List<Todo> todoList = todos.getTodos()
                .stream()
                .filter(todo -> todoTitle.equals(todo.getTitle())
                        && Boolean.FALSE.equals(todo.getDoneStatus())
                        && todoDescription.equals(todo.getDescription()))
                .toList();
        String todoId = todoList.get(0).getId();

        // Attempt to create an association with an invalid category ID
        response = TodoHelper.createAssociation("categories", todoId, "-1", httpClient);
        int statusCode = response.getStatusLine().getStatusCode();

        // Verify that the status code is as expected (404 - Not Found)
        assertEquals(HttpStatus.SC_NOT_FOUND, statusCode);

        // Delete the todo to clean up the test data
        response = deleteTodo(todoId, httpClient);
        statusCode = response.getStatusLine().getStatusCode();

        // Verify that the todo was successfully deleted
        assertEquals(HttpStatus.SC_OK, statusCode);
    }

    /**
     * This test case checks if creating a todo-category association with an invalid todo ID
     * returns the expected status code (404 - Not Found).
     *
     * @throws IOException If there's an I/O exception during the test.
     */
    @Test
    public void testCreateCategoriesOfTodoInvalidTodo() throws IOException {
        // Define test data
        String categoryTitle = "category title";
        String categoryDescription = "category description";

        // Create a new category
        createCategory(categoryTitle, categoryDescription, httpClient);

        // Retrieve all categories
        HttpResponse response = getAllCategories(httpClient);
        CategoryResponse categories = deserialize(response, CategoryResponse.class);

        // Filter categories to find the one we just created
        List<Category> categoryList = categories.getCategories()
                .stream()
                .filter(category -> categoryTitle.equals(category.getTitle())
                        && categoryDescription.equals(category.getDescription()))
                .toList();
        String categoryId = categoryList.get(0).getId();

        // Attempt to create an association with an invalid todo ID
        response = TodoHelper.createAssociation("categories", "-1", categoryId, httpClient);
        int statusCode = response.getStatusLine().getStatusCode();

        // Verify that the status code is as expected (404 - Not Found)
        assertEquals(HttpStatus.SC_NOT_FOUND, statusCode);

        // Delete the category to clean up the test data
        response = deleteCategory(categoryId, httpClient);
        statusCode = response.getStatusLine().getStatusCode();

        // Verify that the category was successfully deleted
        assertEquals(HttpStatus.SC_OK, statusCode);
    }

    /**
     * This test case checks if attempting to retrieve categories associated with a nonexistent todo
     * returns the expected result, which is failing (HTTP status code 404 - Not Found).
     *
     * @throws IOException If there's an I/O exception during the test.
     */
    @Test
    public void testGetCategoriesOfNonexistentTodoExpectedResultFailing() throws IOException {
        // Attempt to retrieve categories associated with a nonexistent todo
        HttpResponse response = TodoHelper.getAssociation("categories", "-1", httpClient);
        int statusCode = response.getStatusLine().getStatusCode();

        // Assert that the status code is not equal to the expected value (HTTP status code 404 - Not Found)
        assertNotEquals(String.format("The API behavior for this test is different than expected. Expected %s but got %s.", HttpStatus.SC_NOT_FOUND, statusCode), HttpStatus.SC_OK, statusCode);
    }

    /**
     * This test case checks the actual behavior when attempting to retrieve categories associated with a
     * nonexistent todo. It expects that the API behavior is different from the expected result.
     *
     * @throws IOException If there's an I/O exception during the test.
     */
    @Test
    public void testGetCategoriesOfNonexistentTodoActualBehaviour() throws IOException {
        // Attempt to retrieve categories associated with a nonexistent todo
        HttpResponse response = TodoHelper.getAssociation("categories", "-1", httpClient);
        int statusCode = response.getStatusLine().getStatusCode();

        // Verify that the status code is equal to the expected value (HTTP status code 200 - OK)
        assertEquals(HttpStatus.SC_OK, statusCode);
    }

}
