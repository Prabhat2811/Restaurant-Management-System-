package com.management.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.management.entity.MenuItem;

public interface MenuItemRepository extends JpaRepository<MenuItem, Integer> {
	List<MenuItem> findByCategory_Id(Integer categoryId);
    List<MenuItem> findByAvailableTrue();
    List<MenuItem> findByCategory_Restaurant_Id(Integer restaurantId);
}
