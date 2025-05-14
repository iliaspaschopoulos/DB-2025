package com.example.dbapp.controller;

import com.example.dbapp.model.Festival;
import com.example.dbapp.model.Website;
import com.example.dbapp.repository.FestivalRepository;
import com.example.dbapp.repository.WebsiteRepository;
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

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class WebsiteControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WebsiteRepository websiteRepository;

    @Autowired
    private FestivalRepository festivalRepository;

    private ObjectMapper objectMapper;

    private Festival festival1;
    private Website website1;
    private Website website2;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        websiteRepository.deleteAll();
        festivalRepository.deleteAll();

        festival1 = new Festival();
        // festival1.setName("Website Test Festival");
        festival1.setStartDate(LocalDate.now());
        festival1.setEndDate(LocalDate.now().plusDays(5));
        festival1 = festivalRepository.save(festival1);

        website1 = new Website();
        website1.setUrl("https://www.festivalwebsite.com");
        website1.setFestival(festival1);
        website1.setImageUrl("https://www.festivalwebsite.com/image.jpg");
        website1.setDescription("Main festival website.");
        website1 = websiteRepository.save(website1);

        website2 = new Website();
        website2.setUrl("https://www.anotherfest.org");
        // website2 can be without a festival or with another festival
        website2.setDescription("Another festival's portal.");
        website2 = websiteRepository.save(website2);
    }

    @Test
    void getAllWebsites() throws Exception {
        mockMvc.perform(get("/api/websites"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].url", is(website1.getUrl())))
                .andExpect(jsonPath("$[1].url", is(website2.getUrl())));
    }

    @Test
    void getWebsiteById() throws Exception {
        mockMvc.perform(get("/api/websites/" + website1.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.url", is(website1.getUrl())))
                .andExpect(jsonPath("$.description", is(website1.getDescription())))
                .andExpect(jsonPath("$.festival.id", is(festival1.getId())));
    }

    @Test
    void getWebsiteById_notFound() throws Exception {
        mockMvc.perform(get("/api/websites/9999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void createWebsite() throws Exception {
        Website newSite = new Website();
        newSite.setUrl("https://www.newfest.com");
        newSite.setFestival(festival1); // Link to existing festival
        newSite.setImageUrl("https://www.newfest.com/logo.png");
        newSite.setDescription("Brand new festival site.");

        mockMvc.perform(post("/api/websites")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newSite)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.url", is("https://www.newfest.com")))
                .andExpect(jsonPath("$.description", is("Brand new festival site.")))
                .andExpect(jsonPath("$.festival.id", is(festival1.getId())));
    }

    @Test
    void updateWebsite() throws Exception {
        Website updatedSite = new Website();
        updatedSite.setUrl(website1.getUrl()); // URL might be a business key, often not changed, but testable
        updatedSite.setFestival(website1.getFestival());
        updatedSite.setImageUrl("https://www.festivalwebsite.com/updated_image.jpg");
        updatedSite.setDescription("Main festival website with updated info.");

        mockMvc.perform(put("/api/websites/" + website1.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedSite)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.imageUrl", is("https://www.festivalwebsite.com/updated_image.jpg")))
                .andExpect(jsonPath("$.description", is("Main festival website with updated info.")));
    }

    @Test
    void updateWebsite_setFestivalNull() throws Exception {
        Website siteToUpdate = websiteRepository.findById(website1.getId()).orElseThrow();
        siteToUpdate.setFestival(null); // Test unlinking festival

        mockMvc.perform(put("/api/websites/" + siteToUpdate.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(siteToUpdate)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.festival").doesNotExist());
    }

    @Test
    void updateWebsite_notFound() throws Exception {
        Website updatedSite = new Website();
        updatedSite.setUrl("https://www.nonexistent.com");
        updatedSite.setDescription("Trying to update non-existent site.");

        mockMvc.perform(put("/api/websites/9999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedSite)))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteWebsite() throws Exception {
        mockMvc.perform(delete("/api/websites/" + website1.getId()))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/websites/" + website1.getId()))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteWebsite_notFound() throws Exception {
        mockMvc.perform(delete("/api/websites/9999"))
                .andExpect(status().isNotFound());
    }
}
