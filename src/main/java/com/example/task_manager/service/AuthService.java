package com.example.task_manager.service;

import com.example.task_manager.dto.LoginDTO;
import com.example.task_manager.dto.UserDTO;
import com.example.task_manager.exception.CustomException;

public interface AuthService {

    void register(UserDTO request) throws CustomException;

    String authenticate(LoginDTO loginRequest) throws CustomException;
}
