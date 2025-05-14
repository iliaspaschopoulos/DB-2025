package com.example.dbapp.service;

import com.example.dbapp.model.Artist;
import com.example.dbapp.repository.ArtistRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
public class ArtistService {

    @Autowired
    private ArtistRepository artistRepository;

    @Transactional(readOnly = true)
    public List<Artist> getAllArtists() {
        return artistRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Artist> getArtistById(Integer id) {
        return artistRepository.findById(id);
    }

    @Transactional
    public Artist createOrUpdateArtist(Artist artist) {
        return artistRepository.save(artist);
    }

    @Transactional
    public void deleteArtist(Integer id) {
        artistRepository.deleteById(id);
    }
}
