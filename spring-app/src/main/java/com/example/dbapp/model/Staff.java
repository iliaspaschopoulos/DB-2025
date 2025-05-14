package com.example.dbapp.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Column;

@Entity
public class Staff {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "staff_id")
    private Integer id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column // CHECK(age > 0)
    private Integer age;

    @Column(nullable = false, length = 50)
    private String role;

    @Column(name = "experience_level", length = 20)
    // CHECK (experience_level IN ('ειδικευόμενος', 'αρχάριος', 'μέσος', 'έμπειρος', 'πολύ έμπειρος'))
    private String experienceLevel;

    // Constructors
    public Staff() {
    }

    public Staff(String name, Integer age, String role, String experienceLevel) {
        this.name = name;
        this.age = age;
        this.role = role;
        this.experienceLevel = experienceLevel;
    }

    // Getters and Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getExperienceLevel() {
        return experienceLevel;
    }

    public void setExperienceLevel(String experienceLevel) {
        this.experienceLevel = experienceLevel;
    }
}
