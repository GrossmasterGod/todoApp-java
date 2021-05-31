package com.example.demo.repository;

import com.example.demo.model.Task;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskRepository extends CrudRepository<Task, Integer> {

    @Query(value = "select * from task where task.project_id = ?", nativeQuery = true)
    Iterable<Task> getAllByProjectId(int id);

    @Query(value = "select * from task where task.project_id = ? AND task.active = ?", nativeQuery = true)
    Iterable<Task> getAllByProjectIdAndActive(int projectId, boolean active);

    @Modifying
    @Query(value = "update task set task.priority = ? where task.id = ?", nativeQuery=true)
    Integer updatePriority(int priority, int taskId);

}
