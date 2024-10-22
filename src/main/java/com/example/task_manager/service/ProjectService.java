package com.example.task_manager.service;

import com.example.task_manager.dto.ProjectDTO;
import com.example.task_manager.enums.ProjectStatus;
import com.example.task_manager.exception.CustomException;
import com.example.task_manager.response.MasterDataResponse;
import com.example.task_manager.response.PaginatedResponse;
import com.example.task_manager.response.ProjectResponse;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;

public interface ProjectService {

    void createProject(ProjectDTO request);

  PaginatedResponse<ProjectResponse> getProject(Long id, Long companyId, String name, LocalDateTime updatedAt, ProjectStatus status, LocalDateTime startDate, LocalDateTime endDate, int page, int size, String sortBy, String sortOrder) throws CustomException;

    ProjectResponse updateProject(ProjectDTO projectDTO);

    ResponseEntity<String> deleteProject(Long id);
}
