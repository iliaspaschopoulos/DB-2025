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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class RatingControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private RatingRepository ratingRepository;

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private PerformanceRepository performanceRepository;

    @Autowired
    private VisitorRepository visitorRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private ArtistRepository artistRepository;

    @Autowired
    private SceneRepository sceneRepository;

    @Autowired
    private FestivalRepository festivalRepository;

    private ObjectMapper objectMapper;

    private Ticket ticket1;
    private Performance performance1;
    private Visitor visitor1;
    private Rating rating1;
    private Rating rating2;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        ratingRepository.deleteAll();
        ticketRepository.deleteAll();
        performanceRepository.deleteAll();
        visitorRepository.deleteAll();
        eventRepository.deleteAll();
        artistRepository.deleteAll();
        sceneRepository.deleteAll();
        festivalRepository.deleteAll();

        Festival festival = new Festival();
        festival.setFestivalName("Rating Fest"); // Corrected: Was setName, now setFestivalName
        festival.setStartDate(LocalDate.now());
        festival.setEndDate(LocalDate.now().plusDays(2));
        festival = festivalRepository.save(festival);

        Scene scene = new Scene();
        scene.setSceneName("Rating Stage");
        scene.setCapacity(1000);
        scene.setFestival(festival);
        scene = sceneRepository.save(scene);

        Event event = new Event();
        event.setEventName("Rating Event"); // Corrected: Was setName, now setEventName
        event.setEventDate(LocalDate.now().plusDays(1));
        event.setScene(scene);
        event = eventRepository.save(event);

        visitor1 = new Visitor();
        visitor1.setFirstName("Critical");
        visitor1.setLastName("Reviewer");
        visitor1.setAge(33);
        visitor1 = visitorRepository.save(visitor1);

        Artist artist = new Artist();
        artist.setName("Rated Artist");
        artist.setDateOfBirth(LocalDate.of(1990,1,1));
        artist = artistRepository.save(artist);

        performance1 = new Performance();
        performance1.setEvent(event);
        performance1.setArtist(artist);
        performance1.setPerformanceType("headline");
        performance1.setStartTime(LocalTime.of(20,0));
        performance1.setDuration(LocalTime.of(1,30));
        performance1 = performanceRepository.save(performance1);

        ticket1 = new Ticket();
        ticket1.setEvent(event);
        ticket1.setVisitor(visitor1);
        ticket1.setPurchaseDate(LocalDate.now());
        ticket1.setCost(new BigDecimal("50.00"));
        ticket1 = ticketRepository.save(ticket1);

        rating1 = new Rating();
        rating1.setTicket(ticket1);
        rating1.setPerformance(performance1);
        rating1.setVisitor(visitor1);
        rating1.setOverallScore(5);
        rating1.setInterpretationScore(4);
        rating1.setSoundLightingScore(5);
        rating1.setStagePresenceScore(5);
        rating1.setOrganizationScore(3);
        rating1.setRatingDate(LocalDateTime.now());
        rating1 = ratingRepository.save(rating1);

        // Create a second rating for variety in getAll test
        Visitor visitor2 = new Visitor();
        visitor2.setFirstName("Casual");
        visitor2.setLastName("Listener");
        visitor2.setAge(22);
        visitor2 = visitorRepository.save(visitor2);

        Ticket ticket2 = new Ticket();
        ticket2.setEvent(event);
        ticket2.setVisitor(visitor2);
        ticket2.setPurchaseDate(LocalDate.now());
        ticket2.setCost(new BigDecimal("50.00"));
        ticket2 = ticketRepository.save(ticket2);

        rating2 = new Rating();
        rating2.setTicket(ticket2);
        rating2.setPerformance(performance1);
        rating2.setVisitor(visitor2);
        rating2.setOverallScore(4);
        rating2.setRatingDate(LocalDateTime.now().minusHours(1));
        rating2 = ratingRepository.save(rating2);
    }

    @Test
    void getAllRatings() throws Exception {
        mockMvc.perform(get("/api/ratings"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].overallScore", is(rating1.getOverallScore())))
                .andExpect(jsonPath("$[1].overallScore", is(rating2.getOverallScore())));
    }

    @Test
    void getRatingById() throws Exception {
        mockMvc.perform(get("/api/ratings/" + rating1.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.overallScore", is(rating1.getOverallScore())))
                .andExpect(jsonPath("$.visitor.firstName", is(visitor1.getFirstName())))
                .andExpect(jsonPath("$.performance.id", is(performance1.getId())));
    }

    @Test
    void getRatingById_notFound() throws Exception {
        mockMvc.perform(get("/api/ratings/9999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void createRating() throws Exception {
        Rating newRating = new Rating();
        newRating.setTicket(ticket1); // Re-use existing ticket for simplicity
        newRating.setPerformance(performance1); // Re-use existing performance
        newRating.setVisitor(visitor1); // Re-use existing visitor
        newRating.setOverallScore(3);
        newRating.setInterpretationScore(3);
        newRating.setSoundLightingScore(4);
        newRating.setStagePresenceScore(2);
        newRating.setOrganizationScore(3);
        newRating.setRatingDate(LocalDateTime.now());

        // Need to ensure this combination of ticket, performance, visitor is unique if there are constraints
        // For this test, we assume it's a new rating context or the old one is deleted by @Transactional

        mockMvc.perform(post("/api/ratings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newRating)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.overallScore", is(3)))
                .andExpect(jsonPath("$.visitor.id", is(visitor1.getId())));
    }

    @Test
    void updateRating() throws Exception {
        Rating updatedRating = new Rating();
        // Essential to set the ID for an update
        updatedRating.setId(rating1.getId()); 
        // Link existing entities
        updatedRating.setTicket(rating1.getTicket());
        updatedRating.setPerformance(rating1.getPerformance());
        updatedRating.setVisitor(rating1.getVisitor());
        // Update scores
        updatedRating.setOverallScore(4); // Changed from 5 to 4
        updatedRating.setInterpretationScore(5);
        updatedRating.setSoundLightingScore(rating1.getSoundLightingScore());
        updatedRating.setStagePresenceScore(rating1.getStagePresenceScore());
        updatedRating.setOrganizationScore(rating1.getOrganizationScore());
        updatedRating.setRatingDate(rating1.getRatingDate()); // Usually rating date doesn't change

        mockMvc.perform(put("/api/ratings/" + rating1.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedRating)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.overallScore", is(4)))
                .andExpect(jsonPath("$.interpretationScore", is(5)));
    }

    @Test
    void updateRating_notFound() throws Exception {
        Rating updatedRating = new Rating();
        updatedRating.setOverallScore(1);

        mockMvc.perform(put("/api/ratings/9999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedRating)))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteRating() throws Exception {
        mockMvc.perform(delete("/api/ratings/" + rating1.getId()))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/ratings/" + rating1.getId()))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteRating_notFound() throws Exception {
        mockMvc.perform(delete("/api/ratings/9999"))
                .andExpect(status().isNotFound());
    }
}
