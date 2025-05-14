package com.example.dbapp.controller;

import com.example.dbapp.model.Artist;
import com.example.dbapp.repository.ArtistRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Collections;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class ArtistControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ArtistRepository artistRepository;

    private Artist sampleArtist;

    @BeforeEach
    void setUp() {
        artistRepository.deleteAll();
        artistRepository.flush();

        sampleArtist = new Artist();
        sampleArtist.setName("Sample Artist Name");
        sampleArtist.setStageName("The Sampler");
        sampleArtist.setDateOfBirth(LocalDate.of(1980, 6, 15));
        sampleArtist.setWebsite("http://sampleartist.com");
        sampleArtist.setInstagramProfile("@sampler");
        sampleArtist.setArtistGenres(Collections.emptySet());
        sampleArtist.setPerformances(Collections.emptySet());
        sampleArtist.setBandMembers(Collections.emptySet());
    }

    @Test
    void testCreateArtist() throws Exception {
        Artist newArtist = new Artist();
        newArtist.setName("New Artist");
        newArtist.setStageName("The Newcomer");
        newArtist.setDateOfBirth(LocalDate.of(1995, 3, 10));
        newArtist.setWebsite("http://newartist.com");
        newArtist.setInstagramProfile("@newcomer");
        newArtist.setArtistGenres(Collections.emptySet());
        newArtist.setPerformances(Collections.emptySet());
        newArtist.setBandMembers(Collections.emptySet());

        mockMvc.perform(post("/api/artists")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newArtist)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.artistId", notNullValue()))
                .andExpect(jsonPath("$.artistName", is("New Artist")))
                .andExpect(jsonPath("$.stageName", is("The Newcomer")));
    }

    @Test
    void testGetArtistById() throws Exception {
        Artist savedArtist = artistRepository.save(sampleArtist);

        mockMvc.perform(get("/api/artists/" + savedArtist.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.artistId", is(savedArtist.getId())))
                .andExpect(jsonPath("$.artistName", is(sampleArtist.getName())));
    }

    @Test
    void testGetArtistById_NotFound() throws Exception {
        mockMvc.perform(get("/api/artists/99999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetAllArtists() throws Exception {
        artistRepository.save(sampleArtist);

        mockMvc.perform(get("/api/artists"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].artistName", is(sampleArtist.getName())));
    }

    @Test
    void testUpdateArtist() throws Exception {
        Artist savedArtist = artistRepository.save(sampleArtist);

        Artist updatedInfo = new Artist();
        updatedInfo.setName("Updated Artist Name");
        updatedInfo.setStageName("The Veteran");
        updatedInfo.setDateOfBirth(savedArtist.getDateOfBirth());
        updatedInfo.setWebsite("http://updatedartist.com");
        updatedInfo.setInstagramProfile("@veteran");
        updatedInfo.setArtistGenres(Collections.emptySet());
        updatedInfo.setPerformances(Collections.emptySet());
        updatedInfo.setBandMembers(Collections.emptySet());

        mockMvc.perform(put("/api/artists/" + savedArtist.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedInfo)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.artistId", is(savedArtist.getId())))
                .andExpect(jsonPath("$.artistName", is("Updated Artist Name")))
                .andExpect(jsonPath("$.stageName", is("The Veteran")));
    }

    @Test
    void testUpdateArtist_NotFound() throws Exception {
        Artist updatedInfo = new Artist();
        updatedInfo.setName("NonExistent Artist");
        updatedInfo.setDateOfBirth(LocalDate.of(1990, 1, 1));
        updatedInfo.setArtistGenres(Collections.emptySet());
        updatedInfo.setPerformances(Collections.emptySet());
        updatedInfo.setBandMembers(Collections.emptySet());

        mockMvc.perform(put("/api/artists/99999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedInfo)))
                .andExpect(status().isNotFound());
    }

    @Test
    void testDeleteArtist() throws Exception {
        Artist savedArtist = artistRepository.save(sampleArtist);

        mockMvc.perform(delete("/api/artists/" + savedArtist.getId()))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/artists/" + savedArtist.getId()))
                .andExpect(status().isNotFound());
    }
}
