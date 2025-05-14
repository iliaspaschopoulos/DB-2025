package com.example.dbapp.controller;

import com.example.dbapp.model.*;
import com.example.dbapp.repository.*;
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
import java.time.LocalTime;
import java.math.BigDecimal;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class PerformanceControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PerformanceRepository performanceRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private ArtistRepository artistRepository;

    @Autowired
    private BandRepository bandRepository;

    @Autowired
    private SceneRepository sceneRepository;

    @Autowired
    private FestivalRepository festivalRepository;

    @Autowired
    private LocationRepository locationRepository;

    private ObjectMapper objectMapper;

    private Event event1;
    private Artist artist1;
    private Band band1;
    private Performance performance1;
    private Performance performance2;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        performanceRepository.deleteAll();
        eventRepository.deleteAll();
        artistRepository.deleteAll();
        bandRepository.deleteAll();
        sceneRepository.deleteAll();
        festivalRepository.deleteAll();
        locationRepository.deleteAll();

        Location location = new Location();
        location.setAddress("123 Test Street");
        location.setCity("Testville");
        location.setCountry("Testland");
        location.setContinent("Testcontinent");
        location.setLatitude(new BigDecimal("0.0"));
        location.setLongitude(new BigDecimal("0.0"));
        Location savedLocation = locationRepository.save(location);

        Festival festival = new Festival();
        festival.setFestivalName("Test Fest");
        festival.setStartDate(LocalDate.now());
        festival.setEndDate(LocalDate.now().plusDays(2));
        festival.setYear(LocalDate.now().getYear());
        festival.setLocation(savedLocation);
        festival = festivalRepository.save(festival);

        Scene scene = new Scene();
        scene.setSceneName("Main Stage");
        scene.setCapacity(10000);
        scene.setFestival(festival);
        scene = sceneRepository.save(scene);

        event1 = new Event();
        event1.setEventName("Event 1");
        event1.setEventDate(LocalDate.now().plusDays(1));
        event1.setScene(scene);
        event1.setFestival(festival); // Set the festival for the event
        event1 = eventRepository.save(event1);

        artist1 = new Artist();
        artist1.setName("Solo Artist");
        artist1.setDateOfBirth(LocalDate.of(1980, 1, 1));
        artist1 = artistRepository.save(artist1);

        band1 = new Band();
        band1.setBandName("The Testers");
        band1.setFormationDate(LocalDate.of(2000, 1, 1));
        band1 = bandRepository.save(band1);

        performance1 = new Performance();
        performance1.setEvent(event1);
        performance1.setArtist(artist1);
        performance1.setPerformanceType("headline");
        performance1.setStartTime(LocalTime.of(20, 0));
        performance1.setDuration(LocalTime.of(1, 30));
        performance1 = performanceRepository.save(performance1);

        performance2 = new Performance();
        performance2.setEvent(event1);
        performance2.setBand(band1);
        performance2.setPerformanceType("warm up");
        performance2.setStartTime(LocalTime.of(18, 0));
        performance2.setDuration(LocalTime.of(1, 0));
        performance2 = performanceRepository.save(performance2);
    }

    @Test
    void getAllPerformances() throws Exception {
        mockMvc.perform(get("/api/performances"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].performanceType", is(performance1.getPerformanceType())))
                .andExpect(jsonPath("$[1].performanceType", is(performance2.getPerformanceType())));
    }

    @Test
    void getPerformanceById() throws Exception {
        mockMvc.perform(get("/api/performances/" + performance1.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.performanceType", is(performance1.getPerformanceType())))
                .andExpect(jsonPath("$.artist.name", is(artist1.getName())))
                .andExpect(jsonPath("$.startTime", is("20:00:00")))
                .andExpect(jsonPath("$.duration", is("01:30:00")));
    }

    @Test
    void getPerformanceById_notFound() throws Exception {
        mockMvc.perform(get("/api/performances/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void createPerformance_withArtist() throws Exception {
        Performance newPerformance = new Performance();
        newPerformance.setEvent(event1);
        newPerformance.setArtist(artist1);
        newPerformance.setPerformanceType("Special guest");
        newPerformance.setStartTime(LocalTime.of(22, 0, 0));
        newPerformance.setDuration(LocalTime.of(0, 45, 0));

        mockMvc.perform(post("/api/performances")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newPerformance)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.performanceType", is("Special guest")))
                .andExpect(jsonPath("$.artist.id", is(artist1.getId())))
                .andExpect(jsonPath("$.band", is((Object) null)))
                .andExpect(jsonPath("$.startTime", is("22:00:00")))
                .andExpect(jsonPath("$.duration", is("00:45:00")));
    }

    @Test
    void createPerformance_withBand() throws Exception {
        Performance newPerformance = new Performance();
        newPerformance.setEvent(event1);
        newPerformance.setBand(band1);
        newPerformance.setPerformanceType("headline");
        newPerformance.setStartTime(LocalTime.of(21, 0, 0));
        newPerformance.setDuration(LocalTime.of(1, 15, 0));

        mockMvc.perform(post("/api/performances")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newPerformance)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.performanceType", is("headline")))
                .andExpect(jsonPath("$.band.id", is(band1.getId())))
                .andExpect(jsonPath("$.artist", is((Object) null)));
    }

    @Test
    void updatePerformance() throws Exception {
        Performance updatedPerformance = new Performance();
        updatedPerformance.setEvent(performance1.getEvent());
        updatedPerformance.setArtist(performance1.getArtist());
        updatedPerformance.setPerformanceType("encore");
        updatedPerformance.setStartTime(LocalTime.of(21, 30));
        updatedPerformance.setDuration(LocalTime.of(0, 30));

        mockMvc.perform(put("/api/performances/" + performance1.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedPerformance)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.performanceType", is("encore")))
                .andExpect(jsonPath("$.startTime", is("21:30:00")))
                .andExpect(jsonPath("$.duration", is("00:30:00")));
    }

    @Test
    void updatePerformance_notFound() throws Exception {
        Performance updatedPerformance = new Performance();
        updatedPerformance.setPerformanceType("Non Existent");

        mockMvc.perform(put("/api/performances/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedPerformance)))
                .andExpect(status().isNotFound());
    }

    @Test
    void deletePerformance() throws Exception {
        mockMvc.perform(delete("/api/performances/" + performance1.getId()))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/performances/" + performance1.getId()))
                .andExpect(status().isNotFound());
    }

    @Test
    void deletePerformance_notFound() throws Exception {
        mockMvc.perform(delete("/api/performances/999"))
                .andExpect(status().isNotFound());
    }
}
