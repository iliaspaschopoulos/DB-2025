package com.example.dbapp.model;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class BandMemberId implements Serializable {

    private Integer bandId;
    private Integer artistId;

    // Constructors
    public BandMemberId() {}

    public BandMemberId(Integer bandId, Integer artistId) {
        this.bandId = bandId;
        this.artistId = artistId;
    }

    // Getters and Setters
    public Integer getBandId() {
        return bandId;
    }

    public void setBandId(Integer bandId) {
        this.bandId = bandId;
    }

    public Integer getArtistId() {
        return artistId;
    }

    public void setArtistId(Integer artistId) {
        this.artistId = artistId;
    }

    // hashCode and equals
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BandMemberId that = (BandMemberId) o;
        return Objects.equals(bandId, that.bandId) &&
               Objects.equals(artistId, that.artistId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(bandId, artistId);
    }
}
