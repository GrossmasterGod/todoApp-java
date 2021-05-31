package com.example.demo.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity(name = "users")
@Data
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotEmpty
    private String username;

    @NotEmpty
    private String password;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "registration_date")
    private Date registrationDate;

    @Column(name = "last_update")
    private Date updateDate;

    @NotEmpty
    @Email
    private String email;

    @ManyToMany(mappedBy = "userList")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Set<Project> projectSet = new HashSet<>();

    @PrePersist
    private void onCreate() {
        registrationDate = new Date();
    }

    @PreUpdate
    private void onUpdate() {
        updateDate = new Date();
    }
}

