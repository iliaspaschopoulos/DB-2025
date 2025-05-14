package com.example.dbapp.service;

import com.example.dbapp.model.Performance;
import com.example.dbapp.repository.PerformanceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PerformanceService {

    private final PerformanceRepository performanceRepository;

    @Autowired
    public PerformanceService(PerformanceRepository performanceRepository) {
        this.performanceRepository = performanceRepository;
    }

    public List<Performance> getAllPerformances() {
        return performanceRepository.findAll();
    }

    public Optional<Performance> getPerformanceById(Integer id) {
        return performanceRepository.findById(id);
    }

    public Performance savePerformance(Performance performance) {
        return performanceRepository.save(performance);
    }

    public Performance updatePerformance(Integer id, Performance performanceDetails) {
        Performance performance = performanceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Performance not found with id " + id));

        performance.setEvent(performanceDetails.getEvent());
        performance.setArtist(performanceDetails.getArtist());
        performance.setBand(performanceDetails.getBand());
        performance.setStartTime(performanceDetails.getStartTime());
        performance.setEndTime(performanceDetails.getEndTime());
        performance.setContractSigned(performanceDetails.getContractSigned());
 
        return performanceRepository.save(performance);
    }

    public void deletePerformance(Integer id) {
        performanceRepository.deleteById(id);
    }
}
