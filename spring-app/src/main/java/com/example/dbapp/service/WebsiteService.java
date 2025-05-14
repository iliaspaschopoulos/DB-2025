package com.example.dbapp.service;

import com.example.dbapp.model.Website;
import com.example.dbapp.repository.WebsiteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class WebsiteService {

    private final WebsiteRepository websiteRepository;

    @Autowired
    public WebsiteService(WebsiteRepository websiteRepository) {
        this.websiteRepository = websiteRepository;
    }

    public List<Website> getAllWebsites() {
        return websiteRepository.findAll();
    }

    public Optional<Website> getWebsiteById(Integer id) {
        return websiteRepository.findById(id);
    }

    public Website saveWebsite(Website website) {
        return websiteRepository.save(website);
    }

    public Website updateWebsite(Integer id, Website websiteDetails) {
        Website website = websiteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Website not found with id " + id));

        website.setUrl(websiteDetails.getUrl());
        website.setFestival(websiteDetails.getFestival());
        website.setImageUrl(websiteDetails.getImageUrl());
        website.setDescription(websiteDetails.getDescription());

        return websiteRepository.save(website);
    }

    public void deleteWebsite(Integer id) {
        websiteRepository.deleteById(id);
    }
}
