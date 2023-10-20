package tests;

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
    public static void testServiceOn() throws IOException {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpResponse response = ApiHelper.sendHttpRequest("get", "http://localhost:4567/docs", null, httpClient);
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

        response = CategoryHelper.getAssociation("projects", categoryId, httpClient);
        ProjectResponse projectAssociations = deserialize(response, ProjectResponse.class);
        assertTrue(CollectionUtils.isEmpty(projectAssociations.getProjects()));

        response = CategoryHelper.createAssociation("projects", categoryId, projectId, httpClient);
        int statusCode = response.getStatusLine().getStatusCode();
        assertEquals(HttpStatus.SC_CREATED, statusCode);

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

        response = CategoryHelper.deleteAssociation("projects", categoryId, projectId, httpClient);
        statusCode = response.getStatusLine().getStatusCode();
        assertEquals(HttpStatus.SC_OK, statusCode);

        response = CategoryHelper.getAssociation("projects", categoryId, httpClient);
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
    public void testCreateProjectsOfCategoriesInvalidProject() throws IOException {
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

        response = CategoryHelper.createAssociation("projects", categoryId, "-1", httpClient);
        int statusCode = response.getStatusLine().getStatusCode();
        assertEquals(HttpStatus.SC_NOT_FOUND, statusCode);

        response = deleteCategory(categoryId, httpClient);
        statusCode = response.getStatusLine().getStatusCode();
        assertEquals(HttpStatus.SC_OK, statusCode);
    }

    @Test
    public void testCreateProjectsOfCategoriesInvalidCategory() throws IOException {
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

        response = CategoryHelper.createAssociation("projects", "-1", projectId, httpClient);
        int statusCode = response.getStatusLine().getStatusCode();
        assertEquals(HttpStatus.SC_NOT_FOUND, statusCode);

        response = deleteProject(projectId, httpClient);
        statusCode = response.getStatusLine().getStatusCode();
        assertEquals(HttpStatus.SC_OK, statusCode);
    }

    @Test
    public void testGetProjectsOfNonexistentCategoryExpectedResultFailing() throws IOException {
        HttpResponse response = CategoryHelper.getAssociation("projects","-1", httpClient);
        int statusCode = response.getStatusLine().getStatusCode();
        assertNotEquals(String.format("The API behaviour for this test is different than expected. Expected %s but got %s.", HttpStatus.SC_NOT_FOUND, statusCode), HttpStatus.SC_OK, statusCode);
    }

    @Test
    public void testGetProjectsOfNonexistentCategoryActualBehaviour() throws IOException {
        HttpResponse response = CategoryHelper.getAssociation("projects", "-1", httpClient);
        int statusCode = response.getStatusLine().getStatusCode();
        assertEquals(HttpStatus.SC_OK, statusCode);
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



        response = ProjectHelper.getAssociation("categories", projectId, httpClient);
        CategoryResponse categoryAssociations = deserialize(response, CategoryResponse.class);
        assertTrue(CollectionUtils.isEmpty(categoryAssociations.getCategories()));

        response = ProjectHelper.createAssociation("categories", projectId, categoryId, httpClient);
        int statusCode = response.getStatusLine().getStatusCode();
        assertEquals(HttpStatus.SC_CREATED, statusCode);

        response = ProjectHelper.getAssociation("categories", projectId, httpClient);
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

        response = ProjectHelper.deleteAssociation("categories", projectId, categoryId, httpClient);
        statusCode = response.getStatusLine().getStatusCode();
        assertEquals(HttpStatus.SC_OK, statusCode);

        response = ProjectHelper.getAssociation("categories", projectId, httpClient);
        categoryAssociations = deserialize(response, CategoryResponse.class);
        assertTrue(CollectionUtils.isEmpty(categoryAssociations.getCategories()));

        response = deleteCategory(categoryId, httpClient);
        statusCode = response.getStatusLine().getStatusCode();
        assertEquals(HttpStatus.SC_OK, statusCode);

        response = deleteProject(projectId, httpClient);
        statusCode = response.getStatusLine().getStatusCode();
        assertEquals(HttpStatus.SC_OK, statusCode);
    }


    @Test
    public void testCreateCategoriesOfProjectInvalidCategory() throws IOException {
        String projectTitle = "project title";
        String projectDescription = "project description";
        createProject(projectTitle, false, false, projectDescription, httpClient);

        HttpResponse response = getAllProjects(httpClient);
        ProjectResponse projects = deserialize(response, ProjectResponse.class);

        List<Project> projectList = projects.getProjects()
                .stream()
                .filter(project -> projectTitle.equals(project.getTitle())
                        && Boolean.FALSE.equals(project.getCompleted())
                        && Boolean.FALSE.equals(project.getActive())
                        && projectDescription.equals(project.getDescription()))
                .toList();
        String projectId = projectList.get(0).getId();

        response = ProjectHelper.createAssociation("categories", projectId, "-1", httpClient);
        int statusCode = response.getStatusLine().getStatusCode();
        assertEquals(HttpStatus.SC_NOT_FOUND, statusCode);

        response = deleteProject(projectId, httpClient);
        statusCode = response.getStatusLine().getStatusCode();
        assertEquals(HttpStatus.SC_OK, statusCode);
    }

    @Test
    public void testCreateCategoriesOfProjectInvalidProject() throws IOException {
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

        response = ProjectHelper.createAssociation("categories", "-1", categoryId, httpClient);
        int statusCode = response.getStatusLine().getStatusCode();
        assertEquals(HttpStatus.SC_NOT_FOUND, statusCode);

        response = deleteCategory(categoryId, httpClient);
        statusCode = response.getStatusLine().getStatusCode();
        assertEquals(HttpStatus.SC_OK, statusCode);
    }
    @Test
    public void testGetCategoriesOfNonexistentProjectExpectedResultFailing() throws IOException {
        HttpResponse response = ProjectHelper.getAssociation("categories", "-1", httpClient);
        int statusCode = response.getStatusLine().getStatusCode();
        assertNotEquals(String.format("The API behaviour for this test is different than expected. Expected %s but got %s.", HttpStatus.SC_NOT_FOUND, statusCode), HttpStatus.SC_OK, statusCode);
    }

    @Test
    public void testGetCategoriesOfNonexistentProjectActualBehaviour() throws IOException {
        HttpResponse response = ProjectHelper.getAssociation("categories","-1", httpClient);
        int statusCode = response.getStatusLine().getStatusCode();
        assertEquals(HttpStatus.SC_OK, statusCode);
    }

    @Test
    public void testCreateGetDeleteProjectsOfTodoRelationship() throws IOException {
        String todoTitle = "todo title";
        String todoDescription = "todo description";
        String projectTitle = "project title";
        String projectDescription = "project description";

        createTodo(todoTitle,false,todoDescription,httpClient);
        createProject(projectTitle, false, false, projectDescription, httpClient);

        HttpResponse response = getAllTodos(httpClient);
        TodoResponse todos = deserialize(response, TodoResponse.class);

        List<Todo> todoList = todos.getTodos()
                .stream()
                .filter(todo -> todoTitle.equals(todo.getTitle())
                        && todoDescription.equals(todo.getDescription())
                        && Boolean.FALSE.equals(todo.getDoneStatus()))
                .toList();
        String todoId = todoList.get(0).getId();

        response = getAllProjects(httpClient);
        ProjectResponse projects = deserialize(response, ProjectResponse.class);

        List<Project> projectList = projects.getProjects()
                .stream()
                .filter(project -> projectTitle.equals(project.getTitle())
                        && Boolean.FALSE.equals(project.getActive())
                        && Boolean.FALSE.equals(project.getCompleted())
                        && projectDescription.equals(project.getDescription()))
                .toList();
        String projectId = projectList.get(0).getId();

        response = TodoHelper.getAssociation("tasksof", todoId, httpClient);
        ProjectResponse projectAssociations = deserialize(response, ProjectResponse.class);
        assertTrue(CollectionUtils.isEmpty(projectAssociations.getProjects()));

        response = TodoHelper.createAssociation("tasksof", todoId, projectId, httpClient);
        int statusCode = response.getStatusLine().getStatusCode();
        assertEquals(HttpStatus.SC_CREATED, statusCode);

        response = TodoHelper.getAssociation("tasksof", todoId, httpClient);
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

        response = TodoHelper.deleteAssociation("tasksof",todoId, projectId, httpClient);
        statusCode = response.getStatusLine().getStatusCode();
        assertEquals(HttpStatus.SC_OK, statusCode);

        response = TodoHelper.getAssociation("tasksof",todoId, httpClient);
        projectAssociations = deserialize(response, ProjectResponse.class);
        assertTrue(CollectionUtils.isEmpty(projectAssociations.getProjects()));

        response = deleteTodo(todoId, httpClient);
        statusCode = response.getStatusLine().getStatusCode();
        assertEquals(HttpStatus.SC_OK, statusCode);

        response = deleteProject(projectId, httpClient);
        statusCode = response.getStatusLine().getStatusCode();
        assertEquals(HttpStatus.SC_OK, statusCode);
    }
    @Test
    public void testCreateProjectsOfTodoInvalidProject() throws IOException {
        String todoTitle = "category title";
        String todoDescription = "category description";
        createTodo(todoTitle,false,todoDescription,httpClient);

        HttpResponse response = getAllTodos(httpClient);
        TodoResponse todos = deserialize(response, TodoResponse.class);

        List<Todo> todoList = todos.getTodos()
                .stream()
                .filter(todo -> todoTitle.equals(todo.getTitle())
                        && todoDescription.equals(todo.getDescription())
                        && Boolean.FALSE.equals(todo.getDoneStatus()))
                .toList();
        String todoId = todoList.get(0).getId();

        response = TodoHelper.createAssociation("tasksof",todoId, "-1", httpClient);
        int statusCode = response.getStatusLine().getStatusCode();
        assertEquals(HttpStatus.SC_NOT_FOUND, statusCode);

        response = deleteTodo(todoId, httpClient);
        statusCode = response.getStatusLine().getStatusCode();
        assertEquals(HttpStatus.SC_OK, statusCode);
    }
    @Test
    public void testCreateProjectsOfTodoInvalidTodo() throws IOException {
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

        response = TodoHelper.createAssociation("tasksof", "-1", projectId, httpClient);
        int statusCode = response.getStatusLine().getStatusCode();
        assertEquals(HttpStatus.SC_NOT_FOUND, statusCode);

        response = deleteProject(projectId, httpClient);
        statusCode = response.getStatusLine().getStatusCode();
        assertEquals(HttpStatus.SC_OK, statusCode);
    }

    @Test
    public void testGetProjectsOfNonexistentTodoExpectedResultFailing() throws IOException {
        HttpResponse response = TodoHelper.getAssociation("tasksof","-1", httpClient);
        int statusCode = response.getStatusLine().getStatusCode();
        assertNotEquals(String.format("The API behaviour for this test is different than expected. Expected %s but got %s.", HttpStatus.SC_NOT_FOUND, statusCode), HttpStatus.SC_OK, statusCode);
    }

    @Test
    public void testGetProjectsOfNonexistentTodoActualBehaviour() throws IOException {
        HttpResponse response = TodoHelper.getAssociation("tasksof", "-1", httpClient);
        int statusCode = response.getStatusLine().getStatusCode();
        assertEquals(HttpStatus.SC_OK, statusCode);
    }

    @Test
    public void testCreateGetDeleteTodoOfProjectRelationship() throws IOException {
        String todoTitle = "todo title";
        String todoDescription = "todo description";
        String projectTitle = "project title";
        String projectDescription = "project description";

        createTodo(todoTitle,false,todoDescription,httpClient);
        createProject(projectTitle, false, false, projectDescription, httpClient);

        HttpResponse response = getAllTodos(httpClient);
        TodoResponse todos = deserialize(response, TodoResponse.class);

        List<Todo> todoList = todos.getTodos()
                .stream()
                .filter(todo -> todoTitle.equals(todo.getTitle())
                        && todoDescription.equals(todo.getDescription())
                        && Boolean.FALSE.equals(todo.getDoneStatus()))
                .toList();
        String todoId = todoList.get(0).getId();

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

        response = ProjectHelper.getAssociation("tasks", projectId, httpClient);
        TodoResponse todoAssociations = deserialize(response, TodoResponse.class);
        assertTrue(CollectionUtils.isEmpty(todoAssociations.getTodos()));

        response = ProjectHelper.createAssociation("tasks", projectId, todoId, httpClient);
        int statusCode = response.getStatusLine().getStatusCode();
        assertEquals(HttpStatus.SC_CREATED, statusCode);

        response = ProjectHelper.getAssociation("tasks", projectId, httpClient);
        todoAssociations = deserialize(response, TodoResponse.class);
        assertFalse(CollectionUtils.isEmpty(todoAssociations.getTodos()));
        Optional<Todo> categoryAssociationOptional = todoAssociations.getTodos()
                .stream()
                .filter(todo -> todoId.equals(todo.getId()))
                .findFirst();
        assertTrue(categoryAssociationOptional.isPresent());
        Todo todoAssociation = categoryAssociationOptional.get();
        assertEquals(todoTitle, todoAssociation.getTitle());
        assertEquals(todoDescription, todoAssociation.getDescription());

        response = ProjectHelper.deleteAssociation("tasks", projectId, todoId, httpClient);
        statusCode = response.getStatusLine().getStatusCode();
        assertEquals(HttpStatus.SC_OK, statusCode);

        response = ProjectHelper.getAssociation("tasks", projectId, httpClient);
        todoAssociations = deserialize(response, TodoResponse.class);
        assertTrue(CollectionUtils.isEmpty(todoAssociations.getTodos()));

        response = deleteTodo(todoId, httpClient);
        statusCode = response.getStatusLine().getStatusCode();
        assertEquals(HttpStatus.SC_OK, statusCode);

        response = deleteProject(projectId, httpClient);
        statusCode = response.getStatusLine().getStatusCode();
        assertEquals(HttpStatus.SC_OK, statusCode);
    }

    @Test
    public void testCreateTodoOfProjectInvalidTodo() throws IOException {
        String projectTitle = "project title";
        String projectDescription = "project description";
        createProject(projectTitle, false, false, projectDescription, httpClient);

        HttpResponse response = getAllProjects(httpClient);
        ProjectResponse projects = deserialize(response, ProjectResponse.class);

        List<Project> projectList = projects.getProjects()
                .stream()
                .filter(project -> projectTitle.equals(project.getTitle())
                        && Boolean.FALSE.equals(project.getCompleted())
                        && Boolean.FALSE.equals(project.getActive())
                        && projectDescription.equals(project.getDescription()))
                .toList();
        String projectId = projectList.get(0).getId();

        response = ProjectHelper.createAssociation("tasks", projectId, "-1", httpClient);
        int statusCode = response.getStatusLine().getStatusCode();
        assertEquals(HttpStatus.SC_NOT_FOUND, statusCode);

        response = deleteProject(projectId, httpClient);
        statusCode = response.getStatusLine().getStatusCode();
        assertEquals(HttpStatus.SC_OK, statusCode);
    }
    @Test
    public void testCreateTodoOfProjectInvalidProject() throws IOException {
        String todoTitle = "todo title";
        String todoDescription = "todo description";
        createTodo(todoTitle,false,todoDescription,httpClient);

        HttpResponse response = getAllTodos(httpClient);
        TodoResponse todos = deserialize(response, TodoResponse.class);

        List<Todo> todoList = todos.getTodos()
                .stream()
                .filter(todo -> todoTitle.equals(todo.getTitle())
                        && todoDescription.equals(todo.getDescription())
                        && Boolean.FALSE.equals(todo.getDoneStatus()))
                .toList();
        String todoId = todoList.get(0).getId();

        response = ProjectHelper.createAssociation("tasks", "-1", todoId, httpClient);
        int statusCode = response.getStatusLine().getStatusCode();
        assertEquals(HttpStatus.SC_NOT_FOUND, statusCode);

        response = deleteTodo(todoId, httpClient);
        statusCode = response.getStatusLine().getStatusCode();
        assertEquals(HttpStatus.SC_OK, statusCode);
    }

    @Test
    public void testGetTodosOfNonexistentProjectExpectedResultFailing() throws IOException {
        HttpResponse response = ProjectHelper.getAssociation("tasks", "-1", httpClient);
        int statusCode = response.getStatusLine().getStatusCode();
        assertNotEquals(String.format("The API behaviour for this test is different than expected. Expected %s but got %s.", HttpStatus.SC_NOT_FOUND, statusCode), HttpStatus.SC_OK, statusCode);
    }

    @Test
    public void testGetTodosOfNonexistentProjectActualBehaviour() throws IOException {
        HttpResponse response = ProjectHelper.getAssociation("tasks","-1", httpClient);
        int statusCode = response.getStatusLine().getStatusCode();
        assertEquals(HttpStatus.SC_OK, statusCode);
    }






///////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Test
    public void testCreateGetDeleteTodosOfCategoryRelationship() throws IOException {
        String categoryTitle = "category title";
        String categoryDescription = "category description";
        String todoTitle = "todo title";
        String todoDescription = "todo description";

        createCategory(categoryTitle, categoryDescription, httpClient);
        createTodo(todoTitle, false, todoDescription, httpClient);

        HttpResponse response = getAllCategories(httpClient);
        CategoryResponse categories = deserialize(response, CategoryResponse.class);

        List<Category> categoryList = categories.getCategories()
                .stream()
                .filter(category -> categoryTitle.equals(category.getTitle())
                        && categoryDescription.equals(category.getDescription()))
                .toList();
        String categoryId = categoryList.get(0).getId();

        response = getAllTodos(httpClient);
        TodoResponse todos = deserialize(response, TodoResponse.class);

        List<Todo> todoList = todos.getTodos()
                .stream()
                .filter(todo -> todoTitle.equals(todo.getTitle())
                        && Boolean.FALSE.equals(todo.getDoneStatus()
                        && todoDescription.equals(todo.getDescription())))
                .toList();
        String todoId = todoList.get(0).getId();

        response = CategoryHelper.getAssociation("todos", categoryId, httpClient);
        TodoResponse todoAssociations = deserialize(response, TodoResponse.class);
        assertTrue(CollectionUtils.isEmpty(todoAssociations.getTodos()));

        response = CategoryHelper.createAssociation("todos", categoryId, todoId, httpClient);
        int statusCode = response.getStatusLine().getStatusCode();
        assertEquals(HttpStatus.SC_CREATED, statusCode);

        response = CategoryHelper.getAssociation("todos", categoryId, httpClient);
        todoAssociations = deserialize(response, TodoResponse.class);
        assertFalse(CollectionUtils.isEmpty(todoAssociations.getTodos()));
        Optional<Todo> todoAssociationOptional = todoAssociations.getTodos()
                .stream()
                .filter(todo -> todoId.equals(todo.getId()))
                .findFirst();
        assertTrue(todoAssociationOptional.isPresent());
        Todo todoAssociation = todoAssociationOptional.get();
        assertEquals(todoTitle, todoAssociation.getTitle());
        assertEquals(todoDescription, todoAssociation.getDescription());
        assertEquals(Boolean.FALSE, todoAssociation.getDoneStatus());

        response = CategoryHelper.deleteAssociation("todos", categoryId, todoId, httpClient);
        statusCode = response.getStatusLine().getStatusCode();
        assertEquals(HttpStatus.SC_OK, statusCode);

        response = CategoryHelper.getAssociation("todos", categoryId, httpClient);
        todoAssociations = deserialize(response, TodoResponse.class);
        assertTrue(CollectionUtils.isEmpty(todoAssociations.getTodos()));

        response = deleteCategory(categoryId, httpClient);
        statusCode = response.getStatusLine().getStatusCode();
        assertEquals(HttpStatus.SC_OK, statusCode);

        response = deleteTodo(todoId, httpClient);
        statusCode = response.getStatusLine().getStatusCode();
        assertEquals(HttpStatus.SC_OK, statusCode);
    }

    @Test
    public void testCreateTodosOfCategoriesInvalidTodo() throws IOException {
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

        response = CategoryHelper.createAssociation("todos", categoryId, "-1", httpClient);
        int statusCode = response.getStatusLine().getStatusCode();
        assertEquals(HttpStatus.SC_NOT_FOUND, statusCode);

        response = deleteCategory(categoryId, httpClient);
        statusCode = response.getStatusLine().getStatusCode();
        assertEquals(HttpStatus.SC_OK, statusCode);
    }

    @Test
    public void testCreateTodosOfCategoriesInvalidCategory() throws IOException {
        String todoTitle = "todo title";
        String todoDescription = "todo description";

        createTodo(todoTitle, false, todoDescription, httpClient);

        HttpResponse response = getAllTodos(httpClient);
        TodoResponse projects = deserialize(response, TodoResponse.class);

        List<Todo> todoList = projects.getTodos()
                .stream()
                .filter(todo -> todoTitle.equals(todo.getTitle())
                        && Boolean.FALSE.equals(todo.getDoneStatus()
                        && todoDescription.equals(todo.getDescription())))
                .toList();
        String todoId = todoList.get(0).getId();

        response = CategoryHelper.createAssociation("todos", "-1", todoId, httpClient);
        int statusCode = response.getStatusLine().getStatusCode();
        assertEquals(HttpStatus.SC_NOT_FOUND, statusCode);

        response = deleteTodo(todoId, httpClient);
        statusCode = response.getStatusLine().getStatusCode();
        assertEquals(HttpStatus.SC_OK, statusCode);
    }

    @Test
    public void testGetTodosOfNonexistentCategoryExpectedResultFailing() throws IOException {
        HttpResponse response = CategoryHelper.getAssociation("todos","-1", httpClient);
        int statusCode = response.getStatusLine().getStatusCode();
        assertNotEquals(String.format("The API behaviour for this test is different than expected. Expected %s but got %s.", HttpStatus.SC_NOT_FOUND, statusCode), HttpStatus.SC_OK, statusCode);
    }

    @Test
    public void testGetTodosOfNonexistentCategoryActualBehaviour() throws IOException {
        HttpResponse response = CategoryHelper.getAssociation("todos", "-1", httpClient);
        int statusCode = response.getStatusLine().getStatusCode();
        assertEquals(HttpStatus.SC_OK, statusCode);
    }

    @Test
    public void testCreateGetDeleteCategoriesOfTodoRelationship() throws IOException {
        String categoryTitle = "category title";
        String categoryDescription = "category description";
        String todoTitle = "todo title";
        String todoDescription = "todo description";

        createCategory(categoryTitle, categoryDescription, httpClient);
        createTodo(todoTitle, false, todoDescription, httpClient);

        HttpResponse response = getAllCategories(httpClient);
        CategoryResponse categories = deserialize(response, CategoryResponse.class);

        List<Category> categoryList = categories.getCategories()
                .stream()
                .filter(category -> categoryTitle.equals(category.getTitle())
                        && categoryDescription.equals(category.getDescription()))
                .toList();
        String categoryId = categoryList.get(0).getId();

        response = getAllTodos(httpClient);
        TodoResponse projects = deserialize(response, TodoResponse.class);

        List<Todo> todoList = projects.getTodos()
                .stream()
                .filter(todo -> todoTitle.equals(todo.getTitle())
                        && Boolean.FALSE.equals(todo.getDoneStatus()
                        && todoDescription.equals(todo.getDescription())))
                .toList();
        String todoId = todoList.get(0).getId();

        response = TodoHelper.getAssociation("categories", todoId, httpClient);
        CategoryResponse categoryAssociations = deserialize(response, CategoryResponse.class);
        assertTrue(CollectionUtils.isEmpty(categoryAssociations.getCategories()));

        response = TodoHelper.createAssociation("categories", todoId, categoryId, httpClient);
        int statusCode = response.getStatusLine().getStatusCode();
        assertEquals(HttpStatus.SC_CREATED, statusCode);

        response = TodoHelper.getAssociation("categories", todoId, httpClient);
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

        response = TodoHelper.deleteAssociation("categories", todoId, categoryId, httpClient);
        statusCode = response.getStatusLine().getStatusCode();
        assertEquals(HttpStatus.SC_OK, statusCode);

        response = TodoHelper.getAssociation("categories", todoId, httpClient);
        categoryAssociations = deserialize(response, CategoryResponse.class);
        assertTrue(CollectionUtils.isEmpty(categoryAssociations.getCategories()));

        response = deleteCategory(categoryId, httpClient);
        statusCode = response.getStatusLine().getStatusCode();
        assertEquals(HttpStatus.SC_OK, statusCode);

        response = deleteTodo(todoId, httpClient);
        statusCode = response.getStatusLine().getStatusCode();
        assertEquals(HttpStatus.SC_OK, statusCode);
    }

    @Test
    public void testCreateCategoriesOfTodoInvalidCategory() throws IOException {
        String todoTitle = "todo title";
        String todoDescription = "todo description";
        createTodo(todoTitle, false, todoDescription, httpClient);

        HttpResponse response = getAllTodos(httpClient);
        TodoResponse todos = deserialize(response, TodoResponse.class);

        List<Todo> todoList = todos.getTodos()
                .stream()
                .filter(todo -> todoTitle.equals(todo.getTitle())
                        && Boolean.FALSE.equals(todo.getDoneStatus())
                        && todoDescription.equals(todo.getDescription()))
                .toList();
        String todoId = todoList.get(0).getId();

        response = TodoHelper.createAssociation("categories", todoId, "-1", httpClient);
        int statusCode = response.getStatusLine().getStatusCode();
        assertEquals(HttpStatus.SC_NOT_FOUND, statusCode);

        response = deleteTodo(todoId, httpClient);
        statusCode = response.getStatusLine().getStatusCode();
        assertEquals(HttpStatus.SC_OK, statusCode);
    }

    @Test
    public void testCreateCategoriesOfTodoInvalidTodo() throws IOException {
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

        response = TodoHelper.createAssociation("categories", "-1", categoryId, httpClient);
        int statusCode = response.getStatusLine().getStatusCode();
        assertEquals(HttpStatus.SC_NOT_FOUND, statusCode);

        response = deleteCategory(categoryId, httpClient);
        statusCode = response.getStatusLine().getStatusCode();
        assertEquals(HttpStatus.SC_OK, statusCode);
    }

    @Test
    public void testGetCategoriesOfNonexistentTodoExpectedResultFailing() throws IOException {
        HttpResponse response = TodoHelper.getAssociation("categories", "-1", httpClient);
        int statusCode = response.getStatusLine().getStatusCode();
        assertNotEquals(String.format("The API behaviour for this test is different than expected. Expected %s but got %s.", HttpStatus.SC_NOT_FOUND, statusCode), HttpStatus.SC_OK, statusCode);
    }

    @Test
    public void testGetCategoriesOfNonexistentTodoActualBehaviour() throws IOException {
        HttpResponse response = TodoHelper.getAssociation("categories","-1", httpClient);
        int statusCode = response.getStatusLine().getStatusCode();
        assertEquals(HttpStatus.SC_OK, statusCode);
    }
}
