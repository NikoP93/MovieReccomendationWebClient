package com.example.movierecommendationwebclient.dtos;

import com.example.movierecommendationwebclient.model.Movie;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class MovieResponse {

    private List<Movie> movies;

    @JsonProperty("results")
    public List<Movie> getMovies() {
        return movies;
    }

    public void setMovies(List<Movie> movies) {
        this.movies = movies;
    }
}
