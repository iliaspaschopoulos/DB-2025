package com.example.dbapp.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity
@Table(name = "Artist_Genre")
public class ArtistGenre {

    @EmbeddedId
    private ArtistGenreId id = new ArtistGenreId();

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("artistId") // This maps the artistId field of the EmbeddedId
    @JoinColumn(name = "artist_id")
    private Artist artist;

    @Column(name = "subgenre", length = 50)
    private String subgenre;

    public ArtistGenre() {
    }

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
        if (artist != null && this.id != null) {
            this.id.setArtistId(artist.getArtistId());
        }
    }

    // We need to ensure genre is also set in id
    public String getGenre() {
        return this.id != null ? this.id.getGenre() : null;
    }

    public void setGenre(String genre) {
        if (this.id == null) {
            this.id = new ArtistGenreId();
        }
        this.id.setGenre(genre);
    }


    public String getSubgenre() {
        return subgenre;
    }

    public void setSubgenre(String subgenre) {
        this.subgenre = subgenre;
    }
}
