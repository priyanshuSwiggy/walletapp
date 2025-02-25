package com.swiggy.walletapp.controller;

import com.swiggy.walletapp.dto.UserDto;
import com.swiggy.walletapp.service.UserService;
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

@RequiredArgsConstructor
@RestController
@Tag(name = "UserController", description = "APIs for managing users")
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @Operation(summary = "Register a new user",
            description = "Register a new user in the database")
    @Parameter(name = "userDto", description = "The Dto containing information for registering a new user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201",
                    description = "Successfully registered a new user",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserDto.class))}),
            @ApiResponse(responseCode = "400",
                    description = "Invalid input for registering a user")
    })
    @PostMapping
    public ResponseEntity<String> registerClient(@RequestBody UserDto userDto) {
        userService.register(userDto);
        return new ResponseEntity<>("User registered successfully", HttpStatus.CREATED);
    }
}