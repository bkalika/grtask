package com.at.gr.controller;

import com.at.gr.dto.ApplicationExceptionDto;
import com.at.gr.dto.RepositoryResponseDto;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Created by bogdan.kalika@gmail.com
 * Date: 7/31/2024
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWireMock(port = 8443)
@DirtiesContext
public class GitHubControllerTest {

    private final WebTestClient webTestClient;

    private final WireMockServer wireMockServer;

    @Autowired
    public GitHubControllerTest(WebTestClient webTestClient, WireMockServer wireMockServer) {
        this.webTestClient = webTestClient;
        this.wireMockServer = wireMockServer;
    }

    @BeforeEach
    void setUp() {
        WireMock.reset();
    }

    @Test
    void userRepositories_ShouldReturnUserRepositories() throws IOException {
        String jsonResponseRepos = new String(Files.readAllBytes(Paths.get("src/test/resources/repos.json")));
        String jsonResponseBranches = new String(Files.readAllBytes(Paths.get("src/test/resources/branches.json")));
        wireMockServer.stubFor(get(urlPathMatching("/users/.+/repos"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody(jsonResponseRepos)
                ));

        wireMockServer.stubFor(get(urlPathMatching("/repos/.+/.+/branches"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody(jsonResponseBranches)
                ));

        webTestClient.get()
                .uri("/api/v1/users/{username}/repositories", "testuser")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(RepositoryResponseDto.class)
                .hasSize(2)
                .consumeWith(response -> {
                    List<RepositoryResponseDto> repositories = response.getResponseBody();
                    assert repositories != null;
                    assertEquals("Repo1", repositories.getFirst().repositoryName());
                    assertEquals("testuser", repositories.getFirst().ownerLogin());
                    assertEquals("chapter", repositories.getFirst().branches().getFirst().name());
                    assertEquals("44081", repositories.getFirst().branches().getFirst().lastCommitSha());
                    assertEquals("main", repositories.getFirst().branches().get(1).name());
                    assertEquals("612df450a", repositories.getFirst().branches().get(1).lastCommitSha());

                    assertEquals("Repo3", repositories.get(1).repositoryName());
                    assertEquals("testuser", repositories.get(1).ownerLogin());
                    assertEquals("chapter", repositories.get(1).branches().getFirst().name());
                    assertEquals("44081", repositories.get(1).branches().getFirst().lastCommitSha());
                    assertEquals("main", repositories.get(1).branches().get(1).name());
                    assertEquals("612df450a", repositories.get(1).branches().get(1).lastCommitSha());
                });
    }

    @Test
    void userRepositories_when_acceptHeader_unequal() {
        webTestClient.get()
                .uri("/api/v1/users/te/repositories")
                .accept(MediaType.ALL)
                .exchange()
                .expectStatus().is4xxClientError()
                .expectBody(ApplicationExceptionDto.class)
                .value(response -> {
                    assertNotNull(response);
                    assertEquals(HttpStatus.BAD_REQUEST.value(), response.status());
                    assertEquals("Invalid Accept header", response.message());
                });
    }
}
