package com.example.movierecommendationwebclient.service;



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

    private static final Logger logger = LoggerFactory.getLogger(MovieService.class);

    public MovieService() {
        this.webClient = WebClient.create();
    }

    // Fetch genre list from TMDB and map it to a genre name-to-ID map
    public Mono<Map<String, Integer>> fetchGenres() throws URISyntaxException {
        return webClient.get()
                .uri(new URI(url + "/genre/movie/list?api_key=" + apiKey)) // Concatenate URL with API key
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                .map(response -> (List<Map<String, Object>>) response.get("genres"))
                .map(genres -> genres.stream()
                        .collect(Collectors.toMap(
                                genre -> (String) genre.get("name"),
                                genre -> (Integer) genre.get("id")
                        )));
    }

    public Mono<List<Movie>> fetchMoviesByGenre(int genreId) throws URISyntaxException {
        Mono<List<Movie>> movieList = webClient
                .get()
                .uri(new URI(url + "/discover/movie?api_key=" + apiKey + "&with_genres=" + genreId))
                .accept(MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<Movie>>() {});

        return movieList;

    }
}