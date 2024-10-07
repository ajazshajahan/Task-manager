package com.example.task_manager.controller;

import com.example.task_manager.dto.LoginDTO;
import com.example.task_manager.dto.UserDTO;
import com.example.task_manager.response.AppResponse;
import com.example.task_manager.response.LoginResponse;
import com.example.task_manager.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class UserController {

    private final AuthService authService;

    @Autowired
    public UserController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<AppResponse<Void>> UserSignUp(@RequestBody UserDTO request) {
        authService.register(request);
        AppResponse<Void> appResponse = new AppResponse<>(null, "SUCCESS", "Registration successful");

        return ResponseEntity.status(HttpStatus.CREATED).body(appResponse);
    }

    @PostMapping("/login")
    public ResponseEntity<AppResponse<LoginResponse>> login(@RequestBody LoginDTO loginRequest) {
        AppResponse<LoginResponse> appResponse = new AppResponse<>();

        try {
            String token = authService.authenticate(loginRequest);
            LoginResponse loginResponse = new LoginResponse(token);

            appResponse.setData(loginResponse);
            appResponse.setStatus("SUCCESS");
            appResponse.setMessage("Login successful");

            return ResponseEntity.ok(appResponse);
        } catch (AuthenticationException e) {
            appResponse.setStatus("FAILURE");
            appResponse.setMessage("Invalid credentials");

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(appResponse);
        }
    }
}
