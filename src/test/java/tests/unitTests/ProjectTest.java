package tests.unitTests;

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

    /**
     * This test case verifies the creation, retrieval, and deletion of a project by its ID.
     * It also checks if the retrieved project matches the one created.
     *
     * @throws IOException If there's an I/O exception during the test.
     */
    @Test
    public void testCreateGetAllAndDeleteByIdProject() throws IOException {
        // Define test data
        String title = "testProject";
        Boolean active = Boolean.FALSE;
        Boolean completed = Boolean.FALSE;
        String description = "test description";

        // Create a new Project object
        HttpResponse response = createProject(title, completed, active, description, httpClient);

        // Check if the project was successfully created (HTTP status code 201 - Created)
        int statusCode = response.getStatusLine().getStatusCode();
        assertEquals(HttpStatus.SC_CREATED, statusCode);

        // Retrieve all projects
        response = getAllProjects(httpClient);
        ProjectResponse projects = deserialize(response, ProjectResponse.class);

        // Check if retrieving all projects was successful (HTTP status code 200 - OK)
        statusCode = response.getStatusLine().getStatusCode();
        assertEquals(HttpStatus.SC_OK, statusCode);

        // Filter the list of projects to find the one we just created
        List<Project> projectList = projects.getProjects()
                .stream()
                .filter(project -> title.equals(project.getTitle())
                        && completed.equals(project.getCompleted())
                        && active.equals(project.getActive())
                        && description.equals(project.getDescription()))
                .toList();

        // Check if the project list is not empty
        assertFalse(CollectionUtils.isEmpty(projectList));

        // Delete each created project (should be just one)
        projectList.forEach(project -> {
            try {
                deleteProject(project.getId(), httpClient);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    /**
     * This test case checks the behavior of the HEAD request for retrieving all projects.
     * It verifies that the HEAD response does not contain a body and compares the headers
     * with the GET request response, excluding the "Date" header.
     *
     * @throws IOException If there's an I/O exception during the test.
     */
    @Test
    public void testHeadAllProjects() throws IOException {
        // Send HEAD and GET requests to retrieve all projects
        HttpResponse headResponse = headAllProjects(httpClient);
        HttpResponse getResponse = getAllProjects(httpClient);

        // Check if the HEAD response does not contain a body
        assertNull(headResponse.getEntity());

        // Verify that all headers in the HEAD response are the same as in the GET response,
        // except for the "Date" header.
        assertEquals(headResponse.getAllHeaders().length, getResponse.getAllHeaders().length);

        for (int i = 0; i < headResponse.getAllHeaders().length; i++) {
            if (!headResponse.getAllHeaders()[i].getName().equalsIgnoreCase("Date")) {
                assertEquals(headResponse.getAllHeaders()[i].getElements(), getResponse.getAllHeaders()[i].getElements());
            }
        }
    }

    /**
     * This test case checks the behavior of the HEAD request for retrieving a project by ID.
     * It verifies that the HEAD response does not contain a body and compares the headers
     * with the GET request response, excluding the "Date" header.
     *
     * @throws IOException If there's an I/O exception during the test.
     */
    @Test
    public void testHeadProjectById() throws IOException {
        // Define test data
        String title = "testProject";
        Boolean active = Boolean.FALSE;
        Boolean completed = Boolean.FALSE;
        String description = "test description";

        // Create a Project object to fetch its ID for the head request
        createProject(title, completed, active, description, httpClient);
        HttpResponse getAllResponse = getAllProjects(httpClient);
        ProjectResponse projects = deserialize(getAllResponse, ProjectResponse.class);
        String projectID = projects.getProjects().get(0).getId();

        // Send HEAD and GET requests to retrieve the project by ID
        HttpResponse headResponse = headProject(projectID, httpClient);
        HttpResponse getResponse = getProject(projectID, httpClient);

        // Check if the HEAD response does not contain a body
        assertNull(headResponse.getEntity());

        // Verify that all headers in the HEAD response are the same as in the GET response,
        // except for the "Date" header.
        assertEquals(headResponse.getAllHeaders().length, getResponse.getAllHeaders().length);

        for (int i = 0; i < headResponse.getAllHeaders().length; i++) {
            if (!headResponse.getAllHeaders()[i].getName().equalsIgnoreCase("Date")) {
                assertEquals(headResponse.getAllHeaders()[i].getElements(), getResponse.getAllHeaders()[i].getElements());
            }
        }
    }

    /**
     * This test case verifies the creation, modification (using PUT and POST), and deletion of a project.
     * It also checks if the retrieved project matches the modified values.
     *
     * @throws IOException If there's an I/O exception during the test.
     */
    @Test
    public void testCreateAndModify() throws IOException {
        // Define test data
        String title = "testProject";
        Boolean active = Boolean.FALSE;
        Boolean completed = Boolean.FALSE;
        String description = "test description";

        // Create a new Project object
        HttpResponse response = createProject(title, completed, active, description, httpClient);

        // Check if the project was successfully created (HTTP status code 201 - Created)
        int statusCode = response.getStatusLine().getStatusCode();
        assertEquals(HttpStatus.SC_CREATED, statusCode);

        // Retrieve all projects and filter the list to find the created project
        response = getAllProjects(httpClient);
        ProjectResponse projects = deserialize(response, ProjectResponse.class);
        List<Project> projectList = projects.getProjects()
                .stream()
                .filter(project -> title.equals(project.getTitle())
                        && completed.equals(project.getCompleted())
                        && active.equals(project.getActive())
                        && description.equals(project.getDescription()))
                .toList();
        String id = projectList.get(0).getId();

        // Modify the created project using PUT
        String newDescription = "new test description";
        response = modifyProjectPut(id, title, completed, active, newDescription, httpClient);
        statusCode = response.getStatusLine().getStatusCode();
        assertEquals(HttpStatus.SC_OK, statusCode);

        // Get the modified project by ID and check if it matches the modified values
        response = getProject(id, httpClient);
        projects = deserialize(response, ProjectResponse.class);
        Project project = projects.getProjects().get(0);
        assertEquals(title, project.getTitle());
        assertEquals(completed, project.getCompleted());
        assertEquals(active, project.getActive());
        assertEquals(newDescription, project.getDescription());

        // Modify the created project using POST
        Boolean newActive = Boolean.TRUE;
        response = modifyProjectPut(id, title, completed, newActive, description, httpClient);
        statusCode = response.getStatusLine().getStatusCode();
        assertEquals(HttpStatus.SC_OK, statusCode);

        // Get the modified project by ID and check if it matches the modified values
        response = getProject(id, httpClient);
        projects = deserialize(response, ProjectResponse.class);
        project = projects.getProjects().get(0);
        assertEquals(title, project.getTitle());
        assertEquals(completed, project.getCompleted());
        assertEquals(newActive, project.getActive());
        assertEquals(description, project.getDescription());

        // Delete the created project
        response = deleteProject(project.getId(), httpClient);
        statusCode = response.getStatusLine().getStatusCode();
        assertEquals(HttpStatus.SC_OK, statusCode);
    }

    /**
     * This test case checks the behavior of creating a project with invalid data.
     * It expects a Bad Request (HTTP status code 400) and validates the error message.
     *
     * @throws IOException If there's an I/O exception during the test.
     */
    @Test
    public void testErrorCreate() throws IOException {
        // Define test data with invalid values
        String title = "testProject";
        String active = "fals";  // Invalid value for "active"
        Boolean completed = false;
        String description = "test description";

        // Attempt to create a project with invalid data
        HttpResponse response = createProject(title, completed, active, description, httpClient);

        // Deserialize the response error
        ResponseError e = deserialize(response, ResponseError.class);
        int statusCode = response.getStatusLine().getStatusCode();

        // Verify that the expected error response is received (HTTP status code 400 - Bad Request)
        assertEquals(HttpStatus.SC_BAD_REQUEST, statusCode);
        assertEquals("Failed Validation: active should be BOOLEAN", e.getErrorMessages().get(0));
    }


    /**
     * This test case verifies the behavior when attempting to retrieve a project by a nonexistent ID.
     * It expects a "Not Found" (HTTP status code 404) response.
     *
     * @throws IOException If there's an I/O exception during the test.
     */
    @Test
    public void testGetByNonexistentId() throws IOException {
        // Attempt to retrieve a project with a nonexistent ID
        HttpResponse response = getProject("-1", httpClient);
        int statusCode = response.getStatusLine().getStatusCode();

        // Verify that the expected response is received (HTTP status code 404 - Not Found)
        assertEquals(HttpStatus.SC_NOT_FOUND, statusCode);
    }

    /**
     * This test case verifies the behavior when attempting to modify a project using the PUT method with a nonexistent ID.
     * It expects a "Not Found" (HTTP status code 404) response.
     *
     * @throws IOException If there's an I/O exception during the test.
     */
    @Test
    public void testPutNonexistentID() throws IOException {
        // Define test data
        String title = "testProject";
        Boolean doneStatus = Boolean.FALSE;
        String description = "test description";

        // Attempt to modify a project using the PUT method with a nonexistent ID
        HttpResponse response = modifyProjectPut("-1", title, doneStatus, doneStatus, description, httpClient);
        int statusCode = response.getStatusLine().getStatusCode();

        // Verify that the expected response is received (HTTP status code 404 - Not Found)
        assertEquals(HttpStatus.SC_NOT_FOUND, statusCode);
    }

    /**
     * This test case verifies the behavior when attempting to modify a project using the PUT method
     * with invalid data (invalid value for "active"). It expects a "Bad Request" (HTTP status code 400)
     * response with a specific error message.
     *
     * @throws IOException If there's an I/O exception during the test.
     */
    @Test
    public void testPutError() throws IOException {
        // Define test data
        String title = "testProject";
        Boolean active = false;
        Boolean completed = false;
        String description = "test description";

        // Create a project with valid data
        createProject(title, completed, active, description, httpClient);

        // Retrieve all projects and filter the list to find the created project
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

        // Attempt to modify the project using the PUT method with invalid data
        String invalidActive = "fals";
        response = modifyProjectPut(id, title, completed, invalidActive, description, httpClient);

        // Deserialize the response error
        ResponseError e = deserialize(response, ResponseError.class);
        int statusCode = response.getStatusLine().getStatusCode();

        // Verify that the expected response is received (HTTP status code 400 - Bad Request)
        assertEquals(HttpStatus.SC_BAD_REQUEST, statusCode);
        assertEquals("Failed Validation: active should be BOOLEAN", e.getErrorMessages().get(0));

        // Delete the created project
        response = deleteProject(id, httpClient);
        statusCode = response.getStatusLine().getStatusCode();
        assertEquals(HttpStatus.SC_OK, statusCode);
    }

    /**
     * This test case verifies the behavior when attempting to modify a project using the POST method
     * with an invalid ID. It expects a "Not Found" (HTTP status code 404) response.
     *
     * @throws IOException If there's an I/O exception during the test.
     */
    @Test
    public void testPostInvalidID() throws IOException {
        // Define test data
        String title = "testProject";
        Boolean completed = Boolean.FALSE;
        Boolean active = Boolean.FALSE;
        String description = "test description";

        // Attempt to modify a project using the POST method with an invalid ID
        HttpResponse response = modifyProjectPost("-1", title, completed, active, description, httpClient);
        int statusCode = response.getStatusLine().getStatusCode();

        // Verify that the expected response is received (HTTP status code 404 - Not Found)
        assertEquals(HttpStatus.SC_NOT_FOUND, statusCode);
    }

    /**
     * This test case verifies the behavior when attempting to modify a project using the POST method
     * with invalid data (invalid value for "active"). It expects a "Bad Request" (HTTP status code 400)
     * response with a specific error message.
     *
     * @throws IOException If there's an I/O exception during the test.
     */
    @Test
    public void testPostInvalidActive() throws IOException {
        // Define test data
        String title = "testProject";
        Boolean active = false;
        Boolean completed = false;
        String description = "test description";

        // Create a project with valid data
        createProject(title, completed, active, description, httpClient);

        // Retrieve all projects and filter the list to find the created project
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

        // Attempt to modify the project using the POST method with invalid data
        String invalidActive = "fals";
        response = modifyProjectPost(id, title, completed, invalidActive, description, httpClient);

        // Deserialize the response error
        ResponseError e = deserialize(response, ResponseError.class);
        int statusCode = response.getStatusLine().getStatusCode();

        // Verify that the expected response is received (HTTP status code 400 - Bad Request)
        assertEquals(HttpStatus.SC_BAD_REQUEST, statusCode);
        assertEquals("Failed Validation: active should be BOOLEAN", e.getErrorMessages().get(0));

        // Delete the created project
        response = deleteProject(id, httpClient);
        statusCode = response.getStatusLine().getStatusCode();
        assertEquals(HttpStatus.SC_OK, statusCode);
    }

    /**
     * This test case verifies the behavior when attempting to delete a project by a nonexistent ID.
     * It expects a "Not Found" (HTTP status code 404) response.
     *
     * @throws IOException If there's an I/O exception during the test.
     */
    @Test
    public void testDeleteNonexistentID() throws IOException {
        // Attempt to delete a project with a nonexistent ID
        HttpResponse response = deleteProject("-1", httpClient);
        int statusCode = response.getStatusLine().getStatusCode();

        // Verify that the expected response is received (HTTP status code 404 - Not Found)
        assertEquals(HttpStatus.SC_NOT_FOUND, statusCode);
    }

}
