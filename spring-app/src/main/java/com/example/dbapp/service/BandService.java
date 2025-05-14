package com.example.dbapp.service;

import com.example.dbapp.model.Band;
import com.example.dbapp.repository.BandRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BandService {

    private final BandRepository bandRepository;

    @Autowired
    public BandService(BandRepository bandRepository) {
        this.bandRepository = bandRepository;
    }

    public List<Band> getAllBands() {
        return bandRepository.findAll();
    }

    public Optional<Band> getBandById(Integer id) {
        return bandRepository.findById(id);
    }

    public Band saveBand(Band band) {
        return bandRepository.save(band);
    }

    public Band updateBand(Integer id, Band bandDetails) {
        Band band = bandRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Band not found with id " + id));

        band.setBandName(bandDetails.getBandName());
        // band.setBandMembers(bandDetails.getBandMembers()); // Managed separately
        // band.setPerformances(bandDetails.getPerformances()); // Managed separately
        return bandRepository.save(band);
    }

    public void deleteBand(Integer id) {
        bandRepository.deleteById(id);
    }
}
