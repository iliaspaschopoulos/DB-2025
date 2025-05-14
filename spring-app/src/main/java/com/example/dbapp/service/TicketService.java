package com.example.dbapp.service;

import com.example.dbapp.model.Ticket;
import com.example.dbapp.repository.TicketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TicketService {

    private final TicketRepository ticketRepository;

    @Autowired
    public TicketService(TicketRepository ticketRepository) {
        this.ticketRepository = ticketRepository;
    }

    public List<Ticket> getAllTickets() {
        return ticketRepository.findAll();
    }

    public Optional<Ticket> getTicketById(Integer id) {
        return ticketRepository.findById(id);
    }

    public Ticket saveTicket(Ticket ticket) {
        return ticketRepository.save(ticket);
    }

    public Ticket updateTicket(Integer id, Ticket ticketDetails) {
        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ticket not found with id " + id));

        ticket.setEvent(ticketDetails.getEvent());
        ticket.setVisitor(ticketDetails.getVisitor());
        ticket.setPurchaseDate(ticketDetails.getPurchaseDate());
        ticket.setTicketCategory(ticketDetails.getTicketCategory());
        ticket.setCost(ticketDetails.getCost());
        ticket.setPaymentMethod(ticketDetails.getPaymentMethod());
        ticket.setEan(ticketDetails.getEan());
        ticket.setUsed(ticketDetails.getUsed());
        // ticket.setResaleQueues(ticketDetails.getResaleQueues()); // Managed separately
        return ticketRepository.save(ticket);
    }

    public void deleteTicket(Integer id) {
        ticketRepository.deleteById(id);
    }
}
