package com.example.dbapp.controller;

import com.example.dbapp.model.ArtistGenre;
import com.example.dbapp.model.ArtistGenreId;
import com.example.dbapp.service.ArtistGenreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/artist-genres")
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

    @GetMapping("/find")
    public ResponseEntity<?> getArtistGenreById(@RequestParam Integer artistId, @RequestParam String genre) {
        try {
            ArtistGenreId id = new ArtistGenreId(artistId, genre);
            return artistGenreService.getArtistGenreById(id)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (ResponseStatusException e) { 
            return ResponseEntity.status(e.getStatusCode()).body(e.getReason());
        }
    }

    @PostMapping
    public ResponseEntity<?> createArtistGenre(@RequestBody ArtistGenre artistGenre) {
        try {
            // Payload must include artistGenre.id.artistId and artistGenre.id.genre
            if (artistGenre.getId() == null || artistGenre.getId().getArtistId() == null || artistGenre.getId().getGenre() == null || artistGenre.getId().getGenre().trim().isEmpty()) {
                 return ResponseEntity.badRequest().body("Request body must include 'id' with 'artistId' and 'genre'.");
            }
            ArtistGenre savedArtistGenre = artistGenreService.saveArtistGenre(artistGenre);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedArtistGenre);
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getReason());
        } catch (Exception e) { 
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred: " + e.getMessage());
        }
    }

    @PutMapping("/update")
    public ResponseEntity<?> updateArtistGenre(@RequestParam Integer artistId, @RequestParam String genre, @RequestBody ArtistGenre artistGenreDetails) {
        try {
            ArtistGenreId id = new ArtistGenreId(artistId, genre);
            ArtistGenre updatedArtistGenre = artistGenreService.updateArtistGenre(id, artistGenreDetails);
            return ResponseEntity.ok(updatedArtistGenre);
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getReason());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred: " + e.getMessage());
        }
    }

    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteArtistGenre(@RequestParam Integer artistId, @RequestParam String genre) {
        try {
            ArtistGenreId id = new ArtistGenreId(artistId, genre);
            artistGenreService.deleteArtistGenre(id);
            return ResponseEntity.noContent().build();
        } catch (ResponseStatusException e) { 
            return ResponseEntity.status(e.getStatusCode()).body(e.getReason());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
