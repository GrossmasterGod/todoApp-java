package com.example.demo.repository;

import com.example.demo.model.Task;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@DataJpaTest
public class TaskRepositoryTest {

    @Autowired
    private TaskRepository taskRepository;

    @Test
    public void getAllByProjectId_ThenReturnTaskIterable() {

        List<Task> taskIterable = (List<Task>) taskRepository.getAllByProjectId(1);
        assertEquals(3, taskIterable.size());
        assertEquals(1, taskIterable.get(0).getId());
        assertEquals(4, taskIterable.get(1).getId());


    }

    @Test
    public void getAllByProjectIdAndActive_ThenReturnTaskIterable() {
        List<Task> taskList = (List<Task>) taskRepository.getAllByProjectIdAndActive(1, true);

        assertEquals(2, taskList.size());
        assertEquals(4 ,taskList.get(0).getId());
        assertEquals(5, taskList.get(1).getId());
    }

//    @Test
//    public void updatePriority() {
//        Task task = taskRepository.findById(1).orElseThrow(() -> new RuntimeException("Task not found"));
//        System.out.println(task);
//        taskRepository.updatePriority(2, 1);
//
//        List<Task> taskList = (List<Task>) taskRepository.findAll();
//        System.out.println(taskList);
//        assertEquals(task.getId(), taskList.get(0).getId());
//    }
}
