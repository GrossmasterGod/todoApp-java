package com.example.demo.controller;

import com.example.demo.exception.DataNotFoundException;
import com.example.demo.exception.ValidationException;
import com.example.demo.model.Project;
import com.example.demo.model.Task;
import com.example.demo.model.dto.TaskDto;
import com.example.demo.service.ProjectService;
import com.example.demo.service.TaskService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/tasks")
public class TaskController {

    private static final Logger logger = LogManager.getLogger(TaskController.class);

    private final TaskService taskService;

    private final ProjectService projectService;

    @Autowired
    public TaskController(TaskService taskService, ProjectService projectService) {
        this.taskService = taskService;
        this.projectService = projectService;

    }

    @GetMapping(value = "/list/{projectId}")
    public ResponseEntity<Iterable<Task>> getTaskListByProjectId(@PathVariable("projectId") int projectId) {
        return ResponseEntity.ok().body(taskService.getTaskByProjectId(projectId));
    }

    @PostMapping(value = "/save/{projectId}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Task> saveTask(@Valid @RequestBody Task task, BindingResult bindingResult,
                                         @PathVariable("projectId") int projectId) {
        Project project = projectService.getById(projectId).orElseThrow(() -> {
            logger.error("Project not found. Id: {}", projectId);
            return new DataNotFoundException("Project not found. Id: " + projectId);
        });
        task.setProject(project);

        if (bindingResult.hasErrors()) {
            logger.error("Validation exception. Field: {}", bindingResult.getFieldError().getField());
            throw new ValidationException(bindingResult.getFieldError().getField());
        }

        return ResponseEntity.ok().body(taskService.save(task));
    }

    @DeleteMapping(value = "/delete/{taskId}")
    public ResponseEntity<?> deleteTask(@PathVariable("taskId") int taskId) {
        taskService.deleteById(taskId);

        return ResponseEntity.ok().build();
    }

    @PostMapping(value = "/update/{taskId}/projects/{projectId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Task> updateTask(@RequestBody TaskDto taskDto, @PathVariable("taskId") int taskId,
                                           @PathVariable("projectId") int projectId) {
        Project project = projectService.getById(projectId).orElseThrow(() -> {
            logger.error("Project not found. Id: {}", projectId);
            return new DataNotFoundException("Project not found. Id: " + projectId);
        });

        taskDto.setId(taskId);
        taskDto.setProject(project);
        return ResponseEntity.ok().body(taskService.updateDto(taskDto));
    }

    @GetMapping(value = "/{taskId}")
    public ResponseEntity<Task> getTaskById(@PathVariable("taskId") int taskId) {
        Task task = taskService.getById(taskId).orElseThrow(() -> {
            logger.error("Task not found. Id: {}", taskId);
            return new DataNotFoundException("Task not found. Id: " + taskId);
        });

        return ResponseEntity.ok().body(task);
    }

    @GetMapping(value = "/getAll")
    public ResponseEntity<Iterable<Task>> getAllTasks() {
        return ResponseEntity.ok().body(taskService.getAll());
    }

    @PutMapping(value = "/active/{taskId}")
    public ResponseEntity<Task> updateTaskStatus(@PathVariable("taskId") int taskId) {
        Task task = taskService.getById(taskId).orElseThrow(() -> {
            logger.error("Task not found. Id: {}", taskId);
            return new DataNotFoundException("Task not found. Id: " + taskId);
        });

        task = taskService.changeStatus(task);
        return ResponseEntity.ok().body(task);
    }

    @GetMapping(value = "/active/{taskId}/{projectId}")
    public ResponseEntity<Iterable<Task>> getTaskByStatus(@RequestParam("isActive") boolean isActive,
                                                          @PathVariable("projectId") int projectId) {

        return ResponseEntity.ok().body(taskService.getTaskByProjectIdAndActive(projectId, isActive));
    }

    @PutMapping(value = "/priority/{taskId}")
    public ResponseEntity<?> updateTaskPriority(@RequestParam("priority") int priority,
                                                @PathVariable("taskId") int taskId) {
        taskService.updatePriority(priority, taskId);

        return ResponseEntity.ok().build();
    }

}
