package com.example.movierecommendationwebclient.dtos;

import com.example.movierecommendationwebclient.model.Genre;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class GenreResponse {
    @JsonProperty("genres") // Map the "genres" field in JSON to this property
    private List<Genre> genres;

    public List<Genre> getGenres() {
        return genres;
    }

    public void setGenres(List<Genre> genres) {
        this.genres = genres;
    }
}
