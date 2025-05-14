package com.example.dbapp.model;

import jakarta.persistence.*;
import java.time.LocalDate; // Changed from java.util.Date
import java.util.HashSet; // Added for genres
import java.util.Set; // Added for artistGenres

@Entity
@Table(name = "Artist")
public class Artist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "artist_id")
    private Integer artistId;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "stage_name", length = 100)
    private String stageName;

    @Column(name = "date_of_birth", nullable = false)
    private LocalDate dateOfBirth; // Changed from Date

    @Column(name = "website", length = 255)
    private String website;

    @Column(name = "instagram_profile", length = 255)
    private String instagramProfile;

    @OneToMany(mappedBy = "artist", fetch = FetchType.LAZY) 
    private Set<ArtistGenre> artistGenres; 

    @OneToMany(mappedBy = "artist", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Performance> performances;

    @OneToMany(mappedBy = "artist", fetch = FetchType.LAZY)
    private Set<BandMember> bandMembers;

    @OneToMany(mappedBy = "artist", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<ArtistGenre> genres = new HashSet<>();

    // Constructors
    public Artist() {
    }

    // Getters and Setters
    public Integer getArtistId() { 
        return artistId;
    }

    public void setArtistId(Integer artistId) {
        this.artistId = artistId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStageName() {
        return stageName;
    }

    public void setStageName(String stageName) {
        this.stageName = stageName;
    }

    public LocalDate getDateOfBirth() { 
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) { 
        this.dateOfBirth = dateOfBirth;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getInstagramProfile() {
        return instagramProfile;
    }

    public void setInstagramProfile(String instagramProfile) {
        this.instagramProfile = instagramProfile;
    }

    public Set<ArtistGenre> getArtistGenres() { 
        return artistGenres;
    }

    public void setArtistGenres(Set<ArtistGenre> artistGenres) { 
        this.artistGenres = artistGenres;
    }

    public Set<Performance> getPerformances() {
        return performances;
    }

    public void setPerformances(Set<Performance> performances) {
        this.performances = performances;
    }

    public Set<BandMember> getBandMembers() {
        return bandMembers;
    }

    public void setBandMembers(Set<BandMember> bandMembers) {
        this.bandMembers = bandMembers;
    }

    public Set<ArtistGenre> getGenres() {
        return genres;
    }

    public void setGenres(Set<ArtistGenre> genres) {
        this.genres = genres;
    }
}
