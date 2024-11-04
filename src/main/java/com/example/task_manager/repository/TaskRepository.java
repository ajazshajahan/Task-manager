package com.example.task_manager.repository;

import com.example.task_manager.entity.Project;
import com.example.task_manager.entity.Task;
import com.example.task_manager.enums.ProjectStatus;
import com.example.task_manager.enums.TaskStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface TaskRepository extends JpaRepository<Task,Long> {


    Optional<Task> findByIdAndCompanyId(Long id, Long companyId);

    Page<Task> findByTitleAndCompanyId(String title, Long companyId,Pageable pageable);

    Page<Task> findByUpdatedAtAndCompanyId(LocalDateTime updatedAt, Long companyId,Pageable pageable);

    Page<Task> findByTitleAndUpdatedAtAndCompanyId(String title, LocalDateTime updatedAt, Long companyId,Pageable pageable);

    Page<Task> findByStatusAndCompanyId(TaskStatus status, Long companyId, Pageable pageable);

    Page<Task> findByStartDateAndCompanyId(LocalDateTime startDate, Long companyId,Pageable pageable);

    Page<Task> findByEndDateAndCompanyId(LocalDateTime endDate, Long companyId,Pageable pageable);

    Page<Task> findByCompanyId(Long companyId, Pageable pageable);
}
