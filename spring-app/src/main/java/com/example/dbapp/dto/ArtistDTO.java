package com.example.dbapp.dto;

import java.time.LocalDate;
import java.util.Set;
import java.util.stream.Collectors;

import com.example.dbapp.model.Artist;
import com.example.dbapp.model.ArtistGenre; // Assuming ArtistGenre has a simple structure or a DTO too

public class ArtistDTO {

    private Integer artistId;
    private String name;
    private String stageName;
    private LocalDate dateOfBirth;
    private String website;
    private String instagramProfile;
    private Set<String> genres; // Example: Just genre names

    // Constructors
    public ArtistDTO() {
    }

    public ArtistDTO(Artist artist) {
        this.artistId = artist.getArtistId();
        this.name = artist.getName();
        this.stageName = artist.getStageName();
        this.dateOfBirth = artist.getDateOfBirth();
        this.website = artist.getWebsite();
        this.instagramProfile = artist.getInstagramProfile();
        // Example: Map ArtistGenre entities to a simpler representation, e.g., set of genre names
        // This requires ArtistGenre to have a method like getGenreName() or similar
        // and ArtistGenreId to have getGenre()
        if (artist.getGenres() != null) {
            this.genres = artist.getGenres().stream()
                                .map(ag -> ag.getId().getGenre()) // Assuming ArtistGenreId has getGenre()
                                .collect(Collectors.toSet());
        }
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

    public Set<String> getGenres() {
        return genres;
    }

    public void setGenres(Set<String> genres) {
        this.genres = genres;
    }
}
