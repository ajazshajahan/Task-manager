package com.example.task_manager.service.impl;

import com.example.task_manager.dto.LoginDTO;
import com.example.task_manager.dto.UserDTO;
import com.example.task_manager.entity.User;
import com.example.task_manager.repository.UserRepository;
import com.example.task_manager.service.AuthService;
import com.example.task_manager.service.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class UserServiceImpl implements AuthService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final JwtService jwtService;


    @Autowired
    public UserServiceImpl(UserRepository userRepository,
                           PasswordEncoder passwordEncoder,
                           JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }


    @Override
    public void register(UserDTO request) {

        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new RuntimeException("Username already taken");
        }

        // Create a new user entity
        User newUser = new User();
        newUser.setCompanyId(request.getCompanyId());
        newUser.setUsername(request.getUsername());

        // Encode the password before saving
        newUser.setPassword(passwordEncoder.encode(request.getPassword()));

        // Set default authorities (e.g., ROLE_USER)
        newUser.setAuthorities(Collections.singleton("ROLE_USER"));

        // Save the new user in the repository
        userRepository.save(newUser);
    }

    @Override
    public String authenticate(LoginDTO loginRequest) {
        String username = loginRequest.getUsername();
        String password = loginRequest.getPassword();

        return userRepository.findByUsername(username)
                .filter(user -> passwordEncoder.matches(password, user.getPassword()))
                .map(user -> jwtService.generateToken(username))
                .orElseThrow(() -> new RuntimeException("Invalid username or password"));
    }

}

