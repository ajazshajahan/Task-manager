package com.example.task_manager.service.impl;

import com.example.task_manager.dto.LoginDTO;
import com.example.task_manager.dto.UserDTO;
import com.example.task_manager.entity.User;
import com.example.task_manager.exception.CustomException;
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
    public void register(UserDTO request) throws CustomException {
        if (userExists(request.getUsername())) {
            throw new CustomException("Username already taken");
        }

        User newUser = createUserFromDto(request);
        userRepository.save(newUser);
    }

    @Override
    public String authenticate(LoginDTO loginRequest) throws CustomException {
        User user = findUserByUsername(loginRequest.getUsername());
        validatePassword(loginRequest.getPassword(), user.getPassword());

        return jwtService.generateToken(user.getUsername());
    }

    private boolean userExists(String username) {
        return userRepository.findByUsername(username).isPresent();
    }

    private User createUserFromDto(UserDTO request) {
        User user = new User();
        user.setCompanyId(request.getCompanyId());
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setAuthorities(Collections.singleton("ROLE_USER"));
        return user;
    }

    private User findUserByUsername(String username) throws CustomException {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new CustomException("Invalid username or password"));
    }

    private void validatePassword(String rawPassword, String encodedPassword) throws CustomException {
        if (!passwordEncoder.matches(rawPassword, encodedPassword)) {
            throw new CustomException("Invalid username or password");
        }
    }
}
