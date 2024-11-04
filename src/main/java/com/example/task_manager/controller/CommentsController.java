package com.example.task_manager.controller;

import com.example.task_manager.dto.CommentsDTO;
import com.example.task_manager.response.*;
import com.example.task_manager.service.CommentsService;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.example.task_manager.constant.AppConstant.FAILED;
import static com.example.task_manager.constant.AppConstant.SUCCESS;

@Slf4j
@RestController
@RequestMapping("/api")
public class CommentsController {

    private static final Logger log = LoggerFactory.getLogger(CommentsController.class);
    private final CommentsService commentsService;

    @Autowired
    public CommentsController(CommentsService commentsService) {
        this.commentsService = commentsService;
    }

    @PostMapping("/save/comment")
    public ResponseEntity<AppResponse<Void>> commentsSaving(@RequestBody CommentsDTO request) {

        commentsService.saveComments(request);

        AppResponse<Void> appResponse = new AppResponse<>(null, "SUCCESS", "Comments saved Successfully");

        return ResponseEntity.status(HttpStatus.CREATED).body(appResponse);


    }

    public ResponseEntity<AppResponse<PaginatedResponse<CommentsResponse>>> getComments
            (@RequestParam(name = "id",required = false) Long id,
             @RequestParam(name = "taskId",required = false) Long taskId,
             @RequestParam(name = "userId",required = false) Long userId,
             @RequestParam(value = "page", defaultValue = "0") int page,
             @RequestParam(value = "size", defaultValue = "10") int size,
             @RequestParam(value = "sortBy", defaultValue = "id") String sortBy,
             @RequestParam(value = "sortOrder", defaultValue = "asc") String sortOrder) {
        log.info("Received request to fetch project data with id: {},taskId: {},userId: {}, page: {}, size: {}, sortBy: {}, sortOrder: {}",
                id, taskId,userId, page, size, sortBy, sortOrder);

        try {

            PaginatedResponse<CommentsResponse> response = commentsService.getComments(id,taskId,userId, page, size, sortBy, sortOrder);

            if (response.getData().isEmpty()) {
                log.warn("No comments found for filters: id: {},taskId: {},userId: {}",
                        id, taskId,userId);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                        AppResponse.<PaginatedResponse<CommentsResponse>>builder()
                                .status(FAILED)
                                .message("No projects found")
                                .build()
                );
            }

            log.info("Successfully fetched comments data: {}", response);
            return ResponseEntity.ok(
                    AppResponse.<PaginatedResponse<CommentsResponse>>builder()
                            .data(response)
                            .status(SUCCESS)
                            .message("Project data fetched successfully")
                            .build()
            );

        } catch (Exception e) {
            log.error("Failed to fetch project data for id: {}, taskId: {}, userId: {}, page: {}, size: {}, sortBy: {}, sortOrder: {}",
                    id, taskId,userId, page, size, sortBy, sortOrder, e);

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    AppResponse.<PaginatedResponse<CommentsResponse>>builder()
                            .status(FAILED)
                            .message("Failed to fetch Comments data: " + e.getMessage())
                            .build()
            );
        }
    }

    @PutMapping("/update/comment")
    public ResponseEntity<AppResponse<CommentsResponse>> updateComment(@RequestBody CommentsDTO commentsDTO) {

        AppResponse<CommentsResponse> appResponse = new AppResponse<>();

        try {


            CommentsResponse commentsResponse = commentsService.updateComments(commentsDTO);


            if (commentsResponse == null) {
                appResponse.setStatus("FAILED");
                appResponse.setMessage("Task not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(appResponse);
            }

            appResponse.setData(commentsResponse);
            appResponse.setStatus("SUCCESS");
            appResponse.setMessage("Comment Found successfully");

            return ResponseEntity.ok(appResponse);
        } catch (Exception ex) {
            appResponse.setStatus("FAILED");
            appResponse.setMessage("An error occurred while fetching the comment: " + ex.getMessage());

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(appResponse);
        }
    }

    @DeleteMapping("/delete/comment")
    public ResponseEntity<String> deleteComments(@RequestParam(name = "id", required = false) Long id) {

        return commentsService.deleteComments(id);
    }
}
