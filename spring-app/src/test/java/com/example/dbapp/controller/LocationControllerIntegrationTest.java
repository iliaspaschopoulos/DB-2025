package com.example.dbapp.controller;

import com.example.dbapp.model.Location;
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
import java.util.Collections;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional // Rollback database changes after each test
public class LocationControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private LocationRepository locationRepository;

    private Location sampleLocation;

    @BeforeEach
    void setUp() {
        locationRepository.deleteAll();
        locationRepository.flush();

        sampleLocation = new Location();
        sampleLocation.setLocationName("Test Location");
        sampleLocation.setAddress("123 Test Street");
        sampleLocation.setLatitude(new BigDecimal("40.7128"));
        sampleLocation.setLongitude(new BigDecimal("-74.0060"));
        sampleLocation.setCity("Test City");
        sampleLocation.setCountry("Test Country");
        sampleLocation.setContinent("Test Continent");
    }

    @Test
    void testCreateLocation() throws Exception {
        Location newLocation = new Location();
        newLocation.setLocationName("New Location");
        newLocation.setAddress("456 New Avenue");
        newLocation.setLatitude(new BigDecimal("34.0522"));
        newLocation.setLongitude(new BigDecimal("-118.2437"));
        newLocation.setCity("New City");
        newLocation.setCountry("New Country");
        newLocation.setContinent("New Continent");

        mockMvc.perform(post("/api/locations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newLocation)))
                .andExpect(status().isOk()) // Assuming 200 OK for creation, adjust if it's 201 Created
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.locationName", is("New Location")))
                .andExpect(jsonPath("$.address", is("456 New Avenue")));
    }

    @Test
    void testGetLocationById() throws Exception {
        Location savedLocation = locationRepository.save(sampleLocation);

        mockMvc.perform(get("/api/locations/" + savedLocation.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(savedLocation.getId())))
                .andExpect(jsonPath("$.locationName", is(sampleLocation.getLocationName())));
    }

    @Test
    void testGetLocationById_NotFound() throws Exception {
        mockMvc.perform(get("/api/locations/99999")) // Assuming 99999 is an ID that won't exist
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetAllLocations() throws Exception {
        locationRepository.save(sampleLocation);

        mockMvc.perform(get("/api/locations"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].locationName", is(sampleLocation.getLocationName())));
    }

    @Test
    void testUpdateLocation() throws Exception {
        Location savedLocation = locationRepository.save(sampleLocation);

        Location updatedInfo = new Location();
        updatedInfo.setLocationName("Updated Location Name");
        updatedInfo.setAddress("789 Updated Road");
        updatedInfo.setLatitude(savedLocation.getLatitude()); // Keep some fields same or update as needed
        updatedInfo.setLongitude(savedLocation.getLongitude());
        updatedInfo.setCity(savedLocation.getCity());
        updatedInfo.setCountry(savedLocation.getCountry());
        updatedInfo.setContinent(savedLocation.getContinent());


        mockMvc.perform(put("/api/locations/" + savedLocation.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedInfo)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(savedLocation.getId())))
                .andExpect(jsonPath("$.locationName", is("Updated Location Name")))
                .andExpect(jsonPath("$.address", is("789 Updated Road")));
    }

    @Test
    void testUpdateLocation_NotFound() throws Exception {
        Location updatedInfo = new Location();
        updatedInfo.setLocationName("Updated Location Name");
        updatedInfo.setAddress("789 Updated Road");
        // Set other required fields if any for a valid Location object to be sent
        updatedInfo.setLatitude(new BigDecimal("0"));
        updatedInfo.setLongitude(new BigDecimal("0"));
        updatedInfo.setCity("City");
        updatedInfo.setCountry("Country");
        updatedInfo.setContinent("Continent");


        mockMvc.perform(put("/api/locations/99999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedInfo)))
                .andExpect(status().isNotFound());
    }

    @Test
    void testDeleteLocation() throws Exception {
        Location savedLocation = locationRepository.save(sampleLocation);

        mockMvc.perform(delete("/api/locations/" + savedLocation.getId()))
                .andExpect(status().isNoContent()); // Assuming 204 No Content for successful deletion

        mockMvc.perform(get("/api/locations/" + savedLocation.getId()))
                .andExpect(status().isNotFound());
    }
}
