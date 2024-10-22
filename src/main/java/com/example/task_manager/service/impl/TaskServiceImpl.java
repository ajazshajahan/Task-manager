package com.example.task_manager.service.impl;

import com.example.task_manager.dto.TaskDTO;
import com.example.task_manager.entity.Project;
import com.example.task_manager.entity.Task;
import com.example.task_manager.entity.User;
import com.example.task_manager.enums.ProjectStatus;
import com.example.task_manager.enums.TaskStatus;
import com.example.task_manager.exception.CustomException;
import com.example.task_manager.repository.ProjectRepository;
import com.example.task_manager.repository.TaskRepository;
import com.example.task_manager.repository.UserRepository;
import com.example.task_manager.response.PaginatedResponse;
import com.example.task_manager.response.ProjectResponse;
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

    public TaskServiceImpl(TaskRepository taskRepository,
                           UserRepository userRepository,
                           ProjectRepository projectRepository) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
        this.projectRepository = projectRepository;
    }

    @Override
    public void createTask(TaskDTO request) {

        try {
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

            taskRepository.save(task);

        } catch (Exception ex) {

            throw new RuntimeException("task cannot save right now");
        }
    }

    @Override
    public ResponseEntity<String> deleteTask(Long id) {
        Optional<Task> optionalTask = taskRepository.findById(id);
        if (optionalTask.isEmpty()) {

            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("task with ID " + id + " not found.");
        }

        taskRepository.deleteById(id);

        return ResponseEntity.ok("task Deleted Successfully");
    }

    @Override
    public PaginatedResponse<TaskResponse> getTask(Long id, Long companyId, String title, LocalDateTime updatedAt,
                                                   TaskStatus status, LocalDateTime startDate, LocalDateTime endDate,
                                                   int page, int size, String sortBy, String sortOrder) throws CustomException {

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(sortOrder), sortBy));

        // Fetch a single task if an ID is provided
        if (id != null) {
            Task task = taskRepository.findByIdAndCompanyId(id, companyId)
                    .orElseThrow(() -> new CustomException("Task not found with the given id and companyId."));

            return new PaginatedResponse<>(Collections.singletonList(new TaskResponse(
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
            )), 1, false);
        }

        Page<Task> tasks;


        if (companyId != null) {
            if (title != null && updatedAt != null) {
                tasks = taskRepository.findByTitleAndUpdatedAtAndCompanyId(title, updatedAt, companyId, pageable);
            } else if (title != null) {
                tasks = taskRepository.findByTitleAndCompanyId(title, companyId, pageable);
            } else if (updatedAt != null) {
                tasks = taskRepository.findByUpdatedAtAndCompanyId(updatedAt, companyId, pageable);
            } else if (status != null) {
                tasks = taskRepository.findByStatusAndCompanyId(status, companyId, pageable);
            } else if (startDate != null) {
                tasks = taskRepository.findByStartDateAndCompanyId(startDate, companyId, pageable);
            } else if (endDate != null) {
                tasks = taskRepository.findByEndDateAndCompanyId(endDate, companyId, pageable);
            } else {
                tasks = taskRepository.findByCompanyId(companyId, pageable);
            }
        } else {
            tasks = Page.empty();
        }


        List<TaskResponse> taskResponses = tasks.stream()
                .map(task -> new TaskResponse(
                        task.getId(),
                        task.getProject() != null ? task.getProject().getId() : null, // Handle potential null
                        task.getUser() != null ? task.getUser().getId() : null, // Handle potential null
                        task.getTitle(),
                        task.getDescription(),
                        task.getPriority(), // Include priority
                        task.getStatus(),
                        task.getStartDate(),
                        task.getEndDate(),
                        task.getCompanyId(), // Include companyId
                        task.getUpdatedAt()
                ))
                .collect(Collectors.toList());


        return new PaginatedResponse<>(taskResponses, tasks.getTotalElements(), tasks.hasNext());
    }



    @Override
    public TaskResponse updateTask(TaskDTO taskDTO) {

        Optional<Task> optionalTask = taskRepository.findById(taskDTO.getId());

        if (optionalTask.isPresent()){

            Task task = optionalTask.get();

            task.setTitle(taskDTO.getTitle());
            task.setCompanyId(taskDTO.getCompanyId());
            task.setProject(getProject(taskDTO.getProjectId()));
            task.setUser(getUser(taskDTO.getUserId()));
            task.setDescription(taskDTO.getDescription());
            task.setStatus(taskDTO.getStatus());
            task.setPriority(taskDTO.getPriority());
            task.setStartDate(taskDTO.getStartDate());
            task.setEndDate(taskDTO.getEndDate());

            taskRepository.save(task);

            TaskResponse taskResponse = new TaskResponse();
            taskResponse.setId(task.getId());
            taskResponse.setTitle(task.getTitle());
            taskResponse.setCompanyId(task.getCompanyId());
            taskResponse.setUserId(getUserId(task.getUser()));
            taskResponse.setProjectId(getProjectId(task.getProject()));
            taskResponse.setStatus(task.getStatus());
            taskResponse.setUpdatedAt(task.getUpdatedAt());
            taskResponse.setDescription(task.getDescription());
            taskResponse.setStartDate(task.getStartDate());
            taskResponse.setEndDate(task.getEndDate());

            return taskResponse;
        }

        return null;
    }

    private Long getProjectId(Project project) {

        Optional<Task> optionalTask = taskRepository.findById(project.getId());

        if (optionalTask.isPresent()) {

            Task task = optionalTask.get();

            return task.getId();
        }

        return null;
    }

    private Long getUserId(User user) {

        Optional<Task> optionalTask = taskRepository.findById(user.getId());

        if (optionalTask.isPresent()) {

            Task task = optionalTask.get();

            return task.getId();
        }

        return null;
    }


    private User getUser(Long userId) {
        Optional<User> optionalUser = userRepository.findById(userId);

        return optionalUser.orElse(null);
    }


    private Project getProject(Long projectId) {

        Optional<Project> optionalProject = projectRepository.findById(projectId);

        return optionalProject.orElse(null);
    }
}
