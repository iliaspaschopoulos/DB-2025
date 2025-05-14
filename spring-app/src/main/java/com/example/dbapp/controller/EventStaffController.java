package com.example.dbapp.controller;

import com.example.dbapp.model.EventStaff;
import com.example.dbapp.model.EventStaffId;
import com.example.dbapp.service.EventStaffService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/eventstaff")
public class EventStaffController {

    private final EventStaffService eventStaffService;

    @Autowired
    public EventStaffController(EventStaffService eventStaffService) {
        this.eventStaffService = eventStaffService;
    }

    @GetMapping
    public List<EventStaff> getAllEventStaff() {
        return eventStaffService.getAllEventStaff();
    }

    @GetMapping("/find") // Example: /api/eventstaff/find?eventId=1&sceneId=1&staffId=1&staffCategory=Sound
    public ResponseEntity<EventStaff> getEventStaffById(@RequestParam Integer eventId, @RequestParam Integer sceneId, @RequestParam Integer staffId, @RequestParam String staffCategory) {
        EventStaffId id = new EventStaffId(eventId, sceneId, staffId, staffCategory);
        return eventStaffService.getEventStaffById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public EventStaff createEventStaff(@RequestBody EventStaff eventStaff) {
        return eventStaffService.saveEventStaff(eventStaff);
    }

    // Update for EventStaff might not be typical as it's a link table.
    // If there were other attributes on the EventStaff entity itself, an update method would be relevant.

    @DeleteMapping("/delete") // Example: /api/eventstaff/delete?eventId=1&sceneId=1&staffId=1&staffCategory=Sound
    public ResponseEntity<Void> deleteEventStaff(@RequestParam Integer eventId, @RequestParam Integer sceneId, @RequestParam Integer staffId, @RequestParam String staffCategory) {
        EventStaffId id = new EventStaffId(eventId, sceneId, staffId, staffCategory);
        eventStaffService.deleteEventStaff(id);
        return ResponseEntity.noContent().build();
    }
}
