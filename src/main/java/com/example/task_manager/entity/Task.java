package com.example.task_manager.entity;

import com.example.task_manager.enums.TaskPriority;
import com.example.task_manager.enums.TaskStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table
@Getter
@Setter
public class Task extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn (name = "userId",nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "projectId",nullable = false)
    private Project project;

    @OneToMany
    @JoinColumn(name = "commentsId",nullable = false)
    private Comments comments;

    @Column( nullable = false)
    private String title;;

    @Column( nullable = false)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column( nullable = false)
    private TaskPriority priority;

    @Enumerated(EnumType.STRING)
    @Column( nullable = false)
    private TaskStatus status;

    @Column(nullable = false)
    private LocalDateTime startDate;

    @Column(nullable = false)
    private LocalDateTime endDate;

}
