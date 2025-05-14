package com.example.dbapp.model;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class EventStaffId implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer eventId;
    private Integer sceneId;
    private Integer staffId;
    private String staffCategory; // VARCHAR(50)

    public EventStaffId() {
    }

    public EventStaffId(Integer eventId, Integer sceneId, Integer staffId, String staffCategory) {
        this.eventId = eventId;
        this.sceneId = sceneId;
        this.staffId = staffId;
        this.staffCategory = staffCategory;
    }

    // Getters, Setters, equals, and hashCode
    public Integer getEventId() {
        return eventId;
    }

    public void setEventId(Integer eventId) {
        this.eventId = eventId;
    }

    public Integer getSceneId() {
        return sceneId;
    }

    public void setSceneId(Integer sceneId) {
        this.sceneId = sceneId;
    }

    public Integer getStaffId() {
        return staffId;
    }

    public void setStaffId(Integer staffId) {
        this.staffId = staffId;
    }

    public String getStaffCategory() {
        return staffCategory;
    }

    public void setStaffCategory(String staffCategory) {
        this.staffCategory = staffCategory;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EventStaffId that = (EventStaffId) o;
        return Objects.equals(eventId, that.eventId) &&
               Objects.equals(sceneId, that.sceneId) &&
               Objects.equals(staffId, that.staffId) &&
               Objects.equals(staffCategory, that.staffCategory);
    }

    @Override
    public int hashCode() {
        return Objects.hash(eventId, sceneId, staffId, staffCategory);
    }
}
