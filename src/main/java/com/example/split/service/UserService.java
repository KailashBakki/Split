package com.example.split.service;

import com.example.split.dto.UserRequest;
import com.example.split.dto.UserResponse;
import com.example.split.entity.Transact;
import com.example.split.exception.UserNotFoundException;

import java.util.List;

public interface UserService {
    List<UserResponse> getAllUsers();
    UserResponse createUser(UserRequest userRequest); //throws UserExceptions.DuplicateUserException;
    UserResponse getUserById(Long userId);// throws UserExceptions.UserNotFoundException;
    String deleteUser(Long userId);

//    UserResponse updateUser(Long userId, UserResponse userResponse) throws UserNotFoundException;

    UserResponse updateUser(Long userId, UserRequest userRequest);
    List<Transact> getTransactionsForUser(Long userId);
    public Long getUserIdByEmail(String email);
}
