package com.example.demo.repository;

import com.example.demo.model.Project;
import com.example.demo.model.User;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@DataJpaTest
public class ProjectRepositoryTest {

    @Autowired
    private ProjectRepository projectRepository;

    @Test
    public void findAllProjectByUserId_thenReturnProject() {
        Project project = new Project();
        project.setId(2);
        project.setName("SQL Project");
        List<Project> projectIterable = (List<Project>) projectRepository.findAllByUser(1);

        assertEquals(projectIterable.get(0).getId(), project.getId());
        assertEquals(projectIterable.get(0).getName().toLowerCase(), project.getName().toLowerCase());
    }

}
