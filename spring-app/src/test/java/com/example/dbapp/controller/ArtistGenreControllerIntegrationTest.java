package com.example.dbapp.controller;

import com.example.dbapp.model.Artist;
import com.example.dbapp.model.ArtistGenre;
import com.example.dbapp.model.ArtistGenreId;
import com.example.dbapp.repository.ArtistGenreRepository;
import com.example.dbapp.repository.ArtistRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class ArtistGenreControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ArtistRepository artistRepository;

    @Autowired
    private ArtistGenreRepository artistGenreRepository;

    private ObjectMapper objectMapper;
    private Artist artist;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        artistGenreRepository.deleteAll();
        artistRepository.deleteAll();
        artist = new Artist();
        artist.setName("Test Artist");
        artist.setDateOfBirth(LocalDate.of(1990, 1, 1));
        artist = artistRepository.save(artist);
    }

    @Test
    void getAllArtistGenres_empty() throws Exception {
        mockMvc.perform(get("/api/artist-genres"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void createAndGetArtistGenre() throws Exception {
        ArtistGenre ag = new ArtistGenre();
        ag.setId(new ArtistGenreId(artist.getArtistId(), "Rock"));
        ag.setGenre("Rock");
        ag.setSubgenre("Alt");

        // Create
        mockMvc.perform(post("/api/artist-genres")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ag)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id.artistId", is(artist.getArtistId())))
                .andExpect(jsonPath("$.id.genre", is("Rock")))
                .andExpect(jsonPath("$.subgenre", is("Alt")));

        // Get all
        mockMvc.perform(get("/api/artist-genres"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));

        // Get by id
        mockMvc.perform(get("/api/artist-genres/find")
                        .param("artistId", artist.getArtistId().toString())
                        .param("genre", "Rock"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.subgenre", is("Alt")));
    }

    @Test
    void updateArtistGenre() throws Exception {
        ArtistGenre existing = new ArtistGenre();
        existing.setId(new ArtistGenreId(artist.getArtistId(), "Jazz"));
        existing.setArtist(artist);
        existing.setGenre("Jazz");
        existing.setSubgenre("Smooth");
        artistGenreRepository.save(existing);

        ArtistGenre updateDetails = new ArtistGenre();
        updateDetails.setSubgenre("Bebop");

        mockMvc.perform(put("/api/artist-genres/update")
                        .param("artistId", artist.getArtistId().toString())
                        .param("genre", "Jazz")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDetails)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.subgenre", is("Bebop")));
    }

    @Test
    void deleteArtistGenre() throws Exception {
        ArtistGenre existing = new ArtistGenre();
        existing.setId(new ArtistGenreId(artist.getArtistId(), "Pop"));
        existing.setArtist(artist);
        existing.setGenre("Pop");
        existing.setSubgenre("Synth");
        artistGenreRepository.save(existing);

        mockMvc.perform(delete("/api/artist-genres/delete")
                        .param("artistId", artist.getArtistId().toString())
                        .param("genre", "Pop"))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/artist-genres/find")
                        .param("artistId", artist.getArtistId().toString())
                        .param("genre", "Pop"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getArtistGenre_notFound() throws Exception {
        mockMvc.perform(get("/api/artist-genres/find")
                        .param("artistId", "999")
                        .param("genre", "Unknown"))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateArtistGenre_notFound() throws Exception {
        ArtistGenre updateDetails = new ArtistGenre();
        updateDetails.setSubgenre("Bar");

        mockMvc.perform(put("/api/artist-genres/update")
                        .param("artistId", "999")
                        .param("genre", "Foo")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDetails)))
                .andExpect(status().isBadRequest());

        mockMvc.perform(put("/api/artist-genres/update")
                        .param("artistId", artist.getArtistId().toString())
                        .param("genre", "NonExistentGenre")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDetails)))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteArtistGenre_notFound() throws Exception {
        mockMvc.perform(delete("/api/artist-genres/delete")
                        .param("artistId", "123")
                        .param("genre", "Nope"))
                .andExpect(status().isNotFound());
    }
}
