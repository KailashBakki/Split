package com.example.split.controller;

import com.example.split.dto.TransactRequest;
import com.example.split.dto.TransactResponse;
import com.example.split.service.TransactService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/split/transacts")
public class TransactionController {
    private final TransactService transactService;

    @PostMapping("/make")
    public ResponseEntity<TransactResponse> makeTransaction(@Valid @RequestBody TransactRequest transactRequest){
        return new ResponseEntity<TransactResponse>(transactService.createTransaction(transactRequest), HttpStatus.CREATED);
    }

    @GetMapping("/all")
    public ResponseEntity<List<TransactResponse>> getAllUsers(){
        return new ResponseEntity<>(transactService.getAllTransactions(), HttpStatus.OK);
    }

    @GetMapping("/get/{transactId}")
    public ResponseEntity<TransactResponse> getUserById(@Valid @PathVariable Long transactId){
        return new ResponseEntity<>(transactService.getTransactionById(transactId),HttpStatus.OK);
    }

    @DeleteMapping("/delete/{userId}")
    public ResponseEntity<String> deleteUser(@Valid @PathVariable Long userId){
        transactService.deleteTransaction(userId);
        return new ResponseEntity<>("Transaction deleted successfully!!!",HttpStatus.OK);
    }

    @PutMapping("/update/{transactId}")
    public ResponseEntity<TransactResponse> updateTransact(@Valid @PathVariable Long transactId, @Valid @RequestBody TransactRequest transactRequest){
        System.out.println("in control class"+transactRequest.toString() );
        return new ResponseEntity<>(transactService.updateTransaction(transactId,transactRequest), HttpStatus.OK);
    }
}
