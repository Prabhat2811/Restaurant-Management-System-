package com.management.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.management.dto.OrderDto;
import com.management.dto.OrderItemDto;
import com.management.dto.OrderStatus;
import com.management.dto.PaymentStatus;
import com.management.dto.ResponseStructure;
import com.management.entity.Customer;
import com.management.entity.DeliveryAgent;
import com.management.entity.MenuItem;
import com.management.entity.Order;
import com.management.entity.OrderItem;
import com.management.entity.Payment;
import com.management.entity.Restaurant;
import com.management.exception.IdNotFoundException;
import com.management.exception.ResourceNotFoundException;
import com.management.exception.RuleViolationException;
import com.management.repository.CustomerRepository;
import com.management.repository.DeliveryAgentRepository;
import com.management.repository.MenuItemRepository;
import com.management.repository.OrderRepository;
import com.management.repository.RestaurantRepository;

@Service
public class OrderService {
    @Autowired
    private OrderRepository orderRepository;
    @Autowired 
    private CustomerRepository customerRepository;
    @Autowired
    private RestaurantRepository restaurantRepository;
    @Autowired 
    private MenuItemRepository menuItemRepository;
    @Autowired 
    private DeliveryAgentRepository agentRepository;

    public ResponseStructure<Order> placeOrder(OrderDto dto) {
        Customer customer = customerRepository.findById(dto.getCustomerId())
                .orElseThrow(() -> new IdNotFoundException("Customer Not Found"));
        Restaurant restaurant = restaurantRepository.findById(dto.getRestaurantId())
                .orElseThrow(() -> new IdNotFoundException("Restaurant Not Found"));

        if (!restaurant.getIsOpen())
            throw new RuleViolationException("Restaurant is currently closed");

        Order order = new Order();
        order.setCustomer(customer);
        order.setRestaurant(restaurant);
        order.setStatus(OrderStatus.PLACED);

        List<OrderItem> items = new ArrayList<>();
        double total = 0.0;

        for (OrderItemDto i : dto.getItems()) {
            MenuItem menuItem = menuItemRepository.findById(i.getMenuItemId())
                    .orElseThrow(() -> new IdNotFoundException("Menu Item Not Found: " + i.getMenuItemId()));
            if (!menuItem.getAvailable())
                throw new RuleViolationException("Menu Item not available: " + menuItem.getName());

            OrderItem orderItem = new OrderItem();
            orderItem.setMenuItem(menuItem);
            orderItem.setQuantity(i.getQuantity());
            orderItem.setPrice(menuItem.getPrice() * i.getQuantity());
            items.add(orderItem);
            total += orderItem.getPrice();
        }

        order.setOrderItems(items);
        order.setTotalAmount(total);

        Payment payment = new Payment();
        payment.setAmount(total);
        payment.setPaymentMethod(dto.getPaymentMethod());
        payment.setPaymentStatus(PaymentStatus.PENDING);
        order.setPayment(payment);

        return ResponseStructure.<Order>builder()
                .statusCode(HttpStatus.CREATED.value())
                .message("Order Placed Successfully")
                .data(orderRepository.save(order)).build();
    }

    public ResponseStructure<Order> updateOrderStatus(Integer orderId, OrderStatus status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IdNotFoundException("Order Not Found"));

        if (order.getStatus() == OrderStatus.DELIVERED ||
            order.getStatus() == OrderStatus.CANCELLED)
            throw new RuleViolationException("Cannot update a " + order.getStatus() + " order");

        order.setStatus(status);

        if (status == OrderStatus.DELIVERED) {
            order.getPayment().setPaymentStatus(PaymentStatus.SUCCESS);
            if (order.getDeliveryAgent() != null)
                order.getDeliveryAgent().setAvailable(true);
        }

        return ResponseStructure.<Order>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Order Status Updated to " + status)
                .data(orderRepository.save(order)).build();
    }

    public ResponseStructure<Order> assignAgent(Integer orderId, Integer agentId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IdNotFoundException("Order Not Found"));
        DeliveryAgent agent = agentRepository.findById(agentId)
                .orElseThrow(() -> new IdNotFoundException("Agent Not Found"));

        if (!agent.getAvailable())
            throw new RuleViolationException("Delivery Agent is not available");

        order.setDeliveryAgent(agent);
        order.setStatus(OrderStatus.OUT_FOR_DELIVERY);
        agent.setAvailable(false);
        agentRepository.save(agent);

        return ResponseStructure.<Order>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Agent Assigned Successfully")
                .data(orderRepository.save(order)).build();
    }

    public ResponseStructure<List<Order>> getOrdersByCustomer(Integer customerId) {
        List<Order> orders = orderRepository.findByCustomer_Id(customerId);
        if (orders.isEmpty()) throw new ResourceNotFoundException("No Orders Found");
        return ResponseStructure.<List<Order>>builder()
                .statusCode(HttpStatus.FOUND.value())
                .message(orders.size() + " Order(s) Found")
                .data(orders).build();
    }

    public ResponseStructure<List<Order>> getOrdersByRestaurant(Integer restaurantId) {
        List<Order> orders = orderRepository.findByRestaurant_Id(restaurantId);
        if (orders.isEmpty()) throw new ResourceNotFoundException("No Orders Found");
        return ResponseStructure.<List<Order>>builder()
                .statusCode(HttpStatus.FOUND.value())
                .message(orders.size() + " Order(s) Found")
                .data(orders).build();
    }

    public ResponseStructure<List<Order>> getOrdersByAgent(Integer agentId) {
        List<Order> orders = orderRepository.findByDeliveryAgent_Id(agentId);
        if (orders.isEmpty()) throw new ResourceNotFoundException("No Orders Found");
        return ResponseStructure.<List<Order>>builder()
                .statusCode(HttpStatus.FOUND.value())
                .message(orders.size() + " Order(s) Found")
                .data(orders).build();
    }
}