package com.example.task_manager.service;

import com.example.task_manager.dto.TaskDTO;
import com.example.task_manager.enums.ProjectStatus;
import com.example.task_manager.enums.TaskStatus;
import com.example.task_manager.exception.CustomException;
import com.example.task_manager.response.PaginatedResponse;
import com.example.task_manager.response.TaskResponse;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;

public interface TaskService {
    void createTask(TaskDTO request);

    ResponseEntity<String> deleteTask(Long id);

   PaginatedResponse<TaskResponse> getTask(Long id, Long companyId, String title, LocalDateTime updatedAt, TaskStatus status,
                                          LocalDateTime startDate, LocalDateTime endDate, int page, int size, String sortBy, String sortOrder) throws CustomException;

    TaskResponse updateTask(TaskDTO taskDTO);
}
