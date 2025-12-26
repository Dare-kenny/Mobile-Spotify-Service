package com.example.Spotify.History.Backend.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class SpotifyRecentlyPlayedResponse {

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Artist {
        private String name;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Album {
        private List<Image> images;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Image {
        private String url;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Track {
        private String name;
        private Album album;
        private List<Artist> artists;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Item {
        @JsonProperty("played_at")
        private String playedAt;

        private Track track;
    }

    private List<Item> items;


}
