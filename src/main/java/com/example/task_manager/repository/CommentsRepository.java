package com.example.task_manager.repository;

import com.example.task_manager.entity.Comments;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentsRepository extends JpaRepository<Comments, Long> {


    Page<Comments> findByTaskId(Long taskId, Pageable pageable);

    Page<Comments> findByUserId(Long userId, Pageable pageable);

    Page<Comments> findByTaskIdAndUserId(Long taskId,Long userId,Pageable pageable);
}
