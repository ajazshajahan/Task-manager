package com.example.task_manager.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
public class UserDTO {

    private Long companyId;

    private String username;

    private String password;

    private Set<String> authorities = new HashSet<>();

}
