package com.example.dbapp.model;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType; // Added import
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Column;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import jakarta.persistence.Transient; // Import for @Transient
import jakarta.persistence.OneToMany;
import jakarta.persistence.CascadeType;
import java.time.LocalTime;
import java.util.Set;
import com.fasterxml.jackson.annotation.JsonIdentityInfo; // Added
import com.fasterxml.jackson.annotation.ObjectIdGenerators; // Added

@Entity
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id") // Added
@Table(name = "Performance",
       uniqueConstraints = {
           // CONSTRAINT chk_performance_artist_or_band CHECK (
           // (artist_id IS NOT NULL AND band_id IS NULL)
           // OR (artist_id IS NULL AND band_id IS NOT NULL)
           // ) -> This needs to be handled via validation or lifecycle callbacks in JPA
       }
)
public class Performance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "performance_id")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY) // Changed to LAZY
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @ManyToOne
    @JoinColumn(name = "artist_id") // Nullable
    private Artist artist;

    @ManyToOne
    @JoinColumn(name = "band_id") // Nullable
    private Band band;

    @Column(name = "performance_type", length = 50)
    // CHECK (performance_type IN ('warm up','headline','Special guest')) -> DB constraint
    private String performanceType;

    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    @Column(name = "duration", nullable = false)
    // CHECK (DATEDIFF(MINUTE, '00:00:00', duration) <= 180) -> DB constraint
    private LocalTime duration; // Representing TIME as LocalTime. Duration logic might need custom handling.

    @Column(name = "break_duration")
    // CHECK (DATEDIFF(MINUTE, '00:05:00', break_duration) BETWEEN 0 AND 25) -> DB constraint
    private LocalTime breakDuration;

    @Transient // Not a DB column, calculated or temporary
    private LocalTime endTime;

    @Transient // Not a DB column
    private Boolean contractSigned;

    @OneToMany(mappedBy = "performance", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Rating> ratings;

    // Constructors
    public Performance() {
    }

    // Getters and Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public Artist getArtist() {
        return artist;
    }

    public void setArtist(Artist artist) {
        this.artist = artist;
    }

    public Band getBand() {
        return band;
    }

    public void setBand(Band band) {
        this.band = band;
    }

    public String getPerformanceType() {
        return performanceType;
    }

    public void setPerformanceType(String performanceType) {
        this.performanceType = performanceType;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public LocalTime getDuration() {
        return duration;
    }

    public void setDuration(LocalTime duration) {
        this.duration = duration;
    }

    public LocalTime getBreakDuration() {
        return breakDuration;
    }

    public void setBreakDuration(LocalTime breakDuration) {
        this.breakDuration = breakDuration;
    }

    public LocalTime getEndTime() {
        if (this.startTime != null && this.duration != null) {
            return this.startTime.plusHours(this.duration.getHour())
                                .plusMinutes(this.duration.getMinute())
                                .plusSeconds(this.duration.getSecond());
        }
        return endTime; // Return the transient field if set directly, or null
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    public Boolean getContractSigned() {
        return contractSigned;
    }

    public void setContractSigned(Boolean contractSigned) {
        this.contractSigned = contractSigned;
    }

    public Set<Rating> getRatings() {
        return ratings;
    }

    public void setRatings(Set<Rating> ratings) {
        this.ratings = ratings;
    }
    
    // Consider adding validation for chk_performance_artist_or_band
    // and for the CHECK constraints on performanceType, duration, breakDuration
    // using Bean Validation (e.g. @AssertTrue on a method or custom annotations)
}
