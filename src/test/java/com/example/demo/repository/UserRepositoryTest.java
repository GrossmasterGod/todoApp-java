package com.example.demo.repository;

import com.example.demo.model.User;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@DataJpaTest
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    public void findByUsername_ThenReturnOptional() {

        Optional<User> userOptional = userRepository.findByUsername("test");

        assertEquals(4, userOptional.get().getId());
        assertNotNull(userOptional.get());
    }

    @Test
    public void findByUsernameAndPassword_ThenReturnOptional() {
        Optional<User> userOptional = userRepository.findByUsernameAndPassword("test", "$2a$10$UL0oHA21nLWCrUknMKamR.sstth5KgFA6qukhoNbRH4pKXPlLYDXO");

        assertEquals(4, userOptional.get().getId());
        assertNotNull(userOptional.get());
    }

    @Test
    public void findByEmail_ThenReturnOptional() {
        Optional<User> userOptional = userRepository.findByEmail("test@gmail.com");

        assertEquals(4, userOptional.get().getId());
        assertNotNull(userOptional.get());
    }
}
