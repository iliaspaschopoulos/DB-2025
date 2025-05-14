package com.example.dbapp.service;

import com.example.dbapp.model.Artist;
import com.example.dbapp.model.Band;
import com.example.dbapp.model.Event;
import com.example.dbapp.model.Performance;
import com.example.dbapp.repository.ArtistRepository;
import com.example.dbapp.repository.BandRepository;
import com.example.dbapp.repository.EventRepository;
import com.example.dbapp.repository.PerformanceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Service
public class PerformanceService {

    private final PerformanceRepository performanceRepository;
    private final EventRepository eventRepository;
    private final ArtistRepository artistRepository;
    private final BandRepository bandRepository;

    @Autowired
    public PerformanceService(PerformanceRepository performanceRepository,
                              EventRepository eventRepository,
                              ArtistRepository artistRepository,
                              BandRepository bandRepository) {
        this.performanceRepository = performanceRepository;
        this.eventRepository = eventRepository;
        this.artistRepository = artistRepository;
        this.bandRepository = bandRepository;
    }

    private void validatePerformance(Performance performance) {
        if ((performance.getArtist() == null && performance.getBand() == null) ||
            (performance.getArtist() != null && performance.getBand() != null)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Performance must have either an Artist or a Band, but not both.");
        }

        // Validate duration (max 180 minutes)
        if (performance.getDuration() != null) {
            long durationInMinutes = ChronoUnit.MINUTES.between(LocalTime.MIDNIGHT, performance.getDuration());
            if (durationInMinutes > 180) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Duration cannot exceed 180 minutes.");
            }
        }

        // Validate break duration (between 5 and 30 minutes inclusive, if not null)
        // The schema check is DATEDIFF(MINUTE, '00:05:00', break_duration) BETWEEN 0 AND 25
        // This means break_duration should be between 00:05:00 and 00:30:00
        if (performance.getBreakDuration() != null) {
            long breakDurationInMinutes = ChronoUnit.MINUTES.between(LocalTime.MIDNIGHT, performance.getBreakDuration());
            if (breakDurationInMinutes < 5 || breakDurationInMinutes > 30) {
                 throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Break duration must be between 5 and 30 minutes.");
            }
        }
        // Performance type validation
        String pType = performance.getPerformanceType();
        if (pType != null && !List.of("warm up", "headline", "Special guest").contains(pType)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid performance type. Must be 'warm up', 'headline', or 'Special guest'.");
        }
    }

    @Transactional(readOnly = true)
    public List<Performance> getAllPerformances() {
        return performanceRepository.findAllWithDetails();
    }

    @Transactional(readOnly = true)
    public Optional<Performance> getPerformanceById(Integer id) {
        return performanceRepository.findByIdWithDetails(id);
    }

    @Transactional
    public Performance savePerformance(Performance performance) {
        // Fetch and set related entities
        if (performance.getEvent() != null && performance.getEvent().getEventId() != null) {
            Event event = eventRepository.findById(performance.getEvent().getEventId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Event not found with ID: " + performance.getEvent().getEventId()));
            performance.setEvent(event);
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Event ID must be provided for performance.");
        }

        if (performance.getArtist() != null && performance.getArtist().getArtistId() != null) {
            Artist artist = artistRepository.findById(performance.getArtist().getArtistId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Artist not found with ID: " + performance.getArtist().getArtistId()));
            performance.setArtist(artist);
            performance.setBand(null); // Ensure band is null if artist is set
        } else if (performance.getBand() != null && performance.getBand().getBandId() != null) {
            Band band = bandRepository.findById(performance.getBand().getBandId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Band not found with ID: " + performance.getBand().getBandId()));
            performance.setBand(band);
            performance.setArtist(null); // Ensure artist is null if band is set
        }
        
        validatePerformance(performance);
        return performanceRepository.save(performance);
    }

    @Transactional
    public Performance updatePerformance(Integer id, Performance performanceDetails) {
        Performance existingPerformance = performanceRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Performance not found with id: " + id));

        if (performanceDetails.getEvent() != null && performanceDetails.getEvent().getEventId() != null) {
            Event event = eventRepository.findById(performanceDetails.getEvent().getEventId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Event not found with ID: " + performanceDetails.getEvent().getEventId()));
            existingPerformance.setEvent(event);
        } else {
             throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Event ID must be provided for performance update.");
        }


        if (performanceDetails.getArtist() != null && performanceDetails.getArtist().getArtistId() != null) {
            Artist artist = artistRepository.findById(performanceDetails.getArtist().getArtistId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Artist not found with ID: " + performanceDetails.getArtist().getArtistId()));
            existingPerformance.setArtist(artist);
            existingPerformance.setBand(null);
        } else if (performanceDetails.getBand() != null && performanceDetails.getBand().getBandId() != null) {
            Band band = bandRepository.findById(performanceDetails.getBand().getBandId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Band not found with ID: " + performanceDetails.getBand().getBandId()));
            existingPerformance.setBand(band);
            existingPerformance.setArtist(null);
        } else { // If neither artist nor band ID is provided in details, keep existing or clear both if one was removed
            if (performanceDetails.getArtist() == null && performanceDetails.getBand() == null) {
                 // This case should be caught by validatePerformance if both are null.
                 // If one was provided as null explicitly, it means to remove it.
                 // If artistId was null in payload, and bandId was null in payload:
                 // existingPerformance.setArtist(null);
                 // existingPerformance.setBand(null);
                 // This logic is tricky, rely on explicit setting or validatePerformance will catch it.
            }
        }


        existingPerformance.setPerformanceType(performanceDetails.getPerformanceType());
        existingPerformance.setStartTime(performanceDetails.getStartTime());
        existingPerformance.setDuration(performanceDetails.getDuration());
        existingPerformance.setBreakDuration(performanceDetails.getBreakDuration());

        validatePerformance(existingPerformance);
        return performanceRepository.save(existingPerformance);
    }

    @Transactional
    public void deletePerformance(Integer id) {
        if (!performanceRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Performance not found for deletion with id: " + id);
        }
        performanceRepository.deleteById(id);
    }
}
