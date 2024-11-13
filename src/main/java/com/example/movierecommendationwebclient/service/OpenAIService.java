package com.example.movierecommendationwebclient.service;

import com.example.movierecommendationwebclient.dtos.ChatCompletionRequest;
import com.example.movierecommendationwebclient.dtos.ChatCompletionResponse;
import com.example.movierecommendationwebclient.dtos.MyResponse;
import com.example.movierecommendationwebclient.model.Movie;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class OpenAIService {



    @Value("${app.api-key}")
    private String apiKey;

    @Value("${app.api-url}")
    private String url;

    @Value("${app.model}")
    private String model;

    @Value("${app.temperature}")
    private double temperature;

    @Value("${app.max_tokens}")
    private int maxTokens;

    @Value("${app.top_p}")
    private double topP;

    @Value("${app.frequency_penalty}")
    private double frequencyPenalty;

    @Value("${app.presence_penalty}")
    private double presencePenalty;

    private final WebClient client;

    public OpenAIService() {
        this.client = WebClient.create();

    }

    private static final String SYSTEM_MESSAGE_FOR_RECOMMENDATION = """
    You are a movie recommendation assistant with expertise in analyzing various movie options in a genre.
    Given a list of movies, select different recommendations based on unique qualities each time you respond.
    Encourage variety in recommendations rather than always choosing the most popular option.
    """;


    public Mono<MyResponse> fetchMovieRecommendation(String genre, List<Movie> movies) {
        // Prepare a list of movie titles for the prompt
        String movieList = movies.stream()
                .map(movie -> movie.getTitle() + " (" + movie.getReleaseDate() + ")")
                .collect(Collectors.joining(", "));

        System.out.println("THis is the movielist in OpenAIService" + movieList.length());

        // Construct the user prompt with genre and movie options
        String prompt = "The user wants a fresh recommendation in the " + genre + " genre. Considering this list of options: "
                + movieList + ", pick the best movie and explain your reasoning and the main plot in a few sentences";

        // Create the ChatCompletionRequest object
        ChatCompletionRequest request = new ChatCompletionRequest();
        request.setModel(model);
        request.setTemperature(temperature);
        request.setMax_tokens(maxTokens);
        request.setTop_p(topP);
        request.setFrequency_penalty(frequencyPenalty);
        request.setPresence_penalty(presencePenalty);

        // Add system message and user prompt
        request.getMessages().add(new ChatCompletionRequest.Message("system", SYSTEM_MESSAGE_FOR_RECOMMENDATION));
        request.getMessages().add(new ChatCompletionRequest.Message("user", prompt));

        // Send request to OpenAI API
        return client.post()
                .uri(url)
                .header("Authorization", "Bearer " + apiKey)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(ChatCompletionResponse.class)
                .map(response -> {
                    // Extract the response message content
                    String responseMessage = response.getChoices().get(0).getMessage().getContent();
                    return new MyResponse(responseMessage);
                });
    }
}