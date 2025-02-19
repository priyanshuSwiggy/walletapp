package com.swiggy.walletapp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.swiggy.walletapp.dto.UserDto;
import com.swiggy.walletapp.enums.Currency;
import com.swiggy.walletapp.exception.UserAlreadyExistsException;
import com.swiggy.walletapp.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class UserControllerTest {

    public static final String USER_REGISTER_URL = "/users";

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private UserDto userDto;

    @BeforeEach
    void setUp() {
        userDto = new UserDto("username", "password", Currency.INR);
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
    }

    @Test
    public void testRegisterUser_Successfully_WhenValidInputFromUser() throws Exception {
        doNothing().when(userService).register(userDto);

        mockMvc.perform(post(USER_REGISTER_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isCreated())
                .andExpect(content().string("User registered successfully"));
    }

    @Test
    public void testRegisterUser_Failure_WhenSameUserRegistersAgain() throws Exception {
        doThrow(new UserAlreadyExistsException("User already exists")).when(userService).register(userDto);

        mockMvc.perform(post(USER_REGISTER_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("User already exists"));
    }
}