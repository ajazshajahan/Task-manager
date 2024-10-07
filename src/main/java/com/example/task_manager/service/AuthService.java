package com.example.task_manager.service;

import com.example.task_manager.dto.LoginDTO;
import com.example.task_manager.dto.UserDTO;

public interface AuthService {

    void register(UserDTO request);

    String authenticate(LoginDTO loginRequest);
}
