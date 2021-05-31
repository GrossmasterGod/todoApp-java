package com.example.demo.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
public class Project {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public int id;

    @NotEmpty
    public String name;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    public List<Task> taskList;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "USER_PROJECT",
            joinColumns = {@JoinColumn(name = "project_id")},
            inverseJoinColumns = {@JoinColumn(name = "user_id")})
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonIgnore
    private List<User> userList = new ArrayList<>();

    public void setTaskList(List<Task> taskList) {
        this.taskList = taskList;
        if(taskList != null) {
            this.taskList.addAll(taskList);
        }
    }

    public void setUserList(List<User> userList) {
        if(this.userList.isEmpty()) {
            this.userList = userList;
        } else {
            this.userList.addAll(userList);
        }
    }
}
