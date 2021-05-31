package com.example.demo.controller;

import com.example.demo.model.User;
import com.example.demo.security.details.UserDetailsImpl;
import com.example.demo.security.details.UserDetailsServiceImpl;
import com.example.demo.security.jwt.JwtTokenRequest;
import com.example.demo.security.jwt.JwtTokenResponse;
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
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

import static org.mockito.BDDMockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles(profiles = "dev")
public class JwtAuthRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    AuthenticationManager authenticationManager;

    @MockBean
    private UserService userService;


    @MockBean
    private JwtTokenResponse jwtTokenResponse;

    @MockBean
    private HttpServletRequest httpServletRequest;


    @MockBean
    private UserDetailsServiceImpl userDetailsService;

    @MockBean
    private JwtTokenUtil jwtTokenUtil;

    private User user;

    private final static String TEST_TOKEN = "Bearer token";

    @Before
    public void setup() {
        user = new User();
        user.setPassword("password");
        user.setEmail("email");

    }

    @Test
    public void testPostCreateToken() throws Exception {
        given(userDetailsService.loadUserByUsername(anyString())).willReturn(new UserDetailsImpl(user));
        given(jwtTokenUtil.generateToken(any(UserDetailsImpl.class))).willReturn(TEST_TOKEN);
        ObjectMapper objectMapper = new ObjectMapper();

        JwtTokenRequest jwtTokenRequest = new JwtTokenRequest();
        jwtTokenRequest.setPassword("password");
        jwtTokenRequest.setUsername("username");
        String jwtTokenRequestJson = objectMapper.writeValueAsString(jwtTokenRequest);
        willDoNothing().given(jwtTokenResponse).setToken(TEST_TOKEN);
        given(jwtTokenResponse.getToken()).willReturn(TEST_TOKEN);
        mockMvc.perform(MockMvcRequestBuilders.post("/authenticate")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jwtTokenRequestJson))
                .andExpect(status().isOk())
                .andExpect(content().string(TEST_TOKEN))
                .andDo(print());


    }

    @Test
    public void getRefreshAndGetAuthenticationToken() throws Exception {
        given(userService.getById(anyInt())).willReturn(Optional.of(user));
        given(userDetailsService.loadUserByUsername(anyString())).willReturn(new UserDetailsImpl(user));
        given(jwtTokenUtil.generateToken(new UserDetailsImpl(user))).willReturn("Bearer token");

        given(httpServletRequest.getHeader(anyString())).willReturn(TEST_TOKEN);

        mockMvc.perform(MockMvcRequestBuilders.get("/refresh")
                .with(user("test").password("test"))
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, TEST_TOKEN))
                .andExpect(status().isOk())
                .andExpect(content().string(TEST_TOKEN))
                .andDo(print());

    }

    @Test
    public void getRefreshAndGetAuthenticationToken_Exception() throws Exception {
        given(userService.getById(anyInt())).willReturn(Optional.of(user));
        given(userDetailsService.loadUserByUsername(anyString())).willReturn(new UserDetailsImpl(user));
        given(jwtTokenUtil.generateToken(new UserDetailsImpl(user))).willReturn("Bearer token");

        given(httpServletRequest.getHeader(anyString())).willReturn(TEST_TOKEN);
        given(jwtTokenUtil.refreshToken(anyString())).willReturn(TEST_TOKEN);

        mockMvc.perform(MockMvcRequestBuilders.get("/refresh")
                .with(user("test").password("test"))
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, TEST_TOKEN))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(""))
                .andDo(print());
    }
}
