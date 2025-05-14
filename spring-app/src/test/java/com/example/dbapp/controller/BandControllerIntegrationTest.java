package com.example.dbapp.controller;

import com.example.dbapp.model.Band;
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
import java.util.Collections;

import static org.hamcrest.Matchers.*;
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

    private ObjectMapper objectMapper;
    private Band band1;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        bandRepository.deleteAll();

        band1 = new Band();
        band1.setBandName("Test Band 1");
        band1.setFormationDate(LocalDate.of(2000, 1, 1));
        band1.setWebsite("http://testband1.com");
        band1 = bandRepository.save(band1);
    }

    @Test
    void createBand() throws Exception {
        Band newBand = new Band();
        newBand.setBandName("New Band");
        newBand.setFormationDate(LocalDate.of(2010, 5, 5));
        newBand.setWebsite("http://newband.com");

        mockMvc.perform(post("/api/bands")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newBand)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.bandId", notNullValue()))
                .andExpect(jsonPath("$.bandName", is("New Band")))
                .andExpect(jsonPath("$.formationDate", is("2010-05-05")))
                .andExpect(jsonPath("$.website", is("http://newband.com")));
    }

    @Test
    void getAllBands() throws Exception {
        Band band2 = new Band();
        band2.setBandName("Test Band 2");
        band2.setFormationDate(LocalDate.of(2005, 2, 2));
        band2.setWebsite("http://testband2.com");
        bandRepository.save(band2);

        mockMvc.perform(get("/api/bands"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[*].bandName", containsInAnyOrder(band1.getBandName(), band2.getBandName())))
                .andExpect(jsonPath("$[*].website", containsInAnyOrder(band1.getWebsite(), band2.getWebsite())));
    }

    @Test
    void getBandById() throws Exception {
        mockMvc.perform(get("/api/bands/" + band1.getBandId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.bandId", is(band1.getBandId())))
                .andExpect(jsonPath("$.bandName", is(band1.getBandName())))
                .andExpect(jsonPath("$.formationDate", is(band1.getFormationDate().toString())))
                .andExpect(jsonPath("$.website", is(band1.getWebsite())));
    }

    @Test
    void getBandById_notFound() throws Exception {
        mockMvc.perform(get("/api/bands/9999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateBand() throws Exception {
        Band updatedInfo = new Band();
        updatedInfo.setBandName("Updated Band");
        updatedInfo.setFormationDate(LocalDate.of(1999, 9, 9));
        updatedInfo.setWebsite("http://updatedband.com");

        mockMvc.perform(put("/api/bands/" + band1.getBandId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedInfo)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.bandId", is(band1.getBandId())))
                .andExpect(jsonPath("$.bandName", is("Updated Band")))
                .andExpect(jsonPath("$.formationDate", is("1999-09-09")))
                .andExpect(jsonPath("$.website", is("http://updatedband.com")));
    }

    @Test
    void updateBand_notFound() throws Exception {
        Band nonExistent = new Band();
        nonExistent.setBandName("Ghost Band");

        mockMvc.perform(put("/api/bands/9999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(nonExistent)))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteBand() throws Exception {
        mockMvc.perform(delete("/api/bands/" + band1.getBandId()))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/bands/" + band1.getBandId()))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteBand_notFound() throws Exception {
        mockMvc.perform(delete("/api/bands/9999"))
                .andExpect(status().isNotFound());
    }
}
