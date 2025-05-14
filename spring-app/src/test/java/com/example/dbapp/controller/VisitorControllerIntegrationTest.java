package com.example.dbapp.controller;

import com.example.dbapp.model.Visitor;
import com.example.dbapp.repository.VisitorRepository;
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

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class VisitorControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private VisitorRepository visitorRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private Visitor visitor1;
    private Visitor visitor2;

    @BeforeEach
    void setUp() {
        visitorRepository.deleteAll();

        visitor1 = new Visitor();
        visitor1.setFirstName("John");
        visitor1.setLastName("Doe");
        visitor1.setContact("john.doe@example.com");
        visitor1.setAge(30);
        visitor1 = visitorRepository.save(visitor1);

        visitor2 = new Visitor();
        visitor2.setFirstName("Jane");
        visitor2.setLastName("Smith");
        visitor2.setContact("jane.smith@example.com");
        visitor2.setAge(25);
        visitor2 = visitorRepository.save(visitor2);
    }

    @Test
    void getAllVisitors() throws Exception {
        mockMvc.perform(get("/api/visitors"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].firstName", is(visitor1.getFirstName())))
                .andExpect(jsonPath("$[1].firstName", is(visitor2.getFirstName())));
    }

    @Test
    void getVisitorById() throws Exception {
        mockMvc.perform(get("/api/visitors/" + visitor1.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.firstName", is(visitor1.getFirstName())))
                .andExpect(jsonPath("$.lastName", is(visitor1.getLastName())));
    }

    @Test
    void getVisitorById_notFound() throws Exception {
        mockMvc.perform(get("/api/visitors/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void createVisitor() throws Exception {
        Visitor newVisitor = new Visitor();
        newVisitor.setFirstName("Alice");
        newVisitor.setLastName("Wonderland");
        newVisitor.setContact("alice@example.com");
        newVisitor.setAge(22);

        mockMvc.perform(post("/api/visitors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newVisitor)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.firstName", is("Alice")))
                .andExpect(jsonPath("$.age", is(22)));
    }

    @Test
    void updateVisitor() throws Exception {
        Visitor updatedVisitor = new Visitor();
        updatedVisitor.setFirstName("Johnathan");
        updatedVisitor.setLastName(visitor1.getLastName()); // Keep last name
        updatedVisitor.setContact("john.doe.updated@example.com");
        updatedVisitor.setAge(31);

        mockMvc.perform(put("/api/visitors/" + visitor1.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedVisitor)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.firstName", is("Johnathan")))
                .andExpect(jsonPath("$.contact", is("john.doe.updated@example.com")));
    }

    @Test
    void updateVisitor_notFound() throws Exception {
        Visitor updatedVisitor = new Visitor();
        updatedVisitor.setFirstName("NonExistent");

        mockMvc.perform(put("/api/visitors/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedVisitor)))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteVisitor() throws Exception {
        mockMvc.perform(delete("/api/visitors/" + visitor1.getId()))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/visitors/" + visitor1.getId()))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteVisitor_notFound() throws Exception {
        mockMvc.perform(delete("/api/visitors/999"))
                .andExpect(status().isNotFound());
    }
}
