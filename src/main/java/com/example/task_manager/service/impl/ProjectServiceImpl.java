package com.example.task_manager.service.impl;

import com.example.task_manager.context.UserContextInfo;
import com.example.task_manager.dto.ProjectDTO;
import com.example.task_manager.entity.MasterData;
import com.example.task_manager.entity.Project;
import com.example.task_manager.enums.ProjectStatus;
import com.example.task_manager.exception.CustomException;
import com.example.task_manager.repository.ProjectRepository;
import com.example.task_manager.response.MasterDataResponse;
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
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.example.task_manager.context.ContextHolder.getUserContextInfo;

@Service
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository projectRepository;
    private final UserContextInfo context = getUserContextInfo();

    @Autowired
    public ProjectServiceImpl(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    @Override
    public void createProject(ProjectDTO request) {

        try {

            Project project = new Project();

            project.setCompanyId(request.getCompanyId());
            project.setName(request.getName());
            project.setDescription(request.getDescription());
            project.setStartDate(request.getStartDate());
            project.setEndDate(request.getEndDate());
            project.setStatus(request.getStatus());

            projectRepository.save(project);

        } catch (Exception ex) {

            throw new RuntimeException("Project Cannot save");
        }
    }

    @Override
    public PaginatedResponse<ProjectResponse> getProject(Long id, Long companyId, String name, LocalDateTime updatedAt,
                                                         ProjectStatus status, LocalDateTime startDate, LocalDateTime endDate,
                                                         int page, int size, String sortBy, String sortOrder) throws CustomException {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(sortOrder), sortBy));


        if (id != null) {
            Project project = projectRepository.findByIdAndCompanyId(id, companyId)
                    .orElseThrow(() -> new CustomException("Project not found with the given id and companyId."));


            return new PaginatedResponse<>(Collections.singletonList(new ProjectResponse(
                    project.getId(),
                    project.getCompanyId(),
                    project.getName(),
                    project.getDescription(),
                    project.getStatus(),
                    project.getStartDate(),
                    project.getEndDate(),
                    project.getUpdatedAt()
            )), 1, false);
        }

        Page<Project> projects;

        if (name != null && updatedAt != null && companyId != null) {
            projects = projectRepository.findByNameAndUpdatedAtAndCompanyId(name, updatedAt, companyId, pageable);
        } else if (name != null && companyId != null) {
            projects = projectRepository.findByNameAndCompanyId(name, companyId, pageable);
        } else if (updatedAt != null && companyId != null) {
            projects = projectRepository.findByUpdatedAtAndCompanyId(updatedAt, companyId, pageable);
        } else if (status != null && companyId != null) {
            projects = projectRepository.findByStatusAndCompanyId(status, companyId, pageable);
        } else if (startDate != null && companyId != null) {
            projects = projectRepository.findByStartDateAndCompanyId(startDate, companyId, pageable);
        } else if (endDate != null && companyId != null) {
            projects = projectRepository.findByEndDateAndCompanyId(endDate, companyId, pageable);
        } else if (companyId != null) {
            projects = projectRepository.findByCompanyId(companyId, pageable);
        } else {
            projects = Page.empty();
        }


        List<ProjectResponse> projectResponses = projects.stream()
                .map(project -> new ProjectResponse(
                        project.getId(),
                        project.getCompanyId(),
                        project.getName(),
                        project.getDescription(),
                        project.getStatus(),
                        project.getStartDate(),
                        project.getEndDate(),
                        project.getUpdatedAt()
                ))
                .collect(Collectors.toList());


        return new PaginatedResponse<>(projectResponses, projects.getTotalElements(), projects.hasNext());
    }


    @Override
    public ProjectResponse updateProject(ProjectDTO projectDTO) {
        Optional<Project> optionalProject = projectRepository.findById(projectDTO.getId());

        if (optionalProject.isPresent()) {
            Project project = optionalProject.get();


            project.setName(projectDTO.getName());
            project.setDescription(projectDTO.getDescription());
            project.setStatus(projectDTO.getStatus());
            project.setStartDate(projectDTO.getStartDate());
            project.setEndDate(projectDTO.getEndDate());

            projectRepository.save(project);

            ProjectResponse projectResponse = new ProjectResponse();
            projectResponse.setId(project.getId());
            projectResponse.setName(project.getName());
            projectResponse.setDescription(project.getDescription());
            projectResponse.setStartDate(project.getStartDate());
            projectResponse.setEndDate(project.getEndDate());
            projectResponse.setStatus(project.getStatus());

            return projectResponse;
        }

        return null; 
    }

    @Override
    public ResponseEntity<String> deleteProject(Long id) {

        Optional<Project> optionalProject = projectRepository.findById(id);

        if (optionalProject.isEmpty()){

            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Project with ID " + id + " not found.");
        }

        projectRepository.deleteById(id);

        return ResponseEntity.ok("Project Deleted Successfully");
    }

}



