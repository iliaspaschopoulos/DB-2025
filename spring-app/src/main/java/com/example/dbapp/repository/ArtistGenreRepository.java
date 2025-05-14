package com.example.dbapp.repository;

import com.example.dbapp.model.ArtistGenre;
import com.example.dbapp.model.ArtistGenreId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ArtistGenreRepository extends JpaRepository<ArtistGenre, ArtistGenreId> {
}
