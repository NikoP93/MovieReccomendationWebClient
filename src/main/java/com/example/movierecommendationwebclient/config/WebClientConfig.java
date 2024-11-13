package com.example.movierecommendationwebclient.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {



    @Bean
    public WebClient tmdbWebClient() {
        return WebClient.builder()
                .baseUrl("https://api.themoviedb.org/3")
                .build();

    }

}
