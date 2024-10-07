package com.example.task_manager.context;

import org.springframework.stereotype.Component;

@Component
public class ContextHolder {

    public static UserContextInfo getUserContextInfo() {


        String username = getUsernameFromAuthentication();
        Long companyId = getCompanyIdFromPrincipal();
        String role = getRoleFromAuthorities();

        return new UserContextInfo(companyId, username, role);

    }

    private static String getRoleFromAuthorities() {
        return "ROLE_USER";
    }

    private static Long getCompanyIdFromPrincipal() {

        return 1L;
    }

    private static String getUsernameFromAuthentication() {

        return "ajaz";
    }
}
