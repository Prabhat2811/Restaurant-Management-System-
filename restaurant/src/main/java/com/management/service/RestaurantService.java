package com.management.service;

import java.lang.module.ModuleDescriptor.Builder;
import java.util.List;

import org.jspecify.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.management.dto.DashboardResponse;
import com.management.dto.ResponseStructure;
import com.management.dto.RestaurantDto;
import com.management.entity.MenuItem;
import com.management.entity.Restaurant;
import com.management.exception.IdNotFoundException;
import com.management.exception.ResourceNotFoundException;
import com.management.repository.RestaurantRepository;

@Service
public class RestaurantService {
    @Autowired
    private RestaurantRepository restaurantRepository;

    public ResponseStructure<Restaurant> addRestaurant(RestaurantDto dto) {
        Restaurant r = new Restaurant();
        r.setName(dto.getName());
        r.setCuisine(dto.getCuisine());
        r.setAddress(dto.getAddress());
        r.setPhone(dto.getPhone());
        r.setIsOpen(true);
        r.setRating(0.0);
        return ResponseStructure.<Restaurant>builder()
                .statusCode(HttpStatus.CREATED.value())
                .message("Restaurant Added Successfully")
                .data(restaurantRepository.save(r)).build();
    }

    public ResponseStructure<Restaurant> updateRestaurant(RestaurantDto dto) {
        Restaurant r = restaurantRepository.findById(dto.getId())
                .orElseThrow(() -> new IdNotFoundException("Restaurant Not Found"));
        r.setName(dto.getName());
        r.setCuisine(dto.getCuisine());
        r.setAddress(dto.getAddress());
        r.setPhone(dto.getPhone());
        r.setIsOpen(dto.getIsOpen());
        return ResponseStructure.<Restaurant>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Restaurant Updated Successfully")
                .data(restaurantRepository.save(r)).build();
    }

    public ResponseStructure<List<Restaurant>> getAllOpen() {
        List<Restaurant> list = restaurantRepository.findByIsOpenTrue();
        if (list.isEmpty()) throw new ResourceNotFoundException("No Open Restaurants Found");
        return ResponseStructure.<List<Restaurant>>builder()
                .statusCode(HttpStatus.OK.value())
                .message(list.size() + " Restaurant(s) Found")
                .data(list).build();
    }

    public ResponseStructure<List<Restaurant>> getByCuisine(String cuisine) {

        List<Restaurant> list =
                restaurantRepository.findByCuisineContainingIgnoreCase(cuisine);

        return ResponseStructure.<List<Restaurant>>builder()
                .statusCode(HttpStatus.OK.value())
                .message(list.size() + " Restaurant(s) Found")
                .data(list)
                .build();
    }

    public ResponseStructure<String> deleteRestaurant(Integer id) {
        Restaurant r = restaurantRepository.findById(id)
                .orElseThrow(() -> new IdNotFoundException("Restaurant Not Found"));
        restaurantRepository.delete(r);
        return ResponseStructure.<String>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Restaurant Deleted Successfully")
                .data("Deleted: " + r.getName()).build();
    }
    
    public ResponseStructure<List<Restaurant>> getAllRestaurants() {

        List<Restaurant> list = restaurantRepository.findAll();

        return ResponseStructure.<List<Restaurant>>builder()
                .statusCode(HttpStatus.OK.value())
                .message(list.size() + " Restaurant(s) Found")
                .data(list)
                .build();
    }
    
    public ResponseStructure<List<Restaurant>> searchByName(String keyword) {

        List<Restaurant> list =
                restaurantRepository
                .findByNameContainingIgnoreCaseOrCuisineContainingIgnoreCase(
                        keyword,
                        keyword);

        return ResponseStructure.<List<Restaurant>>builder()
                .statusCode(HttpStatus.OK.value())
                .message(list.size() + " Restaurant(s) Found")
                .data(list)
                .build();
    }

    public @Nullable ResponseStructure<Restaurant> getById(Integer id) {

        Restaurant restaurant = restaurantRepository.findById(id)
                .orElseThrow(() -> new IdNotFoundException("Restaurant Not Found"));

        return ResponseStructure.<Restaurant>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Restaurant Found")
                .data(restaurant)
                .build();
    }
    
    public ResponseStructure<DashboardResponse> getDashboard(Integer restaurantId) {

        Restaurant restaurant =
                restaurantRepository.findById(restaurantId)
                        .orElseThrow(() ->
                                new IdNotFoundException(
                                        "Restaurant Not Found"));

        int totalCategories =
                restaurant.getCategories().size();

        int totalItems =
                restaurant.getCategories()
                        .stream()
                        .mapToInt(
                                c -> c.getItems().size())
                        .sum();

        long availableItems =
                restaurant.getCategories()
                        .stream()
                        .flatMap(
                                c -> c.getItems().stream())
                        .filter(MenuItem::getAvailable)
                        .count();

        long unavailableItems =
                totalItems - availableItems;

        DashboardResponse dashboard =
                DashboardResponse.builder()
                        .totalCategories(totalCategories)
                        .totalItems(totalItems)
                        .availableItems(availableItems)
                        .unavailableItems(unavailableItems)
                        .build();

        return ResponseStructure
                .<DashboardResponse>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Dashboard Loaded Successfully")
                .data(dashboard)
                .build();
    }
}
