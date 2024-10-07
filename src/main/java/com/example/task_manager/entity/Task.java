package com.example.task_manager.entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Task extends BaseEntity {

    private Long id;

    private String name;

    private String description;

    private String priority;

    private String status;


}
