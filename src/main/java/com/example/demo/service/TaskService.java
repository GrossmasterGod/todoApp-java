package com.example.demo.service;

import com.example.demo.mapper.TaskMapper;
import com.example.demo.model.Task;
import com.example.demo.model.dto.TaskDto;
import com.example.demo.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Optional;

@Service
@Transactional
public class TaskService {

    private final TaskRepository taskRepository;

    private final TaskMapper taskMapper;

    @Autowired
    public TaskService(TaskRepository taskRepository, TaskMapper taskMapper) {
        this.taskRepository = taskRepository;
        this.taskMapper = taskMapper;
    }

    public Task save(Task task) {
        if(task.getId() > 0) {
            task.setId(0);
            return taskRepository.save(task);
        }

        task.setActive(true);
        task.setCreateDate(LocalDateTime.now());

        if(task.getPriority() == 0) {
            task.setPriority(1);
        }

        return taskRepository.save(task);
    }

    public Optional<Task> getById(int id) {
        return taskRepository.findById(id);
    }

    public Iterable<Task> getAll() {
        return taskRepository.findAll();
    }

    public Task update(Task task) {
        return taskRepository.save(task);
    }

    public void delete(Task task) {
        taskRepository.delete(task);
    }

    public void deleteById(int id) {
        taskRepository.deleteById(id);
    }

    public Iterable<Task> getTaskByProjectId(int id) {
       return taskRepository.getAllByProjectId(id);
    }

    public Iterable<Task> getTaskByProjectIdAndActive(int projectId, boolean active) {
        return taskRepository.getAllByProjectIdAndActive(projectId, active);
    }

    public Task changeStatus(Task task) {
        task.setActive(!task.isActive());
        update(task);

        return task;
    }

    public Task updateDto(TaskDto taskDto) {
        Task currentTask = taskMapper.task(taskDto);

        if(taskDto.getFinishDateAsDate() != null) {
            currentTask.setFinishDate(taskDto.getFinishDateAsDate());
        }
        if(taskDto.getCreateDateAsDate() != null) {
            currentTask.setCreateDate(taskDto.getCreateDateAsDate());
        }

        return update(currentTask);
    }

    public Integer updatePriority(int priority, int taskId) {
        return taskRepository.updatePriority(priority, taskId);
    }
}
