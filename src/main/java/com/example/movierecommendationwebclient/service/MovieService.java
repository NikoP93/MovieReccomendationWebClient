package com.example.movierecommendationwebclient.service;


import com.example.movierecommendationwebclient.dtos.TmdbResponse;
import com.example.movierecommendationwebclient.model.Movie;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class MovieService {

    private final WebClient tmdbWebClient;

    @Value("${tmdb.api-key}")
    private String apiKey;

    private static final Logger logger = LoggerFactory.getLogger(MovieService.class);

    public MovieService(WebClient tmdbWebClient) {
        this.tmdbWebClient = tmdbWebClient;
    }

    // Fetch genre list from TMDB and map it to a genre name-to-ID map
    public Mono<Map<String, Integer>> fetchGenres() {
        return tmdbWebClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/genre/movie/list")
                        .queryParam("api_key", apiKey)
                        .build())
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                .map(response -> (List<Map<String, Object>>) response.get("genres"))
                .map(genres -> genres.stream()
                        .collect(Collectors.toMap(
                                genre -> (String) genre.get("name"),
                                genre -> (Integer) genre.get("id")
                        )));
    }

    // Fetch movies by genre ID
    public Mono<List<Movie>> fetchMoviesByGenre(int genreId) {
        return tmdbWebClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/discover/movie")
                        .queryParam("api_key", apiKey)
                        .queryParam("with_genres", genreId)
                        .build())
                .retrieve()
                .bodyToMono(TmdbResponse.class) // Map to TmdbResponse
                .map(TmdbResponse::getResults)
                .doOnNext(movies -> logger.info("number of movies " + movies.size())); // Extract the list of movies
    }
}