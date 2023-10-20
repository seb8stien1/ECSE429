package tests;

import config.RandomOrderTestRunner;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
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
        assertEquals(HttpStatus.SC_CREATED, statusCode);

//        check Project object was created
        response = getAllProjects(httpClient);
        ProjectResponse projects = deserialize(response, ProjectResponse.class);

        statusCode = response.getStatusLine().getStatusCode();
        assertEquals(HttpStatus.SC_OK, statusCode);

        List<Project> projectList = projects.getProjects()
                .stream()
                .filter(project -> title.equals(project.getTitle())
                        && completed.equals(project.getCompleted())
                        && active.equals(project.getActive())
                        && description.equals(project.getDescription()))
                .toList();
        assertFalse(CollectionUtils.isEmpty(projectList));

//        delete each Project created, should be just one
        projectList.forEach(project-> {
            try {
                deleteProject(project.getId(), httpClient);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Test
    public void testHeadAllProjects() throws IOException {
        HttpResponse headResponse = headAllProjects(httpClient);
        HttpResponse getResponse = getAllProjects(httpClient);

        // check the head response does not return anything in the body
        assertNull(headResponse.getEntity());

        // we will now check that all the headers returned are the same as the ones returned for the get request
        // except for the date attribute
        assertEquals(headResponse.getAllHeaders().length, getResponse.getAllHeaders().length);

        for (int i = 0; i < headResponse.getAllHeaders().length; i++) {
            if (!headResponse.getAllHeaders()[i].getName().equalsIgnoreCase("Date")) {
                assertEquals(headResponse.getAllHeaders()[i].getElements(), getResponse.getAllHeaders()[i].getElements());
            }
        }

    }

    @Test
    public void testHeadProjectById() throws IOException {
        String title = "testProject";
        Boolean active = Boolean.FALSE;
        Boolean completed = Boolean.FALSE;
        String description = "test description";

//        create Category object to fetch ID that will be used in the head request
        createProject(title,completed,active,description,httpClient);
        HttpResponse getAllResponse = getAllProjects(httpClient);
        ProjectResponse projects = deserialize(getAllResponse, ProjectResponse.class);
        String projectID = projects.getProjects().get(0).getId();

        // making requests
        HttpResponse headResponse = headProject(projectID, httpClient);
        HttpResponse getResponse = getProject(projectID, httpClient);

        // check the head response does not return anything in the body
        assertNull(headResponse.getEntity());

        // we will now check that all the headers returned are the same as the ones returned for the get request
        // except for the date attribute
        assertEquals(headResponse.getAllHeaders().length, getResponse.getAllHeaders().length);

        for (int i = 0; i < headResponse.getAllHeaders().length; i++) {
            if (!headResponse.getAllHeaders()[i].getName().equalsIgnoreCase("Date")) {
                assertEquals(headResponse.getAllHeaders()[i].getElements(), getResponse.getAllHeaders()[i].getElements());
            }
        }
    }

    @Test
    public void testCreateAndModify() throws IOException {
        String title = "testProject";
        Boolean active = Boolean.FALSE;
        Boolean completed = Boolean.FALSE;
        String description = "test description";

//        create Project object
        HttpResponse response = createProject(title, completed, active, description, httpClient);
        int statusCode = response.getStatusLine().getStatusCode();
        assertEquals(HttpStatus.SC_CREATED, statusCode);

//        get Project objects and match to the one we just created
        response = getAllProjects(httpClient);
        ProjectResponse projects = deserialize(response, ProjectResponse.class);

        List<Project> ProjectList = projects.getProjects()
                .stream()
                .filter(project -> title.equals(project.getTitle())
                        && completed.equals(project.getCompleted())
                        && active.equals(project.getActive())
                        && description.equals(project.getDescription()))
                .toList();
        String id = ProjectList.get(0).getId();

//        modify the created Project using put
        String newDescription = "new test description";
        response = modifyProjectPut(id, title, completed, active, newDescription, httpClient);
        statusCode = response.getStatusLine().getStatusCode();
        assertEquals(HttpStatus.SC_OK, statusCode);

//        get the created Project by id
        response = getProject(id, httpClient);
        projects = deserialize(response, ProjectResponse.class);
        Project project = projects.getProjects().get(0);
        assertEquals(title, project.getTitle());
        assertEquals(completed, project.getCompleted());
        assertEquals(active, project.getActive());
        assertEquals(newDescription, project.getDescription());

//        modify the created Project using post
        Boolean newActive = Boolean.TRUE;
        response = modifyProjectPut(id, title, completed, newActive, description, httpClient);
        statusCode = response.getStatusLine().getStatusCode();
        assertEquals(HttpStatus.SC_OK, statusCode);

//        get the created Project by id
        response = getProject(id, httpClient);
        projects = deserialize(response, ProjectResponse.class);
        project = projects.getProjects().get(0);
        assertEquals(title, project.getTitle());
        assertEquals(completed, project.getCompleted());
        assertEquals(newActive, project.getActive());
        assertEquals(description, project.getDescription());

//        delete the created Project
        response = deleteProject(project.getId(), httpClient);
        statusCode = response.getStatusLine().getStatusCode();
        assertEquals(HttpStatus.SC_OK, statusCode);
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
        assertEquals(HttpStatus.SC_BAD_REQUEST, statusCode);
        assertEquals("Failed Validation: active should be BOOLEAN", e.getErrorMessages().get(0));
    }

    @Test
    public void testGetByNonexistentId() throws IOException {
        HttpResponse response = getProject("-1", httpClient);
        int statusCode = response.getStatusLine().getStatusCode();
        assertEquals(HttpStatus.SC_NOT_FOUND, statusCode);
    }

    @Test
    public void testPutNonexistentID() throws IOException {
        String title = "testProject";
        Boolean doneStatus = Boolean.FALSE;
        String description = "test description";
        HttpResponse response = modifyProjectPut("-1", title, doneStatus, doneStatus, description, httpClient);
        int statusCode = response.getStatusLine().getStatusCode();
        assertEquals(HttpStatus.SC_NOT_FOUND, statusCode);
    }

    @Test
    public void testPutError() throws IOException {
        String title = "testProject";
        Boolean active = false;
        Boolean completed = false;
        String description = "test description";

        createProject(title, completed, active, description, httpClient);
        HttpResponse response = getAllProjects(httpClient);
        ProjectResponse projects = deserialize(response, ProjectResponse.class);

        List<Project> projectList = projects.getProjects()
                .stream()
                .filter(project -> title.equals(project.getTitle())
                        && active.equals(project.getActive())
                        && completed.equals(project.getCompleted())
                        && description.equals(project.getDescription()))
                .toList();
        String id = projectList.get(0).getId();

        String invalidActive = "fals";

        response = modifyProjectPut(id, title, completed, invalidActive
                , description, httpClient);
        ResponseError e = deserialize(response, ResponseError.class);
        int statusCode = response.getStatusLine().getStatusCode();
        assertEquals(HttpStatus.SC_BAD_REQUEST, statusCode);
        assertEquals("Failed Validation: active should be BOOLEAN", e.getErrorMessages().get(0));

        response = deleteProject(id, httpClient);
        statusCode = response.getStatusLine().getStatusCode();
        assertEquals(HttpStatus.SC_OK, statusCode);
    }

    @Test
    public void testPostInvalidID() throws IOException {
        String title = "testProject";
        Boolean completed = Boolean.FALSE;
        Boolean active = Boolean.FALSE;
        String description = "test description";
        HttpResponse response = modifyProjectPost("-1", title, completed, active, description, httpClient);
        int statusCode = response.getStatusLine().getStatusCode();
        assertEquals(HttpStatus.SC_NOT_FOUND, statusCode);
    }

    @Test
    public void testPostInvalidActive() throws IOException {
        String title = "testProject";
        Boolean active = false;
        Boolean completed = false;
        String description = "test description";

        createProject(title, completed, active, description, httpClient);
        HttpResponse response = getAllProjects(httpClient);
        ProjectResponse projects = deserialize(response, ProjectResponse.class);

        List<Project> projectList = projects.getProjects()
                .stream()
                .filter(project -> title.equals(project.getTitle())
                        && active.equals(project.getActive())
                        && completed.equals(project.getCompleted())
                        && description.equals(project.getDescription()))
                .toList();
        String id = projectList.get(0).getId();

        String invalidActive = "fals";

        response = modifyProjectPost(id, title, completed, invalidActive
                , description, httpClient);
        ResponseError e = deserialize(response, ResponseError.class);
        int statusCode = response.getStatusLine().getStatusCode();
        assertEquals(HttpStatus.SC_BAD_REQUEST, statusCode);
        assertEquals("Failed Validation: active should be BOOLEAN", e.getErrorMessages().get(0));

        response = deleteProject(id, httpClient);
        statusCode = response.getStatusLine().getStatusCode();
        assertEquals(HttpStatus.SC_OK, statusCode);
    }

    @Test
    public void testDeleteNonexistentID() throws IOException {
        HttpResponse response = deleteProject("-1", httpClient);
        int statusCode = response.getStatusLine().getStatusCode();
        assertEquals(HttpStatus.SC_NOT_FOUND, statusCode);
    }
}
