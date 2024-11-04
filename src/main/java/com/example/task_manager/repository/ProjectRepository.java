package com.example.task_manager.repository;

import com.example.task_manager.entity.Project;
import com.example.task_manager.enums.ProjectStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface ProjectRepository extends JpaRepository<Project, Long> {


    Optional<Project> findByIdAndCompanyId(Long id, Long companyId);

    Page<Project> findByNameAndCompanyId(String name, Long companyId,Pageable pageable);

    Page<Project> findByUpdatedAtAndCompanyId(LocalDateTime updatedAt, Long companyId,Pageable pageable);

    Page<Project> findByNameAndUpdatedAtAndCompanyId(String name, LocalDateTime updatedAt, Long companyId,Pageable pageable);

    Page<Project> findByStatusAndCompanyId(ProjectStatus status, Long companyId,Pageable pageable);

    Page<Project> findByStartDateAndCompanyId(LocalDateTime startDate, Long companyId,Pageable pageable);

    Page<Project> findByEndDateAndCompanyId(LocalDateTime endDate, Long companyId,Pageable pageable);

    Page<Project> findByCompanyId(Long companyId, Pageable pageable);
}
