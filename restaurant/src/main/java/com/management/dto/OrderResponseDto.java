package com.management.dto;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter 
@Setter 
public class OrderResponseDto {

    private Integer id;
    private String status;
    private Double totalAmount;
    private LocalDateTime orderedAt;

    private String restaurantName;

    private String paymentMethod;
    private String paymentStatus;

    private List<OrderItemDto> items;
}
