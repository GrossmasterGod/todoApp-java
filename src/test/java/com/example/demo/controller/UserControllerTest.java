package com.example.demo.controller;

import com.example.demo.model.User;
import com.example.demo.model.dto.UserDto;
import com.example.demo.security.details.UserDetailsImpl;
import com.example.demo.security.details.UserDetailsServiceImpl;
import com.example.demo.security.jwt.JwtTokenUtil;
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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.ArrayList;
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
public class UserControllerTest {

    ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    private List<User> userList;

    @MockBean
    private UserDetailsServiceImpl userDetailsService;

    @MockBean
    private JwtTokenUtil jwtTokenUtil;

    private final static String TEST_TOKEN = "Bearer token";


    @Before
    public void setup() {
        userList = new ArrayList<>();

        User user = new User();
        user.setUsername("test");
        user.setPassword("test");
        user.setEmail("test@gmail.com");

        userList.add(user);

        given(userService.getById(anyInt())).willReturn(Optional.of(user));
        given(userDetailsService.loadUserByUsername(anyString())).willReturn(new UserDetailsImpl(user));
        given(jwtTokenUtil.generateToken(new UserDetailsImpl(user))).willReturn("Bearer token");

    }

    @Test
    public void getAllUsers() throws Exception {
        given(userService.getAll()).willReturn(userList);

        String userListJson = objectMapper.writeValueAsString(userList);

        mockMvc.perform(MockMvcRequestBuilders.get("/users/getAll")
                .with(user("test").password("test"))
                .header(HttpHeaders.AUTHORIZATION, TEST_TOKEN))
                .andExpect(status().isOk())
                .andExpect(content().string(userListJson))
                .andDo(print());
    }

    @Test
    public void postRegisterUser() throws Exception {
        given(userService.save(any(UserDto.class))).willReturn(userList.get(0));
        given(bCryptPasswordEncoder.encode(anyString())).willReturn("password");

        String userJson = objectMapper.writeValueAsString(userList.get(0));

        mockMvc.perform(MockMvcRequestBuilders.post("/users/register")
                .with(user("test").password("test"))
                .header(HttpHeaders.AUTHORIZATION, TEST_TOKEN)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(userJson))
                .andExpect(status().isCreated())
                .andExpect(content().string(userJson))
                .andDo(print());


    }

    @Test
    public void getUserSubscribe() throws Exception {
        given(userService.findByUsername(anyString())).willReturn(Optional.of(userList.get(0)));
        String userJson = objectMapper.writeValueAsString(userList.get(0));


        mockMvc.perform(MockMvcRequestBuilders.get("/users/subscribe")
                .header(HttpHeaders.AUTHORIZATION, TEST_TOKEN)
                .with(user("test").password("test"))
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(userJson))
                .andExpect(status().isOk())
                .andExpect(content().string(userJson))
                .andDo(print());

    }

    @Test
    public void getUserById() throws Exception {
        given(userService.getById(anyInt())).willReturn(Optional.of(userList.get(0)));

        String userJson = objectMapper.writeValueAsString(userList.get(0));

        mockMvc.perform(MockMvcRequestBuilders.get("/users/get/1")
                .with(user("test").password("test"))
                .header(HttpHeaders.AUTHORIZATION, TEST_TOKEN))
                .andExpect(status().isFound())
                .andExpect(content().string(userJson))
                .andDo(print());
    }

    @Test
    public void putUpdateUser() throws Exception {
        given(userService.update(any(User.class))).willReturn(userList.get(0));

        String userJson = objectMapper.writeValueAsString(userList.get(0));

        mockMvc.perform(MockMvcRequestBuilders.put("/users/update/1")
                .with(user("test").password("test"))
                .header(HttpHeaders.AUTHORIZATION, TEST_TOKEN)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(userJson))
                .andExpect(status().isOk())
                .andExpect(content().string(userJson))
                .andDo(print());

    }

    @Test
    public void deleteUserById() throws Exception {
        willDoNothing().given(userService).deleteById(anyInt());

        mockMvc.perform(MockMvcRequestBuilders.delete("/users/delete/1")
                .with(user("test").password("test"))
                .header(HttpHeaders.AUTHORIZATION, TEST_TOKEN))
                .andExpect(status().isOk())
                .andDo(print());
    }
}
