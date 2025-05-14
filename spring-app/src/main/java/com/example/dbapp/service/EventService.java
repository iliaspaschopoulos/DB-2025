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

        final Integer festivalIdToUse;
        if (event.getFestival() != null && event.getFestival().getId() != null) {
            festivalIdToUse = event.getFestival().getId();
        } else if (event.getFestivalId() != null) {
            festivalIdToUse = event.getFestivalId();
        } else {
            festivalIdToUse = null;
        }

        if (festivalIdToUse == null) {
            throw new IllegalArgumentException("Festival ID is required to create an event.");
        }
        Festival festival = festivalRepository.findById(festivalIdToUse)
                .orElseThrow(() -> new RuntimeException("Festival not found with id " + festivalIdToUse + " when creating event."));
        event.setFestival(festival);

        final Integer sceneIdToUse;
        if (event.getScene() != null && event.getScene().getId() != null) {
            sceneIdToUse = event.getScene().getId();
        } else if (event.getSceneId() != null) {
            sceneIdToUse = event.getSceneId();
        } else {
            sceneIdToUse = null;
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

        final Integer festivalIdToUse;
        if (eventDetails.getFestival() != null && eventDetails.getFestival().getId() != null) {
            festivalIdToUse = eventDetails.getFestival().getId();
        } else if (eventDetails.getFestivalId() != null) {
            festivalIdToUse = eventDetails.getFestivalId();
        } else {
            festivalIdToUse = null;
        }

        if (festivalIdToUse != null) {
            Festival festival = festivalRepository.findById(festivalIdToUse)
                    .orElseThrow(() -> new RuntimeException("Festival not found with id " + festivalIdToUse));
            event.setFestival(festival);
        } else if (eventDetails.getFestival() != null && eventDetails.getFestivalId() == null) { // If festival object is present but ID is null, and festivalId field is also null
            throw new IllegalArgumentException("Festival ID must be provided if festival object is present in update.");
        }

        final Integer sceneIdToUse;
        if (eventDetails.getScene() != null && eventDetails.getScene().getId() != null) {
            sceneIdToUse = eventDetails.getScene().getId();
        } else if (eventDetails.getSceneId() != null) {
            sceneIdToUse = eventDetails.getSceneId();
        } else {
            sceneIdToUse = null;
        }

        if (sceneIdToUse != null) {
            Scene scene = sceneRepository.findById(sceneIdToUse)
                    .orElseThrow(() -> new RuntimeException("Scene not found with id " + sceneIdToUse));
            event.setScene(scene);
        } else if (eventDetails.getScene() != null && eventDetails.getSceneId() == null) { // If scene object is present but ID is null, and sceneId field is also null
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
