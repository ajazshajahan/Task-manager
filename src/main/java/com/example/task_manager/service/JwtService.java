package com.example.task_manager.service;

public interface JwtService {

    String generateToken(String username);

    boolean validateToken(String token);

}
