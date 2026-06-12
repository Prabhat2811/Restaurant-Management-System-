package com.management.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.management.dto.DeliveryAgentDto;
import com.management.dto.ResponseStructure;
import com.management.entity.DeliveryAgent;
import com.management.service.DeliveryAgentService;

@RestController
@RequestMapping("/delivery-agent")
public class DeliveryAgentController {
    @Autowired
    private DeliveryAgentService agentService;

    @PostMapping("/add")
    public ResponseEntity<ResponseStructure<DeliveryAgent>> add(@RequestBody DeliveryAgentDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(agentService.addAgent(dto));
    }

    @GetMapping("/available")
    public ResponseEntity<ResponseStructure<List<DeliveryAgent>>> getAvailable() {
        return ResponseEntity.status(HttpStatus.FOUND).body(agentService.getAvailableAgents());
    }

    @GetMapping("/all")
    public ResponseEntity<ResponseStructure<List<DeliveryAgent>>> getAll() {
        return ResponseEntity.status(HttpStatus.FOUND).body(agentService.getAllAgents());
    }
}
