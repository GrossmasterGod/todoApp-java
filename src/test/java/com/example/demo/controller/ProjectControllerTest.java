package com.example.demo.controller;

import com.example.demo.model.Project;
import com.example.demo.model.Task;
import com.example.demo.model.User;
import com.example.demo.security.details.UserDetailsImpl;
import com.example.demo.security.details.UserDetailsServiceImpl;
import com.example.demo.security.jwt.JwtTokenUtil;
import com.example.demo.service.ProjectService;
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
import java.util.*;

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
public class ProjectControllerTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProjectService projectService;


    @MockBean
    private UserService userService;

    @MockBean
    private UserDetailsServiceImpl userDetailsService;

    @MockBean
    private JwtTokenUtil jwtTokenUtil;

    private List<Project> projectList;

    private User user;

    private final static String TEST_TOKEN = "Bearer token";

    @Before
    public void setup() {
        projectList = new ArrayList<>();

        Project project = new Project();
        project.setName("Test project");

        List<Task> taskList = new ArrayList<>();
        Task task = new Task();
        task.setTitle("title");
        task.setCreateDate(LocalDateTime.now());
        task.setActive(false);
        task.setPriority(1);
        task.setProject(project);
        taskList.add(task);
        project.setTaskList(taskList);
        Project project1 = new Project();
        project.setName("Test project 2");

        projectList.add(project);
        projectList.add(project1);

        user = new User();
        user.setPassword("password");
        user.setEmail("email");
        Set<Project> projectSet = new HashSet<>();
        projectSet.add(projectList.get(0));
        user.setProjectSet(projectSet);

        given(userService.getById(anyInt())).willReturn(Optional.of(user));
        given(userDetailsService.loadUserByUsername(anyString())).willReturn(new UserDetailsImpl(user));
        given(jwtTokenUtil.generateToken(new UserDetailsImpl(user))).willReturn("Bearer token");


    }

    @Test
    public void postProjectSave() throws Exception {
        given(userService.findByUsername(anyString())).willReturn(Optional.of(user));
        given(projectService.save(any(Project.class))).willReturn(projectList.get(0));

        String projectJson = objectMapper.writeValueAsString(projectList.get(0));


        mockMvc.perform(MockMvcRequestBuilders.post("/projects/save")
                .with(user("test").password("test"))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(projectJson)
                .header(HttpHeaders.AUTHORIZATION, TEST_TOKEN))
                .andExpect(status().isCreated())
                .andExpect(content().string(projectJson))
                .andDo(print());
    }

    @Test
    public void deleteProject() throws Exception {
        willDoNothing().given(projectService).deleteById(anyInt());

        mockMvc.perform(MockMvcRequestBuilders.delete("/projects/delete/1")
                .with(user("test").password("test"))
                .header(HttpHeaders.AUTHORIZATION, TEST_TOKEN))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    public void putProjectUpdate() throws Exception {
        given(projectService.update(any(Project.class))).willReturn(projectList.get(0));

        String testResponse = objectMapper.writeValueAsString(projectList.get(0));

        mockMvc.perform(MockMvcRequestBuilders.put("/projects/edit/1")
                .with(user("test").password("test"))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(testResponse)
                .header(HttpHeaders.AUTHORIZATION, TEST_TOKEN))
                .andExpect(status().isOk())
                .andExpect(content().json(testResponse))
                .andDo(print());

    }

    @Test
    public void getProjectList() throws Exception {
        given(userService.findByUsername(anyString())).willReturn(Optional.of(user));
        given(projectService.getAllByUserId(anyInt())).willReturn(projectList);

        String projectListJson = objectMapper.writeValueAsString(projectList);

        mockMvc.perform(MockMvcRequestBuilders.get("/projects/list")
                .with(user("test").password("test"))
                .header(HttpHeaders.AUTHORIZATION, TEST_TOKEN))
                .andExpect(status().isOk())
                .andExpect(content().string(projectListJson))
                .andDo(print());

    }

    @Test
    public void getProject() throws Exception {
        given(projectService.getById(anyInt())).willReturn(Optional.of(projectList.get(0)));
        String projectJson = objectMapper.writeValueAsString(projectList.get(0));


        mockMvc.perform(MockMvcRequestBuilders.get("/projects/1")
                .with(user("test").password("test"))
                .header(HttpHeaders.AUTHORIZATION, TEST_TOKEN))
                .andExpect(status().isOk())
                .andExpect(content().string(projectJson))
                .andDo(print());
    }

    @Test
    public void getAllProjects() throws Exception {
        given(projectService.getAll()).willReturn(projectList);
        String projectListJson = objectMapper.writeValueAsString(projectList);


        mockMvc.perform(MockMvcRequestBuilders.get("/projects/getAll")
                .with(user("test").password("test"))
                .header(HttpHeaders.AUTHORIZATION, TEST_TOKEN))
                .andExpect(status().isOk())
                .andExpect(content().string(projectListJson))
                .andDo(print());
    }

    @Test
    public void postShareProjects() throws Exception {
        given(projectService.share(anyString(), anyInt())).willReturn(projectList.get(0));
        String projectJson = objectMapper.writeValueAsString(projectList.get(0));


        mockMvc.perform(MockMvcRequestBuilders.post("/projects/share/1")
                .with(user("test").password("test"))
                .header(HttpHeaders.AUTHORIZATION, TEST_TOKEN)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .param("credentials", "credentials"))
                .andExpect(status().isOk())
                .andExpect(content().string(projectJson))
                .andDo(print());
    }
}
