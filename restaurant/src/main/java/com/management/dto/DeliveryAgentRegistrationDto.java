package com.management.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DeliveryAgentRegistrationDto {

    private String name;
    private String email;
    private String password;
    private String phone;

    private String vehicleNumber;
    private double rating;
}
