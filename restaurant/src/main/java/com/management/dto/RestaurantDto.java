package com.management.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RestaurantDto {
	private Integer id;
	private String name;
	private String cuisine;
	private String address;
	private String phone;
	private Boolean isOpen;
}
