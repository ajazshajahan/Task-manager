package com.example.task_manager.response;

import com.example.task_manager.enums.ProjectStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProjectResponse {

    private Long id;

    private Long companyId;

    private String name;

    private String description;

    private ProjectStatus status;

    private LocalDateTime startDate;

    private LocalDateTime endDate;

    private LocalDateTime updatedAt;
}
