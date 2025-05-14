package com.example.dbapp.service;

import com.example.dbapp.model.Scene;
import com.example.dbapp.repository.SceneRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SceneService {

    private final SceneRepository sceneRepository;

    @Autowired
    public SceneService(SceneRepository sceneRepository) {
        this.sceneRepository = sceneRepository;
    }

    public List<Scene> getAllScenes() {
        return sceneRepository.findAll();
    }

    public Optional<Scene> getSceneById(Integer id) {
        return sceneRepository.findById(id);
    }

    public Scene saveScene(Scene scene) {
        return sceneRepository.save(scene);
    }

    public Scene updateScene(Integer id, Scene sceneDetails) {
        Scene scene = sceneRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Scene not found with id " + id));

        scene.setSceneName(sceneDetails.getSceneName());
        scene.setCapacity(sceneDetails.getCapacity());
        // scene.setEvents(sceneDetails.getEvents()); // Managed separately
        return sceneRepository.save(scene);
    }

    public void deleteScene(Integer id) {
        sceneRepository.deleteById(id);
    }
}
