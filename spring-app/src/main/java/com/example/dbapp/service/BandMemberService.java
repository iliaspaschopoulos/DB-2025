package com.example.dbapp.service;

import com.example.dbapp.model.BandMember;
import com.example.dbapp.model.BandMemberId;
import com.example.dbapp.repository.BandMemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BandMemberService {

    private final BandMemberRepository bandMemberRepository;

    @Autowired
    public BandMemberService(BandMemberRepository bandMemberRepository) {
        this.bandMemberRepository = bandMemberRepository;
    }

    public List<BandMember> getAllBandMembers() {
        return bandMemberRepository.findAll();
    }

    public Optional<BandMember> getBandMemberById(BandMemberId id) {
        return bandMemberRepository.findById(id);
    }

    public BandMember saveBandMember(BandMember bandMember) {
        return bandMemberRepository.save(bandMember);
    }

    public BandMember updateBandMember(BandMemberId id, BandMember bandMemberDetails) {
        BandMember bandMember = bandMemberRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("BandMember not found with id " + id));

        // Update Artist and Band if they are different in bandMemberDetails
        // and if this is a supported operation.
        // For now, we assume the main purpose is to manage the existence of the relationship.
        // If bandMemberDetails contains new artist or band, update them:
        if (bandMemberDetails.getArtist() != null) {
            bandMember.setArtist(bandMemberDetails.getArtist());
        }
        if (bandMemberDetails.getBand() != null) {
            bandMember.setBand(bandMemberDetails.getBand());
        }
        if (bandMemberDetails.getRole() != null) {
            bandMember.setRole(bandMemberDetails.getRole());
        }
        if (bandMemberDetails.getJoinDate() != null) {
            bandMember.setJoinDate(bandMemberDetails.getJoinDate());
        }
        
        return bandMemberRepository.save(bandMember);
    }

    public void deleteBandMember(BandMemberId id) {
        bandMemberRepository.deleteById(id);
    }
}
