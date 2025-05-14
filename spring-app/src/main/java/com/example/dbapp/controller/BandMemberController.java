package com.example.dbapp.controller;

import com.example.dbapp.model.BandMember;
import com.example.dbapp.model.BandMemberId;
import com.example.dbapp.service.BandMemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/band_members") // Changed from "/api/bandmembers"
public class BandMemberController {

    private final BandMemberService bandMemberService;

    @Autowired
    public BandMemberController(BandMemberService bandMemberService) {
        this.bandMemberService = bandMemberService;
    }

    @GetMapping
    public List<BandMember> getAllBandMembers() {
        return bandMemberService.getAllBandMembers();
    }

    @GetMapping("/find") // Example: /api/band_members/find?bandId=1&artistId=1
    public ResponseEntity<BandMember> getBandMemberById(@RequestParam Integer bandId, @RequestParam Integer artistId) {
        BandMemberId id = new BandMemberId(bandId, artistId);
        return bandMemberService.getBandMemberById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<BandMember> createBandMember(@RequestBody BandMember bandMember) {
        if (bandMember.getBand() != null && bandMember.getArtist() != null && bandMember.getId() == null) {
            bandMember.setId(new BandMemberId(bandMember.getBand().getId(), bandMember.getArtist().getId()));
        } else if (bandMember.getId() != null && bandMember.getBand() != null && bandMember.getId().getBandId() == null) {
            bandMember.getId().setBandId(bandMember.getBand().getId());
        } else if (bandMember.getId() != null && bandMember.getArtist() != null && bandMember.getId().getArtistId() == null) {
            bandMember.getId().setArtistId(bandMember.getArtist().getId());
        }
        BandMember savedBandMember = bandMemberService.saveBandMember(bandMember);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedBandMember);
    }

    @PutMapping
    public ResponseEntity<BandMember> updateBandMember(@RequestBody BandMember bandMemberDetails) {
        BandMemberId id = bandMemberDetails.getId();
        if (id == null || id.getBandId() == null || id.getArtistId() == null) {
            return ResponseEntity.badRequest().build(); // ID must be present and complete
        }

        // Explicitly check if BandMember exists BEFORE calling the main update service method
        if (!bandMemberService.getBandMemberById(id).isPresent()) {
            return ResponseEntity.notFound().build(); // Return 404 early if BandMember itself not found
        }

        if (bandMemberDetails.getBand() != null && bandMemberDetails.getBand().getId() != null && !bandMemberDetails.getBand().getId().equals(id.getBandId())) {
            return ResponseEntity.badRequest().build();
        }
        if (bandMemberDetails.getArtist() != null && bandMemberDetails.getArtist().getId() != null && !bandMemberDetails.getArtist().getId().equals(id.getArtistId())) {
            return ResponseEntity.badRequest().build();
        }

        try {
            BandMember updatedBandMember = bandMemberService.updateBandMember(id, bandMemberDetails);
            if (updatedBandMember == null) {
                // This can happen if the service's update logic itself determines a "not found"
                // or failure condition resulting in null, even after initial existence check.
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(updatedBandMember);
        } catch (RuntimeException e) {
            // Catch other runtime exceptions from the service during the update.
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/delete") // Example: /api/band_members/delete?bandId=1&artistId=1
    public ResponseEntity<Void> deleteBandMember(@RequestParam Integer bandId, @RequestParam Integer artistId) {
        BandMemberId id = new BandMemberId(bandId, artistId);
        if (bandMemberService.getBandMemberById(id).isPresent()) {
            bandMemberService.deleteBandMember(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
