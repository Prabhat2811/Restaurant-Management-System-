package com.management.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.management.entity.DeliveryAgent;

public interface DeliveryAgentRepository extends JpaRepository<DeliveryAgent, Integer> {
	List<DeliveryAgent> findByAvailableTrue();
    boolean existsByVehicleNumber(String vehicleNumber);
}
