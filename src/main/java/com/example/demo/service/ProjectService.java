package com.example.demo.service;

import com.example.demo.model.Mail;
import com.example.demo.exception.AlreadySharedException;
import com.example.demo.exception.DataNotFoundException;
import com.example.demo.model.Project;
import com.example.demo.model.User;
import com.example.demo.repository.ProjectRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Optional;

@Service
@Transactional
public class ProjectService {

    private static final Logger logger = LogManager.getLogger(ProjectService.class);

    private final ProjectRepository projectRepository;

    private final UserService userService;

    private final MailService mailService;

    @Autowired
    public ProjectService(ProjectRepository projectRepository, UserService userService, MailService mailService) {
        this.projectRepository = projectRepository;
        this.userService = userService;
        this.mailService = mailService;
    }

    public Project save(Project project) {
        return projectRepository.save(project);
    }

    public Optional<Project> getById(int id) {
        return projectRepository.findById(id);
    }

    public Iterable<Project> getAll() {
        return projectRepository.findAll();
    }

    public Project update(Project project) {
        return projectRepository.save(project);
    }

    public void delete(Project project) {
        projectRepository.delete(project);
    }

    public void deleteById(int id) {
        projectRepository.deleteById(id);
    }

    public Iterable<Project> getAllByUserId(int id) {
        return projectRepository.findAllByUser(id);
    }

    public Project share(String credentials, int projectId) throws Exception {
        User user;
        if (credentials.contains("@")) {
            user = userService.getByEmail(credentials)
                    .orElseThrow(() -> new DataNotFoundException("User not found. Email: " + credentials));
        } else {
            user = userService.findByUsername(credentials)
                    .orElseThrow(() -> new DataNotFoundException("User not found. Username: " + credentials));
        }

        Project project = getById(projectId)
                .orElseThrow(() -> new DataNotFoundException("Project not found. Id: " + projectId));

        if (project.getUserList().contains(user)) {
            logger.error("Project already shared. Project name: {}", project.getName());
            throw new AlreadySharedException("Project already shared: " + project.getName());
        }

        project.setUserList(Collections.singletonList(user));
        mailService.sendEmail(createMail(user.getEmail(), project.getName()));

        return save(project);
    }

    private Mail createMail(String userEmail, String projectName) {
        Mail mail = new Mail();
        mail.setMailFrom("toDoApp@gmail.com");
        mail.setMailTo(userEmail);
        mail.setMailSubject("ToDo App");
        mail.setMailContent("Hi! New project was shared with you: " + projectName);

        return mail;
    }
}
