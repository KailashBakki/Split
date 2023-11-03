package com.example.split.serviceImpl;

import com.example.split.dto.TransactRequest;
import com.example.split.dto.TransactResponse;
import com.example.split.entity.Transact;
import com.example.split.entity.User;
import com.example.split.exception.InvalidTransactionException;
import com.example.split.exception.TransactionNotFoundException;
import com.example.split.repo.TransactRepository;
import com.example.split.repo.UserRepository;
import com.example.split.service.TransactService;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
@Service
public class TransactServiceImpl implements TransactService {

    public static final Logger logger = LoggerFactory.getLogger(TransactService.class);
    @Autowired
    private ModelMapper mapper;
    @Autowired
    private UserRepository userRepo;
    @Autowired
    private TransactRepository transactRepo;
    @Override
    public List<TransactResponse> getAllTransactions() {
        logger.info("fetching all transactoins");
        List<Transact> transacts = transactRepo.findAll();
        return transacts.stream()
                .map(transact -> mapper.map(transact, TransactResponse.class))
                .collect(Collectors.toList());
    }

    @Override
    public TransactResponse getTransactionById(Long transactionId) {
        Transact transact = transactRepo.findById(transactionId)
                .orElseThrow(() -> new TransactionNotFoundException("Transaction with id " + transactionId + " not found."));
        logger.info("fetching 1 transaction by txnID");
        return mapper.map(transact, TransactResponse.class);
    }

    @Override
    public TransactResponse createTransaction(TransactRequest transactionRequest) {
        Optional<User> fromUserOptional = userRepo.findById(transactionRequest.getFromUser().getUserId());
        Optional<User> toUserOptional = userRepo.findById(transactionRequest.getToUser().getUserId());

        if (fromUserOptional.isEmpty() || toUserOptional.isEmpty()) {
            logger.error("InvalidTransactionException while making transaction");
            throw new InvalidTransactionException("Invalid fromUserId or toUserId");
        }

        User fromUser = fromUserOptional.get();
        User toUser = toUserOptional.get();

        Transact newTransaction = Transact.builder()
                .description(transactionRequest.getDescription())
                .amount(transactionRequest.getAmount())
                .fromUser/*(fromUser.getUserId())*/(fromUser)
                .toUser(toUser)//(toUser.getUserId())
                .isPaid(Boolean.valueOf(transactionRequest.getIsPaid()))
                .last_updated(LocalDateTime.now())
                .build();

        Transact savedTransaction = transactRepo.save(newTransaction);
        TransactResponse response = mapper.map(savedTransaction,TransactResponse.class);
        logger.info("creating transaction ");
        return response;

    }

    @Override
    public void deleteTransaction(Long transactionId) {
        if (!transactRepo.existsById(transactionId)) {
            logger.error("TransactionNotFoundException while deleting txn");
            throw new TransactionNotFoundException("Transaction not found with id: " + transactionId);
        }
        logger.info("Deleting transaction");
        transactRepo.deleteById(transactionId);
    }

    @Override
    public TransactResponse updateTransaction(Long transactionId, TransactRequest transactRequest){
        //Validate the input and retrieve the relevant user details
        Optional<User> fromUserOptional = userRepo.findById(transactRequest.getFromUser().getUserId());
        Optional<User> toUserOptional = userRepo.findById(transactRequest.getToUser().getUserId());


        if (fromUserOptional.isEmpty() || toUserOptional.isEmpty()) {
            // Handle invalid user IDs
            System.out.println("in txn service invalid");
            throw new InvalidTransactionException("Invalid fromUserId or toUserId");
        }

        Optional<Transact> existingTransactionOptional = transactRepo.findById(transactionId);

        if (existingTransactionOptional.isEmpty()) {
            System.out.println("in txn service notf");
            throw new TransactionNotFoundException("Transaction not found with id: " + transactionId);
        }

        User fromUser = fromUserOptional.get();
        User toUser = toUserOptional.get();
        Transact existingTransaction = existingTransactionOptional.get();

        // Update the transaction with the new data
        existingTransaction.setDescription(transactRequest.getDescription());
        existingTransaction.setAmount(transactRequest.getAmount());
        existingTransaction.setFromUser(transactRequest.getFromUser()/*.getUserId()*/);//changed
        existingTransaction.setToUser(transactRequest.getToUser()/*.getUserId()*/);//changed

        System.out.println(transactRequest.getIsPaid());
        existingTransaction.setPaid(Boolean.valueOf(transactRequest.getIsPaid()));
        existingTransaction.setLast_updated(LocalDateTime.now());
        System.out.println(LocalDateTime.now());
        System.out.println("=============================================================");

        // Save the updated transaction
        Transact updatedTransaction = transactRepo.save(existingTransaction);
        System.out.println("updt\n "+updatedTransaction.toString());

        // Use ModelMapper to map the updated transaction to TransactResponse
        TransactResponse response = mapper.map(updatedTransaction, TransactResponse.class);

        System.out.println("in ======txn service fininsh");
        return response;


    }
}
