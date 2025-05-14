package com.example.dbapp.controller;

import com.example.dbapp.model.Performance;
import com.example.dbapp.service.PerformanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/performances")
public class PerformanceController {

    private final PerformanceService performanceService;

    @Autowired
    public PerformanceController(PerformanceService performanceService) {
        this.performanceService = performanceService;
    }

    @GetMapping
    public List<Performance> getAllPerformances() {
        return performanceService.getAllPerformances();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Performance> getPerformanceById(@PathVariable Integer id) {
        return performanceService.getPerformanceById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> createPerformance(@RequestBody Performance performance) {
        try {
            Performance savedPerformance = performanceService.savePerformance(performance);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedPerformance);
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getReason());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updatePerformance(@PathVariable Integer id, @RequestBody Performance performanceDetails) {
        try {
            Performance updatedPerformance = performanceService.updatePerformance(id, performanceDetails);
            return ResponseEntity.ok(updatedPerformance);
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getReason());
        } catch (Exception e) {
            // Log the exception e
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred during update: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePerformance(@PathVariable Integer id) {
        try {
            performanceService.deletePerformance(id);
            return ResponseEntity.noContent().build();
        } catch (ResponseStatusException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                return ResponseEntity.notFound().build();
            }
            // Potentially rethrow or handle other statuses differently
            return ResponseEntity.status(e.getStatusCode()).build();
        }
    }
}
