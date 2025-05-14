package com.example.dbapp.dto;

import java.time.LocalDate;

public record BandMemberDTO(Integer bandId, Integer artistId, String role, LocalDate joinDate) {
}
