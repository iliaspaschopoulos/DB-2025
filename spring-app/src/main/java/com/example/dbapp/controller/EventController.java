package com.example.dbapp.controller;

import com.example.dbapp.model.Event;
import com.example.dbapp.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/events")
public class EventController {

    private final EventService eventService;

    @Autowired
    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @GetMapping
    public List<Event> getAllEvents() {
        return eventService.getAllEvents();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Event> getEventById(@PathVariable Integer id) {
        return eventService.getEventById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Event> createEvent(@RequestBody Event event) {
        try {
            System.err.println("EventController.createEvent received event. Festival: " +
                (event.getFestival() == null ? "null" : "ID=" + event.getFestival().getFestivalId()) +
                ", Scene: " +
                (event.getScene() == null ? "null" : "ID=" + event.getScene().getSceneId()) +
                ", EventDate: " + event.getEventDate());

            Event createdEvent = eventService.saveEvent(event);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdEvent);
        } catch (IllegalArgumentException e) { // Catch specific exception first
            System.err.println("Error in createEvent (IllegalArgumentException): " + e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (RuntimeException e) {
            System.err.println("Error in createEvent (RuntimeException): " + e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Event> updateEvent(@PathVariable Integer id, @RequestBody Event eventDetails) {
        try {
            System.err.println("EventController.updateEvent for ID " + id + " received eventDetails. Festival: " +
                (eventDetails.getFestival() == null ? "null" : (eventDetails.getFestival().getFestivalId() == null ? "ID is null" : "ID=" + eventDetails.getFestival().getFestivalId())) +
                ", Scene: " +
                (eventDetails.getScene() == null ? "null" : (eventDetails.getScene().getSceneId() == null ? "ID is null" : "ID=" + eventDetails.getScene().getSceneId())) +
                ", EventDate: " + eventDetails.getEventDate());

            Event updatedEvent = eventService.updateEvent(id, eventDetails);
            return ResponseEntity.ok(updatedEvent);
        } catch (IllegalArgumentException e) { // Catch specific exception first
            System.err.println("Error in updateEvent for ID " + id + " (IllegalArgumentException): " + e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (RuntimeException e) {
            System.err.println("Error in updateEvent for ID " + id + " (RuntimeException): " + e.getMessage());
            if (e.getMessage() != null && e.getMessage().toLowerCase().contains("not found")) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEvent(@PathVariable Integer id) {
        try {
            eventService.deleteEvent(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            if (e.getMessage() != null && e.getMessage().contains("not found")) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
