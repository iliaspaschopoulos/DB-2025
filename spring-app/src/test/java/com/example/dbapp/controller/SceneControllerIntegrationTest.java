package com.example.dbapp.controller;

import com.example.dbapp.model.Scene;
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

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class SceneControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private SceneRepository sceneRepository;

    private Scene sampleScene;

    @BeforeEach
    void setUp() {
        sceneRepository.deleteAll();
        sceneRepository.flush();

        sampleScene = new Scene();
        sampleScene.setSceneName("Main Stage"); // Mapped to 'name' column in DB
        sampleScene.setDescription("The largest stage at the festival.");
        sampleScene.setCapacity(5000);
        sampleScene.setEquipmentInfo("Full PA, lighting rig, and backline available.");
    }

    @Test
    void testCreateScene() throws Exception {
        Scene newScene = new Scene();
        newScene.setSceneName("Acoustic Tent");
        newScene.setDescription("An intimate setting for acoustic performances.");
        newScene.setCapacity(200);
        newScene.setEquipmentInfo("Small PA, basic lighting.");

        mockMvc.perform(post("/api/scenes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newScene)))
                .andExpect(status().isOk()) // Or isCreated()
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.sceneName", is("Acoustic Tent"))) // Field name in JSON response
                .andExpect(jsonPath("$.capacity", is(200)));
    }

    @Test
    void testGetSceneById() throws Exception {
        Scene savedScene = sceneRepository.save(sampleScene);

        mockMvc.perform(get("/api/scenes/" + savedScene.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(savedScene.getId())))
                .andExpect(jsonPath("$.sceneName", is(sampleScene.getSceneName())));
    }

    @Test
    void testGetSceneById_NotFound() throws Exception {
        mockMvc.perform(get("/api/scenes/99999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetAllScenes() throws Exception {
        sceneRepository.save(sampleScene);

        mockMvc.perform(get("/api/scenes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].sceneName", is(sampleScene.getSceneName())));
    }

    @Test
    void testUpdateScene() throws Exception {
        Scene savedScene = sceneRepository.save(sampleScene);

        Scene updatedInfo = new Scene();
        updatedInfo.setSceneName("Main Stage - Updated");
        updatedInfo.setDescription(savedScene.getDescription()); // Keep some fields or update
        updatedInfo.setCapacity(5500);
        updatedInfo.setEquipmentInfo(savedScene.getEquipmentInfo());

        mockMvc.perform(put("/api/scenes/" + savedScene.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedInfo)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(savedScene.getId())))
                .andExpect(jsonPath("$.sceneName", is("Main Stage - Updated")))
                .andExpect(jsonPath("$.capacity", is(5500)));
    }

    @Test
    void testUpdateScene_NotFound() throws Exception {
        Scene updatedInfo = new Scene();
        updatedInfo.setSceneName("NonExistent Stage");
        updatedInfo.setCapacity(100);
        // Set other required fields if any
        updatedInfo.setDescription("N/A");
        updatedInfo.setEquipmentInfo("N/A");

        mockMvc.perform(put("/api/scenes/99999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedInfo)))
                .andExpect(status().isNotFound());
    }

    @Test
    void testDeleteScene() throws Exception {
        Scene savedScene = sceneRepository.save(sampleScene);

        mockMvc.perform(delete("/api/scenes/" + savedScene.getId()))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/scenes/" + savedScene.getId()))
                .andExpect(status().isNotFound());
    }
}
