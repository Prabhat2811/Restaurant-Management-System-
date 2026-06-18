package com.management.service;

import java.util.List;

import org.jspecify.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.management.dto.MenuCategoryDto;
import com.management.dto.ResponseStructure;
import com.management.entity.MenuCategory;
import com.management.entity.Restaurant;
import com.management.exception.IdNotFoundException;
import com.management.exception.ResourceNotFoundException;
import com.management.repository.MenuCategoryRepository;
import com.management.repository.RestaurantRepository;

@Service
public class MenuCategoryService {
    @Autowired
    private MenuCategoryRepository categoryRepository;
    @Autowired
    private RestaurantRepository restaurantRepository;

    public ResponseStructure<MenuCategory> addCategory(MenuCategoryDto dto) {
        Restaurant r = restaurantRepository.findById(dto.getRestaurantId())
                .orElseThrow(() -> new IdNotFoundException("Restaurant Not Found"));
        MenuCategory cat = new MenuCategory();
        cat.setName(dto.getName());
        cat.setRestaurant(r);
        return ResponseStructure.<MenuCategory>builder()
                .statusCode(HttpStatus.CREATED.value())
                .message("Category Added")
                .data(categoryRepository.save(cat)).build();
    }

    public ResponseStructure<List<MenuCategory>> getByRestaurant(Integer restaurantId) {
        List<MenuCategory> list = categoryRepository.findByRestaurant_Id(restaurantId);
        if (list.isEmpty()) throw new ResourceNotFoundException("No Categories Found");
        return ResponseStructure.<List<MenuCategory>>builder()
                .statusCode(HttpStatus.OK.value())
                .message(list.size() + " Category(s) Found")
                .data(list).build();
    }

	
}
