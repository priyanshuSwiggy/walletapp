package com.swiggy.walletapp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.swiggy.walletapp.entity.Client;
import com.swiggy.walletapp.exception.InvalidCredentialsException;
import com.swiggy.walletapp.exception.UserAlreadyExistsException;
import com.swiggy.walletapp.service.ClientService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ClientControllerTest {

    public static final String CLIENT_REGISTER_URL = "/clients/register";
    public static final String CLIENT_LOGIN_URL = "/clients/login";
    public static final String REQ_PARAM_USERNAME = "username";
    public static final String REQ_PARAM_PASSWORD = "password";

    @Mock
    private ClientService clientService;

    @InjectMocks
    private ClientController clientController;

    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private Client client;

    @BeforeEach
    void setUp() {
        client = new Client("username", "password");
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(clientController).build();
    }

    @Test
    public void registerClient_Successfully_WhenValidInputFromClient() throws Exception {
        doNothing().when(clientService).register(client);

        mockMvc.perform(post(CLIENT_REGISTER_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(client)))
                .andExpect(status().isOk())
                .andExpect(content().string("Client registered successfully"));
    }

    @Test
    public void registerClient_Failure_WhenInvalidInputFromClient() throws Exception {
        doThrow(new UserAlreadyExistsException("User already exists")).when(clientService).register(client);

        mockMvc.perform(post(CLIENT_REGISTER_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(client)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("User already exists"));
    }

    @Test
    public void loginClient_Successfully_WhenValidCredentials() throws Exception {
        when(clientService.login("username", "password")).thenReturn(client);

        mockMvc.perform(post(CLIENT_LOGIN_URL)
                        .param(REQ_PARAM_USERNAME, "username")
                        .param(REQ_PARAM_PASSWORD, "password"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(client)));
    }

    @Test
    public void loginClient_Failure_WhenVInvalidCredentials() throws Exception {
        when(clientService.login("username", "password")).thenThrow(new InvalidCredentialsException("Invalid username or password"));

        mockMvc.perform(post(CLIENT_LOGIN_URL)
                        .param(REQ_PARAM_USERNAME, "username")
                        .param(REQ_PARAM_PASSWORD, "password"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string(""));

        assertThrows(InvalidCredentialsException.class, () -> clientService.login(REQ_PARAM_USERNAME, REQ_PARAM_PASSWORD));
    }
}