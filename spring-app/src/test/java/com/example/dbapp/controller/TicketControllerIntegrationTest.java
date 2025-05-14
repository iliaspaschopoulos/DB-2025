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

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class TicketControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private VisitorRepository visitorRepository;

    @Autowired
    private SceneRepository sceneRepository;

    @Autowired
    private FestivalRepository festivalRepository;

    @Autowired
    private LocationRepository locationRepository;

    private ObjectMapper objectMapper;

    private Event event1;
    private Visitor visitor1;
    private Ticket ticket1;
    private Ticket ticket2;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        ticketRepository.deleteAll();
        eventRepository.deleteAll();
        visitorRepository.deleteAll();
        sceneRepository.deleteAll();
        festivalRepository.deleteAll();
        locationRepository.deleteAll();

        locationRepository.flush();
        festivalRepository.flush();
        sceneRepository.flush();
        visitorRepository.flush();
        eventRepository.flush();
        ticketRepository.flush();

        Location location = new Location();
        location.setLocationName("Ticket Test Location");
        location.setAddress("123 Ticket St");
        location.setLatitude(new BigDecimal("34.0522"));
        location.setLongitude(new BigDecimal("-118.2437"));
        location.setCity("Ticketville");
        location.setCountry("Ticketland");
        location.setContinent("Ticketerra");
        Location savedLocation = locationRepository.saveAndFlush(location);

        Festival festival = new Festival();
        festival.setYear(LocalDate.now().getYear());
        festival.setStartDate(LocalDate.now());
        festival.setEndDate(LocalDate.now().plusDays(2));
        festival.setLocation(savedLocation);
        festival = festivalRepository.saveAndFlush(festival);

        Scene scene = new Scene();
        scene.setSceneName("Ticket Stage");
        scene.setCapacity(5000);
        scene = sceneRepository.saveAndFlush(scene);

        event1 = new Event();
        event1.setEventDate(LocalDate.now().plusDays(1));
        event1.setScene(scene);
        event1 = eventRepository.saveAndFlush(event1);

        visitor1 = new Visitor();
        visitor1.setFirstName("Ticket");
        visitor1.setLastName("Holder");
        visitor1.setAge(28);
        visitor1 = visitorRepository.saveAndFlush(visitor1);

        Visitor visitor2 = new Visitor();
        visitor2.setFirstName("Another");
        visitor2.setLastName("Attendee");
        visitor2.setAge(35);
        visitor2 = visitorRepository.saveAndFlush(visitor2);

        ticket1 = new Ticket();
        ticket1.setEvent(event1);
        ticket1.setVisitor(visitor1);
        ticket1.setPurchaseDate(LocalDate.now());
        ticket1.setCost(new BigDecimal("75.00"));
        ticket1.setPaymentMethod("credit card");
        ticket1.setTicketCategory("General Admission");
        ticket1 = ticketRepository.saveAndFlush(ticket1);

        ticket2 = new Ticket();
        ticket2.setEvent(event1);
        ticket2.setVisitor(visitor2);
        ticket2.setPurchaseDate(LocalDate.now().minusDays(1));
        ticket2.setCost(new BigDecimal("120.00"));
        ticket2.setPaymentMethod("debit card");
        ticket2.setTicketCategory("VIP");
        ticket2 = ticketRepository.saveAndFlush(ticket2);
    }

    @Test
    void getAllTickets() throws Exception {
        mockMvc.perform(get("/api/tickets"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].ticketCategory", is(ticket1.getTicketCategory())))
                .andExpect(jsonPath("$[1].ticketCategory", is(ticket2.getTicketCategory())));
    }

    @Test
    void getTicketById() throws Exception {
        mockMvc.perform(get("/api/tickets/" + ticket1.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.ticketCategory", is(ticket1.getTicketCategory())))
                .andExpect(jsonPath("$.visitor.firstName", is(visitor1.getFirstName())))
                .andExpect(jsonPath("$.cost", is(75.00)));
    }

    @Test
    void getTicketById_notFound() throws Exception {
        mockMvc.perform(get("/api/tickets/9999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void createTicket() throws Exception {
        Visitor newVisitor = new Visitor();
        newVisitor.setFirstName("New");
        newVisitor.setLastName("Buyer");
        newVisitor.setAge(40);
        newVisitor = visitorRepository.save(newVisitor);

        Ticket newTicket = new Ticket();
        newTicket.setEvent(event1);
        newTicket.setVisitor(newVisitor);
        newTicket.setPurchaseDate(LocalDate.now());
        newTicket.setCost(new BigDecimal("85.50"));
        newTicket.setPaymentMethod("credit card");
        newTicket.setTicketCategory("Early Bird");
        newTicket.setUsed(false);

        mockMvc.perform(post("/api/tickets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newTicket)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.ticketCategory", is("Early Bird")))
                .andExpect(jsonPath("$.visitor.id", is(newVisitor.getId())))
                .andExpect(jsonPath("$.cost", is(85.50)));
    }

    @Test
    void updateTicket() throws Exception {
        Ticket updatedTicket = new Ticket();
        updatedTicket.setEvent(ticket1.getEvent());
        updatedTicket.setVisitor(ticket1.getVisitor());
        updatedTicket.setPurchaseDate(ticket1.getPurchaseDate());
        updatedTicket.setCost(new BigDecimal("70.00"));
        updatedTicket.setPaymentMethod(ticket1.getPaymentMethod());
        updatedTicket.setTicketCategory("General Admission - Updated");
        updatedTicket.setUsed(true);

        mockMvc.perform(put("/api/tickets/" + ticket1.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedTicket)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.ticketCategory", is("General Admission - Updated")))
                .andExpect(jsonPath("$.cost", is(70.00)))
                .andExpect(jsonPath("$.used", is(true)));
    }

    @Test
    void updateTicket_notFound() throws Exception {
        Ticket updatedTicket = new Ticket();
        updatedTicket.setTicketCategory("Non Existent Ticket");

        mockMvc.perform(put("/api/tickets/9999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedTicket)))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteTicket() throws Exception {
        mockMvc.perform(delete("/api/tickets/" + ticket1.getId()))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/tickets/" + ticket1.getId()))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteTicket_notFound() throws Exception {
        mockMvc.perform(delete("/api/tickets/9999"))
                .andExpect(status().isNotFound());
    }
}
