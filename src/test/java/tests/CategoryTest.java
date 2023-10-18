package tests;

import config.RandomOrderTestRunner;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.http.HttpResponse;
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
        assertEquals(201, statusCode);

//        check Category object was created
        response = getAllCategories(httpClient);
        CategoryResponse categories = deserialize(response, CategoryResponse.class);

        statusCode = response.getStatusLine().getStatusCode();
        assertEquals(200, statusCode);

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
        assertEquals(201, statusCode);

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
        response = modifyCategory1(id, title, newDescription, httpClient);
        statusCode = response.getStatusLine().getStatusCode();
        assertEquals(200, statusCode);

//        get the created Category by id
        response = getCategory(id, httpClient);
        categories = deserialize(response, CategoryResponse.class);
        Category category = categories.getCategories().get(0);
        assertEquals(title, category.getTitle());
        assertEquals(newDescription, category.getDescription());

        String newTitle = "new title";
//        modify the created Category using post
        response = modifyCategory2(id, newTitle, description, httpClient);
        statusCode = response.getStatusLine().getStatusCode();
        assertEquals(200, statusCode);

//        get the created Category by id
        response = getCategory(id, httpClient);
        categories = deserialize(response, CategoryResponse.class);
        category = categories.getCategories().get(0);
        assertEquals(newTitle, category.getTitle());
        assertEquals(description, category.getDescription());

//        delete the created Category
        response = deleteCategory(category.getId(), httpClient);
        statusCode = response.getStatusLine().getStatusCode();
        assertEquals(200, statusCode);
    }

    @Test
    public void testErrorCreate() throws IOException {
        String title = "";
        String description = "test description";

//        create Category object
        HttpResponse response = createCategory(title, description, httpClient);

        ResponseError e = deserialize(response, ResponseError.class);
        int statusCode = response.getStatusLine().getStatusCode();
        assertEquals(400, statusCode);
        assertEquals("Failed Validation: title : can not be empty", e.getErrorMessages().get(0));
    }

    @Test
    public void test404GetById() throws IOException {
        HttpResponse response = getCategory("-1", httpClient);
        int statusCode = response.getStatusLine().getStatusCode();
        assertEquals(404, statusCode);
    }

    @Test
    public void test404Put() throws IOException {
        String title = "testCategory";
        String description = "test description";
        HttpResponse response = modifyCategory1("-1", title, description, httpClient);
        int statusCode = response.getStatusLine().getStatusCode();
        assertEquals(404, statusCode);
    }

    @Test
    public void test400Put() throws IOException {
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

        response = modifyCategory1(id, invalidTitle, description, httpClient);
        ResponseError e = deserialize(response, ResponseError.class);
        int statusCode = response.getStatusLine().getStatusCode();
        assertEquals(400, statusCode);
        assertEquals("Failed Validation: title : can not be empty", e.getErrorMessages().get(0));

        deleteCategory(id, httpClient);
    }

    @Test
    public void test404Post() throws IOException {
        String title = "testCategory";
        String description = "test description";
        HttpResponse response = modifyCategory2("-1", title, description, httpClient);
        int statusCode = response.getStatusLine().getStatusCode();
        assertEquals(404, statusCode);
    }

    @Test
    public void test400Post() throws IOException {
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
        response = modifyCategory2(id, invalidTitle, description, httpClient);
        ResponseError e = deserialize(response, ResponseError.class);
        int statusCode = response.getStatusLine().getStatusCode();
        assertEquals(400, statusCode);
        assertEquals("Failed Validation: title : can not be empty", e.getErrorMessages().get(0));

        deleteCategory(id, httpClient);
    }

    @Test
    public void test404Delete() throws IOException {
        HttpResponse response = deleteCategory("-1", httpClient);
        int statusCode = response.getStatusLine().getStatusCode();
        assertEquals(404, statusCode);
    }
}
