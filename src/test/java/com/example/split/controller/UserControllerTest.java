package com.example.split.controller;

import com.example.split.dto.UserRequest;
import com.example.split.dto.UserResponse;
import com.example.split.entity.Transact;
import com.example.split.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {
    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    @Test
    void createUser() {
        UserRequest userRequest = new UserRequest();
        UserResponse userResponse = new UserResponse();
        when(userService.createUser(userRequest)).thenReturn(userResponse);
        ResponseEntity<UserResponse> response = userController.createUser(userRequest);
        verify(userService, times(1)).createUser(userRequest);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(userResponse, response.getBody());
    }

    @Test
    void getAllUsers() {
        List<UserResponse> expectedUsers = Collections.singletonList(new UserResponse());
        when(userService.getAllUsers()).thenReturn(expectedUsers);
        ResponseEntity<List<UserResponse>> response = userController.getAllUsers();
        verify(userService, times(1)).getAllUsers();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedUsers, response.getBody());
    }

    @Test
    void getUserById() {
        Long userId = 1L;
        UserResponse expectedUser = new UserResponse();
        when(userService.getUserById(userId)).thenReturn(expectedUser);
        ResponseEntity<UserResponse> response = userController.getUserById(userId);
        verify(userService, times(1)).getUserById(userId);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedUser, response.getBody());
    }

    @Test
    void deleteUser() {
        Long userId = 1L;
        String expectedMessage = "User deleted successfully";
        when(userService.deleteUser(userId)).thenReturn(expectedMessage);
        ResponseEntity<String> response = userController.deleteUser(userId);
        verify(userService, times(1)).deleteUser(userId);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedMessage, response.getBody());
    }

    @Test
    void updateUser() {
        Long userId = 1L;
        UserRequest userRequest = new UserRequest();
        UserResponse expectedUserResponse = new UserResponse();
        when(userService.updateUser(userId, userRequest)).thenReturn(expectedUserResponse);
        ResponseEntity<UserResponse> response = userController.updateUser(userId, userRequest);
        verify(userService, times(1)).updateUser(userId, userRequest);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedUserResponse, response.getBody());
    }

    @Test
    void getTransactionsForUser() {
        Long userId = 1L;
        List<Transact> expectedTransacts = Arrays.asList(new Transact(), new Transact());
        when(userService.getTransactionsForUser(userId)).thenReturn(expectedTransacts);
        ResponseEntity<List<Transact>> response = userController.getTransactionsForUser(userId);
        verify(userService, times(1)).getTransactionsForUser(userId);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedTransacts, response.getBody());
    }

    @Test
    void getUserIdByEmailWhenUserExists() {
        // Given
        String mail = "test@example.com";
        Long userId = 1L;
        UserResponse expectedUserResponse = new UserResponse();
        expectedUserResponse.setUserId(userId);
        expectedUserResponse.setUsername("test_user"); // Set other fields in userResponse if needed

        when(userService.getUserIdByEmail(mail)).thenReturn(userId);
        when(userService.getUserById(userId)).thenReturn(expectedUserResponse);

        // When
        ResponseEntity<UserResponse> actualUserResponse = userController.getUserIdByEmail(mail);

        // Then
        assertEquals(expectedUserResponse, actualUserResponse.getBody());
        assertEquals(HttpStatus.OK, actualUserResponse.getStatusCode());
    }

    @Test
    void getUserIdByEmailWhenUserDoesNotExist() {
        // Given
        String mail = "test@example.com";

        when(userService.getUserIdByEmail(mail)).thenReturn(null);

        // When
        ResponseEntity<UserResponse> actualUserResponse = userController.getUserIdByEmail(mail);

        // Then
        assertEquals(HttpStatus.NOT_FOUND, actualUserResponse.getStatusCode());
    }
}