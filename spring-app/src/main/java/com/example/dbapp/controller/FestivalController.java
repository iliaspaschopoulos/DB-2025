package com.example.dbapp.controller;

import com.example.dbapp.model.Festival;
import com.example.dbapp.service.FestivalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/festivals")
public class FestivalController {

    private final FestivalService festivalService;

    @Autowired
    public FestivalController(FestivalService festivalService) {
        this.festivalService = festivalService;
    }

    @GetMapping
    public List<Festival> getAllFestivals() {
        return festivalService.getAllFestivals();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Festival> getFestivalById(@PathVariable Integer id) {
        return festivalService.getFestivalById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Festival createFestival(@RequestBody Festival festival) {
        return festivalService.saveFestival(festival);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Festival> updateFestival(@PathVariable Integer id, @RequestBody Festival festivalDetails) {
        try {
            Festival updatedFestival = festivalService.updateFestival(id, festivalDetails);
            return ResponseEntity.ok(updatedFestival);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFestival(@PathVariable Integer id) {
        festivalService.deleteFestival(id);
        return ResponseEntity.noContent().build();
    }
}
