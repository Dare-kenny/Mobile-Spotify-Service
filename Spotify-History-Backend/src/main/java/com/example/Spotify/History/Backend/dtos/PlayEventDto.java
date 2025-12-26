package com.example.Spotify.History.Backend.dtos;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class PlayEventDto {
    private String trackName;
    private List<String> artistNames;
    private String playedAt;
    private long playedAtMs;
    private String albumImageUrl;
}
