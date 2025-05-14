package com.example.dbapp.model;

import jakarta.persistence.*;

@Entity
@Table(name = "Artist_Genre")
public class ArtistGenre {

    @EmbeddedId
    private ArtistGenreId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("artistId") 
    @JoinColumn(name = "artist_id", nullable = false)
    private Artist artist; 

    @Column(name = "subgenre", length = 50)
    private String subgenre;

    // Getters and Setters
    public ArtistGenreId getId() {
        return id;
    }

    public void setId(ArtistGenreId id) {
        this.id = id;
    }

    public Artist getArtist() { 
        return artist;
    }

    public void setArtist(Artist artist) { 
        this.artist = artist;
    }

    public String getSubgenre() {
        return subgenre;
    }

    public void setSubgenre(String subgenre) {
        this.subgenre = subgenre;
    }

    // Helper to get genre from ID, if needed, though direct access to id.getGenre() is also possible
    public String getGenre() {
        return this.id != null ? this.id.getGenre() : null;
    }
}
