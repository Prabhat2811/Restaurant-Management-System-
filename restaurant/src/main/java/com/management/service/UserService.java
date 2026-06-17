package com.management.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.management.dto.LoginDto;
import com.management.dto.ResponseStructure;
import com.management.dto.Role;
import com.management.dto.UserDto;
import com.management.entity.Customer;
import com.management.entity.DeliveryAgent;
import com.management.entity.User;
import com.management.exception.DuplicateEntryException;
import com.management.exception.IdNotFoundException;
import com.management.exception.ResourceNotFoundException;
import com.management.exception.RuleViolationException;
import com.management.repository.CustomerRepository;
import com.management.repository.DeliveryAgentRepository;
import com.management.repository.UserRepository;

@Service
public class UserService {
	
	@Autowired
	private UserRepository userRepository;

	@Autowired
	private CustomerRepository customerRepository;

	@Autowired
	private DeliveryAgentRepository deliveryAgentRepository;

	public ResponseStructure<User> registerUser(UserDto dto) {

	    if (userRepository.existsByEmail(dto.getEmail()))
	        throw new DuplicateEntryException("Email already exists: " + dto.getEmail());

	    if (userRepository.existsByPhone(dto.getPhone()))
	        throw new DuplicateEntryException("Phone already exists: " + dto.getPhone());

	    User user = new User();
	    user.setName(dto.getName());
	    user.setEmail(dto.getEmail());
	    user.setPassword(dto.getPassword());
	    user.setPhone(dto.getPhone());
	    user.setRole(dto.getRole());

	    User savedUser = userRepository.save(user);

	    // Create Customer automatically
	    if (savedUser.getRole() == Role.CUSTOMER) {

	        Customer customer = new Customer();
	        customer.setUser(savedUser);

	        customerRepository.save(customer);
	    }

	    // Create Delivery Agent automatically
	    else if (savedUser.getRole() == Role.DELIVERY_AGENT) {

	        DeliveryAgent agent = new DeliveryAgent();
	        agent.setUser(savedUser);
	        agent.setAvailable(true);
	        agent.setRating(0.0);

	        // Temporary vehicle number
	        agent.setVehicleNumber("TEMP-" + savedUser.getId());

	        deliveryAgentRepository.save(agent);
	    }

	    return ResponseStructure.<User>builder()
	            .statusCode(HttpStatus.CREATED.value())
	            .message("User Registered Successfully")
	            .data(savedUser)
	            .build();
	}
    
    public ResponseStructure<User> login(LoginDto dto) {
        User user = userRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("No account found with this email"));

        if (!user.getPassword().equals(dto.getPassword()))
            throw new RuleViolationException("Incorrect password");

        return ResponseStructure.<User>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Login Successful")
                .data(user)
                .build();
    }

    public ResponseStructure<User> getUserById(Integer id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IdNotFoundException("User Not Found with Id: " + id));
        return ResponseStructure.<User>builder()
                .statusCode(HttpStatus.FOUND.value())
                .message("User Found")
                .data(user).build();
    }

    public ResponseStructure<List<User>> getAllUsers() {
        List<User> users = userRepository.findAll();
        if (users.isEmpty()) throw new ResourceNotFoundException("No Users Found");
        return ResponseStructure.<List<User>>builder()
                .statusCode(HttpStatus.OK.value())
                .message(users.size() + " User(s) Found")
                .data(users).build();
    }
}
