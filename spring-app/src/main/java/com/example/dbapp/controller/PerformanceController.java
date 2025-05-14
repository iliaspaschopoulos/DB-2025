package com.example.dbapp.controller;

import com.example.dbapp.model.Performance;
import com.example.dbapp.service.PerformanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public Performance createPerformance(@RequestBody Performance performance) {
        return performanceService.savePerformance(performance);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Performance> updatePerformance(@PathVariable Integer id, @RequestBody Performance performanceDetails) {
        try {
            Performance updatedPerformance = performanceService.updatePerformance(id, performanceDetails);
            return ResponseEntity.ok(updatedPerformance);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePerformance(@PathVariable Integer id) {
        performanceService.deletePerformance(id);
        return ResponseEntity.noContent().build();
    }
}
