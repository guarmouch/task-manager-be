package com.taskmanager.ts.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.taskmanager.ts.model.Task;
import com.taskmanager.ts.service.TaskService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class TaskControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Mock
    private TaskService taskService;

    @Autowired
    private ObjectMapper objectMapper;

    @InjectMocks
    private TaskController taskController;

    private Task task1;
    private Task task2;
    private Task updatedTask;

    @BeforeEach
    void setUp() {
        task1 = new Task();
        task1.setId(1L);
        task1.setTitle("Task 1");
        task1.setDescription("Description for Task 1");
        task1.setCreatedAt(LocalDateTime.now());
        task1.setCompleted(false);
        task1.setPriority("Low");
        task1.setStatus("Pending");

        task2 = new Task();
        task2.setId(2L);
        task2.setTitle("Task 2");
        task2.setDescription("Description for Task 2");
        task2.setCreatedAt(LocalDateTime.now());
        task2.setCompleted(false);
        task2.setPriority("High");
        task2.setStatus("Pending");

        updatedTask = new Task();
        updatedTask.setId(1L);
        updatedTask.setTitle("Updated Title");
        updatedTask.setDescription("Updated Description");

        // Set up mock MVC
        mockMvc = MockMvcBuilders.standaloneSetup(taskController).build();
    }
    @Test
    void testGetAllTasks() throws Exception {
        // Mock behavior
        List<Task> tasks = Arrays.asList(task1, task2);
        when(taskService.getAllTasks()).thenReturn(tasks);

        // Perform GET request to /tasks and validate response
        ResponseEntity<List<Task>> responseEntity = taskController.getAllTasks();

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isEqualTo(tasks);
    }
    @Test
    void testGetTaskByIdFound() throws Exception {
        // mock
        when(taskService.getTaskById(anyLong())).thenReturn(Optional.of(task1));

        // Act and Assert

        ResponseEntity<Task> responseEntity = taskController.getTaskById(1L);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isEqualTo(task1);
    }
    @Test
    void testCreatTask() throws Exception{

        when(taskService.createTask(any(Task.class))).thenReturn(task1);
        ResponseEntity<Task> responseEntity = taskController.createTask(task1);

        // Assert: Verify the response
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isEqualTo(task1);
    }
    @Test
    void testUpdateTaskFound(){

        when(taskService.updateTask(eq(1L), any(Task.class))).thenReturn(updatedTask);

        ResponseEntity<Task> responseEntity = taskController.updateTask(1L, task1);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isEqualTo(updatedTask);
    }
    @Test
    void testUpdateTaskNotFound(){

        when(taskService.updateTask(eq(0L), any(Task.class))).thenReturn(null);

        ResponseEntity<Task> responseEntity = taskController.updateTask(0L, task1);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    /*
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        return taskService.deleteTask(id) ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
    }*/
    @Test
    void testDeleteTaskNotFound(){
        when(taskService.deleteTask(1L)).thenReturn(false);  // Simulate that task doesn't exist

        // Act: Call the controller's deleteTask method
        ResponseEntity<Void> responseEntity = taskController.deleteTask(1L);

        // Assert: Check that the response status is 404 Not Found
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }
    @Test
    void testDeleteTaskSuccess() {
        // Arrange: Mock the service to return true (task successfully deleted)
        when(taskService.deleteTask(1L)).thenReturn(true);  // Simulate that task was deleted successfully

        // Act: Call the controller's deleteTask method
        ResponseEntity<Void> responseEntity = taskController.deleteTask(1L);

        // Assert: Check that the response status is 204 No Content
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }
    @Test
    void testUpdateStatusWithPrioritySuccess() {
        // Arrange: Mock the service method to simulate a successful update
        String priority = "High";
        doNothing().when(taskService).updateStatusWithPriority(priority);  // Simulate successful update

        // Act: Call the controller's updateStatusWithPriority method
        ResponseEntity<String> responseEntity = taskController.updateStatusWithPriority(priority);

        // Assert: Check that the response status is 200 OK
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isEqualTo("Tasks with priority High have been updated to 'High Priority'.");
    }

    @Test
    void testUpdateStatusWithPriorityFailure() {
        String priority = "High";
        String errorMessage = "Database connection error";
        doThrow(new RuntimeException(errorMessage)).when(taskService).updateStatusWithPriority(priority);  // Simulate failure

        ResponseEntity<String> responseEntity = taskController.updateStatusWithPriority(priority);

        //Check that the response status is 500 INTERNAL SERVER ERROR
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(responseEntity.getBody()).isEqualTo("Error updating tasks: " + errorMessage);
    }
    @Test
    void testHasPendingTasks() {
        when(taskService.hasPendingTasks()).thenReturn(true);

        ResponseEntity<Boolean> responseEntity = taskController.hasPendingTasks();

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isTrue();
    }

    @Test
    void testHasNoPendingTasks() {
        when(taskService.hasPendingTasks()).thenReturn(false);

        ResponseEntity<Boolean> responseEntity = taskController.hasPendingTasks();

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isFalse();
    }
    @Test
    void testGetTasksByPriorityAndStatusFound() {

        List<Task> tasks = Arrays.asList(task1, task2);

        when(taskService.getTasksByPriorityAndStatus("High", "Pending")).thenReturn(tasks);

        ResponseEntity<List<Task>> responseEntity = taskController.getTasksByPriorityAndStatus("High", "Pending");

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isNotNull();
        assertThat(responseEntity.getBody()).hasSize(2);
    }

    @Test
    void testGetTasksByPriorityAndStatusNoContent() {

        when(taskService.getTasksByPriorityAndStatus("Low", "Completed")).thenReturn(List.of());

        ResponseEntity<List<Task>> responseEntity = taskController.getTasksByPriorityAndStatus("Low", "Completed");

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        assertThat(responseEntity.getBody()).isNull();
    }


}