package com.management.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.management.entity.MenuCategory;

public interface MenuCategoryRepository extends JpaRepository<MenuCategory, Integer> {
	List<MenuCategory> findByRestaurant_Id(Integer restaurantId);

	List<MenuCategory> findByRestaurantId(Integer id);
}
