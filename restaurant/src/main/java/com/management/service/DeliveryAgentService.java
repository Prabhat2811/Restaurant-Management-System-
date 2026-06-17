package com.management.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.management.dto.DeliveryAgentDto;
import com.management.dto.ResponseStructure;
import com.management.entity.DeliveryAgent;
import com.management.entity.User;
import com.management.exception.DuplicateEntryException;
import com.management.exception.IdNotFoundException;
import com.management.exception.ResourceNotFoundException;
import com.management.repository.DeliveryAgentRepository;
import com.management.repository.UserRepository;

@Service
public class DeliveryAgentService {
    @Autowired
    private DeliveryAgentRepository agentRepository;
    @Autowired
    private UserRepository userRepository;

    public ResponseStructure<DeliveryAgent> addAgent(DeliveryAgentDto dto) {
        User user = userRepository.findById(dto.getId())
                .orElseThrow(() -> new IdNotFoundException("User Not Found"));

        if (agentRepository.existsByVehicleNumber(dto.getVehicleNumber()))
            throw new DuplicateEntryException("Vehicle number already exists");

        DeliveryAgent agent = new DeliveryAgent();
        agent.setUser(user);
        agent.setVehicleNumber(dto.getVehicleNumber());
        agent.setRating(dto.getRating());
        agent.setAvailable(true);

        return ResponseStructure.<DeliveryAgent>builder()
                .statusCode(HttpStatus.CREATED.value())
                .message("Agent Added Successfully")
                .data(agentRepository.save(agent)).build();
    }

    public ResponseStructure<List<DeliveryAgent>> getAvailableAgents() {
        List<DeliveryAgent> list = agentRepository.findByAvailableTrue();
        if (list.isEmpty()) throw new ResourceNotFoundException("No Available Agents");
        return ResponseStructure.<List<DeliveryAgent>>builder()
                .statusCode(HttpStatus.OK.value())
                .message(list.size() + " Agent(s) Available")
                .data(list).build();
    }

    public ResponseStructure<List<DeliveryAgent>> getAllAgents() {
        List<DeliveryAgent> list = agentRepository.findAll();
        if (list.isEmpty()) throw new ResourceNotFoundException("No Agents Found");
        return ResponseStructure.<List<DeliveryAgent>>builder()
                .statusCode(HttpStatus.OK.value())
                .message(list.size() + " Agent(s) Found")
                .data(list).build();
    }
    
    public DeliveryAgent setBusy(Integer id) {
        DeliveryAgent agent = agentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Agent not found"));

        agent.setAvailable(false); // BUSY
        return agentRepository.save(agent);
    }

    public DeliveryAgent setAvailable(Integer id) {
        DeliveryAgent agent = agentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Agent not found"));

        agent.setAvailable(true);
        return agentRepository.save(agent);
    }
}
