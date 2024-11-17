package com.example.movierecommendationwebclient.controller;

import com.example.movierecommendationwebclient.dtos.MyResponse;
import com.example.movierecommendationwebclient.service.MovieService;
import com.example.movierecommendationwebclient.service.OpenAIService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.net.URISyntaxException;
import java.util.Map;


@RestController
@CrossOrigin( origins ="*")
public class MovieController {



        private final MovieService movieService;
        private final OpenAIService openAIService;

        @Autowired
        public MovieController(MovieService movieService, OpenAIService openAIService) {
            this.movieService = movieService;
            this.openAIService = openAIService;
        }

        @GetMapping("/recommendation")
        public Mono<MyResponse> getMovieRecommendation(@RequestParam String genreName) throws URISyntaxException {
            return movieService.fetchGenres()
                    .flatMap(genres -> {
                        Integer genreId = genres.get(genreName);
                        if (genreId == null) {
                            return Mono.just(new MyResponse("Invalid genre name: " + genreName));
                        }
                        try {
                            return movieService.fetchMoviesByGenre(genreId)
                                    .flatMap(movies -> openAIService.fetchMovieRecommendation(genreName, movies));
                        } catch (URISyntaxException e) {
                            throw new RuntimeException(e);
                        }
                    });
        }


}
