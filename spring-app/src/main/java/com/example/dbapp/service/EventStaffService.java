package com.example.dbapp.service;

import com.example.dbapp.model.EventStaff;
import com.example.dbapp.model.EventStaffId;
import com.example.dbapp.repository.EventStaffRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EventStaffService {

    private final EventStaffRepository eventStaffRepository;

    @Autowired
    public EventStaffService(EventStaffRepository eventStaffRepository) {
        this.eventStaffRepository = eventStaffRepository;
    }

    public List<EventStaff> getAllEventStaff() {
        return eventStaffRepository.findAll();
    }

    public Optional<EventStaff> getEventStaffById(EventStaffId id) {
        return eventStaffRepository.findById(id);
    }

    public EventStaff saveEventStaff(EventStaff eventStaff) {
        return eventStaffRepository.save(eventStaff);
    }

    // EventStaff is a join table, updates might not be common beyond adding/removing associations.
    // If specific fields in the join table itself (other than FKs) need updating, add here.
    // For this example, we assume no other updatable fields in EventStaff.
    // public EventStaff updateEventStaff(EventStaffId id, EventStaff eventStaffDetails) {
    //     EventStaff eventStaff = eventStaffRepository.findById(id)
    //             .orElseThrow(() -> new RuntimeException("EventStaff not found with id " + id));
    //     // Update fields if any
    //     return eventStaffRepository.save(eventStaff);
    // }

    public void deleteEventStaff(EventStaffId id) {
        eventStaffRepository.deleteById(id);
    }
}
