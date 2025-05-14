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

import java.time.LocalDate;
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
    private ArtistRepository artistRepository;

    @Autowired
    private BandRepository bandRepository;

    @Autowired
    private BandMemberRepository bandMemberRepository;

    private ObjectMapper objectMapper;
    private Artist artist1;
    private Band band1;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        bandMemberRepository.deleteAll();
        artistRepository.deleteAll();
        bandRepository.deleteAll();

        artist1 = new Artist();
        artist1.setName("Artist 1");
        artist1.setDateOfBirth(LocalDate.of(1990, 1, 1));
        artist1 = artistRepository.save(artist1);

        band1 = new Band();
        band1.setBandName("Band 1");
        band1.setFormationDate(LocalDate.of(2000, 2, 2));
        band1 = bandRepository.save(band1);
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
        bm.setRole("Vocalist");
        bm.setJoinDate(LocalDate.now());

        // Create
        String result = mockMvc.perform(post("/api/band-members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bm)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id.bandId", is(band1.getBandId())))
                .andExpect(jsonPath("$.id.artistId", is(artist1.getArtistId())))
                .andReturn().getResponse().getContentAsString();

        // Get all
        mockMvc.perform(get("/api/band-members"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));

        // Get by id
        mockMvc.perform(get("/api/band-members/find")
                        .param("bandId", band1.getBandId().toString())
                        .param("artistId", artist1.getArtistId().toString()))
                .andExpect(status().isOk());
    }

    @Test
    void updateBandMember() throws Exception {
        // Prepare existing
        BandMember existing = new BandMember();
        existing.setId(new BandMemberId(band1.getBandId(), artist1.getArtistId()));
        existing.setBand(band1);
        existing.setArtist(artist1);
        existing.setRole("Guitarist");
        existing.setJoinDate(LocalDate.of(2020, 1, 1));
        existing = bandMemberRepository.save(existing);

        BandMember updated = new BandMember();
        updated.setId(new BandMemberId(band1.getBandId(), artist1.getArtistId()));
        updated.setRole("Lead Guitarist");
        updated.setJoinDate(LocalDate.of(2021, 1, 1));

        mockMvc.perform(put("/api/band-members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updated)))
                .andExpect(status().isOk());
    }

    @Test
    void deleteBandMember() throws Exception {
        // Prepare existing
        BandMember existing = new BandMember();
        existing.setId(new BandMemberId(band1.getBandId(), artist1.getArtistId()));
        existing.setBand(band1);
        existing.setArtist(artist1);
        bandMemberRepository.save(existing);

        mockMvc.perform(delete("/api/band-members/delete")
                        .param("bandId", band1.getBandId().toString())
                        .param("artistId", artist1.getArtistId().toString()))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/band-members/find")
                        .param("bandId", band1.getBandId().toString())
                        .param("artistId", artist1.getArtistId().toString()))
                .andExpect(status().isNotFound());
    }

    @Test
    void getBandMember_notFound() throws Exception {
        mockMvc.perform(get("/api/band-members/find")
                        .param("bandId", "999")
                        .param("artistId", "999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateBandMember_notFound() throws Exception {
        BandMember updated = new BandMember();
        updated.setId(new BandMemberId(999, 999));
        updated.setRole("Ghost Role");
        updated.setJoinDate(LocalDate.now());

        mockMvc.perform(put("/api/band-members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updated)))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteBandMember_notFound() throws Exception {
        mockMvc.perform(delete("/api/band-members/delete")
                        .param("bandId", "123")
                        .param("artistId", "456"))
                .andExpect(status().isNotFound());
    }
}
