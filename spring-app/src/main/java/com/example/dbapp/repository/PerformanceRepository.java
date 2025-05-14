package com.example.dbapp.repository;

import com.example.dbapp.model.Performance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PerformanceRepository extends JpaRepository<Performance, Integer> {

    @Query("SELECT p FROM Performance p " +
           "LEFT JOIN FETCH p.event e " +
           "LEFT JOIN FETCH p.artist a " +
           "LEFT JOIN FETCH p.band b " +
           "LEFT JOIN FETCH a.genres ag ") // Eagerly fetch artist genres if artist is present
    List<Performance> findAllWithDetails();

    @Query("SELECT p FROM Performance p " +
           "LEFT JOIN FETCH p.event e " +
           "LEFT JOIN FETCH p.artist a " +
           "LEFT JOIN FETCH p.band b " +
           "LEFT JOIN FETCH a.genres ag " + // Eagerly fetch artist genres
           "WHERE p.performanceId = :id")
    Optional<Performance> findByIdWithDetails(@Param("id") Integer id);
}
