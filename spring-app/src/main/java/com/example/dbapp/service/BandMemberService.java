package com.example.dbapp.service;

import com.example.dbapp.model.Band;
import com.example.dbapp.model.Artist;
import com.example.dbapp.model.BandMember;
import com.example.dbapp.model.BandMemberId;
import com.example.dbapp.repository.BandMemberRepository;
import com.example.dbapp.repository.BandRepository;
import com.example.dbapp.repository.ArtistRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
public class BandMemberService {

    private final BandMemberRepository bandMemberRepository;
    private final BandRepository bandRepository;
    private final ArtistRepository artistRepository;

    @Autowired
    public BandMemberService(BandMemberRepository bandMemberRepository,
                             BandRepository bandRepository,
                             ArtistRepository artistRepository) {
        this.bandMemberRepository = bandMemberRepository;
        this.bandRepository = bandRepository;
        this.artistRepository = artistRepository;
    }

    @Transactional(readOnly = true)
    public List<BandMember> getAllBandMembers() {
        // Using findAllWithDetails ensures band and artist proxies are initialized if accessed.
        // The N+1 for Artist_Genre is handled in ArtistRepository for /api/artists.
        return bandMemberRepository.findAllWithDetails();
    }

    @Transactional(readOnly = true)
    public Optional<BandMember> getBandMemberById(BandMemberId id) {
        return bandMemberRepository.findByIdWithDetails(id);
    }

    @Transactional
    public BandMember saveBandMember(BandMember bandMember) {
        BandMemberId id = bandMember.getId();
        if (id == null || id.getBandId() == null || id.getArtistId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "BandMember ID (bandId, artistId) must be provided.");
        }

        Band band = bandRepository.findById(id.getBandId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Band not found with ID: " + id.getBandId()));
        Artist artist = artistRepository.findById(id.getArtistId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Artist not found with ID: " + id.getArtistId()));

        bandMember.setBand(band); // Ensure association is set
        bandMember.setArtist(artist); // Ensure association is set
        
        // Role and JoinDate will be null if not provided by frontend, which is fine as they are nullable.
        return bandMemberRepository.save(bandMember);
    }

    @Transactional
    public BandMember updateBandMember(BandMemberId id, BandMember bandMemberDetails) {
        BandMember existingMember = bandMemberRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "BandMember not found with id: " + id));

        // If frontend doesn't send role and joinDate, these will be set to null
        // due to how Spring maps request body to BandMemberDetails object.
        existingMember.setRole(bandMemberDetails.getRole());
        existingMember.setJoinDate(bandMemberDetails.getJoinDate());
        
        // Band and Artist (IDs) are part of the primary key and should not be changed here.
        // If band/artist needs to change, it's a delete and new insert.

        return bandMemberRepository.save(existingMember);
    }

    @Transactional
    public void deleteBandMember(BandMemberId id) {
        if (!bandMemberRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "BandMember not found for deletion with id: " + id);
        }
        bandMemberRepository.deleteById(id);
    }
}
