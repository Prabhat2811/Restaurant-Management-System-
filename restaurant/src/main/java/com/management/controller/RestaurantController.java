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

import com.management.dto.ResponseStructure;
import com.management.dto.RestaurantDto;
import com.management.entity.Restaurant;
import com.management.service.RestaurantService;

@RestController
@RequestMapping("/restaurant")
public class RestaurantController {
    @Autowired
    private RestaurantService restaurantService;

    @PostMapping("/add")
    public ResponseEntity<ResponseStructure<Restaurant>> add(@RequestBody RestaurantDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(restaurantService.addRestaurant(dto));
    }

    @PutMapping("/update")
    public ResponseEntity<ResponseStructure<Restaurant>> update(@RequestBody RestaurantDto dto) {
        return ResponseEntity.ok(restaurantService.updateRestaurant(dto));
    }

    @GetMapping("/open")
    public ResponseEntity<ResponseStructure<List<Restaurant>>> getOpen() {
        return ResponseEntity.status(HttpStatus.FOUND).body(restaurantService.getAllOpen());
    }

    @GetMapping("/cuisine/{cuisine}")
    public ResponseEntity<ResponseStructure<List<Restaurant>>> getByCuisine(@PathVariable String cuisine) {
        return ResponseEntity.status(HttpStatus.FOUND).body(restaurantService.getByCuisine(cuisine));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ResponseStructure<String>> delete(@PathVariable Integer id) {
        return ResponseEntity.ok(restaurantService.deleteRestaurant(id));
    }
}
