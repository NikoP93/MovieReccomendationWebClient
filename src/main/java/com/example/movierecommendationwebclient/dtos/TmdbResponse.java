package com.example.movierecommendationwebclient.dtos;

import com.example.movierecommendationwebclient.model.Movie;

import java.util.List;

public class TmdbResponse {

    private List<Movie> results;

    public List<Movie> getResults() {
        return results;
    }

    public void setResults(List<Movie> results) {
        this.results = results;
    }

    @Override
    public String toString() {
        return "TmdbResponse{" +
                "results=" + results +
                '}';
    }
}
