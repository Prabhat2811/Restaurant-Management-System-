package com.management.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.management.entity.Customer;

public interface CustomerRepository extends JpaRepository<Customer, Integer> {
	 Optional<Customer> findByUser_Id(Integer userId);
}
