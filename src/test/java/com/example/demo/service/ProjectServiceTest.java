package com.example.demo.service;

import com.example.demo.exception.AlreadySharedException;
import com.example.demo.model.Project;
import com.example.demo.model.User;
import com.example.demo.repository.ProjectRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.mockito.BDDMockito.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = ProjectService.class)
public class ProjectServiceTest {

    @MockBean
    private ProjectRepository projectRepository;

    @MockBean
    private UserService userService;

    @InjectMocks
    @Autowired
    private ProjectService projectService;

    private Project project;

    private User user;

    @Before
    public void setup() {
        user = new User();
        user.setUsername("username");
        user.setPassword("password");
        user.setEmail("email");


        project = new Project();
        project.setId(1);
        project.setName("Project name");


    }

    @Test
    public void testSave() {
        given(projectRepository.save(any(Project.class))).willReturn(project);

        Project savedProject = projectService.save(project);

        assertEquals(savedProject.getId(), project.getId());
    }

    @Test
    public void testGetById() {
        given(projectRepository.findById(anyInt())).willReturn(Optional.of(project));

        Optional<Project> optionalFoundedProject = projectService.getById(project.getId());

        assertEquals(optionalFoundedProject.get().getId(), project.getId());
    }

    @Test
    public void testGetAll() {
        given(projectRepository.findAll()).willReturn(Collections.singletonList(project));

        List<Project> projectList = (List<Project>) projectService.getAll();

        assertEquals(projectList.size(), 1);
        assertEquals(projectList.get(0).getId(), project.getId());
    }

    @Test
    public void update() {

        given(projectRepository.save(any(Project.class))).willReturn(project);
        project.setName("updated project");

        Project updatedProject = projectService.update(project);

        assertEquals(updatedProject.getId(), project.getId());
        assertEquals(updatedProject.getName(), project.getName());
    }

    @Test
    public void testDelete() {
        willDoNothing().given(projectRepository).delete(any(Project.class));

        projectService.delete(project);

    }

    @Test
    public void testDeleteById() {
        willDoNothing().given(projectRepository).deleteById(anyInt());

        projectService.deleteById(1);
    }

    @Test
    public void testGetAllByUserId() {
        given(projectRepository.findAllByUser(anyInt())).willReturn(Collections.singletonList(project));

        List<Project> projectList = (List<Project>) projectService.getAllByUserId(1);

        assertEquals(projectList.size(), 1);
        assertEquals(projectList.get(0).getId(), project.getId());
    }

    @Test
    public void shareCredentialsEmail() throws Exception {
        given(userService.getByEmail(anyString())).willReturn(Optional.of(user));
        given(projectRepository.findById(anyInt())).willReturn(Optional.of(project));
        given(projectService.save(any(Project.class))).willReturn(project);

        Project sharedProject = projectService.share("mail@gmail.com", 1);

        verify(projectRepository, times(1)).findById(1);
        verify(userService, times(1)).getByEmail("mail@gmail.com");
        assertEquals(sharedProject, project);
    }

    @Test
    public void shareCredentialsUsername() throws Exception {
        given(userService.findByUsername(anyString())).willReturn(Optional.of(user));
        given(projectRepository.findById(anyInt())).willReturn(Optional.of(project));
        given(projectRepository.save(any(Project.class))).willReturn(project);

        Project sharedProject = projectService.share("firstUser", 1);

        verify(userService, times(1)).findByUsername("firstUser");
        assertEquals(sharedProject, project);
    }

    @Test(expected = AlreadySharedException.class)
    public void shareProjectCredentialsUsername_ProjectAlreadyShared() throws Exception {
        project.setUserList(Collections.singletonList(user));
        given(userService.findByUsername(anyString())).willReturn(Optional.of(user));
        given(projectRepository.findById(anyInt())).willReturn(Optional.of(project));
        given(projectRepository.save(any(Project.class))).willReturn(project);

        projectService.share("firstUser", 1);

    }


}
