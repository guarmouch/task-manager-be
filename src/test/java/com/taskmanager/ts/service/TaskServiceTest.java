package com.taskmanager.ts.service;

import com.taskmanager.ts.model.Task;
import com.taskmanager.ts.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {
    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private TaskService taskService;

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
    }
    @Test
    void getAllTasks_ShouldReturnListOfTasks() {

        List<Task> tasks = Arrays.asList(task1, task2);
        when(taskRepository.findAll()).thenReturn(tasks);


        List<Task> result = taskService.getAllTasks();


        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.contains(task1));
        assertTrue(result.contains(task2));
    }
    @Test
    void getTaskById_WhenTaskExists() {

        when(taskRepository.findById(1L)).thenReturn(Optional.of(task1));


        Optional<Task> result = taskService.getTaskById(1L);


        assertTrue(result.isPresent());
        assertEquals(task1, result.get());
    }
    @Test
    void getTaskById_WhenTaskDoesNotExist() {

        when(taskRepository.findById(1L)).thenReturn(Optional.empty());


        Optional<Task> result = taskService.getTaskById(1L);


        assertFalse(result.isPresent());
    }
    @Test
    public void testCreateTask() {


        when(taskRepository.save(any())).thenReturn(task1);


        Task savedTask = taskService.createTask(task1);


        assertEquals(task1, savedTask);
    }
    @Test
    void updateTask_WhenTaskExists() {

        // Mock the repository's behavior
        when(taskRepository.existsById(1L)).thenReturn(true);
        when(taskRepository.save(updatedTask)).thenReturn(updatedTask);


        Task result = taskService.updateTask(1L, updatedTask);


        assertNotNull(result, "The result should not be null");
        assertEquals(updatedTask.getTitle(), result.getTitle(), "The task title should be updated");
        assertEquals(updatedTask.getDescription(), result.getDescription(), "The task description should be updated");

        // Verify save is called with updatedTask
        verify(taskRepository, times(1)).save(updatedTask);
    }

    @Test
    void updateTask_WhenTaskDoesNotExist() {

        when(taskRepository.existsById(1L)).thenReturn(false);

        Task result = taskService.updateTask(1L, updatedTask);

        assertNull(result, "The result should be null because the task does not exist");

        verify(taskRepository, never()).save(updatedTask);
    }
    @Test
    void deleteTask_WhenTaskExists() {

        when(taskRepository.existsById(1L)).thenReturn(true);

        boolean result = taskService.deleteTask(1L);

        assertTrue(result);
        verify(taskRepository, times(1)).deleteById(1L);
    }
    @Test
    void deleteTask_WhenTaskDoesNotExist() {
        when(taskRepository.existsById(1L)).thenReturn(false);

        boolean result = taskService.deleteTask(1L);

        assertFalse(result);
        verify(taskRepository, never()).deleteById(1L);
    }
    @Test
    void updateStatusWithPriority_ShouldUpdateStatusForTasksWithPriority() {
        List<Task> tasks = Arrays.asList(task1, task2);
        when(taskRepository.findAll()).thenReturn(tasks);

        taskService.updateStatusWithPriority("Low");

        task2.setCompleted(true);
        task2.setStatus("Completed");

        assertEquals("High Priority", task1.getStatus());
        assertEquals("Completed", task2.getStatus());  // No change since priority is different
        verify(taskRepository, times(1)).save(task1);
    }

    @Test
    void hasPendingTasks_ShouldReturnTrue_WhenThereArePendingTasks() {

        List<Task> tasks = Arrays.asList(task1, task2);
        when(taskRepository.findAll()).thenReturn(tasks);


        boolean result = taskService.hasPendingTasks();


        assertTrue(result);
    }

    @Test
    void hasPendingTasks_ShouldReturnFalse_WhenThereAreNoPendingTasks() {

        task2.setCompleted(true);
        task2.setStatus("Completed");
        task1.setCompleted(true);
        task1.setStatus("Completed");
        List<Task> tasks = Arrays.asList(task1, task2);
        when(taskRepository.findAll()).thenReturn(tasks);

        // Act
        boolean result = taskService.hasPendingTasks();

        // Assert
        assertFalse(result);
    }

    @Test
    void getTasksByPriorityAndStatus_ShouldReturnFilteredTasks() {
        // Arrange
        List<Task> tasks = Arrays.asList(task1, task2);
        when(taskRepository.findAll()).thenReturn(tasks);

        // Act
        List<Task> result = taskService.getTasksByPriorityAndStatus("Low", "Pending");

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.contains(task1));
    }

}