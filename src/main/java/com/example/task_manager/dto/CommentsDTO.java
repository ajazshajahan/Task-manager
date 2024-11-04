package com.example.task_manager.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class CommentsDTO {

    private Long id;

    private Long taskId;

    private Long userId;

    private Long companyId;

    private String content;

    private LocalDateTime createdAt;

}