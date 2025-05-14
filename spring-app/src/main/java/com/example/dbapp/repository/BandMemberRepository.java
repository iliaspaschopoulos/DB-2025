package com.example.dbapp.repository;

import com.example.dbapp.model.BandMember;
import com.example.dbapp.model.BandMemberId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BandMemberRepository extends JpaRepository<BandMember, BandMemberId> {

    @Query("SELECT DISTINCT bm FROM BandMember bm " +
           "LEFT JOIN FETCH bm.band b " +
           "LEFT JOIN FETCH bm.artist a " +
           "LEFT JOIN FETCH a.genres ag") // Fetch artist genres to prevent N+1
    List<BandMember> findAllWithDetails();

    @Query("SELECT DISTINCT bm FROM BandMember bm " +
           "LEFT JOIN FETCH bm.band b " +
           "LEFT JOIN FETCH bm.artist a " +
           "LEFT JOIN FETCH a.genres ag " + // Fetch artist genres
           "WHERE bm.id = :id")
    Optional<BandMember> findByIdWithDetails(@Param("id") BandMemberId id);
}
