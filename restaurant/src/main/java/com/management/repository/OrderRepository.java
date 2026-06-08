package com.management.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.management.dto.OrderStatus;
import com.management.entity.Order;

public interface OrderRepository extends JpaRepository<Order, Integer> {
	List<Order> findByCustomer_Id(Integer customerId);
    List<Order> findByRestaurant_Id(Integer restaurantId);
    List<Order> findByDeliveryAgent_Id(Integer agentId);
    List<Order> findByStatus(OrderStatus status);
    List<Order> findByCustomer_IdAndStatus(Integer customerId, OrderStatus status);
}
