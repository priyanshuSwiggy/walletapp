package com.swiggy.walletapp.controller;

import com.swiggy.walletapp.dto.WalletRequestDto;
import com.swiggy.walletapp.dto.WalletResponseDto;
import com.swiggy.walletapp.service.WalletService;
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
@Tag(name = "WalletController", description = "APIs for managing user's wallets")
@RequestMapping("/users/{userId}/wallets")
public class WalletController {

    private final WalletService walletService;

    @Operation(summary = "Create a new wallet for an user",
            description = "Create a new wallet for an user in the database")
    @Parameter(name = "walletRequestDto", description = "The Dto containing information for creating a new wallet for an user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201",
                    description = "Successfully created a new wallet for an user",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = WalletRequestDto.class))}),
            @ApiResponse(responseCode = "400",
                    description = "Invalid input for creating a wallet")
    })
    @PostMapping
    public ResponseEntity<String> createWallet(@PathVariable Long userId, @RequestBody WalletRequestDto walletRequestDto) {
        walletService.createWallet(userId, walletRequestDto);
        return new ResponseEntity<>("Wallet created successfully", HttpStatus.CREATED);
    }

    @Operation(summary = "Get all wallets for a user",
            description = "Retrieve all wallets for a specific user from the database")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Successfully retrieved all wallets for the user",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = WalletRequestDto.class))}),
            @ApiResponse(responseCode = "404",
                    description = "User not found")
    })
    @GetMapping
    public ResponseEntity<List<WalletResponseDto>> getWallets(@PathVariable Long userId) {
        List<WalletResponseDto> wallets = walletService.getWallets(userId);
        return new ResponseEntity<>(wallets, HttpStatus.OK);
    }
}
