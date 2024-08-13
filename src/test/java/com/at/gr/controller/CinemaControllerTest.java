package com.at.gr.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.time.LocalDateTime;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
class CinemaControllerTest {

    private final WebTestClient webTestClient;

    @Autowired
    public CinemaControllerTest(WebTestClient webTestClient) {
        this.webTestClient = webTestClient;
    }

    @Test
    public void movieController() {
        LocalDateTime localDateTimeFrom = LocalDateTime.now();
        LocalDateTime localDateTimeTo = LocalDateTime.now();
        webTestClient.get()
                .uri(uriBuilder ->
                        uriBuilder
                                .path("/api/v1/movies")
                                .queryParam("from", localDateTimeFrom)
                                .queryParam("to", localDateTimeTo)
                                .build())
                .exchange()
                .expectStatus().isOk();
    }
}
