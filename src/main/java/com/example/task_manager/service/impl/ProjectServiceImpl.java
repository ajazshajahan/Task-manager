package com.example.task_manager.service.impl;

import com.example.task_manager.context.UserContextInfo;
import com.example.task_manager.dto.ProjectDTO;
import com.example.task_manager.entity.Project;
import com.example.task_manager.enums.ProjectStatus;
import com.example.task_manager.exception.CustomException;
import com.example.task_manager.repository.ProjectRepository;
import com.example.task_manager.repository.TaskRepository;
import com.example.task_manager.response.PaginatedResponse;
import com.example.task_manager.response.ProjectResponse;
import com.example.task_manager.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.example.task_manager.context.ContextHolder.getUserContextInfo;

@Service
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository projectRepository;
    private final TaskRepository taskRepository;
    private final UserContextInfo context = getUserContextInfo();

    @Autowired
    public ProjectServiceImpl(ProjectRepository projectRepository, TaskRepository taskRepository) {
        this.projectRepository = projectRepository;
        this.taskRepository = taskRepository;
    }

    @Override
    public void createProject(ProjectDTO request) {
        Project project = new Project();
        mapProjectDtoToEntity(request, project);

        try {
            projectRepository.save(project);
        } catch (Exception ex) {
            throw new RuntimeException("Project cannot be saved", ex);
        }
    }

    @Override
    public PaginatedResponse<ProjectResponse> getProject(Long id, Long companyId, String name,
                                                         LocalDateTime updatedAt, ProjectStatus status,
                                                         LocalDateTime startDate, LocalDateTime endDate,
                                                         int page, int size, String sortBy, String sortOrder) throws CustomException {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(sortOrder), sortBy));

        if (id != null) {
            return getProjectById(id, companyId);
        }

        Page<Project> projects = findProjects(companyId, name, updatedAt, status, startDate, endDate, pageable);

        List<ProjectResponse> projectResponses = projects.stream()
                .map(this::mapToProjectResponse)
                .collect(Collectors.toList());

        return new PaginatedResponse<>(projectResponses, projects.getTotalElements(), projects.hasNext());
    }

    private PaginatedResponse<ProjectResponse> getProjectById(Long id, Long companyId) throws CustomException {
        Project project = projectRepository.findByIdAndCompanyId(id, companyId)
                .orElseThrow(() -> new CustomException("Project not found with the given id and companyId."));
        return new PaginatedResponse<>(Collections.singletonList(mapToProjectResponse(project)), 1, false);
    }

    private Page<Project> findProjects(Long companyId, String name, LocalDateTime updatedAt,
                                       ProjectStatus status, LocalDateTime startDate, LocalDateTime endDate,
                                       Pageable pageable) {
        if (companyId == null) return Page.empty();

        if (name != null && updatedAt != null) {
            return projectRepository.findByNameAndUpdatedAtAndCompanyId(name, updatedAt, companyId, pageable);
        } else if (name != null) {
            return projectRepository.findByNameAndCompanyId(name, companyId, pageable);
        } else if (updatedAt != null) {
            return projectRepository.findByUpdatedAtAndCompanyId(updatedAt, companyId, pageable);
        } else if (status != null) {
            return projectRepository.findByStatusAndCompanyId(status, companyId, pageable);
        } else if (startDate != null) {
            return projectRepository.findByStartDateAndCompanyId(startDate, companyId, pageable);
        } else if (endDate != null) {
            return projectRepository.findByEndDateAndCompanyId(endDate, companyId, pageable);
        } else {
            return projectRepository.findByCompanyId(companyId, pageable);
        }
    }

    private ProjectResponse mapToProjectResponse(Project project) {
        return new ProjectResponse(
                project.getId(),
                project.getCompanyId(),
                project.getName(),
                project.getDescription(),
                project.getStatus(),
                project.getStartDate(),
                project.getEndDate(),
                project.getUpdatedAt()
        );
    }

    private void mapProjectDtoToEntity(ProjectDTO projectDTO, Project project) {
        project.setCompanyId(projectDTO.getCompanyId());
        project.setName(projectDTO.getName());
        project.setDescription(projectDTO.getDescription());
        project.setStartDate(projectDTO.getStartDate());
        project.setEndDate(projectDTO.getEndDate());
        project.setStatus(projectDTO.getStatus());
    }

    @Override
    public ProjectResponse updateProject(ProjectDTO projectDTO) throws CustomException {
        Project project = projectRepository.findById(projectDTO.getId())
                .orElseThrow(() -> new CustomException("Project not found for ID: " + projectDTO.getId()));

        mapProjectDtoToEntity(projectDTO, project);
        projectRepository.save(project);

        return mapToProjectResponse(project);
    }

    @Override
    public ResponseEntity<String> deleteProject(Long id) {
        if (!projectRepository.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Project with ID " + id + " not found.");
        }

        if (taskRepository.existsByProjectId(id)) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Project cannot be deleted because it has associated tasks.");
        }


        projectRepository.deleteById(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

}
