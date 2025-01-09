package com.taskmanager.ts.service;

import com.taskmanager.ts.model.Task;
import com.taskmanager.ts.repository.TaskRepository;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
@RequiredArgsConstructor
@Service
public class TaskService {

    @Autowired
    private TaskRepository taskRepository;

    public List<Task> getAllTasks() {

        return taskRepository.findAll();
    }

    public Optional<Task> getTaskById(Long id) {

        return taskRepository.findById(id);
    }

    public Task createTask(Task task) {

        return taskRepository.save(task);
    }

    public Task updateTask(Long id, Task task) {
        if (taskRepository.existsById(id)) {
            task.setId(id);
            return taskRepository.save(task);
        }
        return null;
    }

    public boolean deleteTask(Long id) {
        if (taskRepository.existsById(id)) {
            taskRepository.deleteById(id);
            return true;
        }
        return false;
    }

    //using stream APIs
    public void updateStatusWithPriority(String priority) {
        List<Task> tasks = taskRepository.findAll();

        tasks.forEach(task -> {
            if (task.getPriority().equals(priority)) {
                task.setStatus("High Priority");
                taskRepository.save(task);
            }
        });
    }
    public boolean hasPendingTasks() {
        return taskRepository.findAll().stream()
                .anyMatch(task -> task.getStatus().equals("Pending"));
    }

    /**
     *
     * @param priority
     * @param status
     * @return
     */
    public List<Task> getTasksByPriorityAndStatus(String priority, String status) {
        return taskRepository.findAll().stream()
                .filter(task -> task.getPriority().equals(priority) && task.getStatus().equals(status))
                .collect(Collectors.toList());
    }
}