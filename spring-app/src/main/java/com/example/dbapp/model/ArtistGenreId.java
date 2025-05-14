package com.example.dbapp.model;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class ArtistGenreId implements Serializable {

    private Integer artistId;
    private String genre;

    // Constructors
    public ArtistGenreId() {}

    public ArtistGenreId(Integer artistId, String genre) {
        this.artistId = artistId;
        this.genre = genre;
    }

    // Getters and Setters
    public Integer getArtistId() {
        return artistId;
    }

    public void setArtistId(Integer artistId) {
        this.artistId = artistId;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    // hashCode and equals
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ArtistGenreId that = (ArtistGenreId) o;
        return Objects.equals(artistId, that.artistId) &&
               Objects.equals(genre, that.genre);
    }

    @Override
    public int hashCode() {
        return Objects.hash(artistId, genre);
    }
}
