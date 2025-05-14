package com.example.dbapp.model;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "Band_Member")
public class BandMember {

    @EmbeddedId
    private BandMemberId id = new BandMemberId();

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("bandId")
    @JoinColumn(name = "band_id")
    private Band band;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("artistId")
    @JoinColumn(name = "artist_id")
    private Artist artist;

    public BandMember() {
        // Default constructor
    }

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
        if (this.id == null) this.id = new BandMemberId();
        if (band != null && band.getBandId() != null) {
            this.id.setBandId(band.getBandId());
        }
    }

    public Artist getArtist() {
        return artist;
    }

    public void setArtist(Artist artist) {
        this.artist = artist;
        if (this.id == null) this.id = new BandMemberId();
        if (artist != null && artist.getArtistId() != null) {
            this.id.setArtistId(artist.getArtistId());
        }
    }
}
