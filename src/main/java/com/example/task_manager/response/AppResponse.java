package com.example.task_manager.response;

import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AppResponse<T> {

    private T data;

    private String status;

    private String message;


    public AppResponse(String status, String message) {
        this.status = status;
        this.message = message;
    }

}
