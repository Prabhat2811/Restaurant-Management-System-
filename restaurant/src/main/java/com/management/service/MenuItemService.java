package com.management.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.management.dto.MenuItemDto;
import com.management.dto.ResponseStructure;
import com.management.entity.MenuCategory;
import com.management.entity.MenuItem;
import com.management.exception.IdNotFoundException;
import com.management.exception.ResourceNotFoundException;
import com.management.repository.MenuCategoryRepository;
import com.management.repository.MenuItemRepository;

@Service
public class MenuItemService {
    @Autowired
    private MenuItemRepository menuItemRepository;
    @Autowired
    private MenuCategoryRepository categoryRepository;

    public ResponseStructure<MenuItem> addMenuItem(MenuItemDto dto) {
        MenuCategory category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new IdNotFoundException("Category Not Found"));
        MenuItem item = new MenuItem();
        item.setName(dto.getName());
        item.setDescription(dto.getDescription());
        item.setPrice(dto.getPrice());
        item.setAvailable(true);
        item.setCategory(category);
        return ResponseStructure.<MenuItem>builder()
                .statusCode(HttpStatus.CREATED.value())
                .message("Menu Item Added Successfully")
                .data(menuItemRepository.save(item)).build();
    }

    public ResponseStructure<MenuItem> updateMenuItem(MenuItemDto dto) {
        MenuItem item = menuItemRepository.findById(dto.getId())
                .orElseThrow(() -> new IdNotFoundException("Menu Item Not Found"));
        item.setName(dto.getName());
        item.setDescription(dto.getDescription());
        item.setPrice(dto.getPrice());
        item.setAvailable(dto.getAvailable());
        return ResponseStructure.<MenuItem>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Menu Item Updated Successfully")
                .data(menuItemRepository.save(item)).build();
    }

    public ResponseStructure<List<MenuItemDto>> getByRestaurant(Integer restaurantId) {

        List<MenuItem> items =
                menuItemRepository.findByCategory_Restaurant_Id(restaurantId);

        if (items.isEmpty()) {
            throw new ResourceNotFoundException("No Menu Items Found");
        }

        List<MenuItemDto> dtoList = items.stream().map(item -> {

            MenuItemDto dto = new MenuItemDto();

            dto.setId(item.getId());
            dto.setName(item.getName());
            dto.setDescription(item.getDescription());
            dto.setPrice(item.getPrice());
            dto.setAvailable(item.getAvailable());

            if (item.getCategory() != null) {
                dto.setCategoryId(item.getCategory().getId());
                dto.setCategoryName(item.getCategory().getName());
            }

            return dto;

        }).toList();

        return ResponseStructure.<List<MenuItemDto>>builder()
                .statusCode(HttpStatus.OK.value())
                .message(dtoList.size() + " Item(s) Found")
                .data(dtoList)
                .build();
    }

    public ResponseStructure<String> deleteMenuItem(Integer id) {
        MenuItem item = menuItemRepository.findById(id)
                .orElseThrow(() -> new IdNotFoundException("Menu Item Not Found"));
        menuItemRepository.delete(item);
        return ResponseStructure.<String>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Menu Item Deleted")
                .data("Deleted: " + item.getName()).build();
    }
}
