package com.management.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReviewDto {
	private Integer customerId;
    private Integer restaurantId;
    private Integer rating;
    private String comment;
}
