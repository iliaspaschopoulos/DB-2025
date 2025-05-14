package com.example.dbapp.service;

import com.example.dbapp.model.Visitor;
import com.example.dbapp.repository.VisitorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class VisitorService {

    private final VisitorRepository visitorRepository;

    @Autowired
    public VisitorService(VisitorRepository visitorRepository) {
        this.visitorRepository = visitorRepository;
    }

    public List<Visitor> getAllVisitors() {
        return visitorRepository.findAll();
    }

    public Optional<Visitor> getVisitorById(Integer id) {
        return visitorRepository.findById(id);
    }

    public Visitor saveVisitor(Visitor visitor) {
        return visitorRepository.save(visitor);
    }

    public Visitor updateVisitor(Integer id, Visitor visitorDetails) {
        Visitor visitor = visitorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Visitor not found with id " + id));

        visitor.setFirstName(visitorDetails.getFirstName());
        visitor.setLastName(visitorDetails.getLastName());
        visitor.setContact(visitorDetails.getContact()); // Corrected from getEmail/getPhoneNumber
        visitor.setAge(visitorDetails.getAge());         // Corrected from getDateOfBirth
        // visitor.setTickets(visitorDetails.getTickets()); // Managed separately
        // visitor.setRatings(visitorDetails.getRatings()); // Managed separately
        // visitor.setResaleQueues(visitorDetails.getResaleQueues()); // Managed separately
        return visitorRepository.save(visitor);
    }

    public void deleteVisitor(Integer id) {
        visitorRepository.deleteById(id);
    }
}
