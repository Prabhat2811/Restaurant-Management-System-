package com.management.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.management.entity.Restaurant;

public interface RestaurantRepository extends JpaRepository<Restaurant, Integer> {
	List<Restaurant> findByIsOpenTrue();
    List<Restaurant> findByCuisineIgnoreCase(String cuisine);
    List<Restaurant> findByNameContainingIgnoreCase(String name);
}
