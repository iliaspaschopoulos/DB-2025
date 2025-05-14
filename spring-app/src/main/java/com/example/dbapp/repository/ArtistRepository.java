package com.example.dbapp.repository;

import com.example.dbapp.model.Artist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ArtistRepository extends JpaRepository<Artist, Integer> {

    // Use this method in your ArtistService/Controller when fetching all artists
    // to avoid N+1 queries for genres.
    @Query("SELECT DISTINCT a FROM Artist a LEFT JOIN FETCH a.genres")
    List<Artist> findAllWithGenres();
    
    // If you prefer to override the default findAll behavior, you can name it findAll:
    // @Override
    // @Query("SELECT DISTINCT a FROM Artist a LEFT JOIN FETCH a.genres")
    // List<Artist> findAll();
}
