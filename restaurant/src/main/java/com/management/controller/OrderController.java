package com.management.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.management.dto.OrderDto;
import com.management.dto.OrderStatus;
import com.management.dto.ResponseStructure;
import com.management.entity.Order;
import com.management.service.OrderService;

@RestController
@RequestMapping("/order")
public class OrderController {
    @Autowired
    private OrderService orderService;

    @PostMapping("/place")
    public ResponseEntity<ResponseStructure<Order>> place(@RequestBody OrderDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(orderService.placeOrder(dto));
    }

    @PutMapping("/status/{orderId}")
    public ResponseEntity<ResponseStructure<Order>> updateStatus(
            @PathVariable Integer orderId, @RequestParam OrderStatus status) {
        return ResponseEntity.ok(orderService.updateOrderStatus(orderId, status));
    }

    @PutMapping("/assign/{orderId}/{agentId}")
    public ResponseEntity<ResponseStructure<Order>> assignAgent(
            @PathVariable Integer orderId, @PathVariable Integer agentId) {
        return ResponseEntity.ok(orderService.assignAgent(orderId, agentId));
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<ResponseStructure<List<Order>>> byCustomer(@PathVariable Integer customerId) {
        return ResponseEntity.status(HttpStatus.OK).body(orderService.getOrdersByCustomer(customerId));
    }

    @GetMapping("/restaurant/{restaurantId}")
    public ResponseEntity<ResponseStructure<List<Order>>> byRestaurant(@PathVariable Integer restaurantId) {
        return ResponseEntity.status(HttpStatus.OK).body(orderService.getOrdersByRestaurant(restaurantId));
    }

    @GetMapping("/agent/{agentId}")
    public ResponseEntity<ResponseStructure<List<Order>>> byAgent(@PathVariable Integer agentId) {
        return ResponseEntity.status(HttpStatus.OK).body(orderService.getOrdersByAgent(agentId));
    }
    
    @GetMapping("/all")
    public ResponseEntity<ResponseStructure<List<Order>>> getAllOrders() {

        return ResponseEntity.ok(
                orderService.getAllOrders()
        );
    }
}
