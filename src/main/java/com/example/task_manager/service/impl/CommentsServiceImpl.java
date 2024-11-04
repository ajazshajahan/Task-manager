package com.example.task_manager.service.impl;

import com.example.task_manager.dto.CommentsDTO;
import com.example.task_manager.entity.Comments;
import com.example.task_manager.entity.Task;
import com.example.task_manager.entity.User;
import com.example.task_manager.exception.CustomException;
import com.example.task_manager.repository.CommentsRepository;
import com.example.task_manager.repository.TaskRepository;
import com.example.task_manager.repository.UserRepository;
import com.example.task_manager.response.CommentsResponse;
import com.example.task_manager.response.PaginatedResponse;
import com.example.task_manager.service.CommentsService;
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

@Service
public class CommentsServiceImpl implements CommentsService {

    private final CommentsRepository commentsRepository;
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    @Autowired
    public CommentsServiceImpl(CommentsRepository commentsRepository,
                               TaskRepository taskRepository,
                               UserRepository userRepository) {
        this.commentsRepository = commentsRepository;
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
    }

    @Override
    public void saveComments(CommentsDTO request) throws CustomException {
        Comments comment = new Comments();
        comment.setTask(findTaskById(request.getTaskId()));
        comment.setUser(findUserById(request.getUserId()));
        comment.setCompanyId(request.getCompanyId());
        comment.setContent(request.getContent());
        comment.setCreatedAt(LocalDateTime.now());

        commentsRepository.save(comment);
    }

    @Override
    public ResponseEntity<String> deleteComments(Long id) {
        if (!commentsRepository.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Comments with ID " + id + " not found.");
        }

        commentsRepository.deleteById(id);
        return ResponseEntity.ok("Comments deleted successfully.");
    }

    @Override
    public CommentsResponse updateComments(CommentsDTO commentsDTO) throws CustomException {
        Comments comment = findCommentById(commentsDTO.getId());

        comment.setTask(findTaskById(commentsDTO.getTaskId()));
        comment.setUser(findUserById(commentsDTO.getUserId()));
        comment.setCompanyId(commentsDTO.getCompanyId());
        comment.setContent(commentsDTO.getContent());
        comment.setCreatedAt(LocalDateTime.now());

        commentsRepository.save(comment);
        return mapToCommentsResponse(comment);
    }

    @Override
    public PaginatedResponse<CommentsResponse> getComments(Long id, Long taskId, Long userId, int page, int size, String sortBy, String sortOrder) throws CustomException {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(sortOrder), sortBy));

        if (id != null) {
            Comments comment = findCommentById(id);
            return new PaginatedResponse<>(Collections.singletonList(mapToCommentsResponse(comment)), 1, false);
        }

        Page<Comments> comments = fetchComments(taskId, userId, pageable);
        List<CommentsResponse> commentsResponses = comments.stream()
                .map(this::mapToCommentsResponse)
                .collect(Collectors.toList());

        return new PaginatedResponse<>(commentsResponses, comments.getTotalElements(), comments.hasNext());
    }

    private Page<Comments> fetchComments(Long taskId, Long userId, Pageable pageable) {
        if (taskId != null && userId != null) {
            return commentsRepository.findByTaskIdAndUserId(taskId, userId, pageable);
        } else if (taskId != null) {
            return commentsRepository.findByTaskId(taskId, pageable);
        } else if (userId != null) {
            return commentsRepository.findByUserId(userId, pageable);
        } else {
            return Page.empty();
        }
    }

    private Comments findCommentById(Long id) throws CustomException {
        return commentsRepository.findById(id)
                .orElseThrow(() -> new CustomException("Comment not found with the given ID."));
    }

    private User findUserById(Long userId) throws CustomException {
        return userRepository.findById(userId)
                .orElseThrow(() -> new CustomException("User not found with the given ID."));
    }

    private Task findTaskById(Long taskId) throws CustomException {
        return taskRepository.findById(taskId)
                .orElseThrow(() -> new CustomException("Task not found with the given ID."));
    }

    private CommentsResponse mapToCommentsResponse(Comments comment) {
        return new CommentsResponse(
                comment.getId(),
                comment.getTask() != null ? comment.getTask().getId() : null,
                comment.getUser() != null ? comment.getUser().getId() : null,
                comment.getCompanyId(),
                comment.getContent(),
                comment.getCreatedAt()
        );
    }
}
