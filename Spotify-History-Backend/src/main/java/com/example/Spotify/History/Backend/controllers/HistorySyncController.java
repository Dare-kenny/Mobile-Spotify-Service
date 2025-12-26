package com.example.Spotify.History.Backend.controllers;

import com.example.Spotify.History.Backend.dtos.SyncRequest;
import com.example.Spotify.History.Backend.dtos.SyncResponse;
import com.example.Spotify.History.Backend.services.HistorySyncService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/history")
@RequiredArgsConstructor
public class HistorySyncController {

    private final HistorySyncService historySyncService;

    private String extractBearerToken(String authorization){
        if (authorization == null || !authorization.startsWith("Bearer ")){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,"Missing Bearer Token");
        }
        return authorization.substring("Bearer ".length()).trim();
    }

    @PostMapping("/sync")
    public SyncResponse sync(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorization, @Valid @RequestBody SyncRequest request){

        String token = extractBearerToken(authorization);
        return historySyncService.sync(token,request);
    }
}
