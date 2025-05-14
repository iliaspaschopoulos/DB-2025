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
public class EventStaffControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EventStaffRepository eventStaffRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private SceneRepository sceneRepository;

    @Autowired
    private StaffRepository staffRepository;

    @Autowired
    private FestivalRepository festivalRepository;

    @Autowired
    private LocationRepository locationRepository;

    private ObjectMapper objectMapper;

    private Event event1;
    private Scene scene1;
    private Staff staff1, staff2;
    private EventStaff eventStaff1;
    private EventStaff eventStaff2;
    private Festival savedFestival;
    private Event savedEvent;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        eventStaffRepository.deleteAll();
        eventRepository.deleteAll();
        sceneRepository.deleteAll();
        staffRepository.deleteAll();
        festivalRepository.deleteAll();
        locationRepository.deleteAll();

        Location location = new Location();
        location.setLocationName("Test Location for EventStaff");
        location.setAddress("123 Test St");
        location.setCity("Testville");
        location.setCountry("Testland");
        location.setContinent("Testinent");
        location.setLatitude(BigDecimal.valueOf(10.0));
        location.setLongitude(BigDecimal.valueOf(20.0));
        Location savedLocation = locationRepository.save(location);

        Festival festival = new Festival();
        festival.setYear(LocalDate.now().getYear());
        festival.setStartDate(LocalDate.now());
        festival.setEndDate(LocalDate.now().plusDays(3));
        festival.setLocation(savedLocation);
        savedFestival = festivalRepository.save(festival);

        scene1 = new Scene();
        scene1.setSceneName("Staff Area Stage");
        scene1.setCapacity(100);
        scene1 = sceneRepository.save(scene1);

        event1 = new Event();
        event1.setEventDate(LocalDate.now().plusDays(1));
        event1.setFestival(savedFestival);
        event1.setScene(scene1);
        savedEvent = eventRepository.save(event1);

        staff1 = new Staff();
        staff1.setName("Mike Security");
        staff1.setAge(40);
        staff1.setRole("Head of Security");
        staff1.setExperienceLevel("πολύ έμπειρος");
        staff1 = staffRepository.save(staff1);

        staff2 = new Staff();
        staff2.setName("Sarah Tech");
        staff2.setAge(32);
        staff2.setRole("Sound Engineer");
        staff2.setExperienceLevel("έμπειρος");
        staff2 = staffRepository.save(staff2);

        eventStaff1 = new EventStaff(savedEvent, scene1, staff1, "security");
        eventStaff1 = eventStaffRepository.save(eventStaff1);

        eventStaff2 = new EventStaff(savedEvent, scene1, staff2, "technical");
        eventStaff2 = eventStaffRepository.save(eventStaff2);
    }

    @Test
    void getAllEventStaff() throws Exception {
        mockMvc.perform(get("/api/event-staff"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id.staffCategory", is(eventStaff1.getId().getStaffCategory())))
                .andExpect(jsonPath("$[1].id.staffCategory", is(eventStaff2.getId().getStaffCategory())));
    }

    @Test
    void getEventStaffById() throws Exception {
        String eventStaff1IdJson = objectMapper.writeValueAsString(eventStaff1.getId());

        mockMvc.perform(get("/api/event-staff/byId")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(eventStaff1IdJson))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id.staffCategory", is(eventStaff1.getId().getStaffCategory())))
                .andExpect(jsonPath("$.staff.name", is(staff1.getName())))
                .andExpect(jsonPath("$.event.id", is(savedEvent.getId())));
    }

    @Test
    void getEventStaffById_notFound() throws Exception {
        EventStaffId nonExistentId = new EventStaffId(999, 999, 999, "nonexistent");
        String nonExistentIdJson = objectMapper.writeValueAsString(nonExistentId);

        mockMvc.perform(get("/api/event-staff/byId")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(nonExistentIdJson))
                .andExpect(status().isNotFound());
    }

    @Test
    void createEventStaff() throws Exception {
        Staff staff3 = new Staff();
        staff3.setName("Auxiliary Bob");
        staff3.setAge(25);
        staff3.setRole("General Help");
        staff3.setExperienceLevel("αρχάριος");
        staff3 = staffRepository.save(staff3);

        EventStaff newEventStaff = new EventStaff();
        EventStaffId newId = new EventStaffId(savedEvent.getId(), scene1.getId(), staff3.getId(), "auxiliary");
        newEventStaff.setId(newId);
        newEventStaff.setEvent(savedEvent);
        newEventStaff.setScene(scene1);
        newEventStaff.setStaff(staff3);

        mockMvc.perform(post("/api/event-staff")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newEventStaff)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id.staffCategory", is("auxiliary")))
                .andExpect(jsonPath("$.id.staffId", is(staff3.getId())))
                .andExpect(jsonPath("$.event.id", is(savedEvent.getId())));
    }

    @Test
    void deleteEventStaff() throws Exception {
        String eventStaff1IdJson = objectMapper.writeValueAsString(eventStaff1.getId());

        mockMvc.perform(delete("/api/event-staff")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(eventStaff1IdJson))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/event-staff/byId")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(eventStaff1IdJson))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteEventStaff_notFound() throws Exception {
        EventStaffId nonExistentId = new EventStaffId(999, 999, 999, "nonexistent");
        String nonExistentIdJson = objectMapper.writeValueAsString(nonExistentId);

        mockMvc.perform(delete("/api/event-staff")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(nonExistentIdJson))
                .andExpect(status().isNotFound());
    }
}
