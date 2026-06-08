package com.management.dto;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderDto {
	private Integer customerId;
    private Integer restaurantId;
    private List<OrderItemDto> items;
    private PaymentMethod paymentMethod;
}
