package com.example.task_manager.context;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserContextInfo {

    private Long companyId;

    private String username;

    private String role;
}
