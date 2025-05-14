package com.example.dbapp.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Column;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "Resale_Queue")
public class ResaleQueue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "resale_id")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "ticket_id", nullable = false)
    private Ticket ticket;

    @ManyToOne
    @JoinColumn(name = "seller_id", nullable = false)
    private Visitor seller;

    @ManyToOne
    @JoinColumn(name = "buyer_id", nullable = false)
    private Visitor buyer;

    @ManyToOne
    @JoinColumn(name = "visitor_id")
    private Visitor visitor;

    @Column(name = "listing_date", nullable = false)
    private LocalDate listingDate;

    @Column(name = "entry_time")
    private LocalDateTime entryTime;

    @Column(name = "queue_position")
    private Integer queuePosition;

    @Column(name = "resale_status", length = 20)
    // CHECK (resale_status IN ('Pending', 'Completed'))
    private String resaleStatus;

    @Column(name = "fifo_order")
    private Integer fifoOrder;

    // Constructors
    public ResaleQueue() {
    }

    // Getters and Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Ticket getTicket() {
        return ticket;
    }

    public void setTicket(Ticket ticket) {
        this.ticket = ticket;
    }

    public Visitor getSeller() {
        return seller;
    }

    public void setSeller(Visitor seller) {
        this.seller = seller;
    }

    public Visitor getBuyer() {
        return buyer;
    }

    public void setBuyer(Visitor buyer) {
        this.buyer = buyer;
    }

    public Visitor getVisitor() {
        return visitor;
    }

    public void setVisitor(Visitor visitor) {
        this.visitor = visitor;
    }

    public LocalDate getListingDate() {
        return listingDate;
    }

    public void setListingDate(LocalDate listingDate) {
        this.listingDate = listingDate;
    }

    public LocalDateTime getEntryTime() {
        return entryTime;
    }

    public void setEntryTime(LocalDateTime entryTime) {
        this.entryTime = entryTime;
    }

    public Integer getQueuePosition() {
        return queuePosition;
    }

    public void setQueuePosition(Integer queuePosition) {
        this.queuePosition = queuePosition;
    }

    public String getResaleStatus() {
        return resaleStatus;
    }

    public void setResaleStatus(String resaleStatus) {
        this.resaleStatus = resaleStatus;
    }

    public Integer getFifoOrder() {
        return fifoOrder;
    }

    public void setFifoOrder(Integer fifoOrder) {
        this.fifoOrder = fifoOrder;
    }
}
