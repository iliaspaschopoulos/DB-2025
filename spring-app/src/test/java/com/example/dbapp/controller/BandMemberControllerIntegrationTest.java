package com.example.dbapp.controller;

import com.example.dbapp.model.BandMember;
import com.example.dbapp.model.Artist;
import com.example.dbapp.model.Band;
import com.example.dbapp.model.BandMemberId;
import com.example.dbapp.repository.ArtistRepository;
import com.example.dbapp.repository.BandMemberRepository;
import com.example.dbapp.repository.BandRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
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

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
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
    private BandMemberRepository bandMemberRepository;

    @Autowired
    private BandRepository bandRepository;

    @Autowired
    private ArtistRepository artistRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private Band band1;
    private Artist artist1;
    private BandMember bandMember1;
    private BandMember bandMember2;

    @BeforeEach
    void setUp() {
        bandMemberRepository.deleteAll();
        bandRepository.deleteAll();
        artistRepository.deleteAll();

        band1 = new Band();
        band1.setBandName("Test Band For Members");
        band1.setFormationDate(LocalDate.of(2000,1,1));
        band1 = bandRepository.saveAndFlush(band1);

        artist1 = new Artist();
        artist1.setName("Test Artist For Band");
        artist1.setDateOfBirth(LocalDate.of(1980,1,1));
        artist1 = artistRepository.saveAndFlush(artist1);

        Artist artist2 = new Artist();
        artist2.setName("Another Test Artist");
        artist2.setDateOfBirth(LocalDate.of(1985,5,5));
        artist2 = artistRepository.saveAndFlush(artist2);

        bandMember1 = new BandMember();
        bandMember1.setBand(band1);
        bandMember1.setArtist(artist1);
        bandMember1.setId(new BandMemberId(band1.getId(), artist1.getId()));
        bandMember1.setJoinDate(LocalDate.of(2000, 1, 1));
        bandMember1.setRole("Vocalist");
        bandMember1 = bandMemberRepository.saveAndFlush(bandMember1);

        bandMember2 = new BandMember();
        bandMember2.setBand(band1);
        bandMember2.setArtist(artist2);
        bandMember2.setId(new BandMemberId(band1.getId(), artist2.getId()));
        bandMember2.setJoinDate(LocalDate.of(2002, 6, 1));
        bandMember2.setRole("Guitarist");
        bandMember2 = bandMemberRepository.saveAndFlush(bandMember2);
    }

    @Test
    void getAllBandMembers() throws Exception {
        mockMvc.perform(get("/api/band_members"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].role", is(bandMember1.getRole())))
                .andExpect(jsonPath("$[1].role", is(bandMember2.getRole())));
    }

    @Test
    void getBandMemberById() throws Exception {
        mockMvc.perform(get("/api/band_members/find")
                        .param("bandId", String.valueOf(bandMember1.getId().getBandId()))
                        .param("artistId", String.valueOf(bandMember1.getId().getArtistId())))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.role", is(bandMember1.getRole())))
                .andExpect(jsonPath("$.artist.artistName", is(artist1.getName())))
                .andExpect(jsonPath("$.band.bandName", is(band1.getBandName())));
    }

    @Test
    void getBandMemberById_notFound() throws Exception {
        mockMvc.perform(get("/api/band_members/find")
                        .param("bandId", "999")
                        .param("artistId", "999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void createBandMember() throws Exception {
        Artist newArtist = new Artist();
        newArtist.setName("New Member Artist");
        newArtist.setDateOfBirth(LocalDate.of(1990,1,1));
        newArtist = artistRepository.saveAndFlush(newArtist);

        BandMember newBandMember = new BandMember();
        newBandMember.setBand(band1);
        newBandMember.setArtist(newArtist);
        newBandMember.setId(new BandMemberId(band1.getId(), newArtist.getId()));
        newBandMember.setJoinDate(LocalDate.of(2023, 1, 1));
        newBandMember.setRole("Bassist");

        mockMvc.perform(post("/api/band_members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newBandMember)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.role", is("Bassist")))
                .andExpect(jsonPath("$.id.artistId", is(newArtist.getId())))
                .andExpect(jsonPath("$.id.bandId", is(band1.getId())))
                .andExpect(jsonPath("$.artist.id", is(newArtist.getId())))
                .andExpect(jsonPath("$.band.id", is(band1.getId())));
    }

    @Test
    void updateBandMember() throws Exception {
        BandMember updatedBandMemberDetails = new BandMember();
        updatedBandMemberDetails.setId(new BandMemberId(bandMember1.getBand().getId(), bandMember1.getArtist().getId()));
        updatedBandMemberDetails.setBand(bandMember1.getBand());
        updatedBandMemberDetails.setArtist(bandMember1.getArtist());
        updatedBandMemberDetails.setJoinDate(LocalDate.of(2001, 1, 1));
        updatedBandMemberDetails.setRole("Lead Vocalist");

        mockMvc.perform(put("/api/band_members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedBandMemberDetails)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.role", is("Lead Vocalist")))
                .andExpect(jsonPath("$.joinDate", is("2001-01-01")));
    }

    @Test
    void updateBandMember_notFound() throws Exception {
        BandMember nonExistentBandMember = new BandMember();
        BandMemberId nonExistentId = new BandMemberId(999, 999); // Non-existent ID
        nonExistentBandMember.setId(nonExistentId);

        Band dummyBand = new Band();
        dummyBand.setId(999); 
        dummyBand.setBandName("Dummy Band Name"); // Populate non-nullable field

        Artist dummyArtist = new Artist();
        dummyArtist.setId(999); 
        dummyArtist.setName("Dummy Artist Name"); // Populate non-nullable field (artistName)
        dummyArtist.setDateOfBirth(LocalDate.of(1900, 1, 1)); // Populate non-nullable field

        nonExistentBandMember.setBand(dummyBand);
        nonExistentBandMember.setArtist(dummyArtist);
        nonExistentBandMember.setRole("Non Existent Role");
        nonExistentBandMember.setJoinDate(LocalDate.now());

        mockMvc.perform(put("/api/band_members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(nonExistentBandMember)))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteBandMember() throws Exception {
        mockMvc.perform(delete("/api/band_members/delete")
                        .param("bandId", String.valueOf(bandMember1.getId().getBandId()))
                        .param("artistId", String.valueOf(bandMember1.getId().getArtistId())))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/band_members/find")
                        .param("bandId", String.valueOf(bandMember1.getId().getBandId()))
                        .param("artistId", String.valueOf(bandMember1.getId().getArtistId())))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteBandMember_notFound() throws Exception {
        mockMvc.perform(delete("/api/band_members/delete")
                        .param("bandId", "999")
                        .param("artistId", "999"))
                .andExpect(status().isNotFound());
    }
}
