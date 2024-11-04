package com.example.task_manager.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CommentsResponse {


    private Long id;

    private Long taskId;

    private Long userId;

    private Long companyId;

    private String content;

    private LocalDateTime createdAt;
}
