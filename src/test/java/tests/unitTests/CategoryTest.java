package tests.unitTests;

import config.RandomOrderTestRunner;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.impl.client.CloseableHttpClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import response.*;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import static helpers.ApiHelper.deserialize;
import static helpers.CategoryHelper.*;
import static helpers.TodoHelper.getAllTodos;
import static helpers.ProjectHelper.getAllProjects;
import static org.junit.Assert.*;

@RunWith(RandomOrderTestRunner.class)
public class CategoryTest {
    CloseableHttpClient httpClient;

    // Test to create, retrieve, and delete a category by ID
    @Test
    public void testCreateGetAllAndDeleteByIdCategory() throws IOException {
        // Define category properties
        String title = "testCategory";
        String description = "test description";

        // Record the state of todos and projects before creating a category
        HttpResponse todosResponse = getAllTodos(httpClient);
        TodoResponse todos = deserialize(todosResponse, TodoResponse.class);
        List<Todo> allTodosBefore = todos.getTodos();
        HttpResponse projectResponse = getAllProjects(httpClient);
        ProjectResponse projects = deserialize(projectResponse, ProjectResponse.class);
        List<Project> allProjectsBefore = projects.getProjects();

        // Create a new category
        HttpResponse response = createCategory(title, description, httpClient);
        int statusCode = response.getStatusLine().getStatusCode();
        assertEquals(HttpStatus.SC_CREATED, statusCode);

        // Check if the category was created successfully
        response = getAllCategories(httpClient);
        CategoryResponse categories = deserialize(response, CategoryResponse.class);
        statusCode = response.getStatusLine().getStatusCode();
        assertEquals(HttpStatus.SC_OK, statusCode);

        // Filter and collect categories based on 'title' and 'description' criteria, then assert that the list is not empty
        List<Category> categoryList = categories.getCategories()
                .stream()
                .filter(category -> title.equals(category.getTitle())
                        && description.equals(category.getDescription()))
                .collect(Collectors.toList());
        assertFalse(CollectionUtils.isEmpty(categoryList));

        // Delete the created category
        categoryList.forEach(category -> {
            try {
                deleteCategory(category.getId(), httpClient);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        // Verify that todos and projects are in the same state as before the test
        todosResponse = getAllTodos(httpClient);
        todos = deserialize(todosResponse, TodoResponse.class);
        List<Todo> allTodosAfter = todos.getTodos();
        projectResponse = getAllProjects(httpClient);
        projects = deserialize(projectResponse, ProjectResponse.class);
        List<Project> allProjectsAfter = projects.getProjects();

        assertEquals(allTodosBefore, allTodosAfter);
        assertEquals(allProjectsBefore, allProjectsAfter);
    }

    // Test the HEAD request for all categories
    @Test
    public void testHeadAllCategories() throws IOException {
        // Perform a HEAD request to retrieve category information
        HttpResponse headResponse = headAllCategories(httpClient);
        HttpResponse getResponse = getAllCategories(httpClient);

        // Check that the HEAD response does not return anything in the body
        assertNull(headResponse.getEntity());

        // Compare the headers from the HEAD and GET responses (excluding the 'Date' attribute)
        assertEquals(headResponse.getAllHeaders().length, getResponse.getAllHeaders().length);

        // Iterating through headers, excluding 'Date,' and compare corresponding elements in HEAD and GET responses
        for (int i = 0; i < headResponse.getAllHeaders().length; i++) {
            if (!headResponse.getAllHeaders()[i].getName().equalsIgnoreCase("Date")) {
                assertEquals(headResponse.getAllHeaders()[i].getElements(), getResponse.getAllHeaders()[i].getElements());
            }
        }
    }

    // Test the HEAD request for a category by ID
    @Test
    public void testHeadCategoryById() throws IOException {
        // Create a category to fetch its ID for the head request
        String title = "testCategory";
        String description = "test description";
        createCategory(title, description, httpClient);
        HttpResponse getAllResponse = getAllCategories(httpClient);
        CategoryResponse categories = deserialize(getAllResponse, CategoryResponse.class);
        String categoryID = categories.getCategories().get(0).getId();

        // Making requests for HEAD and GET
        HttpResponse headResponse = headCategory(categoryID, httpClient);
        HttpResponse getResponse = getCategory(categoryID, httpClient);

        // Check that the HEAD response does not return anything in the body
        assertNull(headResponse.getEntity());

        // Compare the headers from the HEAD and GET responses (excluding the 'Date' attribute)
        assertEquals(headResponse.getAllHeaders().length, getResponse.getAllHeaders().length);

        for (int i = 0; i < headResponse.getAllHeaders().length; i++) {
            if (!headResponse.getAllHeaders()[i].getName().equalsIgnoreCase("Date")) {
                assertEquals(headResponse.getAllHeaders()[i].getElements(), getResponse.getAllHeaders()[i].getElements());
            }
        }
    }

    // Test creating, modifying, and deleting a category
    @Test
    public void testGetPostPutById() throws IOException {
        // Define category properties
        String title = "testCategory";
        String description = "test description";

        // Create a new category
        HttpResponse response = createCategory(title, description, httpClient);
        int statusCode = response.getStatusLine().getStatusCode();
        assertEquals(HttpStatus.SC_CREATED, statusCode);

        // Get category objects and match the one we just created
        response = getAllCategories(httpClient);
        CategoryResponse categories = deserialize(response, CategoryResponse.class);

        // Filter categories based on 'title' and 'description' criteria, collect them, and get the 'id' of the first matching category
        List<Category> categoryList = categories.getCategories()
                .stream()
                .filter(category -> title.equals(category.getTitle())
                        && description.equals(category.getDescription()))
                .toList();
        String id = categoryList.get(0).getId();

        // Modify the created category using PUT
        String newDescription = "new test description";
        response = modifyCategoryPut(id, title, newDescription, httpClient);
        statusCode = response.getStatusLine().getStatusCode();
        assertEquals(HttpStatus.SC_OK, statusCode);

        // Get the created category by ID and verify the changes
        response = getCategory(id, httpClient);
        categories = deserialize(response, CategoryResponse.class);
        Category category = categories.getCategories().get(0);
        assertEquals(title, category.getTitle());
        assertEquals(newDescription, category.getDescription());

        // Modify the created category using POST
        String newTitle = "new title";
        response = modifyCategoryPost(id, newTitle, description, httpClient);
        statusCode = response.getStatusLine().getStatusCode();
        assertEquals(HttpStatus.SC_OK, statusCode);

        // Get the created category by ID and verify the changes
        response = getCategory(id, httpClient);
        categories = deserialize(response, CategoryResponse.class);
        category = categories.getCategories().get(0);
        assertEquals(newTitle, category.getTitle());
        assertEquals(description, category.getDescription());

        // Delete the created category
        response = deleteCategory(category.getId(), httpClient);
        statusCode = response.getStatusLine().getStatusCode();
        assertEquals(HttpStatus.SC_OK, statusCode);
    }

    // Test creating a category with an empty title
    @Test
    public void testCreateEmptyTitle() throws IOException {
        // Define category properties with an empty title
        String title = "";
        String description = "test description";

        // Create a category with an empty title and expect an error
        HttpResponse response = createCategory(title, description, httpClient);

        // Verify that a bad request status is returned with an error message
        ResponseError e = deserialize(response, ResponseError.class);
        int statusCode = response.getStatusLine().getStatusCode();
        assertEquals(HttpStatus.SC_BAD_REQUEST, statusCode);
        assertEquals("Failed Validation: title : can not be empty", e.getErrorMessages().get(0));
    }

    // Test retrieving a category by a non-existent ID
    @Test
    public void testGetByIdNonExistent() throws IOException {
        // Attempt to retrieve a category with a non-existent ID
        HttpResponse response = getCategory("-1", httpClient);

        // Verify that a not found status is returned
        int statusCode = response.getStatusLine().getStatusCode();
        assertEquals(HttpStatus.SC_NOT_FOUND, statusCode);
    }

    // Test modifying using non-existent ID using PUT
    @Test
    public void testPutByIdNonExistent() throws IOException {
        String title = "testCategory";
        String description = "test description";
        HttpResponse response = modifyCategoryPut("-1", title, description, httpClient);
        // Verify that a not found status is returned
        int statusCode = response.getStatusLine().getStatusCode();
        assertEquals(HttpStatus.SC_NOT_FOUND, statusCode);
    }

    // Test modifying a category with an empty title using PUT
    @Test
    public void testPutEmptyTitle() throws IOException {
        // Define category properties
        String title = "testCategory";
        String description = "test description";

        // Create a category to get its ID
        createCategory(title, description, httpClient);
        HttpResponse response = getAllCategories(httpClient);
        CategoryResponse categories = deserialize(response, CategoryResponse.class);

        List<Category> categoryList = categories.getCategories()
                .stream()
                .filter(category -> title.equals(category.getTitle())
                        && description.equals(category.getDescription()))
                .toList();
        String id = categoryList.get(0).getId();

        String invalidTitle = "";

        // Attempt to modify the category with an empty title using PUT
        response = modifyCategoryPut(id, invalidTitle, description, httpClient);

        // Verify that a bad request status is returned with an error message
        ResponseError e = deserialize(response, ResponseError.class);
        int statusCode = response.getStatusLine().getStatusCode();
        assertEquals(HttpStatus.SC_BAD_REQUEST, statusCode);
        assertEquals("Failed Validation: title : can not be empty", e.getErrorMessages().get(0));

        // Delete the category created for the test
        response = deleteCategory(id, httpClient);
        statusCode = response.getStatusLine().getStatusCode();
        assertEquals(HttpStatus.SC_OK, statusCode);
    }

    // Test modifying using a non-existent ID (post)
    @Test
    public void testPostModifyNonExistentID() throws IOException {
        String title = "testCategory";
        String description = "test description";
        HttpResponse response = modifyCategoryPost("-1", title, description, httpClient);
        // Verify that a not found status is returned
        int statusCode = response.getStatusLine().getStatusCode();
        assertEquals(HttpStatus.SC_NOT_FOUND, statusCode);
    }

    // Test modifying a category with an empty title using POST
    @Test
    public void testPostModifyEmptyTitle() throws IOException {
        // Define category properties
        String title = "testCategory";
        String description = "test description";

        // Create a category to get its ID
        createCategory(title, description, httpClient);
        HttpResponse response = getAllCategories(httpClient);
        CategoryResponse categories = deserialize(response, CategoryResponse.class);

        List<Category> categoryList = categories.getCategories()
                .stream()
                .filter(category -> title.equals(category.getTitle())
                        && description.equals(category.getDescription()))
                .toList();
        String id = categoryList.get(0).getId();

        String invalidTitle = "";

        // Attempt to modify the category with an empty title using POST
        response = modifyCategoryPost(id, invalidTitle, description, httpClient);

        // Verify that a bad request status is returned with an error message
        ResponseError e = deserialize(response, ResponseError.class);
        int statusCode = response.getStatusLine().getStatusCode();
        assertEquals(HttpStatus.SC_BAD_REQUEST, statusCode);
        assertEquals("Failed Validation: title : can not be empty", e.getErrorMessages().get(0));

        // Delete the category created for the test
        response = deleteCategory(id, httpClient);
        statusCode = response.getStatusLine().getStatusCode();
        assertEquals(HttpStatus.SC_OK, statusCode);
    }

    // Test deleting a category by a non-existent ID
    @Test
    public void testDeleteNonExistentID() throws IOException {
        // Attempt to delete a category with a non-existent ID
        HttpResponse response = deleteCategory("-1", httpClient);

        // Verify that a not found status is returned
        int statusCode = response.getStatusLine().getStatusCode();
        assertEquals(HttpStatus.SC_NOT_FOUND, statusCode);
    }
}
