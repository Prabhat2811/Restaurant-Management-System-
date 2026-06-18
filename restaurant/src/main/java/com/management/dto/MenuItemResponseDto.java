package com.management.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MenuItemResponseDto {

    private Integer id;
    private String name;
    private String description;
    private Double price;
    private Boolean available;
    private Integer categoryId;
    private String categoryName;
}
