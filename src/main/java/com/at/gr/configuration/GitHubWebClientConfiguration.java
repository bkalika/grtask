package com.at.gr.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

import static com.at.gr.handler.GlobalExceptionHandler.webClientErrorHandler;

/**
 * Created by bogdan.kalika@gmail.com
 * Date: 7/29/2024
 */
@Configuration
public class GitHubWebClientConfiguration {

    @Value("${service.github.url}")
    private String gitHubUrl;

    @Value("${service.github.token}")
    private String token;

    @Bean
    public WebClient webClient() {
        return WebClient.builder()
                .filter(webClientErrorHandler())
                .baseUrl(gitHubUrl)
                .defaultHeader("Accept", "application/vnd.github+json")
                .defaultHeader("Authorization", "Bearer " + token)
                .build();
    }
}
