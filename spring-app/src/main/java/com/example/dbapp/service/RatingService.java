package com.example.dbapp.service;

import com.example.dbapp.model.Rating;
import com.example.dbapp.repository.RatingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RatingService {

    private final RatingRepository ratingRepository;

    @Autowired
    public RatingService(RatingRepository ratingRepository) {
        this.ratingRepository = ratingRepository;
    }

    public List<Rating> getAllRatings() {
        return ratingRepository.findAll();
    }

    public Optional<Rating> getRatingById(Integer id) {
        return ratingRepository.findById(id);
    }

    public Rating saveRating(Rating rating) {
        return ratingRepository.save(rating);
    }

    public Rating updateRating(Integer id, Rating ratingDetails) {
        Rating rating = ratingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Rating not found with id " + id));

        rating.setVisitor(ratingDetails.getVisitor());
        rating.setArtist(ratingDetails.getArtist());
        rating.setBand(ratingDetails.getBand());
        rating.setRatingValue(ratingDetails.getRatingValue());
        rating.setComment(ratingDetails.getComment());
        rating.setRatingDate(ratingDetails.getRatingDate());

        return ratingRepository.save(rating);
    }

    public void deleteRating(Integer id) {
        ratingRepository.deleteById(id);
    }
}
