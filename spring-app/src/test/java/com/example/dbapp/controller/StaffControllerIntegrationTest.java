package com.example.dbapp.controller;

import com.example.dbapp.model.Staff;
import com.example.dbapp.repository.StaffRepository;
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
public class StaffControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private StaffRepository staffRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private Staff staff1;
    private Staff staff2;

    @BeforeEach
    void setUp() {
        staffRepository.deleteAll();

        staff1 = new Staff();
        staff1.setName("John Staff");
        staff1.setAge(35);
        staff1.setRole("Security");
        staff1.setExperienceLevel("έμπειρος");
        staff1 = staffRepository.save(staff1);

        staff2 = new Staff();
        staff2.setName("Jane Crew");
        staff2.setAge(28);
        staff2.setRole("Technician");
        staff2.setExperienceLevel("μέσος");
        staff2 = staffRepository.save(staff2);
    }

    @Test
    void getAllStaff() throws Exception {
        mockMvc.perform(get("/api/staff"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name", is(staff1.getName())))
                .andExpect(jsonPath("$[1].name", is(staff2.getName())));
    }

    @Test
    void getStaffById() throws Exception {
        mockMvc.perform(get("/api/staff/" + staff1.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name", is(staff1.getName())))
                .andExpect(jsonPath("$.role", is(staff1.getRole())));
    }

    @Test
    void getStaffById_notFound() throws Exception {
        mockMvc.perform(get("/api/staff/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void createStaff() throws Exception {
        Staff newStaff = new Staff();
        newStaff.setName("Alice Worker");
        newStaff.setAge(22);
        newStaff.setRole("Volunteer");
        newStaff.setExperienceLevel("αρχάριος");

        mockMvc.perform(post("/api/staff")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newStaff)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name", is("Alice Worker")))
                .andExpect(jsonPath("$.experienceLevel", is("αρχάριος")));
    }

    @Test
    void updateStaff() throws Exception {
        Staff updatedStaff = new Staff();
        updatedStaff.setName("Johnathan Staff Member");
        updatedStaff.setAge(staff1.getAge()); // Keep age
        updatedStaff.setRole("Senior Security");
        updatedStaff.setExperienceLevel("πολύ έμπειρος");

        mockMvc.perform(put("/api/staff/" + staff1.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedStaff)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name", is("Johnathan Staff Member")))
                .andExpect(jsonPath("$.role", is("Senior Security")));
    }

    @Test
    void updateStaff_notFound() throws Exception {
        Staff updatedStaff = new Staff();
        updatedStaff.setName("NonExistent Staff");

        mockMvc.perform(put("/api/staff/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedStaff)))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteStaff() throws Exception {
        mockMvc.perform(delete("/api/staff/" + staff1.getId()))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/staff/" + staff1.getId()))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteStaff_notFound() throws Exception {
        mockMvc.perform(delete("/api/staff/999"))
                .andExpect(status().isNotFound());
    }
}
