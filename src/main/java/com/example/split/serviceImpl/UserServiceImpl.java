package com.example.split.serviceImpl;

import com.example.split.dto.UserRequest;
import com.example.split.dto.UserResponse;
import com.example.split.entity.Transact;
import com.example.split.entity.User;
import com.example.split.exception.DuplicateUserException;
import com.example.split.exception.UserNotFoundException;
import com.example.split.repo.UserRepository;
import com.example.split.service.UserService;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.annotation.Validated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
@Service
public class UserServiceImpl implements UserService {
    public static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    private ModelMapper mapper;
    @Autowired
    private UserRepository userRepo;

    @Override
    public List<UserResponse> getAllUsers() {
        logger.info("getting details of all users.");
        List<User> users = userRepo.findAll();
        return users.stream()
                .map(user -> mapper.map(user,UserResponse.class))
                .collect(Collectors.toList());
    }

    @Override
    @Validated
    public UserResponse createUser(UserRequest userRequest) {
        if (!userRepo.existsByEmail(userRequest.getEmail())) {
            User user = mapper.map(userRequest, User.class);
            User savedUser = userRepo.save(user);
            logger.info("User created.");
            return mapper.map(savedUser, UserResponse.class);
        } else {
            logger.error("DuplicateUserException while creating user.");
            throw new DuplicateUserException("User with username " + userRequest.getUsername() + " already exists.");
        }
    }

    @Override
    public UserResponse getUserById(Long userId) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User with id " + userId + " not found."));
        logger.info("Finding user by ID.");
        return mapper.map(user, UserResponse.class);
    }

    @Override
    public String deleteUser(Long userId) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User with id " + userId + " not found."));
        userRepo.delete(user);
        logger.info("Deleting user by userID");
        return "User Successfully deleted!!!";
    }


    @Override
    public UserResponse updateUser(Long userId, UserRequest userRequest) {
        if (!userRepo.existsByEmail(userRequest.getEmail())) {
            User user = userRepo.findById(userId)
                    .orElseThrow(() -> new UserNotFoundException("User with id " + userId + " not found."));
            logger.info("Finding user By email");
            mapper.map(userRequest, user);
            user.setEmail(userRequest.getEmail());
            user.setUsername(userRequest.getUsername());
            User savedUser = userRepo.save(user);
            logger.info("User updated");
            return mapper.map(savedUser, UserResponse.class);
        } else {
            logger.error("DuplicateUserException while updating user");
            throw new DuplicateUserException("User with username " + userRequest.getUsername() + " already exists.");
        }
    }

    @Override
    public List<Transact> getTransactionsForUser(Long userId) {
        Optional<User> userOptional = userRepo.findById(userId);

        if(userOptional.isPresent()){
            User user = userOptional.get();
            List<Transact> payer = user.getTransactionsAsPayer();//debit
            List<Transact> payee = user.getTransactionsAsPayee();//credit

            List<Transact> allTransact = new ArrayList<>();
            allTransact.addAll(payee);
            allTransact.addAll(payer);
            logger.info("fetching transactions of specific user");
            return allTransact;
        }else {
            logger.error("UserNotFoundException while getting transactions of specific user");
            throw new UserNotFoundException("User not found with ID: " + userId);
        }
    }
    public Long getUserIdByEmail(String email) {
        Optional<User> userOptional = userRepo.findByEmail(email);
        return userOptional.map(User::getUserId).orElse(null);
    }
}
