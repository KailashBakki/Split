package com.example.split.service;


import com.example.split.dto.TransactRequest;
import com.example.split.dto.TransactResponse;

import java.util.List;

public interface TransactService {
    List<TransactResponse> getAllTransactions();

    TransactResponse getTransactionById(Long transactionId);

    TransactResponse createTransaction(TransactRequest transactionRequest);

    TransactResponse updateTransaction(Long transactionId, TransactRequest transactionRequest);
//    List<TransactResponse> getTransactByUserId(Long userId);
    void deleteTransaction(Long transactionId);

//    TransactResponse updTransactResponse(Long transactionId, TransactRequest transactRequest);
}
