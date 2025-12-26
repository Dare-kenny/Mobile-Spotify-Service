package com.example.Spotify.History.Backend.dtos;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class SyncResponse {

    private boolean reset;
    private String localDate;
    private String timezone;
    private Long newLastPlayedAtMs;
    private List<PlayEventDto> items;
}
