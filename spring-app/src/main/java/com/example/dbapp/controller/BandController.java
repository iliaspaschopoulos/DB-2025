package com.example.dbapp.controller;

import com.example.dbapp.model.Band;
import com.example.dbapp.service.BandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@CrossOrigin(origins = "http://localhost:3000")
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

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Band> createBand(@RequestBody Band band) {
        Band saved = bandService.saveBand(band);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(saved.getBandId())
                .toUri();
        return ResponseEntity.created(location).body(saved);
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Band> updateBand(@PathVariable Integer id, @RequestBody Band bandDetails) {
        return bandService.getBandById(id)
                .map(existing -> {
                    existing.setBandName(bandDetails.getBandName());
                    existing.setFormationDate(bandDetails.getFormationDate());
                    existing.setWebsite(bandDetails.getWebsite());
                    Band updated = bandService.saveBand(existing);
                    return ResponseEntity.ok(updated);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBand(@PathVariable Integer id) {
        if (bandService.getBandById(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        bandService.deleteBand(id);
        return ResponseEntity.noContent().build();
    }
}
