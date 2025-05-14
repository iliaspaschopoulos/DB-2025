package com.example.dbapp.repository;

import com.example.dbapp.model.BandMember;
import com.example.dbapp.model.BandMemberId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BandMemberRepository extends JpaRepository<BandMember, BandMemberId> {
}
