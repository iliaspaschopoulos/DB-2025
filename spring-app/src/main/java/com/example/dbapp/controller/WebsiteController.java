package com.example.dbapp.controller;

import com.example.dbapp.model.Website;
import com.example.dbapp.service.WebsiteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/websites")
public class WebsiteController {

    private final WebsiteService websiteService;

    @Autowired
    public WebsiteController(WebsiteService websiteService) {
        this.websiteService = websiteService;
    }

    @GetMapping
    public List<Website> getAllWebsites() {
        return websiteService.getAllWebsites();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Website> getWebsiteById(@PathVariable Integer id) {
        return websiteService.getWebsiteById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Website createWebsite(@RequestBody Website website) {
        return websiteService.saveWebsite(website);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Website> updateWebsite(@PathVariable Integer id, @RequestBody Website websiteDetails) {
        try {
            Website updatedWebsite = websiteService.updateWebsite(id, websiteDetails);
            return ResponseEntity.ok(updatedWebsite);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteWebsite(@PathVariable Integer id) {
        websiteService.deleteWebsite(id);
        return ResponseEntity.noContent().build();
    }
}
