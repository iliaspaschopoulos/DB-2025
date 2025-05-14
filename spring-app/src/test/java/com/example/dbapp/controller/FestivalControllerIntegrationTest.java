package com.example.dbapp.controller;

import com.example.dbapp.model.Festival;
import com.example.dbapp.model.Location;
import com.example.dbapp.repository.FestivalRepository;
import com.example.dbapp.repository.LocationRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class FestivalControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private FestivalRepository festivalRepository;

    @Autowired
    private LocationRepository locationRepository;

    private Festival sampleFestival;
    private Location savedLocation;

    @BeforeEach
    void setUp() {
        festivalRepository.deleteAll();
        locationRepository.deleteAll(); // Clear locations too for a clean state
        locationRepository.flush();
        festivalRepository.flush();

        Location location = new Location();
        location.setLocationName("Test Location for Festival");
        location.setAddress("123 Festival Ave");
        location.setLatitude(new BigDecimal("10.0"));
        location.setLongitude(new BigDecimal("20.0"));
        location.setCity("Festival City");
        location.setCountry("Festival Land");
        location.setContinent("Festivania");
        savedLocation = locationRepository.save(location);

        sampleFestival = new Festival();
        sampleFestival.setYear(2025);
        sampleFestival.setStartDate(LocalDate.of(2025, 7, 1));
        sampleFestival.setEndDate(LocalDate.of(2025, 7, 3));
        sampleFestival.setLocation(savedLocation);
    }

    @Test
    void testCreateFestival() throws Exception {
        Festival newFestival = new Festival();
        newFestival.setYear(2026);
        newFestival.setStartDate(LocalDate.of(2026, 8, 10));
        newFestival.setEndDate(LocalDate.of(2026, 8, 12));
        newFestival.setLocation(savedLocation); // Use the same location for simplicity

        mockMvc.perform(post("/api/festivals")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newFestival)))
                .andExpect(status().isOk()) // Or isCreated()
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.year", is(2026)))
                .andExpect(jsonPath("$.location.id", is(savedLocation.getId().intValue())));
    }

    @Test
    void testGetFestivalById() throws Exception {
        Festival savedFestival = festivalRepository.save(sampleFestival);

        mockMvc.perform(get("/api/festivals/" + savedFestival.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(savedFestival.getId())))
                .andExpect(jsonPath("$.year", is(sampleFestival.getYear())))
                .andExpect(jsonPath("$.location.id", is(savedLocation.getId().intValue())));
    }

    @Test
    void testGetFestivalById_NotFound() throws Exception {
        mockMvc.perform(get("/api/festivals/99999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetAllFestivals() throws Exception {
        festivalRepository.save(sampleFestival);

        mockMvc.perform(get("/api/festivals"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].year", is(sampleFestival.getYear())));
    }

    @Test
    void testUpdateFestival() throws Exception {
        Festival savedFestival = festivalRepository.save(sampleFestival);

        Festival updatedInfo = new Festival();
        updatedInfo.setYear(2027);
        updatedInfo.setStartDate(LocalDate.of(2027, 9, 1));
        updatedInfo.setEndDate(LocalDate.of(2027, 9, 3));
        updatedInfo.setLocation(savedLocation);

        mockMvc.perform(put("/api/festivals/" + savedFestival.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedInfo)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(savedFestival.getId())))
                .andExpect(jsonPath("$.year", is(2027)));
    }

    @Test
    void testUpdateFestival_NotFound() throws Exception {
        Festival updatedInfo = new Festival();
        updatedInfo.setYear(2028);
        updatedInfo.setStartDate(LocalDate.now());
        updatedInfo.setEndDate(LocalDate.now().plusDays(2));
        updatedInfo.setLocation(savedLocation); // Needs a location to be a valid Festival object for request body

        mockMvc.perform(put("/api/festivals/99999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedInfo)))
                .andExpect(status().isNotFound());
    }

    @Test
    void testDeleteFestival() throws Exception {
        Festival savedFestival = festivalRepository.save(sampleFestival);

        mockMvc.perform(delete("/api/festivals/" + savedFestival.getId()))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/festivals/" + savedFestival.getId()))
                .andExpect(status().isNotFound());
    }
}
