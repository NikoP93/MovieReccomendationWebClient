package com.example.movierecommendationwebclient.controller;

import com.example.movierecommendationwebclient.dtos.MyResponse;
import com.example.movierecommendationwebclient.service.MovieService;
import com.example.movierecommendationwebclient.service.OpenAIService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestController
@RequestMapping("/api/movies")
public class MovieController {



        private final MovieService movieService;
        private final OpenAIService openAIService;

        @Autowired
        public MovieController(MovieService movieService, OpenAIService openAIService) {
            this.movieService = movieService;
            this.openAIService = openAIService;
        }

        @GetMapping("/recommendation")
        public Mono<MyResponse> getMovieRecommendation(@RequestParam String genreName) {
            return movieService.fetchGenres()
                    .flatMap(genres -> {
                        Integer genreId = genres.get(genreName);
                        if (genreId == null) {
                            return Mono.just(new MyResponse("Invalid genre name: " + genreName));
                        }
                        return movieService.fetchMoviesByGenre(genreId)
                                .flatMap(movies -> openAIService.fetchMovieRecommendation(genreName, movies));
                    });
        }


}
