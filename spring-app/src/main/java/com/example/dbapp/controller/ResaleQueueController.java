package com.example.dbapp.controller;

import com.example.dbapp.model.ResaleQueue;
import com.example.dbapp.service.ResaleQueueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/resalequeues")
public class ResaleQueueController {

    private final ResaleQueueService resaleQueueService;

    @Autowired
    public ResaleQueueController(ResaleQueueService resaleQueueService) {
        this.resaleQueueService = resaleQueueService;
    }

    @GetMapping
    public List<ResaleQueue> getAllResaleQueues() {
        return resaleQueueService.getAllResaleQueues();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResaleQueue> getResaleQueueById(@PathVariable Integer id) {
        return resaleQueueService.getResaleQueueById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResaleQueue createResaleQueue(@RequestBody ResaleQueue resaleQueue) {
        return resaleQueueService.saveResaleQueue(resaleQueue);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResaleQueue> updateResaleQueue(@PathVariable Integer id, @RequestBody ResaleQueue resaleQueueDetails) {
        try {
            ResaleQueue updatedResaleQueue = resaleQueueService.updateResaleQueue(id, resaleQueueDetails);
            return ResponseEntity.ok(updatedResaleQueue);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteResaleQueue(@PathVariable Integer id) {
        resaleQueueService.deleteResaleQueue(id);
        return ResponseEntity.noContent().build();
    }
}
