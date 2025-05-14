package com.example.dbapp.service;

import com.example.dbapp.model.Artist;
import com.example.dbapp.model.ArtistGenre;
import com.example.dbapp.model.ArtistGenreId;
import com.example.dbapp.repository.ArtistGenreRepository;
import com.example.dbapp.repository.ArtistRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
public class ArtistGenreService {

    private final ArtistGenreRepository artistGenreRepository;
    private final ArtistRepository artistRepository;

    @Autowired
    public ArtistGenreService(ArtistGenreRepository artistGenreRepository, ArtistRepository artistRepository) {
        this.artistGenreRepository = artistGenreRepository;
        this.artistRepository = artistRepository;
    }

    public List<ArtistGenre> getAllArtistGenres() {
        return artistGenreRepository.findAll();
    }

    public Optional<ArtistGenre> getArtistGenreById(ArtistGenreId id) {
        if (id == null || id.getArtistId() == null || id.getGenre() == null || id.getGenre().trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Valid ArtistGenreId (artistId, genre) must be provided.");
        }
        return artistGenreRepository.findById(id);
    }

    @Transactional
    public ArtistGenre saveArtistGenre(ArtistGenre artistGenre) {
        if (artistGenre.getId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "ArtistGenre 'id' field (ArtistGenreId) cannot be null.");
        }
        Integer artistIdFromId = artistGenre.getId().getArtistId();
        String genreFromId = artistGenre.getId().getGenre();

        if (artistIdFromId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "ArtistGenre 'id.artistId' cannot be null.");
        }
        if (genreFromId == null || genreFromId.trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "ArtistGenre 'id.genre' cannot be null or empty.");
        }

        Artist artist = artistRepository.findById(artistIdFromId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Artist not found with ID: " + artistIdFromId));
        artistGenre.setArtist(artist); 
        artistGenre.setGenre(genreFromId);
        
        if (artistGenre.getArtist() != null && artistGenre.getArtist().getArtistId() != null &&
            !artistGenre.getArtist().getArtistId().equals(artistIdFromId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Mismatch between artistId in 'id' field and 'artist.artistId' field.");
        }

        return artistGenreRepository.save(artistGenre);
    }

    @Transactional
    public ArtistGenre updateArtistGenre(ArtistGenreId id, ArtistGenre artistGenreDetails) {
        if (id == null || id.getArtistId() == null || id.getGenre() == null || id.getGenre().trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Valid ArtistGenreId (artistId, genre) must be provided for update identification.");
        }

        // Check if the referenced artist exists. If not, it's a bad request as ArtistGenre cannot exist without an Artist.
        artistRepository.findById(id.getArtistId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Artist not found with ID: " + id.getArtistId() + ". Cannot update ArtistGenre."));

        ArtistGenre existingArtistGenre = artistGenreRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "ArtistGenre not found with id: " + id + " for the specified artist."));

        // The PK (artist, genre) cannot change.
        // The artist object is already part of existingArtistGenre if found.
        // No need to explicitly set existingArtistGenre.setArtist() or existingArtistGenre.setGenre()
        // as these are immutable parts of its identity.

        existingArtistGenre.setSubgenre(artistGenreDetails.getSubgenre());

        return artistGenreRepository.save(existingArtistGenre);
    }

    @Transactional
    public void deleteArtistGenre(ArtistGenreId id) {
        if (id == null || id.getArtistId() == null || id.getGenre() == null || id.getGenre().trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Valid ArtistGenreId (artistId, genre) must be provided for deletion.");
        }
        if (!artistGenreRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "ArtistGenre not found with id: " + id + " for deletion.");
        }
        artistGenreRepository.deleteById(id);
    }
}
