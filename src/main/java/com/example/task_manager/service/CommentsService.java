package com.example.task_manager.service;

import com.example.task_manager.dto.CommentsDTO;
import com.example.task_manager.exception.CustomException;
import com.example.task_manager.response.CommentsResponse;
import com.example.task_manager.response.PaginatedResponse;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface CommentsService {

    void saveComments(CommentsDTO request) throws CustomException;

    ResponseEntity<String> deleteComments(Long id);

    CommentsResponse updateComments(CommentsDTO commentsDTO) throws CustomException;

    PaginatedResponse<CommentsResponse> getComments(Long id, Long taskId, Long userId,int page, int size, String sortBy, String sortOrder) throws CustomException;
}
