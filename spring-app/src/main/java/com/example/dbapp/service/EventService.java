package com.example.dbapp.service;

import com.example.dbapp.model.Event;
import com.example.dbapp.model.Festival;
import com.example.dbapp.model.Scene;
import com.example.dbapp.repository.EventRepository;
import com.example.dbapp.repository.FestivalRepository;
import com.example.dbapp.repository.SceneRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class EventService {

    private final EventRepository eventRepository;
    private final FestivalRepository festivalRepository;
    private final SceneRepository sceneRepository;

    @Autowired
    public EventService(EventRepository eventRepository,
                        FestivalRepository festivalRepository,
                        SceneRepository sceneRepository) {
        this.eventRepository = eventRepository;
        this.festivalRepository = festivalRepository;
        this.sceneRepository = sceneRepository;
    }

    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }

    public Optional<Event> getEventById(Integer id) {
        return eventRepository.findById(id);
    }

    @Transactional
    public Event saveEvent(Event event) {
        if (event.getEventDate() == null) {
            throw new IllegalArgumentException("Event date is required.");
        }

        // Correctly get Festival ID
        final Integer festivalIdToUse;
        if (event.getFestival() != null && event.getFestival().getFestivalId() != null) {
            festivalIdToUse = event.getFestival().getFestivalId();
        } else {
            festivalIdToUse = null; // Ensure festivalIdToUse is initialized in all paths
        }

        if (festivalIdToUse == null) {
            throw new IllegalArgumentException("Festival ID is required to create an event.");
        }
        Festival festival = festivalRepository.findById(festivalIdToUse)
                .orElseThrow(() -> new RuntimeException("Festival not found with id " + festivalIdToUse + " when creating event."));
        event.setFestival(festival);

        // Correctly get Scene ID
        final Integer sceneIdToUse;
        if (event.getScene() != null && event.getScene().getSceneId() != null) {
            sceneIdToUse = event.getScene().getSceneId();
        } else {
            sceneIdToUse = null; // Ensure sceneIdToUse is initialized in all paths
        }

        if (sceneIdToUse == null) {
            throw new IllegalArgumentException("Scene ID is required to create an event.");
        }
        Scene scene = sceneRepository.findById(sceneIdToUse)
                .orElseThrow(() -> new RuntimeException("Scene not found with id " + sceneIdToUse + " when creating event."));
        event.setScene(scene);

        return eventRepository.save(event);
    }

    @Transactional
    public Event updateEvent(Integer id, Event eventDetails) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Event not found with id " + id));

        if (eventDetails.getEventDate() != null) {
            event.setEventDate(eventDetails.getEventDate());
        }

        // Correctly get Festival ID from eventDetails
        final Integer festivalIdToUpdate;
        if (eventDetails.getFestival() != null && eventDetails.getFestival().getFestivalId() != null) {
            festivalIdToUpdate = eventDetails.getFestival().getFestivalId();
        } else {
            festivalIdToUpdate = null; // Ensure festivalIdToUpdate is initialized
        }

        if (festivalIdToUpdate != null) {
            Festival festival = festivalRepository.findById(festivalIdToUpdate)
                    .orElseThrow(() -> new RuntimeException("Festival not found with id " + festivalIdToUpdate));
            event.setFestival(festival);
        } else if (eventDetails.getFestival() != null && (eventDetails.getFestival().getFestivalId() == null)) {
            throw new IllegalArgumentException("Festival ID must be provided if festival object is present in update.");
        }

        // Correctly get Scene ID from eventDetails
        final Integer sceneIdToUpdate;
        if (eventDetails.getScene() != null && eventDetails.getScene().getSceneId() != null) {
            sceneIdToUpdate = eventDetails.getScene().getSceneId();
        } else {
            sceneIdToUpdate = null; // Ensure sceneIdToUpdate is initialized
        }

        if (sceneIdToUpdate != null) {
            Scene scene = sceneRepository.findById(sceneIdToUpdate)
                    .orElseThrow(() -> new RuntimeException("Scene not found with id " + sceneIdToUpdate));
            event.setScene(scene);
        } else if (eventDetails.getScene() != null && (eventDetails.getScene().getSceneId() == null)) {
            throw new IllegalArgumentException("Scene ID must be provided if scene object is present in update.");
        }

        return eventRepository.save(event);
    }

    @Transactional
    public void deleteEvent(Integer id) {
        if (!eventRepository.existsById(id)) {
            // Event not found, let repository handle idempotent delete.
        }
        eventRepository.deleteById(id);
    }
}
