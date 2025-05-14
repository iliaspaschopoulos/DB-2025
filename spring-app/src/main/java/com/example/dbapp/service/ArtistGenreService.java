package com.example.dbapp.service;

import com.example.dbapp.model.ArtistGenre;
import com.example.dbapp.model.ArtistGenreId;
import com.example.dbapp.repository.ArtistGenreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ArtistGenreService {

    private final ArtistGenreRepository artistGenreRepository;

    @Autowired
    public ArtistGenreService(ArtistGenreRepository artistGenreRepository) {
        this.artistGenreRepository = artistGenreRepository;
    }

    public List<ArtistGenre> getAllArtistGenres() {
        return artistGenreRepository.findAll();
    }

    public Optional<ArtistGenre> getArtistGenreById(ArtistGenreId id) {
        return artistGenreRepository.findById(id);
    }

    public ArtistGenre saveArtistGenre(ArtistGenre artistGenre) {
        return artistGenreRepository.save(artistGenre);
    }

    // ArtistGenre is a join table, updates might not be common beyond adding/removing associations.
    // If specific fields in the join table itself (other than FKs) need updating, add here.
    // For this example, we assume no other updatable fields in ArtistGenre.
    // public ArtistGenre updateArtistGenre(ArtistGenreId id, ArtistGenre artistGenreDetails) {
    //     ArtistGenre artistGenre = artistGenreRepository.findById(id)
    //             .orElseThrow(() -> new RuntimeException("ArtistGenre not found with id " + id));
    //     // Update fields if any
    //     return artistGenreRepository.save(artistGenre);
    // }

    public void deleteArtistGenre(ArtistGenreId id) {
        artistGenreRepository.deleteById(id);
    }
}
