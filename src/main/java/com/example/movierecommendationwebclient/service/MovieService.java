package com.example.movierecommendationwebclient.service;



import com.example.movierecommendationwebclient.dtos.GenreResponse;
import com.example.movierecommendationwebclient.dtos.MovieResponse;
import com.example.movierecommendationwebclient.model.Genre;
import com.example.movierecommendationwebclient.model.Movie;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class MovieService {


    @Value("${tmdb.api-key}")
    private String apiKey;

    @Value("${tmdb.api-url}")
    private String url;

    private final WebClient webClient;


    public MovieService() {
        this.webClient = WebClient.create();
    }

    // Fetch genre list from TMDB and map it to a genre name-to-ID map
    public Mono<Map<String, Integer>> fetchGenres() throws URISyntaxException {
        return webClient.get()
                .uri(new URI(url + "/genre/movie/list?api_key=" + apiKey))
                .retrieve()
                .bodyToMono(GenreResponse.class) // Map directly to GenreResponse
                .map(response -> response.getGenres().stream() // Process genres list
                        .collect(Collectors.toMap(
                                Genre::getName, // Key: genre name
                                Genre::getId    // Value: genre id
                        )));
    }

    public Mono<List<Movie>> fetchMoviesByGenre(int genreId) throws URISyntaxException {
        return webClient
                .get()
                .uri(new URI(url + "/discover/movie?api_key=" + apiKey + "&with_genres=" + genreId))
                .retrieve()
                .bodyToMono(MovieResponse.class) // Map to MovieResponse
                .map(MovieResponse::getMovies); // Extract movies from the "results" field
    }

}