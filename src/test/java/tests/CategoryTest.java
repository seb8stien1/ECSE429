package tests;

import config.RandomOrderTestRunner;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.impl.client.CloseableHttpClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import response.ResponseError;
import response.Category;
import response.CategoryResponse;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import static helpers.ApiHelper.deserialize;
import static helpers.CategoryHelper.*;
import static helpers.CategoryHelper.deleteCategory;
import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

@RunWith(RandomOrderTestRunner.class)
public class CategoryTest {
    CloseableHttpClient httpClient;
    @Test
    public void testCreateGetAllAndDeleteByIdCategory() throws IOException {
        String title = "testCategory";
        String description = "test description";

//        create Category object
        HttpResponse response = createCategory(title, description, httpClient);

        int statusCode = response.getStatusLine().getStatusCode();
        assertEquals(HttpStatus.SC_CREATED, statusCode);

//        check Category object was created
        response = getAllCategories(httpClient);
        CategoryResponse categories = deserialize(response, CategoryResponse.class);

        statusCode = response.getStatusLine().getStatusCode();
        assertEquals(HttpStatus.SC_OK, statusCode);

        List<Category> categoryList = categories.getCategories()
                .stream()
                .filter(category -> title.equals(category.getTitle())
                        && description.equals(category.getDescription()))
                .collect(Collectors.toList());
        assertFalse(CollectionUtils.isEmpty(categoryList));

//        delete each category created, should be just one
        categoryList.forEach(category-> {
            try {
                deleteCategory(category.getId(), httpClient);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Test
    public void testGetPostPutById() throws IOException {
        String title = "testCategory";
        String description = "test description";

//        create Category object
        HttpResponse response = createCategory(title, description, httpClient);
        int statusCode = response.getStatusLine().getStatusCode();
        assertEquals(HttpStatus.SC_CREATED, statusCode);

//        get category objects and match to the one we just created
        response = getAllCategories(httpClient);
        CategoryResponse categories = deserialize(response, CategoryResponse.class);

        List<Category> categoryList = categories.getCategories()
                .stream()
                .filter(category -> title.equals(category.getTitle())
                        && description.equals(category.getDescription()))
                .toList();
        String id = categoryList.get(0).getId();

//        modify the created category using put
        String newDescription = "new test description";
        response = modifyCategoryPut(id, title, newDescription, httpClient);
        statusCode = response.getStatusLine().getStatusCode();
        assertEquals(HttpStatus.SC_OK, statusCode);

//        get the created Category by id
        response = getCategory(id, httpClient);
        categories = deserialize(response, CategoryResponse.class);
        Category category = categories.getCategories().get(0);
        assertEquals(title, category.getTitle());
        assertEquals(newDescription, category.getDescription());

        String newTitle = "new title";
//        modify the created Category using post
        response = modifyCategoryPost(id, newTitle, description, httpClient);
        statusCode = response.getStatusLine().getStatusCode();
        assertEquals(HttpStatus.SC_OK, statusCode);

//        get the created Category by id
        response = getCategory(id, httpClient);
        categories = deserialize(response, CategoryResponse.class);
        category = categories.getCategories().get(0);
        assertEquals(newTitle, category.getTitle());
        assertEquals(description, category.getDescription());

//        delete the created Category
        response = deleteCategory(category.getId(), httpClient);
        statusCode = response.getStatusLine().getStatusCode();
        assertEquals(HttpStatus.SC_OK, statusCode);
    }

    @Test
    public void testCreateEmptyTitle() throws IOException {
        String title = "";
        String description = "test description";

//        create Category object
        HttpResponse response = createCategory(title, description, httpClient);

        ResponseError e = deserialize(response, ResponseError.class);
        int statusCode = response.getStatusLine().getStatusCode();
        assertEquals(HttpStatus.SC_BAD_REQUEST, statusCode);
        assertEquals("Failed Validation: title : can not be empty", e.getErrorMessages().get(0));
    }

    @Test
    public void testGetByIdNonExistent() throws IOException {
        HttpResponse response = getCategory("-1", httpClient);
        int statusCode = response.getStatusLine().getStatusCode();
        assertEquals(HttpStatus.SC_NOT_FOUND, statusCode);
    }

    @Test
    public void testPutByIdNonExistent() throws IOException {
        String title = "testCategory";
        String description = "test description";
        HttpResponse response = modifyCategoryPut("-1", title, description, httpClient);
        int statusCode = response.getStatusLine().getStatusCode();
        assertEquals(HttpStatus.SC_NOT_FOUND, statusCode);
    }

    @Test
    public void testPutEmptyTitle() throws IOException {
        String title = "testCategory";
        String description = "test description";

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

        response = modifyCategoryPut(id, invalidTitle, description, httpClient);
        ResponseError e = deserialize(response, ResponseError.class);
        int statusCode = response.getStatusLine().getStatusCode();
        assertEquals(HttpStatus.SC_BAD_REQUEST, statusCode);
        assertEquals("Failed Validation: title : can not be empty", e.getErrorMessages().get(0));

        response = deleteCategory(id, httpClient);
        statusCode = response.getStatusLine().getStatusCode();
        assertEquals(HttpStatus.SC_OK, statusCode);
    }

    @Test
    public void testPostModifyNonExistentID() throws IOException {
        String title = "testCategory";
        String description = "test description";
        HttpResponse response = modifyCategoryPost("-1", title, description, httpClient);
        int statusCode = response.getStatusLine().getStatusCode();
        assertEquals(HttpStatus.SC_NOT_FOUND, statusCode);
    }

    @Test
    public void testPostModifyEmptyTitle() throws IOException {
        String title = "testCategory";
        String description = "test description";

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
        response = modifyCategoryPost(id, invalidTitle, description, httpClient);
        ResponseError e = deserialize(response, ResponseError.class);
        int statusCode = response.getStatusLine().getStatusCode();
        assertEquals(HttpStatus.SC_BAD_REQUEST, statusCode);
        assertEquals("Failed Validation: title : can not be empty", e.getErrorMessages().get(0));

        response = deleteCategory(id, httpClient);
        statusCode = response.getStatusLine().getStatusCode();
        assertEquals(HttpStatus.SC_OK, statusCode);
    }

    @Test
    public void testDeleteNonExistentID() throws IOException {
        HttpResponse response = deleteCategory("-1", httpClient);
        int statusCode = response.getStatusLine().getStatusCode();
        assertEquals(HttpStatus.SC_NOT_FOUND, statusCode);
    }
}
