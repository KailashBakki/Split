package com.example.split.serviceImpl;

import com.example.split.dto.UserRequest;
import com.example.split.dto.UserResponse;
import com.example.split.entity.Transact;
import com.example.split.entity.User;
import com.example.split.exception.DuplicateUserException;
import com.example.split.exception.UserNotFoundException;
import com.example.split.repo.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

//@ContextConfiguration(classes = {UserServiceImpl.class})
@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {
    @Mock
    private UserRepository userRepo;

    @Mock
    private ModelMapper mapper;

    @Mock
    private User user;

    @Mock
    private UserResponse userResponse;
    @Mock
    private UserRequest userRequest;

    @InjectMocks
//    @Autowired
    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getAllUsers() {
        List<User> users = new ArrayList<>();
        when(userRepo.findAll()).thenReturn(users);
        List<UserResponse> actual = userService.getAllUsers();
        assertNotNull(actual);
        assertEquals(users.size(), actual.size());
        verify(userRepo, times(1)).findAll();
    }

    @Test
    void createUser() {
        when(userRepo.existsByEmail(userRequest.getEmail())).thenReturn(false);
        when(mapper.map(userRequest, User.class)).thenReturn(user);
        when(userRepo.save((user))).thenReturn(user);
        when(mapper.map(user, UserResponse.class)).thenReturn(userResponse);
        UserResponse actual = userService.createUser(userRequest);
        assertEquals(userResponse, actual);
        verify(userRepo).save(user);
    }

    @Test
    void createUser_DuplicateUser(){
        when(userRepo.existsByEmail(Mockito.<String>any())).thenReturn(true);
        assertThrows(DuplicateUserException.class,
                () -> userService.createUser(new UserRequest("janedoe", "jane.doe@example.org")));
        verify(userRepo).existsByEmail(Mockito.<String>any());
    }

    @Test
    void getUserById_UserFound() {
        Long userId = 1L;
        when(userRepo.findById(userId)).thenReturn(Optional.of(user));
        when(mapper.map(user, UserResponse.class)).thenReturn(userResponse);
        UserResponse actual = userService.getUserById(userId);
        assertEquals(userResponse, actual);//.getUsername(), user.getUsername());
        verify(userRepo, times(1)).findById(userId);
        verify(mapper, times(1)).map(user, UserResponse.class);
    }

    @Test
    void getUserById_UserNotFound() {
        Long userId = 1L;

        // Mocking repository behavior for user not found
        when(userRepo.findById(userId)).thenReturn(Optional.empty());

        // Call the service method and expect an exception
        UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> {
            userService.getUserById(userId);
        });

        // Verify
        assertEquals("User with id " + userId + " not found.", exception.getMessage());
        verify(userRepo, times(1)).findById(userId);
        verify(mapper, never()).map(User.class, UserResponse.class);
    }


    @Test
    void deleteUser_UserFound() {
        Long userId = 1L;
        User user = new User(/* user details */);
        when(userRepo.findById(userId)).thenReturn(Optional.of(user));
        String result = userService.deleteUser(userId);
        verify(userRepo, times(1)).delete(user);
        assertEquals("User Successfully deleted!!!", result);
    }

    @Test
    void deleteUser_UserNotFound() {
        Long userId = 1L;
        when(userRepo.findById(userId)).thenReturn(Optional.empty());
        UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> {
            userService.deleteUser(userId);
        });
        assertEquals("User with id " + userId + " not found.", exception.getMessage());
        verify(userRepo, never()).delete(any(User.class));
    }


    @Test
    void updateUser_DuplicateUser() {
        when(userRepo.existsByEmail(Mockito.<String>any())).thenReturn(true);
        assertThrows(DuplicateUserException.class,
                () -> userService.updateUser(1L, new UserRequest("janedoe", "jane.doe@example.org")));
        verify(userRepo).existsByEmail(Mockito.<String>any());
    }

    @Test
    void updateUser() {
        when(mapper.map(Mockito.<Object>any(), Mockito.<Class<UserResponse>>any()))
                .thenReturn(UserResponse.builder().email("jane.doe@example.org").userId(1L).username("janedoe").build());
        doNothing().when(mapper).map(Mockito.<Object>any(), Mockito.<Object>any());

        User user = new User();
        user.setActive(true);
        user.setEmail("jane.doe@example.org");
        user.setTransactionsAsPayee(new ArrayList<>());
        user.setTransactionsAsPayer(new ArrayList<>());
        user.setUserId(1L);
        user.setUsername("janedoe");
        Optional<User> ofResult = Optional.of(user);

        User user2 = new User();
        user2.setActive(true);
        user2.setEmail("jane.doe@example.org");
        user2.setTransactionsAsPayee(new ArrayList<>());
        user2.setTransactionsAsPayer(new ArrayList<>());
        user2.setUserId(1L);
        user2.setUsername("janedoe");
        when(userRepo.save(Mockito.<User>any())).thenReturn(user2);
        when(userRepo.existsByEmail(Mockito.<String>any())).thenReturn(false);
        when(userRepo.findById(Mockito.<Long>any())).thenReturn(ofResult);
        userService.updateUser(1L, new UserRequest("janedoe", "jane.doe@example.org"));
        verify(mapper).map(Mockito.<Object>any(), Mockito.<Class<UserResponse>>any());
        verify(mapper).map(Mockito.<Object>any(), Mockito.<Object>any());
        verify(userRepo).existsByEmail(Mockito.<String>any());
        verify(userRepo).save(Mockito.<User>any());
        verify(userRepo).findById(Mockito.<Long>any());
    }

//    /**
//     * Method under test: {@link UserServiceImpl#updateUser(Long, UserRequest)}
//     */
//    @Test
//    void testUpdateUser3() {
//        when(modelMapper.map(Mockito.<Object>any(), Mockito.<Class<Object>>any())).thenReturn("Map");
//        when(modelMapper.map(Mockito.<Object>any(), Mockito.<Class<UserResponse>>any()))
//                .thenReturn(UserResponse.builder().email("jane.doe@example.org").userId(1L).username("janedoe").build());
//        doNothing().when(modelMapper).map(Mockito.<Object>any(), Mockito.<Object>any());
//
//        User user = new User();
//        user.setActive(true);
//        user.setEmail("jane.doe@example.org");
//        user.setTransactionsAsPayee(new ArrayList<>());
//        user.setTransactionsAsPayer(new ArrayList<>());
//        user.setUserId(1L);
//        user.setUsername("janedoe");
//        Optional<User> ofResult = Optional.of(user);
//        when(userRepository.save(Mockito.<User>any())).thenThrow(new DuplicateUserException("An error occurred"));
//        when(userRepository.existsByEmail(Mockito.<String>any())).thenReturn(false);
//        when(userRepository.findById(Mockito.<Long>any())).thenReturn(ofResult);
//        assertThrows(DuplicateUserException.class,
//                () -> userServiceImpl.updateUser(1L, new UserRequest("janedoe", "jane.doe@example.org")));
//        verify(modelMapper).map(Mockito.<Object>any(), Mockito.<Class<Object>>any());
//        verify(modelMapper).map(Mockito.<Object>any(), Mockito.<Object>any());
//        verify(userRepository).existsByEmail(Mockito.<String>any());
//        verify(userRepository).save(Mockito.<User>any());
//        verify(userRepository).findById(Mockito.<Long>any());
//    }
//
//    /**
//     * Method under test: {@link UserServiceImpl#updateUser(Long, UserRequest)}
//     */
//    @Test
//    void testUpdateUser4() {
//        when(modelMapper.map(Mockito.<Object>any(), Mockito.<Class<Object>>any())).thenReturn("Map");
//        when(modelMapper.map(Mockito.<Object>any(), Mockito.<Class<UserResponse>>any()))
//                .thenReturn(UserResponse.builder().email("jane.doe@example.org").userId(1L).username("janedoe").build());
//        when(userRepository.existsByEmail(Mockito.<String>any())).thenReturn(false);
//        Optional<User> emptyResult = Optional.empty();
//        when(userRepository.findById(Mockito.<Long>any())).thenReturn(emptyResult);
//        assertThrows(UserNotFoundException.class,
//                () -> userServiceImpl.updateUser(1L, new UserRequest("janedoe", "jane.doe@example.org")));
//        verify(modelMapper).map(Mockito.<Object>any(), Mockito.<Class<Object>>any());
//        verify(userRepository).existsByEmail(Mockito.<String>any());
//        verify(userRepository).findById(Mockito.<Long>any());
//    }
//
//    /**
//     * Method under test: {@link UserServiceImpl#updateUser(Long, UserRequest)}
//     */
//    @Test
//    @Disabled("TODO: Complete this test")
//    void testUpdateUser5() {
//        // TODO: Complete this test.
//        //   Reason: R013 No inputs found that don't throw a trivial exception.
//        //   Diffblue Cover tried to run the arrange/act section, but the method under
//        //   test threw
//        //   java.lang.NullPointerException: Cannot invoke "com.example.split.dto.UserRequest.getEmail()" because "userRequest" is null
//        //       at com.example.split.serviceImpl.UserServiceImpl.updateUser(UserServiceImpl.java:77)
//        //   See https://diff.blue/R013 to resolve this issue.
//
//        when(modelMapper.map(Mockito.<Object>any(), Mockito.<Class<Object>>any())).thenReturn("Map");
//        when(modelMapper.map(Mockito.<Object>any(), Mockito.<Class<UserResponse>>any()))
//                .thenReturn(UserResponse.builder().email("jane.doe@example.org").userId(1L).username("janedoe").build());
//        doNothing().when(modelMapper).map(Mockito.<Object>any(), Mockito.<Object>any());
//
//        User user = new User();
//        user.setActive(true);
//        user.setEmail("jane.doe@example.org");
//        user.setTransactionsAsPayee(new ArrayList<>());
//        user.setTransactionsAsPayer(new ArrayList<>());
//        user.setUserId(1L);
//        user.setUsername("janedoe");
//        Optional<User> ofResult = Optional.of(user);
//        when(userRepository.save(Mockito.<User>any())).thenThrow(new DuplicateUserException("An error occurred"));
//        when(userRepository.existsByEmail(Mockito.<String>any())).thenReturn(false);
//        when(userRepository.findById(Mockito.<Long>any())).thenReturn(ofResult);
//        userServiceImpl.updateUser(1L, null);
//    }
//

    @Test
    void updateUser_UserNotFoundException() {
        Long userId = 1L;
        String newEmail = "newemail@example.com";
        String newUsername = "newusername";
        UserRequest userRequest = new UserRequest();
        userRequest.setEmail(newEmail);
        userRequest.setUsername(newUsername);
        when(userRepo.findById(userId)).thenReturn(java.util.Optional.empty());
        try {
            userService.updateUser(userId, userRequest);
        } catch (UserNotFoundException e) {
            assertEquals("User with id " + userId + " not found.", e.getMessage());
        }
        verify(userRepo, times(1)).findById(userId);
        verify(userRepo, never()).save(any(User.class));
        verify(mapper, never()).map(any(User.class), eq(UserResponse.class));
    }

    @Test
    void getTransactionsForUser_UserFound() {
        Long userId = 1L;
        User user = new User();
        user.setUserId(userId);
        Transact transact1 = new Transact();
        transact1.setTransactId(1L);
        Transact transact2 = new Transact();
        transact2.setTransactId(2L);
        List<Transact> transactionsAsPayer = new ArrayList<>();
        transactionsAsPayer.add(transact1);
        List<Transact> transactionsAsPayee = new ArrayList<>();
        transactionsAsPayee.add(transact2);
        user.setTransactionsAsPayer(transactionsAsPayer);
        user.setTransactionsAsPayee(transactionsAsPayee);
        Optional<User> userOptional = Optional.of(user);
        when(userRepo.findById(userId)).thenReturn(userOptional);
        List<Transact> allTransact = userService.getTransactionsForUser(userId);
        assertEquals(2, allTransact.size());  // There should be 2 transactions
        assertTrue(allTransact.contains(transact1));  // transact1 should be in the list
        assertTrue(allTransact.contains(transact2));  // transact2 should be in the list
        verify(userRepo, times(1)).findById(userId);
    }

    @Test
    void getTransactionsForUser_UserNotFoundException() {
        Long userId = 1L;
        when(userRepo.findById(userId)).thenReturn(Optional.empty());
        try {
            userService.getTransactionsForUser(userId);
        } catch (UserNotFoundException e) {
            assertEquals("User not found with ID: " + userId, e.getMessage());
        }
        verify(userRepo, times(1)).findById(userId);
    }

    @Test
    void getUserIdByEmail_WhenUserExists() {
        // Given
        String email = "test@example.com";
        Long userId = 1L;
        User user = new User();
        user.setUserId(userId);
        Optional<User> userOptional = Optional.of(user);

        when(userRepo.findByEmail(email)).thenReturn(userOptional);

        // When
        Long actualUserId = userService.getUserIdByEmail(email);

        // Then
        assertEquals(userId, actualUserId);
    }

    @Test
    void getUserIdByEmail_WhenUserDoesNotExist() {
        // Given
        String email = "test@example.com";
        Optional<User> userOptional = Optional.empty();

        when(userRepo.findByEmail(email)).thenReturn(userOptional);

        // When
        Long actualUserId = userService.getUserIdByEmail(email);

        // Then
        assertEquals(null, actualUserId);
    }

}