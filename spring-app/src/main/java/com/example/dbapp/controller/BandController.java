package com.example.dbapp.controller;

import com.example.dbapp.model.Band;
import com.example.dbapp.service.BandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bands")
public class BandController {

    private final BandService bandService;

    @Autowired
    public BandController(BandService bandService) {
        this.bandService = bandService;
    }

    @GetMapping
    public List<Band> getAllBands() {
        return bandService.getAllBands();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Band> getBandById(@PathVariable Integer id) {
        return bandService.getBandById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Band createBand(@RequestBody Band band) {
        return bandService.saveBand(band);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Band> updateBand(@PathVariable Integer id, @RequestBody Band bandDetails) {
        try {
            Band updatedBand = bandService.updateBand(id, bandDetails);
            return ResponseEntity.ok(updatedBand);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBand(@PathVariable Integer id) {
        bandService.deleteBand(id);
        return ResponseEntity.noContent().build();
    }
}
