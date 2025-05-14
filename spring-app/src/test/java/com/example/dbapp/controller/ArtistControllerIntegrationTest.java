package com.example.dbapp.controller;

import com.example.dbapp.model.Artist;
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
import java.util.Collections;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class ArtistControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ArtistRepository artistRepository;

    private ObjectMapper objectMapper;

    private Artist artist1;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        artistRepository.deleteAll();

        artist1 = new Artist();
        artist1.setName("Test Artist 1");
        artist1.setStageName("The Tester");
        artist1.setDateOfBirth(LocalDate.of(1985, 5, 15));
        artist1.setWebsite("http://testartist1.com");
        artist1.setInstagramProfile("@tester1");
        // Initialize collections to avoid NullPointerExceptions if accessed
        artist1.setArtistGenres(Collections.emptySet());
        artist1.setPerformances(Collections.emptySet()); 
        artist1.setBandMembers(Collections.emptySet());
        artist1 = artistRepository.save(artist1);
    }

    @Test
    void createArtist() throws Exception {
        Artist newArtist = new Artist();
        newArtist.setName("New Artist");
        newArtist.setStageName("The Newbie");
        newArtist.setDateOfBirth(LocalDate.of(1990, 10, 20));
        newArtist.setWebsite("http://newartist.com");
        newArtist.setInstagramProfile("@newbie");
        newArtist.setArtistGenres(Collections.emptySet());
        newArtist.setPerformances(Collections.emptySet());
        newArtist.setBandMembers(Collections.emptySet());

        mockMvc.perform(post("/api/artists")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newArtist)))
                .andExpect(status().isCreated()) // Expecting 201 Created for new resource
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.artistId", notNullValue()))
                .andExpect(jsonPath("$.name", is("New Artist")))
                .andExpect(jsonPath("$.stageName", is("The Newbie")))
                .andExpect(jsonPath("$.dateOfBirth", is("1990-10-20")))
                .andExpect(jsonPath("$.website", is("http://newartist.com")))
                .andExpect(jsonPath("$.instagramProfile", is("@newbie")));
    }

    @Test
    void getAllArtists() throws Exception {
        // artist1 is already saved in setUp
        Artist artist2 = new Artist();
        artist2.setName("Test Artist 2");
        artist2.setStageName("Another Tester");
        artist2.setDateOfBirth(LocalDate.of(1988, 8, 8));
        artist2.setWebsite("http://testartist2.com");
        artist2.setInstagramProfile("@anotherTester");
        artist2.setArtistGenres(Collections.emptySet());
        artist2.setPerformances(Collections.emptySet());
        artist2.setBandMembers(Collections.emptySet());
        artistRepository.save(artist2);

        mockMvc.perform(get("/api/artists"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[*].name", containsInAnyOrder(artist1.getName(), artist2.getName())))
                .andExpect(jsonPath("$[*].stageName", containsInAnyOrder(artist1.getStageName(), artist2.getStageName())))
                .andExpect(jsonPath("$[*].dateOfBirth", containsInAnyOrder(artist1.getDateOfBirth().toString(), artist2.getDateOfBirth().toString())))
                .andExpect(jsonPath("$[*].website", containsInAnyOrder(artist1.getWebsite(), artist2.getWebsite())))
                .andExpect(jsonPath("$[*].instagramProfile", containsInAnyOrder(artist1.getInstagramProfile(), artist2.getInstagramProfile())));
    }

    @Test
    void getArtistById() throws Exception {
        mockMvc.perform(get("/api/artists/" + artist1.getArtistId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.artistId", is(artist1.getArtistId())))
                .andExpect(jsonPath("$.name", is(artist1.getName())))
                .andExpect(jsonPath("$.stageName", is(artist1.getStageName())))
                .andExpect(jsonPath("$.dateOfBirth", is(artist1.getDateOfBirth().toString())))
                .andExpect(jsonPath("$.website", is(artist1.getWebsite())))
                .andExpect(jsonPath("$.instagramProfile", is(artist1.getInstagramProfile())));
    }

    @Test
    void getArtistById_notFound() throws Exception {
        mockMvc.perform(get("/api/artists/9999")) // Non-existent ID
                .andExpect(status().isNotFound());
    }

    @Test
    void updateArtist() throws Exception {
        Artist updatedArtistInfo = new Artist();
        updatedArtistInfo.setName("Updated Artist Name");
        updatedArtistInfo.setStageName("The Veteran");
        updatedArtistInfo.setDateOfBirth(LocalDate.of(1980, 1, 1)); // Different DOB
        updatedArtistInfo.setWebsite("http://updatedartist.com");
        updatedArtistInfo.setInstagramProfile("@veteran");
        // Collections can remain empty or be updated if needed for the test scope
        updatedArtistInfo.setArtistGenres(Collections.emptySet());
        updatedArtistInfo.setPerformances(Collections.emptySet());
        updatedArtistInfo.setBandMembers(Collections.emptySet());


        mockMvc.perform(put("/api/artists/" + artist1.getArtistId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedArtistInfo)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.artistId", is(artist1.getArtistId())))
                .andExpect(jsonPath("$.name", is("Updated Artist Name")))
                .andExpect(jsonPath("$.stageName", is("The Veteran")))
                .andExpect(jsonPath("$.dateOfBirth", is("1980-01-01")))
                .andExpect(jsonPath("$.website", is("http://updatedartist.com")))
                .andExpect(jsonPath("$.instagramProfile", is("@veteran")));
    }

    @Test
    void updateArtist_notFound() throws Exception {
        Artist nonExistentArtistUpdate = new Artist();
        nonExistentArtistUpdate.setName("Ghost Artist");
        nonExistentArtistUpdate.setDateOfBirth(LocalDate.now());

        mockMvc.perform(put("/api/artists/9999") // Non-existent ID
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(nonExistentArtistUpdate)))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteArtist() throws Exception {
        mockMvc.perform(delete("/api/artists/" + artist1.getArtistId()))
                .andExpect(status().isNoContent()); // Expecting 204 No Content

        // Verify it's actually deleted
        mockMvc.perform(get("/api/artists/" + artist1.getArtistId()))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteArtist_notFound() throws Exception {
        mockMvc.perform(delete("/api/artists/9999")) // Non-existent ID
                .andExpect(status().isNotFound());
    }
}
