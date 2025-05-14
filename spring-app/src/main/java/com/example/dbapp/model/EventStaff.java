package com.example.dbapp.model;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import jakarta.persistence.Column;

@Entity
@Table(name = "Event_Staff")
public class EventStaff {

    @EmbeddedId
    private EventStaffId id;

    @ManyToOne
    @MapsId("eventId")
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @ManyToOne
    @MapsId("sceneId")
    @JoinColumn(name = "scene_id", nullable = false)
    private Scene scene;

    @ManyToOne
    @MapsId("staffId")
    @JoinColumn(name = "staff_id", nullable = false)
    private Staff staff;

    // The staffCategory field is part of the EmbeddedId
    // CHECK (staff_category IN ('technical', 'security', 'auxiliary')) - DB constraint

    // Constructors
    public EventStaff() {
    }

    public EventStaff(Event event, Scene scene, Staff staff, String staffCategory) {
        this.event = event;
        this.scene = scene;
        this.staff = staff;
        this.id = new EventStaffId(event.getEventId(), scene.getSceneId(), staff.getId(), staffCategory);
    }

    // Getters and Setters
    public EventStaffId getId() {
        return id;
    }

    public void setId(EventStaffId id) {
        this.id = id;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public Scene getScene() {
        return scene;
    }

    public void setScene(Scene scene) {
        this.scene = scene;
    }

    public Staff getStaff() {
        return staff;
    }

    public void setStaff(Staff staff) {
        this.staff = staff;
    }

    // staffCategory is part of the ID, access via getId().getStaffCategory()
}
