package com.management.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.management.dto.MenuCategoryDto;
import com.management.dto.ResponseStructure;
import com.management.entity.MenuCategory;
import com.management.service.MenuCategoryService;

@RestController
@RequestMapping("/category")
public class MenuCategoryController {
    @Autowired
    private MenuCategoryService categoryService;

    @PostMapping("/add")
    public ResponseEntity<ResponseStructure<MenuCategory>> add(@RequestBody MenuCategoryDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(categoryService.addCategory(dto));
    }

    @GetMapping("/restaurant/{restaurantId}")
    public ResponseEntity<ResponseStructure<List<MenuCategory>>> getByRestaurant(@PathVariable Integer restaurantId) {
        return ResponseEntity.status(HttpStatus.FOUND).body(categoryService.getByRestaurant(restaurantId));
    }
}
