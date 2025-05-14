package com.example.dbapp.controller;

import com.example.dbapp.model.Event;
import com.example.dbapp.model.Festival;
import com.example.dbapp.model.Scene;
import com.example.dbapp.repository.EventRepository;
import com.example.dbapp.repository.FestivalRepository;
import com.example.dbapp.repository.LocationRepository;
import com.example.dbapp.repository.SceneRepository;
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
public class EventControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private FestivalRepository festivalRepository;

    @Autowired
    private SceneRepository sceneRepository;

    @Autowired
    private LocationRepository locationRepository; // For Festival's Location

    private Event sampleEvent;
    private Festival savedFestival;
    private Scene savedScene;

    @BeforeEach
    void setUp() {
        eventRepository.deleteAll();
        sceneRepository.deleteAll();
        festivalRepository.deleteAll();
        locationRepository.deleteAll();
        
        locationRepository.flush();
        festivalRepository.flush();
        sceneRepository.flush();
        eventRepository.flush();

        // Create and save a Location for the Festival
        com.example.dbapp.model.Location location = new com.example.dbapp.model.Location();
        location.setLocationName("Event Test Location");
        location.setAddress("123 Event Ave");
        location.setLatitude(new BigDecimal("30.0"));
        location.setLongitude(new BigDecimal("40.0"));
        location.setCity("Event City");
        location.setCountry("Event Country");
        location.setContinent("Event Continent");
        locationRepository.save(location);

        // Create and save a Festival
        Festival festival = new Festival();
        festival.setYear(2025);
        festival.setStartDate(LocalDate.of(2025, 1, 1));
        festival.setEndDate(LocalDate.of(2025, 1, 3));
        festival.setLocation(location);
        savedFestival = festivalRepository.saveAndFlush(festival);

        // Create and save a Scene
        Scene scene = new Scene();
        scene.setSceneName("Event Test Stage");
        scene.setCapacity(1000);
        scene.setDescription("Stage for event testing");
        savedScene = sceneRepository.saveAndFlush(scene);

        sampleEvent = new Event();
        sampleEvent.setFestival(savedFestival);
        sampleEvent.setScene(savedScene);
        sampleEvent.setEventDate(LocalDate.of(2025, 1, 2));
    }

    @Test
    void testCreateEvent() throws Exception {
        // Create a map for the request body
        java.util.Map<String, Object> eventRequest = new java.util.HashMap<>();
        eventRequest.put("festivalId", savedFestival.getId());
        eventRequest.put("sceneId", savedScene.getId());
        eventRequest.put("eventDate", "2025-01-02");

        mockMvc.perform(post("/api/events")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(eventRequest))) // Use the map here
                .andDo(result -> { // Add this to print response on failure
                    if (result.getResponse().getStatus() != org.springframework.http.HttpStatus.CREATED.value()) {
                        System.err.println("testCreateEvent failed with status: " + result.getResponse().getStatus());
                        System.err.println("Response body for testCreateEvent: " + result.getResponse().getContentAsString());
                    }
                })
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.eventDate", is("2025-01-02")))
                .andExpect(jsonPath("$.festival", is(savedFestival.getId().intValue())))
                .andExpect(jsonPath("$.scene", is(savedScene.getId().intValue())));
    }

    @Test
    void testGetEventById() throws Exception {
        Event savedEvent = eventRepository.save(sampleEvent);

        mockMvc.perform(get("/api/events/" + savedEvent.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(savedEvent.getId())))
                .andExpect(jsonPath("$.eventDate", is(sampleEvent.getEventDate().toString())))
                .andExpect(jsonPath("$.festival", is(savedFestival.getId().intValue())))
                .andExpect(jsonPath("$.scene", is(savedScene.getId().intValue())));
    }

    @Test
    void testGetEventById_NotFound() throws Exception {
        mockMvc.perform(get("/api/events/99999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetAllEvents() throws Exception {
        eventRepository.save(sampleEvent);

        mockMvc.perform(get("/api/events"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(sampleEvent.getId()))) // Assuming ID is populated after save
                .andExpect(jsonPath("$[0].eventDate", is(sampleEvent.getEventDate().toString()))); // Added date assertion for completeness
    }

    @Test
    void testUpdateEvent() throws Exception {
        Event savedEvent = eventRepository.save(sampleEvent);

        // Create a map for the request body
        java.util.Map<String, Object> eventUpdateRequest = new java.util.HashMap<>();
        eventUpdateRequest.put("festivalId", savedFestival.getId());
        eventUpdateRequest.put("sceneId", savedScene.getId());
        eventUpdateRequest.put("eventDate", "2025-01-03"); // Changed date

        mockMvc.perform(put("/api/events/" + savedEvent.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(eventUpdateRequest))) // Use the map here
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(savedEvent.getId())))
                .andExpect(jsonPath("$.eventDate", is("2025-01-03")));
    }

    @Test
    void testUpdateEvent_NotFound() throws Exception {
        // Create a map for the request body
        java.util.Map<String, Object> eventUpdateRequest = new java.util.HashMap<>();
        eventUpdateRequest.put("festivalId", savedFestival.getId());
        eventUpdateRequest.put("sceneId", savedScene.getId());
        eventUpdateRequest.put("eventDate", "2025-01-04");

        mockMvc.perform(put("/api/events/99999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(eventUpdateRequest))) // Use the map here
                .andExpect(status().isNotFound());
    }

    @Test
    void testDeleteEvent() throws Exception {
        Event savedEvent = eventRepository.save(sampleEvent);

        mockMvc.perform(delete("/api/events/" + savedEvent.getId()))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/events/" + savedEvent.getId()))
                .andExpect(status().isNotFound());
    }
}
