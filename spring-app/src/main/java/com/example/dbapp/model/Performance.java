package com.example.dbapp.model;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Column;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.persistence.OneToMany;
import jakarta.persistence.CascadeType;
import java.time.LocalTime;
import java.util.Set;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

@Entity
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "artist_id", nullable = true)
    private Artist artist;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "band_id", nullable = true)
    private Band band;

    @Column(name = "performance_type", length = 50)
    private String performanceType;

    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    @Column(name = "duration", nullable = false)
    private LocalTime duration;

    @Column(name = "break_duration")
    private LocalTime breakDuration;

    @Transient
    private LocalTime endTime;

    @Transient
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
        return endTime;
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
