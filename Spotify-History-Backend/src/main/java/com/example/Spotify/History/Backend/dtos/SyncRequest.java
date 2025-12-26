package com.example.Spotify.History.Backend.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SyncRequest {

    @NotBlank
    private String timezone;
    private String currentFileLocalDate;
    private Long lastPlayedAtMs;
}
