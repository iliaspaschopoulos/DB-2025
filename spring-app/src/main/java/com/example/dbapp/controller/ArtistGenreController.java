package com.example.dbapp.controller;

import com.example.dbapp.model.ArtistGenre;
import com.example.dbapp.model.ArtistGenreId;
import com.example.dbapp.service.ArtistGenreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/artist_genres") // Changed from "/api/artistgenres"
public class ArtistGenreController {

    private final ArtistGenreService artistGenreService;

    @Autowired
    public ArtistGenreController(ArtistGenreService artistGenreService) {
        this.artistGenreService = artistGenreService;
    }

    @GetMapping
    public List<ArtistGenre> getAllArtistGenres() {
        return artistGenreService.getAllArtistGenres();
    }

    @GetMapping("/find") // Example: /api/artist_genres/find?artistId=1&genreName=Rock
    public ResponseEntity<ArtistGenre> getArtistGenreById(@RequestParam Integer artistId, @RequestParam String genreName) {
        ArtistGenreId id = new ArtistGenreId(artistId, genreName);
        return artistGenreService.getArtistGenreById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<ArtistGenre> createArtistGenre(@RequestBody ArtistGenre artistGenre) {
        if (artistGenre.getArtist() != null && artistGenre.getId() != null && artistGenre.getId().getArtistId() == null) {
            artistGenre.getId().setArtistId(artistGenre.getArtist().getId());
        }
        ArtistGenre savedArtistGenre = artistGenreService.saveArtistGenre(artistGenre);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedArtistGenre);
    }

    @PutMapping
    public ResponseEntity<ArtistGenre> updateArtistGenre(@RequestBody ArtistGenre artistGenre) {
        ArtistGenreId id = artistGenre.getId();
        if (id == null || id.getArtistId() == null || id.getGenre() == null) {
            return ResponseEntity.badRequest().build();
        }
        if (artistGenre.getArtist() != null && !artistGenre.getArtist().getId().equals(id.getArtistId())) {
            return ResponseEntity.badRequest().body(null);
        }

        return artistGenreService.getArtistGenreById(id)
                .map(existingArtistGenre -> {
                    existingArtistGenre.setSubgenre(artistGenre.getSubgenre());
                    ArtistGenre updatedArtistGenre = artistGenreService.saveArtistGenre(existingArtistGenre);
                    return ResponseEntity.ok(updatedArtistGenre);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/delete") // Example: /api/artist_genres/delete?artistId=1&genreName=Rock
    public ResponseEntity<Void> deleteArtistGenre(@RequestParam Integer artistId, @RequestParam String genreName) {
        ArtistGenreId id = new ArtistGenreId(artistId, genreName);
        if (artistGenreService.getArtistGenreById(id).isPresent()) {
            artistGenreService.deleteArtistGenre(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
