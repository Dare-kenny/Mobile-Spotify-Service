package com.example.Spotify.History.Backend.services;

import com.example.Spotify.History.Backend.dtos.PlayEventDto;
import com.example.Spotify.History.Backend.dtos.SpotifyRecentlyPlayedResponse;
import com.example.Spotify.History.Backend.dtos.SyncRequest;
import com.example.Spotify.History.Backend.dtos.SyncResponse;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class HistorySyncService {

    private ZoneId zone(String timezone) {
        try {
            return ZoneId.of(timezone);
        } catch (Exception e) {
            // fallback
            return ZoneId.of("UTC");
        }
    }

    private long parseIsoToMs(String iso) {
        try {
            return Instant.parse(iso).toEpochMilli();
        } catch (Exception e) {
            return 0;
        }
    }


    private String safe(String s) {
        return s == null ? "" : s;
    }


    private PlayEventDto mapItem(SpotifyRecentlyPlayedResponse.Item item) {
        if (item == null || item.getTrack() == null || item.getPlayedAt() == null) return null;

        long playedAtMs = parseIsoToMs(item.getPlayedAt());
        if (playedAtMs <= 0) return null;

        String trackName = safe(item.getTrack().getName());

        List<String> artists = item.getTrack().getArtists() == null ? List.of()
                : item.getTrack().getArtists().stream()
                .map(a -> safe(a.getName()))
                .filter(s -> !s.isBlank())
                .toList();

        String imageUrl = null;
        if (item.getTrack().getAlbum() != null && item.getTrack().getAlbum().getImages() != null
                && !item.getTrack().getAlbum().getImages().isEmpty()) {
            imageUrl = item.getTrack().getAlbum().getImages().get(0).getUrl();
        }

        return PlayEventDto.builder()
                .trackName(trackName)
                .artistNames(artists)
                .playedAt(item.getPlayedAt())
                .playedAtMs(playedAtMs)
                .albumImageUrl(imageUrl)
                .build();
    }

    private List<PlayEventDto> fetchAllAfter(String token, long afterMs) {
        List<PlayEventDto> out = new ArrayList<>();
        Set<String> dedupe = new HashSet<>();

        long cursor = afterMs;

        while (true) {
            SpotifyRecentlyPlayedResponse page = spotifyHistoryClient.recentlyPlayedAfter(token, cursor, PAGE_LIMIT);
            if (page == null || page.getItems() == null || page.getItems().isEmpty()) break;

            List<PlayEventDto> mapped = page.getItems().stream()
                    .map(this::mapItem)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());

            // Deduplicate (playedAtMs + trackName is okay; add more fields if you want)
            for (PlayEventDto e : mapped) {
                String key = e.getPlayedAtMs() + "|" + e.getTrackName();
                if (dedupe.add(key)) out.add(e);
            }

            if (page.getItems().size() < PAGE_LIMIT) break;

            // advance cursor to last played_at + 1ms
            long lastMs = mapped.stream().mapToLong(PlayEventDto::getPlayedAtMs).max().orElse(cursor);
            cursor = lastMs + 1;
        }

        return out;
    }



    private static final int PAGE_LIMIT = 25;

    private final SpotifyHistoryClient spotifyHistoryClient;

    public SyncResponse sync(String spotifyAccessToken, SyncRequest req){

        ZoneId zone = zone(req.getTimezone());
        LocalDate today = LocalDate.now(zone);
        String todayStr = today.toString();

        boolean reset = req.getCurrentFileLocalDate() == null || req.getCurrentFileLocalDate().isBlank() || !req.getCurrentFileLocalDate().equals(todayStr);

        long startAfterMs;
        if (reset){
            startAfterMs = today.atStartOfDay(zone).toInstant().toEpochMilli();
        }else {
            Long last = req.getLastPlayedAtMs();
            startAfterMs = (last == null ? today.atStartOfDay(zone).toInstant().toEpochMilli() : last + 1);
        }

        List<PlayEventDto> all = fetchAllAfter(spotifyAccessToken,startAfterMs);

        all.sort(Comparator.comparingLong(PlayEventDto::getPlayedAtMs));

        Long newLast = all.isEmpty() ? req.getLastPlayedAtMs() : all.get(all.size() - 1).getPlayedAtMs();

        return SyncResponse.builder()
                .reset(reset)
                .localDate(todayStr)
                .timezone(req.getTimezone())
                .newLastPlayedAtMs(newLast)
                .items(all)
                .build();

    }
}
