package com.example.dbapp.controller;

import com.example.dbapp.model.Scene;
import com.example.dbapp.service.SceneService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/scenes")
public class SceneController {

    private final SceneService sceneService;

    @Autowired
    public SceneController(SceneService sceneService) {
        this.sceneService = sceneService;
    }

    @GetMapping
    public List<Scene> getAllScenes() {
        return sceneService.getAllScenes();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Scene> getSceneById(@PathVariable Integer id) {
        return sceneService.getSceneById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Scene createScene(@RequestBody Scene scene) {
        return sceneService.saveScene(scene);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Scene> updateScene(@PathVariable Integer id, @RequestBody Scene sceneDetails) {
        try {
            Scene updatedScene = sceneService.updateScene(id, sceneDetails);
            return ResponseEntity.ok(updatedScene);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteScene(@PathVariable Integer id) {
        sceneService.deleteScene(id);
        return ResponseEntity.noContent().build();
    }
}
