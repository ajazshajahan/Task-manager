package com.example.task_manager.response;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class PaginatedResponse<T> {


    private List<T> data;
    private long totalRecords;
    private boolean hasNextPage;
}
