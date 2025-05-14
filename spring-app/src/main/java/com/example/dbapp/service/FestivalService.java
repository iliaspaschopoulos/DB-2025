package com.example.dbapp.service;

import com.example.dbapp.model.Festival;
import com.example.dbapp.repository.FestivalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class FestivalService {

    private final FestivalRepository festivalRepository;

    @Autowired
    public FestivalService(FestivalRepository festivalRepository) {
        this.festivalRepository = festivalRepository;
    }

    public List<Festival> getAllFestivals() {
        return festivalRepository.findAll();
    }

    public Optional<Festival> getFestivalById(Integer id) {
        return festivalRepository.findById(id);
    }

    public Festival saveFestival(Festival festival) {
        return festivalRepository.save(festival);
    }

    public Festival updateFestival(Integer id, Festival festivalDetails) {
        Festival festival = festivalRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Festival not found with id " + id));

        festival.setYear(festivalDetails.getYear());
        festival.setStartDate(festivalDetails.getStartDate());
        festival.setEndDate(festivalDetails.getEndDate());
        festival.setLocation(festivalDetails.getLocation());
        // events and tickets are typically managed via their own endpoints
        return festivalRepository.save(festival);
    }

    public void deleteFestival(Integer id) {
        festivalRepository.deleteById(id);
    }
}
