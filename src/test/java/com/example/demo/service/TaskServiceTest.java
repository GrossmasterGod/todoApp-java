package com.example.demo.service;

import com.example.demo.model.Project;
import com.example.demo.model.Task;
import com.example.demo.model.User;
import com.example.demo.repository.TaskRepository;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Date;

import static org.junit.Assert.*;
import static org.mockito.BDDMockito.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = TaskService.class)
public class TaskServiceTest {

    @MockBean
    private TaskRepository taskRepository;

    @MockBean
    private UserService userService;

    @InjectMocks
    @Autowired
    private TaskService taskService;

    private Task task;

    private User user;

    @Before
    public void setup() {
        user = new User();
        user.setUsername("username");
        user.setPassword("password");
        user.setEmail("email");


        Project project = new Project();
        project.setId(1);
        project.setName("Project name");
        project.setUserList(Collections.singletonList(user));

        task = new Task();
        task.setTitle("test title 1");
        task.setDescription("test description 1");
        task.setActive(true);
        task.setPriority(2);
        task.setProject(project);
        task.setCreateDate(LocalDateTime.now());
    }

    @Test
    public void testTaskSave_Priority2() {
        given(taskRepository.save(any(Task.class))).willReturn(task);

        Task savedTask = taskService.save(task);

        assertEquals(2, savedTask.getPriority());
        assertNotNull(task.getCreateDate());
    }


    @Test
    public void testTaskSave_Priority0_WillChange1() {
        task.setPriority(0);
        given(taskRepository.save(any(Task.class))).willReturn(task);

        Task savedTask = taskService.save(task);

        assertEquals(1, savedTask.getPriority());
        assertNotNull(task.getCreateDate());
    }

    @Test
    public void testChangeStatus() {
        given(taskRepository.save(any(Task.class))).willReturn(task);
        Task changedStatusTask = taskService.changeStatus(task);
        changedStatusTask.setActive(!changedStatusTask.isActive());
        assertFalse(changedStatusTask.isActive());
        verify(taskRepository, times(1)).save(task);
    }

    @Test
    public void testUpdatePriority() {
        given(taskRepository.updatePriority(anyInt(), anyInt())).willReturn(1);

        taskService.updatePriority(3, 2);

        verify(taskRepository, times(1)).updatePriority(3, 2);

    }

}
