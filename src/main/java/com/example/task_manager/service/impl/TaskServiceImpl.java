package com.example.task_manager.service.impl;

import com.example.task_manager.dto.TaskDTO;
import com.example.task_manager.entity.Project;
import com.example.task_manager.entity.Task;
import com.example.task_manager.entity.User;
import com.example.task_manager.enums.TaskStatus;
import com.example.task_manager.exception.CustomException;
import com.example.task_manager.repository.ProjectRepository;
import com.example.task_manager.repository.TaskRepository;
import com.example.task_manager.repository.UserRepository;
import com.example.task_manager.response.PaginatedResponse;
import com.example.task_manager.response.TaskResponse;
import com.example.task_manager.service.TaskService;
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

@Service
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;

    public TaskServiceImpl(TaskRepository taskRepository, UserRepository userRepository, ProjectRepository projectRepository) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
        this.projectRepository = projectRepository;
    }

    @Override
    public void createTask(TaskDTO request) {
        Task task = mapToTaskEntity(request);
        try {
            taskRepository.save(task);
        } catch (Exception ex) {
            throw new RuntimeException("Task cannot be saved right now", ex);
        }
    }

    @Override
    public ResponseEntity<String> deleteTask(Long id) {
        if (!taskRepository.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Task with ID " + id + " not found.");
        }
        taskRepository.deleteById(id);
        return ResponseEntity.ok("Task deleted successfully");
    }

    @Override
    public PaginatedResponse<TaskResponse> getTask(Long id, Long companyId, String title,
                                                   LocalDateTime updatedAt, TaskStatus status,
                                                   LocalDateTime startDate, LocalDateTime endDate,
                                                   int page, int size, String sortBy, String sortOrder) throws CustomException {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(sortOrder), sortBy));

        if (id != null) {
            return getTaskById(id, companyId);
        }

        Page<Task> tasks = findTasks(companyId, title, updatedAt, status, startDate, endDate, pageable);
        List<TaskResponse> taskResponses = tasks.stream()
                .map(this::mapToTaskResponse)
                .collect(Collectors.toList());

        return new PaginatedResponse<>(taskResponses, tasks.getTotalElements(), tasks.hasNext());
    }

    private PaginatedResponse<TaskResponse> getTaskById(Long id, Long companyId) throws CustomException {
        Task task = taskRepository.findByIdAndCompanyId(id, companyId)
                .orElseThrow(() -> new CustomException("Task not found with the given id and companyId."));
        return new PaginatedResponse<>(Collections.singletonList(mapToTaskResponse(task)), 1, false);
    }

    private Page<Task> findTasks(Long companyId, String title, LocalDateTime updatedAt,
                                 TaskStatus status, LocalDateTime startDate,
                                 LocalDateTime endDate, Pageable pageable) {
        if (companyId == null) return Page.empty();

        if (title != null && updatedAt != null) {
            return taskRepository.findByTitleAndUpdatedAtAndCompanyId(title, updatedAt, companyId, pageable);
        } else if (title != null) {
            return taskRepository.findByTitleAndCompanyId(title, companyId, pageable);
        } else if (updatedAt != null) {
            return taskRepository.findByUpdatedAtAndCompanyId(updatedAt, companyId, pageable);
        } else if (status != null) {
            return taskRepository.findByStatusAndCompanyId(status, companyId, pageable);
        } else if (startDate != null) {
            return taskRepository.findByStartDateAndCompanyId(startDate, companyId, pageable);
        } else if (endDate != null) {
            return taskRepository.findByEndDateAndCompanyId(endDate, companyId, pageable);
        } else {
            return taskRepository.findByCompanyId(companyId, pageable);
        }
    }

    @Override
    public TaskResponse updateTask(TaskDTO taskDTO) throws CustomException {
        Task task = taskRepository.findById(taskDTO.getId())
                .orElseThrow(() -> new CustomException("Task not found for ID: " + taskDTO.getId()));

        updateTaskFromDto(task, taskDTO);
        taskRepository.save(task);
        return mapToTaskResponse(task);
    }

    private void updateTaskFromDto(Task task, TaskDTO taskDTO) {
        task.setTitle(taskDTO.getTitle());
        task.setCompanyId(taskDTO.getCompanyId());
        task.setProject(getProject(taskDTO.getProjectId()));
        task.setUser(getUser(taskDTO.getUserId()));
        task.setDescription(taskDTO.getDescription());
        task.setStatus(taskDTO.getStatus());
        task.setPriority(taskDTO.getPriority());
        task.setStartDate(taskDTO.getStartDate());
        task.setEndDate(taskDTO.getEndDate());
    }

    private Task mapToTaskEntity(TaskDTO request) {
        Task task = new Task();
        task.setTitle(request.getTitle());
        task.setCompanyId(request.getCompanyId());
        task.setProject(getProject(request.getProjectId()));
        task.setUser(getUser(request.getUserId()));
        task.setDescription(request.getDescription());
        task.setStatus(request.getStatus());
        task.setPriority(request.getPriority());
        task.setStartDate(request.getStartDate());
        task.setEndDate(request.getEndDate());
        return task;
    }

    private TaskResponse mapToTaskResponse(Task task) {
        return new TaskResponse(
                task.getId(),
                task.getProject() != null ? task.getProject().getId() : null,
                task.getUser() != null ? task.getUser().getId() : null,
                task.getTitle(),
                task.getDescription(),
                task.getPriority(),
                task.getStatus(),
                task.getStartDate(),
                task.getEndDate(),
                task.getCompanyId(),
                task.getUpdatedAt()
        );
    }

    private User getUser(Long userId) {
        return userRepository.findById(userId).orElse(null);
    }

    private Project getProject(Long projectId) {
        return projectRepository.findById(projectId).orElse(null);
    }
}
