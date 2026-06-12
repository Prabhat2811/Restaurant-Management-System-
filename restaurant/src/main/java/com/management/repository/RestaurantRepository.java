package com.management.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.management.entity.Restaurant;

public interface RestaurantRepository extends JpaRepository<Restaurant, Integer> {
	List<Restaurant> findByIsOpenTrue();
	List<Restaurant> findByCuisineContainingIgnoreCase(String cuisine);
	List<Restaurant> findByNameContainingIgnoreCase(String name);
	List<Restaurant> findByNameContainingIgnoreCaseOrCuisineContainingIgnoreCase(String name, String cuisine);
}
