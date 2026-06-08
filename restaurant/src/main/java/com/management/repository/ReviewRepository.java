package com.management.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.management.entity.Review;

public interface ReviewRepository extends JpaRepository<Review, Integer> {
	List<Review> findByRestaurant_Id(Integer restaurantId);
    List<Review> findByCustomer_Id(Integer customerId);
    boolean existsByCustomer_IdAndRestaurant_Id(Integer customerId, Integer restaurantId);
}
