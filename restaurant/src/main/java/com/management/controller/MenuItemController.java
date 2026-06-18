package com.management.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.management.dto.MenuItemDto;
import com.management.dto.ResponseStructure;
import com.management.entity.MenuItem;
import com.management.service.MenuItemService;

@RestController
@RequestMapping("/menu")
public class MenuItemController {
    @Autowired
    private MenuItemService menuItemService;

    @PostMapping("/add")
    public ResponseEntity<ResponseStructure<MenuItem>> addMenuItem(@RequestBody MenuItemDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(menuItemService.addMenuItem(dto));
    }

    @PutMapping("/update")
    public ResponseEntity<ResponseStructure<MenuItem>> update(@RequestBody MenuItemDto dto) {
        return ResponseEntity.ok(menuItemService.updateMenuItem(dto));
    }

    @GetMapping("/restaurant/{restaurantId}")
    public ResponseEntity<ResponseStructure<List<MenuItemDto>>> getByRestaurant(@PathVariable Integer restaurantId) {
        return ResponseEntity.status(HttpStatus.OK).body(menuItemService.getByRestaurant(restaurantId));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ResponseStructure<String>> delete(@PathVariable Integer id) {
        return ResponseEntity.ok(menuItemService.deleteMenuItem(id));
    }
    
    
}
