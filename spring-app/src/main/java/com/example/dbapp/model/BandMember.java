package com.example.dbapp.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "Band_Member")
public class BandMember {

    @EmbeddedId
    private BandMemberId id;

    @ManyToOne
    @MapsId("bandId")
    @JoinColumn(name = "band_id")
    private Band band;

    @ManyToOne
    @MapsId("artistId")
    @JoinColumn(name = "artist_id")
    private Artist artist;

    @Column(name = "role", length = 50)
    private String role;

    @Column(name = "join_date")
    private LocalDate joinDate;

    // Getters and Setters
    public BandMemberId getId() {
        return id;
    }

    public void setId(BandMemberId id) {
        this.id = id;
    }

    public Band getBand() {
        return band;
    }

    public void setBand(Band band) {
        this.band = band;
    }

    public Artist getArtist() {
        return artist;
    }

    public void setArtist(Artist artist) {
        this.artist = artist;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public LocalDate getJoinDate() {
        return joinDate;
    }

    public void setJoinDate(LocalDate joinDate) {
        this.joinDate = joinDate;
    }
}
