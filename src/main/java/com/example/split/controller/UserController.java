package com.example.split.controller;

import com.example.split.dto.UserRequest;
import com.example.split.dto.UserResponse;
import com.example.split.entity.Transact;
import com.example.split.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/split/users")
public class UserController {

    private final UserService userService;


    @PostMapping("/create")
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody UserRequest userRequest){
        return new ResponseEntity<UserResponse>(userService.createUser(userRequest), HttpStatus.CREATED);
    }

    @GetMapping("/all")
    public ResponseEntity<List<UserResponse>> getAllUsers(){
        return new ResponseEntity<>(userService.getAllUsers(), HttpStatus.OK);
    }

    @GetMapping("/get/{userId}")
    public ResponseEntity<UserResponse> getUserById(@Valid @PathVariable Long userId){
        return new ResponseEntity<>(userService.getUserById(userId),HttpStatus.OK);
    }

    @DeleteMapping("/delete/{userId}")
    public ResponseEntity<String> deleteUser(@Valid @PathVariable Long userId){
        return new ResponseEntity<>(userService.deleteUser(userId),HttpStatus.OK);
    }

    @PutMapping("/update/{userId}")
    public ResponseEntity<UserResponse> updateUser(@Valid @PathVariable Long userId, @Valid @RequestBody UserRequest userRequest){
        return new ResponseEntity<>(userService.updateUser(userId,userRequest), HttpStatus.OK);
    }

    @GetMapping("/getTransacts/{userId}")
    public ResponseEntity<List<Transact>> getTransactionsForUser(@Valid @PathVariable Long userId){
        return new ResponseEntity<>(userService.getTransactionsForUser(userId), HttpStatus.OK);
    }

    @GetMapping("/getUserId/{mail}")
    public ResponseEntity<UserResponse> getUserIdByEmail(@Valid @PathVariable String mail){
        Long userId = userService.getUserIdByEmail(mail);

        if (userId != null) {
            UserResponse userResponse = new UserResponse();
            userResponse.setUserId(userId);
            userResponse =  userService.getUserById(userId);
            // Set other fields in userResponse if needed

            return ResponseEntity.ok(userResponse);
        } else {
            // Handle the case where user was not found
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}
