package com.example.dbapp.controller;

import com.example.dbapp.model.Band;
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
public class BandControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BandRepository bandRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private Band band1;
    private Band band2;

    @BeforeEach
    void setUp() {
        bandRepository.deleteAll();

        band1 = new Band();
        band1.setBandName("Test Band 1");
        band1.setFormationDate(LocalDate.of(2000, 1, 1));
        band1 = bandRepository.save(band1);

        band2 = new Band();
        band2.setBandName("Test Band 2");
        band2.setFormationDate(LocalDate.of(2010, 5, 5));
        band2 = bandRepository.save(band2);
    }

    @Test
    void getAllBands() throws Exception {
        mockMvc.perform(get("/api/bands"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].bandName", is(band1.getBandName())))
                .andExpect(jsonPath("$[1].bandName", is(band2.getBandName())));
    }

    @Test
    void getBandById() throws Exception {
        mockMvc.perform(get("/api/bands/" + band1.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.bandName", is(band1.getBandName())))
                .andExpect(jsonPath("$.formationDate", is(band1.getFormationDate().toString())));
    }

    @Test
    void getBandById_notFound() throws Exception {
        mockMvc.perform(get("/api/bands/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void createBand() throws Exception {
        Band newBand = new Band();
        newBand.setBandName("New Awesome Band");
        newBand.setFormationDate(LocalDate.of(2022, 3, 15));

        mockMvc.perform(post("/api/bands")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newBand)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.bandName", is("New Awesome Band")))
                .andExpect(jsonPath("$.formationDate", is("2022-03-15")));
    }

    @Test
    void updateBand() throws Exception {
        Band updatedBand = new Band();
        updatedBand.setBandName("Updated Band Name");
        updatedBand.setFormationDate(LocalDate.of(2001, 2, 2));


        mockMvc.perform(put("/api/bands/" + band1.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedBand)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.bandName", is("Updated Band Name")))
                .andExpect(jsonPath("$.formationDate", is("2001-02-02")));
    }

    @Test
    void updateBand_notFound() throws Exception {
        Band updatedBand = new Band();
        updatedBand.setBandName("Non Existent Band Update");
        updatedBand.setFormationDate(LocalDate.now());

        mockMvc.perform(put("/api/bands/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedBand)))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteBand() throws Exception {
        mockMvc.perform(delete("/api/bands/" + band1.getId()))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/bands/" + band1.getId()))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteBand_notFound() throws Exception {
        mockMvc.perform(delete("/api/bands/999"))
                .andExpect(status().isNotFound());
    }
}
