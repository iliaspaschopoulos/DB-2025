package com.example.dbapp.repository;

import com.example.dbapp.model.ResaleQueue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ResaleQueueRepository extends JpaRepository<ResaleQueue, Integer> {
}
