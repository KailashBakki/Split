package com.example.split.serviceImpl;

import com.example.split.dto.TransactRequest;
import com.example.split.dto.TransactResponse;
import com.example.split.entity.Transact;
import com.example.split.entity.User;
import com.example.split.exception.InvalidTransactionException;
import com.example.split.exception.TransactionNotFoundException;
import com.example.split.repo.TransactRepository;
import com.example.split.repo.UserRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.springframework.test.context.ContextConfiguration;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ContextConfiguration(classes = {TransactServiceImpl.class})
@ExtendWith(MockitoExtension.class)
class TransactServiceImplTest {

//    @MockBean
//    private ModelMapper mapper;

//    @MockBean
//    private TransactRepository transactRepository;

//    @Autowired
//    private TransactServiceImpl transactServiceImpl;

//    @MockBean
//    private UserRepository userRepository;

    @Mock
    private TransactRepository transactRepo;

    @Mock
    private ModelMapper mapper;
    @Mock
    private UserRepository userRepo;

    @InjectMocks
//    @Autowired
    private TransactServiceImpl transactService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }


    @Test
    void getAllTransactions() {
        Transact transact1 = new Transact();
        transact1.setTransactId(1L);
        transact1.setDescription("Transaction 1");
        Transact transact2 = new Transact();
        transact2.setTransactId(2L);
        transact2.setDescription("Transaction 2");
        List<Transact> transactList = Arrays.asList(transact1, transact2);
        when(transactRepo.findAll()).thenReturn(transactList);
        TransactResponse transactResponse1 = new TransactResponse();
        transactResponse1.setTransactId(1L);
        transactResponse1.setDescription("Transaction 1 Response");
        TransactResponse transactResponse2 = new TransactResponse();
        transactResponse2.setTransactId(2L);
        transactResponse2.setDescription("Transaction 2 Response");
        when(mapper.map(transact1, TransactResponse.class)).thenReturn(transactResponse1);
        when(mapper.map(transact2, TransactResponse.class)).thenReturn(transactResponse2);
        List<TransactResponse> result = transactService.getAllTransactions();
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(transactResponse1, result.get(0));
        assertEquals(transactResponse2, result.get(1));
    }

    @Test
    void getTransactionById() {
        Long transactionId = 1L;
        Transact sampleTransact = new Transact();
        when(transactRepo.findById(transactionId)).thenReturn(Optional.of(sampleTransact));
        TransactResponse expectedResponse = new TransactResponse();
        when(mapper.map(sampleTransact, TransactResponse.class)).thenReturn(expectedResponse);
        TransactResponse actualResponse = transactService.getTransactionById(transactionId);
        assertNotNull(actualResponse);
        assertEquals(expectedResponse, actualResponse);
        verify(transactRepo, times(1)).findById(transactionId);
        verify(mapper, times(1)).map(sampleTransact, TransactResponse.class);
    }

    @Test
    void createTransaction() {
        Long fromUserId = 1L;
        Long toUserId = 2L;
        TransactRequest request = new TransactRequest();
        request.setDescription("Sample description");
        request.setAmount(new BigDecimal("100.00"));
        request.setPaid(true);
        User fromUser = new User();
        fromUser.setUserId(fromUserId);
        User toUser = new User();
        toUser.setUserId(toUserId);
        request.setFromUser(fromUser);
        request.setToUser(toUser);
        when(userRepo.findById(fromUserId)).thenReturn(Optional.of(fromUser));
        when(userRepo.findById(toUserId)).thenReturn(Optional.of(toUser));
        Transact savedTransact = new Transact();
        when(transactRepo.save(any(Transact.class))).thenReturn(savedTransact);
        TransactResponse expectedResponse = new TransactResponse();
        expectedResponse.setTransactId(savedTransact.getTransactId());
        expectedResponse.setDescription(savedTransact.getDescription());
        expectedResponse.setAmount(savedTransact.getAmount());
        expectedResponse.setFromUser(fromUser);
        expectedResponse.setToUser(toUser);
        expectedResponse.setPaid(savedTransact.isPaid());
        expectedResponse.setCreated_at(savedTransact.getLast_updated());
        when(mapper.map(savedTransact, TransactResponse.class)).thenReturn(expectedResponse);
        TransactResponse actualResponse = transactService.createTransaction(request);
        assertNotNull(actualResponse);
        assertEquals(fromUser, actualResponse.getFromUser());
        assertEquals(toUser, actualResponse.getToUser());
    }

    @Test
    void createTransaction_InValidUsers() {
        TransactRequest request = new TransactRequest();
        User fromUser = new User();
        User toUser = new User();
        fromUser.setUserId(1L);  // Valid user ID
        request.setFromUser(fromUser);
        request.setToUser(toUser);
        when(userRepo.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(InvalidTransactionException.class, () -> transactService.createTransaction(request));
    }


    @Test
    void deleteTransaction_Success() {
        Long transactionId = 1L;

        // Mock behavior for existsById() to return true
        when(transactRepo.existsById(transactionId)).thenReturn(true);

        // Execute the deleteTransaction method
        transactService.deleteTransaction(transactionId);

        // Verify that deleteById() was called with the correct transactionId
        verify(transactRepo).deleteById(transactionId);
    }

    @Test
    void deleteTransaction_TransactionNotFound() {
        Long transactionId = 1L;

        // Mock behavior for existsById() to return false
        when(transactRepo.existsById(transactionId)).thenReturn(false);

        // Execute and assert exception
        assertThrows(TransactionNotFoundException.class, () -> transactService.deleteTransaction(transactionId));
    }

    @Test
    void updateTransaction_Valid() {
        TransactRequest request = new TransactRequest();
        User fromUser = new User();
        fromUser.setUserId(1L);  // Valid user ID
        User toUser = new User();
        toUser.setUserId(2L);    // Valid user ID
        request.setFromUser(fromUser);
        request.setToUser(toUser);
        request.setDescription("Updated description");
        request.setAmount(BigDecimal.valueOf(100.0));
        request.setPaid(true);
        Transact existingTransaction = new Transact();
        existingTransaction.setTransactId(1L);
        existingTransaction.setDescription("Original description");
        existingTransaction.setAmount(BigDecimal.valueOf(50.0));
        existingTransaction.setFromUser(new User());
        existingTransaction.setToUser(new User());
        existingTransaction.setPaid(false);
        when(userRepo.findById(1L)).thenReturn(Optional.of(fromUser));
        when(userRepo.findById(2L)).thenReturn(Optional.of(toUser));
        when(transactRepo.findById(1L)).thenReturn(Optional.of(existingTransaction));
        when(transactRepo.save(any(Transact.class))).thenAnswer(invocation -> {
            Transact savedTransact = invocation.getArgument(0);
            return savedTransact;
        });
        when(mapper.map(any(Transact.class), eq(TransactResponse.class))).thenAnswer(invocation -> {
            Transact source = invocation.getArgument(0);
            TransactResponse response = new TransactResponse();
            response.setTransactId(source.getTransactId());
            response.setDescription(source.getDescription());
            response.setAmount(source.getAmount());
            response.setFromUser(source.getFromUser());
            response.setToUser(source.getToUser());
            response.setPaid(source.isPaid());
            return response;
        });
        TransactResponse actualResponse = transactService.updateTransaction(1L, request);
        assertEquals(1L, actualResponse.getTransactId());
        assertEquals("Updated description", actualResponse.getDescription());
        assertEquals(BigDecimal.valueOf(100.0), actualResponse.getAmount());
        assertEquals(fromUser, actualResponse.getFromUser());
        assertEquals(toUser, actualResponse.getToUser());
        assertEquals(true, actualResponse.isPaid());
    }

    @Test
    void updateTransaction_InvalidTransact() {
        // Create a valid request
        TransactRequest request = new TransactRequest();
        User fromUser = new User();
        fromUser.setUserId(1L);  // Valid user ID
        User toUser = new User();
        toUser.setUserId(2L);    // Valid user ID
        request.setFromUser(fromUser);
        request.setToUser(toUser);
        request.setDescription("Updated description");
        request.setAmount(BigDecimal.valueOf(100.0));
        request.setPaid(true);

        // Mock TransactRepository behavior to return an empty optional (invalid transaction ID)
        //when(transactRepo.findById(anyLong())).thenReturn(Optional.empty());

        // Execute and assert exception
        assertThrows(InvalidTransactionException.class, () -> transactService.updateTransaction(1L, request));
    }

    @Test
    void updateTransaction_TransactionNotFound() {
        // Given
        Long transactionId = 1L;
        TransactRequest transactRequest = new TransactRequest();
        Optional<Transact> existingTransactionOptional = Optional.of(new Transact());

        // When
        when(transactRepo.findById(transactionId)).thenReturn(existingTransactionOptional);

        // Then
        assertThrows(TransactionNotFoundException.class, () -> transactService.updateTransaction(transactionId, transactRequest));

        // Verify
        verify(transactRepo).findById(transactionId);
        verifyNoMoreInteractions(transactRepo);
        verifyNoInteractions(userRepo);
    }
}

//    @Test
//    void updateTransaction_TransactionNotFound() {
//        @Test
//        void updateTransaction_TransactionNotFound () {
//            // Mock data
//            Long transactionId = 123L;
//            TransactRequest transactRequest = createTransactRequest();
//            User fromUser = createFromUser();
//            User toUser = createToUser();
//            Transact existingTransaction = createExistingTransaction();
//
//            // Mocking repository behavior
//            when(userRepo.findById(transactRequest.getFromUser().getUserId())).thenReturn(Optional.of(fromUser));
//            when(userRepo.findById(transactRequest.getToUser().getUserId())).thenReturn(Optional.of(toUser));
//            when(transactRepo.findById(transactionId)).thenReturn(Optional.empty()); // Simulating transaction not found
//
//            // Execute the service method
//            assertThrows(TransactionNotFoundException.class, () -> {
//                transactService.updateTransaction(transactionId, transactRequest);
//            });
//
//            // Additional verifications
//            verify(userRepo, times(1)).findById(transactRequest.getFromUser().getUserId());
//            verify(userRepo, times(1)).findById(transactRequest.getToUser().getUserId());
//            verify(transactRepo, times(1)).findById(transactionId);
//            verify(transactRepo, never()).save(any(Transact.class));
//        }
//
//        // Other test cases and helper methods...
//
//        private TransactRequest createTransactRequest () {
//            // Implement this based on your requirements
//        }
//
//        private User createFromUser () {
//            // Implement this based on your requirements
//        }
//
//        private User createToUser () {
//            // Implement this based on your requirements
//        }
//
//        private Transact createExistingTransaction () {
//            // Implement this based on your requirements
//        }
//    }