package com.example.demo.controller;

import com.example.demo.model.Project;
import com.example.demo.model.Task;
import com.example.demo.model.User;
import com.example.demo.security.details.UserDetailsImpl;
import com.example.demo.security.details.UserDetailsServiceImpl;
import com.example.demo.security.jwt.JwtTokenUtil;
import com.example.demo.service.ProjectService;
import com.example.demo.service.TaskService;
import com.example.demo.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles(profiles = "dev")
public class TaskControllerTest {


    private ObjectMapper objectMapper = new ObjectMapper();
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TaskService taskService;

    @MockBean
    private ProjectService projectService;

    @MockBean
    private UserService userService;

    @MockBean
    private UserDetailsServiceImpl userDetailsService;

    @MockBean
    private JwtTokenUtil jwtTokenUtil;

    private final static String TEST_TOKEN = "Bearer token";

    private List<Task> taskList;


    @Before
    public void setup() {
        taskList = new ArrayList<>();

        Task task = new Task();
        task.setTitle("test title 1");
        task.setDescription("test description 1");
        task.setCreateDate(LocalDateTime.now());
        task.setActive(true);
        task.setPriority(1);
        task.setProject(new Project());

        Task task1 = new Task();
        task1.setTitle("test title 2");
        task1.setDescription("test description 2");
        task1.setCreateDate(LocalDateTime.now());
        task1.setActive(true);
        task1.setPriority(2);
        task1.setProject(new Project());

        taskList.add(task);
        taskList.add(task1);

        User user = new User();
        user.setPassword("password");
        user.setEmail("email");


        given(userService.getById(anyInt())).willReturn(Optional.of(user));
        given(userDetailsService.loadUserByUsername(anyString())).willReturn(new UserDetailsImpl(user));
        given(jwtTokenUtil.generateToken(new UserDetailsImpl(user))).willReturn("Bearer token");
    }

    @Test
    public void getTaskList() throws Exception {
        given(taskService.getTaskByProjectId(1)).willReturn(taskList);

        String taskListJson = objectMapper.writeValueAsString(taskList);

        mockMvc.perform(MockMvcRequestBuilders.get("/tasks/list/1")
                .with(user("test").password("test"))
                .accept(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, TEST_TOKEN))
                .andExpect(status().isFound())
                .andExpect(content().string(taskListJson))
                .andDo(print());
    }

    @Test
    public void postSaveTask() throws Exception {
        Project project = new Project();
        project.setId(1);
        project.setName("project name");

        given(projectService.getById(1)).willReturn(Optional.of(project));

        taskList.get(0).setProject(project);
        given(taskService.save(any(Task.class))).willReturn(taskList.get(0));

        String taskJson = objectMapper.writeValueAsString(taskList.get(0));
        mockMvc.perform(MockMvcRequestBuilders.post("/tasks/save/1")
                .with(user("test").password("test"))
                .header(HttpHeaders.AUTHORIZATION, TEST_TOKEN)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(taskList.get(0))))
                .andExpect(status().isOk())
                .andExpect(content().string(taskJson))
                .andDo(print());
    }

    @Test
    public void deleteTask() throws Exception {
        willDoNothing().given(projectService).deleteById(1);


        mockMvc.perform(MockMvcRequestBuilders.delete("/tasks/delete/1")
                .with(user("test").password("test"))
                .header(HttpHeaders.AUTHORIZATION, TEST_TOKEN))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    public void putUpdateTask() throws Exception {
        Project project = new Project();
        project.setId(2);
        project.setName("project name");
        given(projectService.getById(anyInt())).willReturn(Optional.of(project));

        given(taskService.update(any(Task.class))).willReturn(taskList.get(0));

        String taskJson = objectMapper.writeValueAsString(taskList.get(0));

        mockMvc.perform(MockMvcRequestBuilders.put("/tasks/update/1/projects/2")
                .with(user("test").password("test"))
                .header(HttpHeaders.AUTHORIZATION, TEST_TOKEN)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(taskJson))
                .andExpect(status().isOk())
                .andExpect(content().string(taskJson))
                .andDo(print());
    }

    @Test
    public void getTaskById() throws Exception {
        given(taskService.getById(1)).willReturn(Optional.ofNullable(taskList.get(0)));
        String taskJson = objectMapper.writeValueAsString(taskList.get(0));


        mockMvc.perform(MockMvcRequestBuilders.get("/tasks/1")
                .with(user("test").password("test"))
                .header(HttpHeaders.AUTHORIZATION, TEST_TOKEN))
                .andExpect(status().isFound())
                .andExpect(content().string(taskJson))
                .andDo(print());
    }

    @Test
    public void getAllTask() throws Exception {
        given(taskService.getAll()).willReturn(taskList);
        String taskListJson = objectMapper.writeValueAsString(taskList);


        mockMvc.perform(MockMvcRequestBuilders.get("/tasks/getAll")
                .with(user("test").password("test"))
                .header(HttpHeaders.AUTHORIZATION, TEST_TOKEN))
                .andExpect(status().isFound())
                .andExpect(content().string(taskListJson))
                .andDo(print());
    }

    @Test
    public void putUpdateTaskStatus() throws Exception {
        given(taskService.getById(anyInt())).willReturn(Optional.of(taskList.get(0)));
        given(taskService.update(any(Task.class))).willReturn(taskList.get(0));
        given(taskService.changeStatus(any(Task.class))).willReturn(taskList.get(0));

        String taskJson = objectMapper.writeValueAsString(taskList.get(0));

        mockMvc.perform(MockMvcRequestBuilders.put("/tasks/active/1")
                .with(user("test").password("test"))
                .header(HttpHeaders.AUTHORIZATION, TEST_TOKEN))
                .andExpect(status().isOk())
                .andExpect(content().string(taskJson))
                .andDo(print());
    }

    @Test
    public void getTaskByStatus() throws Exception {
        given(taskService.getTaskByProjectIdAndActive(1, true)).willReturn(taskList);
        String taskListJson = objectMapper.writeValueAsString(taskList);

        mockMvc.perform(MockMvcRequestBuilders.get("/tasks/active/1/1")
                .with(user("test").password("test"))
                .header(HttpHeaders.AUTHORIZATION, TEST_TOKEN)
                .param("isActive", "true"))
                .andExpect(status().isFound())
                .andExpect(content().string(taskListJson))
                .andDo(print());

    }

    @Test
    public void putUpdateTaskPriority() throws Exception {
        given(taskService.updatePriority(1, 1)).willReturn(1);

        mockMvc.perform(MockMvcRequestBuilders.put("/tasks/priority/{taskId}", 1)
                .with(user("test").password("test"))
                .header(HttpHeaders.AUTHORIZATION, TEST_TOKEN)
                .param("priority", "1"))
                .andExpect(status().isOk())
                .andDo(print());

    }


}
