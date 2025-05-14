package com.example.dbapp.controller;

import com.example.dbapp.model.Band;
import com.example.dbapp.model.Artist;
import com.example.dbapp.model.BandMember;
import com.example.dbapp.model.BandMemberId;
import com.example.dbapp.service.BandMemberService;
import com.example.dbapp.repository.BandRepository;
import com.example.dbapp.repository.ArtistRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/band-members")
public class BandMemberController {

    private final BandMemberService bandMemberService;
    private final BandRepository bandRepository;
    private final ArtistRepository artistRepository;

    @Autowired
    public BandMemberController(BandMemberService bandMemberService,
                                BandRepository bandRepository,
                                ArtistRepository artistRepository) {
        this.bandMemberService = bandMemberService;
        this.bandRepository = bandRepository;
        this.artistRepository = artistRepository;
    }

    @GetMapping
    public List<BandMember> getAllBandMembers() {
        return bandMemberService.getAllBandMembers();
    }

    @GetMapping(path = "/find")
    public ResponseEntity<BandMember> getBandMemberById(@RequestParam Integer bandId, @RequestParam Integer artistId) {
        BandMemberId id = new BandMemberId(bandId, artistId);
        return bandMemberService.getBandMemberById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> createBandMember(@RequestBody BandMember bandMember) {
        BandMemberId id = bandMember.getId();

        if (id == null || id.getBandId() == null || id.getArtistId() == null) {
            return ResponseEntity.badRequest().body("BandMember ID (bandId, artistId) must be provided in the 'id' field.");
        }

        try {
            if (bandMember.getBand() == null) {
                Band band = bandRepository.findById(id.getBandId())
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Band not found with ID: " + id.getBandId()));
                bandMember.setBand(band);
            }
            if (bandMember.getArtist() == null) {
                Artist artist = artistRepository.findById(id.getArtistId())
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Artist not found with ID: " + id.getArtistId()));
                bandMember.setArtist(artist);
            }

            bandMember.getId().setBandId(bandMember.getBand().getBandId());
            bandMember.getId().setArtistId(bandMember.getArtist().getArtistId());

            BandMember saved = bandMemberService.saveBandMember(bandMember);
            return ResponseEntity.status(HttpStatus.CREATED).body(saved);
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getReason());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred: " + e.getMessage());
        }
    }

    @PutMapping
    public ResponseEntity<?> updateBandMember(@RequestBody BandMember bandMemberDetails) {
        BandMemberId id = bandMemberDetails.getId();
        if (id == null || id.getBandId() == null || id.getArtistId() == null) {
            return ResponseEntity.badRequest().body("BandMember ID (bandId, artistId) must be provided in the 'id' field for update.");
        }

        try {
            BandMember updated = bandMemberService.updateBandMember(id, bandMemberDetails);
            return ResponseEntity.ok(updated);
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getReason());
        } catch (RuntimeException e) {
            if (e.getMessage() != null && e.getMessage().toLowerCase().contains("not found")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred: " + e.getMessage());
        }
    }

    @DeleteMapping(path = "/delete")
    public ResponseEntity<Void> deleteBandMember(@RequestParam Integer bandId, @RequestParam Integer artistId) {
        BandMemberId id = new BandMemberId(bandId, artistId);
        try {
            bandMemberService.deleteBandMember(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
