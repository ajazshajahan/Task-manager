package com.example.task_manager.response;

import com.example.task_manager.enums.TaskPriority;
import com.example.task_manager.enums.TaskStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TaskResponse {

    private Long id;

    private Long projectId;

    private Long userId;

    private String title;

    private String description;

    private TaskPriority priority;

    private TaskStatus status;

    private LocalDateTime startDate;

    private LocalDateTime endDate;

    private Long companyId;

    private LocalDateTime updatedAt;

}
