package com.management.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderItemDto {
	private Integer menuItemId;
	 private String itemName;
	    private Integer quantity;
	    private Double price;
}
