package com.example.dbapp.controller;

import com.example.dbapp.model.Artist;
import com.example.dbapp.service.ArtistService;
import com.example.dbapp.dto.ArtistDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/artists")
public class ArtistController {

    @Autowired
    private ArtistService artistService;

    @GetMapping
    public List<ArtistDTO> getAllArtists() {
        return artistService.getAllArtists().stream()
                            .map(ArtistDTO::new)
                            .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ArtistDTO> getArtistById(@PathVariable Integer id) {
        return artistService.getArtistById(id)
                .map(artist -> ResponseEntity.ok(new ArtistDTO(artist)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ArtistDTO createArtist(@RequestBody Artist artist) {
        Artist createdArtist = artistService.createOrUpdateArtist(artist);
        return new ArtistDTO(createdArtist);
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ArtistDTO> updateArtist(@PathVariable Integer id, @RequestBody Artist artistDetails) {
        return artistService.getArtistById(id)
                .map(artist -> {
                    artist.setName(artistDetails.getName());
                    artist.setStageName(artistDetails.getStageName());
                    artist.setDateOfBirth(artistDetails.getDateOfBirth());
                    artist.setWebsite(artistDetails.getWebsite());
                    artist.setInstagramProfile(artistDetails.getInstagramProfile());
                    Artist updatedArtist = artistService.createOrUpdateArtist(artist);
                    return ResponseEntity.ok(new ArtistDTO(updatedArtist));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteArtist(@PathVariable Integer id) {
        return artistService.getArtistById(id)
                .map(artist -> {
                    artistService.deleteArtist(id);
                    return ResponseEntity.noContent().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
