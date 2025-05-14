package com.example.dbapp.controller;

import com.example.dbapp.model.Artist;
import com.example.dbapp.model.Band;
import com.example.dbapp.model.BandMember;
import com.example.dbapp.model.BandMemberId;
import com.example.dbapp.repository.ArtistRepository;
import com.example.dbapp.repository.BandMemberRepository;
import com.example.dbapp.repository.BandRepository;
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

import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class BandMemberControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BandRepository bandRepository;

    @Autowired
    private ArtistRepository artistRepository;

    @Autowired
    private BandMemberRepository bandMemberRepository;

    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    private Band band1;
    private Band band2;
    private Artist artist1;
    private Artist artist2;

    @BeforeEach
    void setup() {
        // Clear existing data
        bandMemberRepository.deleteAll();
        
        // Create test bands
        band1 = new Band();
        band1.setBandName("Test Band 1");
        band1 = bandRepository.save(band1);

        band2 = new Band();
        band2.setBandName("Test Band 2");
        band2 = bandRepository.save(band2);

        // Create test artists
        artist1 = new Artist();
        artist1.setName("Test Artist 1");
        artist1 = artistRepository.save(artist1);

        artist2 = new Artist();
        artist2.setName("Test Artist 2");
        artist2 = artistRepository.save(artist2);
    }

    @Test
    void getAllBandMembers_empty() throws Exception {
        mockMvc.perform(get("/api/band-members"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void createAndGetBandMember() throws Exception {
        BandMember bm = new BandMember();
        bm.setId(new BandMemberId(band1.getBandId(), artist1.getArtistId()));

        // Create
        mockMvc.perform(post("/api/band-members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bm)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        // Get by ID
        mockMvc.perform(get("/api/band-members/find")
                        .param("bandId", band1.getBandId().toString())
                        .param("artistId", artist1.getArtistId().toString()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id.bandId", is(band1.getBandId())))
                .andExpect(jsonPath("$.id.artistId", is(artist1.getArtistId())));

        // Get all
        mockMvc.perform(get("/api/band-members"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id.bandId", is(band1.getBandId())))
                .andExpect(jsonPath("$[0].id.artistId", is(artist1.getArtistId())));
    }

    @Test
    void updateBandMember() throws Exception {
        BandMember existing = new BandMember();
        existing.setId(new BandMemberId(band1.getBandId(), artist1.getArtistId()));
        existing.setBand(band1);
        existing.setArtist(artist1);
        existing = bandMemberRepository.save(existing);

        BandMember updated = new BandMember();
        updated.setId(new BandMemberId(band1.getBandId(), artist1.getArtistId()));

        mockMvc.perform(put("/api/band-members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updated)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id.bandId", is(band1.getBandId())))
                .andExpect(jsonPath("$.id.artistId", is(artist1.getArtistId())));
    }

    @Test
    void deleteBandMember() throws Exception {
        // Create first
        BandMember bm = new BandMember();
        bm.setId(new BandMemberId(band1.getBandId(), artist1.getArtistId()));
        bm.setBand(band1);
        bm.setArtist(artist1);
        bandMemberRepository.save(bm);

        // Delete
        mockMvc.perform(delete("/api/band-members/delete")
                        .param("bandId", band1.getBandId().toString())
                        .param("artistId", artist1.getArtistId().toString()))
                .andExpect(status().isOk());

        // Verify deleted
        mockMvc.perform(get("/api/band-members"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void updateBandMember_notFound() throws Exception {
        BandMember updated = new BandMember();
        updated.setId(new BandMemberId(999, 999));

        mockMvc.perform(put("/api/band-members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updated)))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteBandMember_notFound() throws Exception {
        mockMvc.perform(delete("/api/band-members/delete")
                        .param("bandId", "999")
                        .param("artistId", "999"))
                .andExpect(status().isNotFound());
    }
}
