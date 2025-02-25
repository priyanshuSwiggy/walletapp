package com.swiggy.walletapp.controller;

import com.swiggy.walletapp.dto.TransactionDto;
import com.swiggy.walletapp.dto.TransactionResponseDto;
import com.swiggy.walletapp.enums.TransactionType;
import com.swiggy.walletapp.service.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@Tag(name = "TransactionController", description = "APIs for managing transactions")
@RequestMapping("/users/{userId}/wallets/{walletId}/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    @Operation(summary = "Retrieve all transactions",
            description = "Retrieve all the transactions for a specific user and wallet")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Successfully retrieved the transactions",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = TransactionResponseDto.class))})
    })
    @GetMapping
    public ResponseEntity<List<TransactionResponseDto>> getTransactions(@PathVariable Long userId, @PathVariable Long walletId, @RequestParam(required = false) TransactionType transactionType) {
        List<TransactionResponseDto> transactions = transactionService.getTransactions(userId, walletId, transactionType);
        return new ResponseEntity<>(transactions, HttpStatus.OK);
    }

    @Operation(summary = "Create a new transaction",
            description = "Create a new transaction for a specific user and wallet")
    @Parameter(name = "transactionDto", description = "The Dto containing information for creating a new transaction")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201",
                    description = "Successfully created a new transaction",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = TransactionDto.class))}),
            @ApiResponse(responseCode = "400",
                    description = "Invalid input for creating a transaction")
    })
    @PostMapping
    public ResponseEntity<String> createTransaction(@PathVariable Long userId, @PathVariable Long walletId, @RequestBody TransactionDto transactionDto) {
        transactionService.createTransaction(userId, walletId, transactionDto);
        return new ResponseEntity<>("Transaction successful", HttpStatus.CREATED);
    }
}