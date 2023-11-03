package com.example.split.controller;


import com.example.split.dto.TransactRequest;
import com.example.split.dto.TransactResponse;
import com.example.split.service.TransactService;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionControllerTest {
    @Mock
    private TransactService transactService;

    @InjectMocks
    private TransactionController transactionController;

    @Test
    public void makeTransaction() {
        // Given
        TransactRequest transactRequest = new TransactRequest();
        TransactResponse transactResponse = new TransactResponse();

        // When
        when(transactService.createTransaction(transactRequest)).thenReturn(transactResponse);
        ResponseEntity<TransactResponse> response = transactionController.makeTransaction(transactRequest);

        // Then
        verify(transactService, times(1)).createTransaction(transactRequest);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(transactResponse, response.getBody());
    }

    @Test
    public void getAllUsers() {
        // Given
        List<TransactResponse> transactResponses = new ArrayList<>();
        transactResponses.add(new TransactResponse());
        transactResponses.add(new TransactResponse());

        // When
        when(transactService.getAllTransactions()).thenReturn(transactResponses);
        ResponseEntity<List<TransactResponse>> response = transactionController.getAllUsers();

        // Then
        verify(transactService, times(1)).getAllTransactions();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(transactResponses, response.getBody());
    }

    @Test
    public void getUserById(){
        // Given
        Long transactId = 1L;
        TransactResponse transactResponse = new TransactResponse();

        // When
        when(transactService.getTransactionById(transactId)).thenReturn(transactResponse);
        ResponseEntity<TransactResponse> response = transactionController.getUserById(transactId);

        // Then
        verify(transactService, times(1)).getTransactionById(transactId);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(transactResponse, response.getBody());
    }

    @Test
    public void deleteUser(){
        // Given
        Long userId = 1L;

        // When
        doNothing().when(transactService).deleteTransaction(userId);
        ResponseEntity<String> response = transactionController.deleteUser(userId);

        // Then
        verify(transactService, times(1)).deleteTransaction(userId);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Transaction deleted successfully!!!", response.getBody());
    }

    @Test
    public void updateTransact(){
        // Given
        Long transactId = 1L;
        TransactRequest transactRequest = new TransactRequest();
        TransactResponse transactResponse = new TransactResponse();

        // When
        when(transactService.updateTransaction(transactId, transactRequest)).thenReturn(transactResponse);
        ResponseEntity<TransactResponse> response = transactionController.updateTransact(transactId, transactRequest);

        // Then
        verify(transactService, times(1)).updateTransaction(transactId, transactRequest);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(transactResponse, response.getBody());
    }
}