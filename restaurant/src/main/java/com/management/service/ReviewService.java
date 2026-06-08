package com.management.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.management.dto.OrderStatus;
import com.management.dto.ResponseStructure;
import com.management.dto.ReviewDto;
import com.management.entity.Customer;
import com.management.entity.Order;
import com.management.entity.Restaurant;
import com.management.entity.Review;
import com.management.exception.IdNotFoundException;
import com.management.exception.ResourceNotFoundException;
import com.management.exception.RuleViolationException;
import com.management.repository.CustomerRepository;
import com.management.repository.OrderRepository;
import com.management.repository.RestaurantRepository;
import com.management.repository.ReviewRepository;

@Service
public class ReviewService {
    @Autowired
    private ReviewRepository reviewRepository;
    @Autowired 
	private CustomerRepository customerRepository;
    @Autowired
	private RestaurantRepository restaurantRepository;
    @Autowired
	private OrderRepository orderRepository;

    public ResponseStructure<Review> addReview(ReviewDto dto) {
        Customer customer = customerRepository.findById(dto.getCustomerId())
                .orElseThrow(() -> new IdNotFoundException("Customer Not Found"));
        Restaurant restaurant = restaurantRepository.findById(dto.getRestaurantId())
                .orElseThrow(() -> new IdNotFoundException("Restaurant Not Found"));

        // check customer has a delivered order from this restaurant
        List<Order> deliveredOrders = orderRepository
                .findByCustomer_IdAndStatus(dto.getCustomerId(), OrderStatus.DELIVERED);
        boolean hasOrdered = deliveredOrders.stream()
                .anyMatch(o -> o.getRestaurant().getId().equals(dto.getRestaurantId()));
        if (!hasOrdered)
            throw new RuleViolationException("You can only review a restaurant after a delivered order");

        if (reviewRepository.existsByCustomer_IdAndRestaurant_Id(dto.getCustomerId(), dto.getRestaurantId()))
            throw new RuleViolationException("You have already reviewed this restaurant");

        if (dto.getRating() < 1 || dto.getRating() > 5)
            throw new RuleViolationException("Rating must be between 1 and 5");

        Review review = new Review();
        review.setCustomer(customer);
        review.setRestaurant(restaurant);
        review.setRating(dto.getRating());
        review.setComment(dto.getComment());

        Review saved = reviewRepository.save(review);

        // update restaurant average rating
        List<Review> allReviews = reviewRepository.findByRestaurant_Id(dto.getRestaurantId());
        double avg = allReviews.stream().mapToInt(Review::getRating).average().orElse(0.0);
        restaurant.setRating(Math.round(avg * 10.0) / 10.0);
        restaurantRepository.save(restaurant);

        return ResponseStructure.<Review>builder()
                .statusCode(HttpStatus.CREATED.value())
                .message("Review Added Successfully")
                .data(saved).build();
    }

    public ResponseStructure<List<Review>> getReviewsByRestaurant(Integer restaurantId) {
        List<Review> reviews = reviewRepository.findByRestaurant_Id(restaurantId);
        if (reviews.isEmpty()) throw new ResourceNotFoundException("No Reviews Found");
        return ResponseStructure.<List<Review>>builder()
                .statusCode(HttpStatus.FOUND.value())
                .message(reviews.size() + " Review(s) Found")
                .data(reviews).build();
    }
}
