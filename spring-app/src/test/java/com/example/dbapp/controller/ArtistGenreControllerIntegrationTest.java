package com.example.dbapp.controller;

import com.example.dbapp.model.Artist;
import com.example.dbapp.model.ArtistGenre;
import com.example.dbapp.model.ArtistGenreId;
import com.example.dbapp.repository.ArtistGenreRepository;
import com.example.dbapp.repository.ArtistRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class ArtistGenreControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ArtistGenreRepository artistGenreRepository;

    @Autowired
    private ArtistRepository artistRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private Artist artist1;
    private ArtistGenre artistGenre1;
    private ArtistGenre artistGenre2;

    @BeforeEach
    void setUp() {
        artistGenreRepository.deleteAll();
        artistRepository.deleteAll();

        artist1 = new Artist();
        artist1.setName("Genre Artist");
        artist1.setDateOfBirth(LocalDate.of(1985, 1, 1));
        artist1 = artistRepository.save(artist1);

        Artist artist2 = new Artist();
        artist2.setName("Another Genre Artist");
        artist2.setDateOfBirth(LocalDate.of(1990, 5, 5));
        artist2 = artistRepository.save(artist2);

        artistGenre1 = new ArtistGenre(artist1, "Rock", "Alternative Rock");
        artistGenre1 = artistGenreRepository.save(artistGenre1);

        artistGenre2 = new ArtistGenre(artist2, "Pop", "Synth Pop");
        artistGenre2 = artistGenreRepository.save(artistGenre2);
    }

    @Test
    void getAllArtistGenres() throws Exception {
        mockMvc.perform(get("/api/artist_genres"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].subgenre", is(artistGenre1.getSubgenre())))
                .andExpect(jsonPath("$[0].id.genre", is(artistGenre1.getId().getGenre())))
                .andExpect(jsonPath("$[1].subgenre", is(artistGenre2.getSubgenre())))
                .andExpect(jsonPath("$[1].id.genre", is(artistGenre2.getId().getGenre())));
    }

    @Test
    void getArtistGenreById() throws Exception {
        mockMvc.perform(get("/api/artist_genres/find")
                        .param("artistId", String.valueOf(artistGenre1.getId().getArtistId()))
                        .param("genreName", artistGenre1.getId().getGenre()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.subgenre", is(artistGenre1.getSubgenre())))
                .andExpect(jsonPath("$.artist.artistName", is(artist1.getName())))
                .andExpect(jsonPath("$.id.genre", is(artistGenre1.getId().getGenre())));
    }

    @Test
    void getArtistGenreById_notFound() throws Exception {
        ArtistGenreId nonExistentId = new ArtistGenreId(999, "NonExistentGenre");

        mockMvc.perform(get("/api/artist_genres/find")
                        .param("artistId", String.valueOf(nonExistentId.getArtistId()))
                        .param("genreName", nonExistentId.getGenre()))
                .andExpect(status().isNotFound());
    }

    @Test
    void createArtistGenre() throws Exception {
        Artist newArtist = new Artist();
        newArtist.setName("Artist For New Genre");
        newArtist.setDateOfBirth(LocalDate.of(1995,2,2));
        newArtist = artistRepository.save(newArtist);

        ArtistGenre newArtistGenre = new ArtistGenre();
        ArtistGenreId newId = new ArtistGenreId(newArtist.getId(), "Electronic");
        newArtistGenre.setId(newId);
        newArtistGenre.setArtist(newArtist);
        newArtistGenre.setSubgenre("Techno");

        mockMvc.perform(post("/api/artist_genres")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newArtistGenre)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.subgenre", is("Techno")))
                .andExpect(jsonPath("$.id.artistId", is(newArtist.getId())))
                .andExpect(jsonPath("$.id.genre", is("Electronic")));
    }

    @Test
    void updateArtistGenre() throws Exception {
        ArtistGenre updatedInfo = new ArtistGenre();
        updatedInfo.setSubgenre("Progressive Rock");
        updatedInfo.setId(artistGenre1.getId());
        updatedInfo.setArtist(artistGenre1.getArtist());

        mockMvc.perform(put("/api/artist_genres")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedInfo)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.subgenre", is("Progressive Rock")))
                .andExpect(jsonPath("$.id.genre", is(artistGenre1.getId().getGenre())));
    }

    @Test
    void updateArtistGenre_notFound() throws Exception {
        ArtistGenreId nonExistentId = new ArtistGenreId(999, "NonExistentGenre");
        ArtistGenre updatedInfo = new ArtistGenre();
        updatedInfo.setId(nonExistentId);
        Artist dummyArtist = new Artist();
        dummyArtist.setId(999);
        updatedInfo.setArtist(dummyArtist);
        updatedInfo.setSubgenre("No Subgenre");

        mockMvc.perform(put("/api/artist_genres")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedInfo)))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteArtistGenre() throws Exception {
        mockMvc.perform(delete("/api/artist_genres/delete")
                        .param("artistId", String.valueOf(artistGenre1.getId().getArtistId()))
                        .param("genreName", artistGenre1.getId().getGenre()))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/artist_genres/find")
                        .param("artistId", String.valueOf(artistGenre1.getId().getArtistId()))
                        .param("genreName", artistGenre1.getId().getGenre()))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteArtistGenre_notFound() throws Exception {
        ArtistGenreId nonExistentId = new ArtistGenreId(999, "NonExistentGenre");

        mockMvc.perform(delete("/api/artist_genres/delete")
                        .param("artistId", String.valueOf(nonExistentId.getArtistId()))
                        .param("genreName", nonExistentId.getGenre()))
                .andExpect(status().isNotFound());
    }
}
