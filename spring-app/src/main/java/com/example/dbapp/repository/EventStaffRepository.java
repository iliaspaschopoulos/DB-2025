package com.example.dbapp.repository;

import com.example.dbapp.model.EventStaff;
import com.example.dbapp.model.EventStaffId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventStaffRepository extends JpaRepository<EventStaff, EventStaffId> {
}
