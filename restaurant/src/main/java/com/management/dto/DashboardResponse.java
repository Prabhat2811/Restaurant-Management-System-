package com.management.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DashboardResponse {

    private Integer totalCategories;
    private Integer totalItems;
    private Long availableItems;
    private Long unavailableItems;
}
