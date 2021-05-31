package com.example.demo.controller;

import com.example.demo.exception.DataNotFoundException;
import com.example.demo.exception.ValidationException;
import com.example.demo.model.Project;
import com.example.demo.model.User;
import com.example.demo.service.ProjectService;
import com.example.demo.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Collections;

@RestController
@RequestMapping("/projects")
public class ProjectController {

    private static final Logger logger = LogManager.getLogger(ProjectController.class);

    private final ProjectService projectService;

    private final UserService userService;

    @Autowired
    public ProjectController(ProjectService projectService, UserService userService) {
        this.projectService = projectService;
        this.userService = userService;
    }

    @PostMapping(value = "/save")
    public ResponseEntity<Project> saveProject(@Valid @RequestBody Project project, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            logger.error("Project has errors {}", bindingResult.getFieldError().getField());
            throw new ValidationException(bindingResult.getFieldError().getField());
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findByUsername(authentication.getName()).orElseThrow(() -> {
            logger.error("User not found. Username {}", authentication.getName());
            return new DataNotFoundException("User not found. Username: " + authentication.getName());
        });

        project.setUserList(Collections.singletonList(user));
        return ResponseEntity.status(HttpStatus.CREATED).body(projectService.save(project));
    }


    @DeleteMapping(value = "/delete/{projectId}")
    public ResponseEntity<?> deleteProject(@PathVariable("projectId") int projectId) {
        projectService.deleteById(projectId);

        return ResponseEntity.ok().build();
    }

    @PutMapping(value = "/edit/{projectId}")
    public ResponseEntity<Project> updateProject(@ModelAttribute Project project) {
        return ResponseEntity.ok().body(projectService.update(project));
    }

    @GetMapping(value = "/list")
    public ResponseEntity<?> getAllProjectsByUserId() throws JsonProcessingException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findByUsername(authentication.getName()).orElseThrow(() -> {
            logger.error("User not found. Username {}", authentication.getName());
            return new DataNotFoundException("User not found. Username: " + authentication.getName());
        });

        return ResponseEntity.ok().body(projectService.getAllByUserId(user.getId()));
    }

    @GetMapping(value = "/{projectId}")
    public ResponseEntity<Project> getProject(@PathVariable("projectId") int projectId) {
        Project project = projectService.getById(projectId).orElseThrow(() -> {
            logger.error("Project not found. Id: {} ", projectId);
            return new DataNotFoundException("Project not found. Id: " + projectId);
        });

        return ResponseEntity.ok().body(project);
    }

    @GetMapping(value = "/getAll")
    public ResponseEntity<Iterable<Project>> getAllProjects() {
        return ResponseEntity.ok().body(projectService.getAll());
    }

    @PostMapping(value = "/share/{projectId}")
    public ResponseEntity<Project> shareProject(@PathVariable("projectId") int projectId,
                                                @RequestParam String credentials) throws Exception {
        return ResponseEntity.ok().body(projectService.share(credentials, projectId));
    }
}
