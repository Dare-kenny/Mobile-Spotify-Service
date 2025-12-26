package com.example.Spotify.History.Backend.services;

import com.example.Spotify.History.Backend.dtos.SpotifyRecentlyPlayedResponse;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@RequiredArgsConstructor
public class SpotifyHistoryClient {

    private final WebClient spotifyWebClient;

    public SpotifyRecentlyPlayedResponse recentlyPlayedAfter(String accessToken, long afterMs, int limit){

        return spotifyWebClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/me/player/recently-played")
                        .queryParam("after",afterMs)
                        .queryParam("limit",limit)
                        .build()
                )
                .header(HttpHeaders.AUTHORIZATION, "Bearer "+accessToken)
                .retrieve()
                .bodyToMono(SpotifyRecentlyPlayedResponse.class)
                .block();
    }
}
