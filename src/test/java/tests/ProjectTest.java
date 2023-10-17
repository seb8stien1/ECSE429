package tests;

import config.RandomOrderTestRunner;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import response.ResponseError;
import response.Project;
import response.ProjectResponse;

import java.io.IOException;
import java.util.List;

import static helpers.ApiHelper.deserialize;
import static helpers.ProjectHelper.*;
import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

@RunWith(RandomOrderTestRunner.class)
public class ProjectTest {
    HttpClient httpClient;
    
    @Test
    public void testCreateGetAllAndDeleteByIdProject() throws IOException {
        String title = "testProject";
        Boolean active = Boolean.FALSE;
        Boolean completed = Boolean.FALSE;
        String description = "test description";

//        create Project object
        HttpResponse response = createProject(title, completed, active, description, httpClient);

        int statusCode = response.getStatusLine().getStatusCode();
        assertEquals(201, statusCode);

//        check Project object was created
        response = getAllProjects(httpClient);
        ProjectResponse Projects = deserialize(response, ProjectResponse.class);

        statusCode = response.getStatusLine().getStatusCode();
        assertEquals(200, statusCode);

        List<Project> ProjectList = Projects.getProjects()
                .stream()
                .filter(Project -> title.equals(Project.getTitle())
                        && completed.equals(Project.getCompleted()
                        && active.equals(Project.getActive())
                        && description.equals(Project.getDescription())))
                .toList();
        assertFalse(CollectionUtils.isEmpty(ProjectList));

//        delete each Project created, should be just one
        ProjectList.forEach(Project-> {
            try {
                deleteProject(Project.getId(), httpClient);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Test
    public void testGetPostPutById() throws IOException {
        String title = "testProject";
        Boolean active = Boolean.FALSE;
        Boolean completed = Boolean.FALSE;
        String description = "test description";

//        create Project object
        HttpResponse response = createProject(title, completed, active, description, httpClient);
        int statusCode = response.getStatusLine().getStatusCode();
        assertEquals(201, statusCode);

//        get Project objects and match to the one we just created
        response = getAllProjects(httpClient);
        ProjectResponse Projects = deserialize(response, ProjectResponse.class);

        List<Project> ProjectList = Projects.getProjects()
                .stream()
                .filter(Project -> title.equals(Project.getTitle())
                        && completed.equals(Project.getCompleted()
                        && active.equals(Project.getActive())
                        && description.equals(Project.getDescription())))
                .toList();
        String id = ProjectList.get(0).getId();

//        modify the created Project using put
        String newDescription = "new test description";
        response = modifyProject1(id, title, completed, active, newDescription, httpClient);
        statusCode = response.getStatusLine().getStatusCode();
        assertEquals(200, statusCode);

//        get the created Project by id
        response = getProject(id, httpClient);
        Projects = deserialize(response, ProjectResponse.class);
        Project Project = Projects.getProjects().get(0);
        assertEquals(title, Project.getTitle());
        assertEquals(completed, Project.getCompleted());
        assertEquals(active, Project.getActive());
        assertEquals(newDescription, Project.getDescription());

//        modify the created Project using post
        Boolean newActive = Boolean.TRUE;
        response = modifyProject1(id, title, completed, newActive, description, httpClient);
        statusCode = response.getStatusLine().getStatusCode();
        assertEquals(200, statusCode);

//        get the created Project by id
        response = getProject(id, httpClient);
        Projects = deserialize(response, ProjectResponse.class);
        Project = Projects.getProjects().get(0);
        assertEquals(title, Project.getTitle());
        assertEquals(completed, Project.getCompleted());
        assertEquals(newActive, Project.getActive());
        assertEquals(description, Project.getDescription());

//        delete the created Project
        response = deleteProject(Project.getId(), httpClient);
        statusCode = response.getStatusLine().getStatusCode();
        assertEquals(200, statusCode);
    }

    @Test
    public void testErrorCreate() throws IOException {
        String title = "testProject";
        String active = "fals";
        Boolean completed = false;
        String description = "test description";

//        create Project object
        HttpResponse response = createProject(title, completed, active, description, httpClient);

        ResponseError e = deserialize(response, ResponseError.class);
        int statusCode = response.getStatusLine().getStatusCode();
        assertEquals(400, statusCode);
        assertEquals("Failed Validation: active should be BOOLEAN", e.getErrorMessages().get(0));
    }

    @Test
    public void test404GetById() throws IOException {
        HttpResponse response = getProject("-1", httpClient);
        int statusCode = response.getStatusLine().getStatusCode();
        assertEquals(404, statusCode);
    }

    @Test
    public void test404Put() throws IOException {
        String title = "testProject";
        Boolean doneStatus = Boolean.FALSE;
        String description = "test description";
        HttpResponse response = modifyProject1("-1", title, doneStatus, doneStatus, description, httpClient);
        int statusCode = response.getStatusLine().getStatusCode();
        assertEquals(404, statusCode);
    }

    @Test
    public void test400Put() throws IOException {
        String title = "testProject";
        Boolean active = false;
        Boolean completed = false;
        String description = "test description";

        createProject(title, completed, active, description, httpClient);
        HttpResponse response = getAllProjects(httpClient);
        ProjectResponse Projects = deserialize(response, ProjectResponse.class);

        List<Project> ProjectList = Projects.getProjects()
                .stream()
                .filter(Project -> title.equals(Project.getTitle())
                        && active.equals(Project.getActive()
                        && completed.equals(Project.getCompleted())
                        && description.equals(Project.getDescription())))
                .toList();
        String id = ProjectList.get(0).getId();

        String invalidActive = "fals";

        response = modifyProject1(id, title, completed, invalidActive
                , description, httpClient);
        ResponseError e = deserialize(response, ResponseError.class);
        int statusCode = response.getStatusLine().getStatusCode();
        assertEquals(400, statusCode);
        assertEquals("Failed Validation: active should be BOOLEAN", e.getErrorMessages().get(0));

        deleteProject(id, httpClient);
    }

    @Test
    public void test404Post() throws IOException {
        String title = "testProject";
        Boolean completed = Boolean.FALSE;
        Boolean active = Boolean.FALSE;
        String description = "test description";
        HttpResponse response = modifyProject2("-1", title, completed, active, description, httpClient);
        int statusCode = response.getStatusLine().getStatusCode();
        assertEquals(404, statusCode);
    }

    @Test
    public void test400Post() throws IOException {
        String title = "testProject";
        Boolean active = false;
        Boolean completed = false;
        String description = "test description";

        createProject(title, completed, active, description, httpClient);
        HttpResponse response = getAllProjects(httpClient);
        ProjectResponse Projects = deserialize(response, ProjectResponse.class);

        List<Project> ProjectList = Projects.getProjects()
                .stream()
                .filter(Project -> title.equals(Project.getTitle())
                        && active.equals(Project.getActive()
                        && completed.equals(Project.getCompleted())
                        && description.equals(Project.getDescription())))
                .toList();
        String id = ProjectList.get(0).getId();

        String invalidActive = "fals";

        response = modifyProject2(id, title, completed, invalidActive
                , description, httpClient);
        ResponseError e = deserialize(response, ResponseError.class);
        int statusCode = response.getStatusLine().getStatusCode();
        assertEquals(400, statusCode);
        assertEquals("Failed Validation: active should be BOOLEAN", e.getErrorMessages().get(0));

        deleteProject(id, httpClient);
    }

    @Test
    public void test404Delete() throws IOException {
        HttpResponse response = deleteProject("-1", httpClient);
        int statusCode = response.getStatusLine().getStatusCode();
        assertEquals(404, statusCode);
    }
}
