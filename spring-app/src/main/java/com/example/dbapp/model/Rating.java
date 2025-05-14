package com.example.dbapp.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Column;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import jakarta.persistence.Transient; // Added for @Transient
import java.time.LocalDateTime;

@Entity
@Table(name = "Rating")
public class Rating {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "rating_id")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "ticket_id", nullable = false)
    private Ticket ticket;

    @ManyToOne
    @JoinColumn(name = "performance_id", nullable = false)
    private Performance performance;

    @ManyToOne
    @JoinColumn(name = "visitor_id", nullable = false)
    private Visitor visitor;

    @Column(name = "interpretation_score")
    private Integer interpretationScore;

    @Column(name = "sound_lighting_score")
    private Integer soundLightingScore;

    @Column(name = "stage_presence_score")
    private Integer stagePresenceScore;

    @Column(name = "organization_score")
    private Integer organizationScore;

    @Column(name = "overall_score")
    private Integer overallScore;

    @Column(name = "rating_date")
    private LocalDateTime ratingDate;

    @Transient
    private Artist artist; // Transient field

    @Transient
    private Band band; // Transient field

    @Transient
    private String comment; // Transient field

    // Constructors
    public Rating() {
    }

    // Getters and Setters for persistent fields
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Ticket getTicket() {
        return ticket;
    }

    public void setTicket(Ticket ticket) {
        this.ticket = ticket;
    }

    public Performance getPerformance() {
        return performance;
    }

    public void setPerformance(Performance performance) {
        this.performance = performance;
    }

    public Visitor getVisitor() {
        return visitor;
    }

    public void setVisitor(Visitor visitor) {
        this.visitor = visitor;
    }

    public Integer getInterpretationScore() {
        return interpretationScore;
    }

    public void setInterpretationScore(Integer interpretationScore) {
        this.interpretationScore = interpretationScore;
    }

    public Integer getSoundLightingScore() {
        return soundLightingScore;
    }

    public void setSoundLightingScore(Integer soundLightingScore) {
        this.soundLightingScore = soundLightingScore;
    }

    public Integer getStagePresenceScore() {
        return stagePresenceScore;
    }

    public void setStagePresenceScore(Integer stagePresenceScore) {
        this.stagePresenceScore = stagePresenceScore;
    }

    public Integer getOrganizationScore() {
        return organizationScore;
    }

    public void setOrganizationScore(Integer organizationScore) {
        this.organizationScore = organizationScore;
    }

    public Integer getOverallScore() {
        return overallScore;
    }

    public void setOverallScore(Integer overallScore) {
        this.overallScore = overallScore;
    }

    public LocalDateTime getRatingDate() {
        return ratingDate;
    }

    public void setRatingDate(LocalDateTime ratingDate) {
        this.ratingDate = ratingDate;
    }

    // Getter and Setter for ratingValue (aliasing overallScore)
    public Integer getRatingValue() {
        return this.overallScore;
    }

    public void setRatingValue(Integer ratingValue) {
        this.overallScore = ratingValue;
    }

    // Getters and Setters for transient fields
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

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
