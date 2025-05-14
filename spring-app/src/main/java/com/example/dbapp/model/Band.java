package com.example.dbapp.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "Band")
public class Band {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "band_id")
    private Integer bandId;

    @Column(name = "band_name", nullable = false, length = 100)
    private String bandName;

    @Column(name = "formation_date")
    private LocalDate formationDate;

    @Column(name = "website", length = 255)
    private String website;

    // Getters and Setters
    public Integer getBandId() {
        return bandId;
    }

    public void setBandId(Integer bandId) {
        this.bandId = bandId;
    }

    public String getBandName() {
        return bandName;
    }

    public void setBandName(String bandName) {
        this.bandName = bandName;
    }

    public LocalDate getFormationDate() {
        return formationDate;
    }

    public void setFormationDate(LocalDate formationDate) {
        this.formationDate = formationDate;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }
}
