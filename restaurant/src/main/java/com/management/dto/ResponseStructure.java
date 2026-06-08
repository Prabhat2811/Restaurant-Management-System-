package com.management.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ResponseStructure<T> {
	private int statusCode;
	private String message;
	private T data;
}
