package com.example.dbapp.controller;

import com.example.dbapp.model.Artist;
import com.example.dbapp.model.Band;
import com.example.dbapp.model.Event;
import com.example.dbapp.model.Performance;
import com.example.dbapp.service.PerformanceService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.time.LocalTime;
import java.util.Arrays;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

@WebMvcTest(controllers = PerformanceController.class,
        excludeAutoConfiguration = {
                DataSourceAutoConfiguration.class,
                HibernateJpaAutoConfiguration.class,
                JpaRepositoriesAutoConfiguration.class
        })
public class PerformanceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PerformanceService performanceService;

    private ObjectMapper objectMapper;

    private Performance performance1;
    private Performance performance2;
    private Event sampleEvent;
    private Artist sampleArtist;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule()); // Important for LocalTime serialization

        sampleEvent = new Event();
        sampleEvent.setEventId(1);

        sampleArtist = new Artist();
        sampleArtist.setArtistId(1);
        sampleArtist.setName("Test Artist");


        performance1 = new Performance();
        performance1.setId(1);
        performance1.setEvent(sampleEvent);
        performance1.setArtist(sampleArtist);
        performance1.setPerformanceType("headline");
        performance1.setStartTime(LocalTime.of(20, 0));
        performance1.setDuration(LocalTime.of(1, 30)); // 1 hour 30 minutes

        performance2 = new Performance();
        performance2.setId(2);
        performance2.setEvent(sampleEvent);
        Band sampleBand = new Band();
        sampleBand.setBandId(1);
        sampleBand.setBandName("Test Band");
        performance2.setBand(sampleBand);
        performance2.setPerformanceType("warm up");
        performance2.setStartTime(LocalTime.of(18, 0));
        performance2.setDuration(LocalTime.of(0, 45)); // 45 minutes
    }

    @Test
    void getAllPerformances_shouldReturnListOfPerformances() throws Exception {
        given(performanceService.getAllPerformances()).willReturn(Arrays.asList(performance1, performance2));

        mockMvc.perform(get("/api/performances"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(performance1.getId())))
                .andExpect(jsonPath("$[1].id", is(performance2.getId())));
    }

    @Test
    void getPerformanceById_whenPerformanceExists_shouldReturnPerformance() throws Exception {
        given(performanceService.getPerformanceById(1)).willReturn(Optional.of(performance1));

        mockMvc.perform(get("/api/performances/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(performance1.getId())))
                .andExpect(jsonPath("$.performanceType", is("headline")));
    }

    @Test
    void getPerformanceById_whenPerformanceNotExists_shouldReturnNotFound() throws Exception {
        given(performanceService.getPerformanceById(99)).willReturn(Optional.empty());

        mockMvc.perform(get("/api/performances/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void createPerformance_whenValidInput_shouldReturnCreatedPerformance() throws Exception {
        Performance newPerformance = new Performance();
        // Set required fields for newPerformance
        newPerformance.setEvent(sampleEvent);
        newPerformance.setArtist(sampleArtist); // or setBand
        newPerformance.setStartTime(LocalTime.of(22,0));
        newPerformance.setDuration(LocalTime.of(1,0));
        newPerformance.setPerformanceType("Special guest");


        given(performanceService.savePerformance(any(Performance.class))).willReturn(newPerformance); // Assume save assigns an ID

        mockMvc.perform(post("/api/performances")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newPerformance)))
                .andExpect(status().isCreated())
                // Add more assertions if savePerformance returns the object with an ID
                .andExpect(jsonPath("$.performanceType", is("Special guest")));
    }
    
    @Test
    void createPerformance_whenServiceThrowsResponseStatusException_shouldReturnError() throws Exception {
        Performance invalidPerformance = new Performance(); // Missing required fields or invalid data
        // Setup for invalidPerformance (e.g., missing event or both artist and band)
        invalidPerformance.setStartTime(LocalTime.of(10,0));
        invalidPerformance.setDuration(LocalTime.of(1,0));


        given(performanceService.savePerformance(any(Performance.class)))
            .willThrow(new ResponseStatusException(HttpStatus.BAD_REQUEST, "Artist or Band must be set, not both or none."));

        mockMvc.perform(post("/api/performances")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidPerformance)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Artist or Band must be set, not both or none."));
    }


    @Test
    void updatePerformance_whenPerformanceExists_shouldReturnUpdatedPerformance() throws Exception {
        Performance updatedDetails = new Performance();
        updatedDetails.setEvent(sampleEvent);
        updatedDetails.setArtist(sampleArtist);
        updatedDetails.setPerformanceType("updated headline");
        updatedDetails.setStartTime(performance1.getStartTime()); // Keep some old values
        updatedDetails.setDuration(performance1.getDuration());   // Keep some old values

        given(performanceService.updatePerformance(eq(1), any(Performance.class))).willReturn(updatedDetails);

        mockMvc.perform(put("/api/performances/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedDetails)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.performanceType", is("updated headline")));
    }

    @Test
    void updatePerformance_whenPerformanceNotExists_shouldReturnNotFound() throws Exception {
        Performance updatedDetails = new Performance();
        // Set fields for updatedDetails
        updatedDetails.setEvent(sampleEvent);
        updatedDetails.setArtist(sampleArtist);
        updatedDetails.setPerformanceType("updated headline");
        updatedDetails.setStartTime(LocalTime.of(20,0));
        updatedDetails.setDuration(LocalTime.of(1,30));


        given(performanceService.updatePerformance(eq(99), any(Performance.class)))
                .willThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Performance not found"));

        mockMvc.perform(put("/api/performances/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedDetails)))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Performance not found"));
    }

    @Test
    void deletePerformance_whenPerformanceExists_shouldReturnNoContent() throws Exception {
        doNothing().when(performanceService).deletePerformance(1);

        mockMvc.perform(delete("/api/performances/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void deletePerformance_whenPerformanceNotExists_shouldReturnNotFound() throws Exception {
        doThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Performance not found"))
                .when(performanceService).deletePerformance(99);

        mockMvc.perform(delete("/api/performances/99"))
                .andExpect(status().isNotFound());
    }
}
