package com.atipera.gr.controller;

import com.atipera.gr.dto.RepositoryResponseDto;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Created by bogdan.kalika@gmail.com
 * Date: 7/31/2024
 */
@ExtendWith({SpringExtension.class, WireMockExtension.class})
@AutoConfigureWireMock(port = 8443)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class GitHubControllerWireMockTest {

    @LocalServerPort
    private int port;

    @Autowired
    private WebTestClient webTestClient;

    @BeforeEach
    void setUp() {
        WireMock.reset();
    }

    @Test
    void getNotForkedRepositories_ShouldReturnNonForkedRepos() {
        stubFor(get(urlPathMatching("/users/testuser/repos"))
//                .withQueryParam("type", equalTo("all"))
                .withHeader("Accept", containing("application/vnd.github+json"))
                .withHeader("Authorization", containing("Bearer"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody("""
                                    [
                                        {
                                            "name": "Repo1",
                                            "owner": {"login": "testuser"},
                                            "fork": false,
                                            "branches_url": "https://api.github.com/repos/testuser/Repo1/branches{/branch}"
                                        },
                                        {
                                            "name": "Repo2",
                                            "owner": {"login": "testuser"},
                                            "fork": false,
                                            "branches_url": "https://api.github.com/repos/testuser/Repo2/branches{/branch}"
                                        },
                                        {
                                            "name": "Repo3",
                                            "owner": {"login": "testuser"},
                                            "fork": true,
                                            "branches_url": "https://api.github.com/repos/testuser/Repo3/branches{/branch}"
                                        }
                                    ]
                                """)
                ));

        // Use WebTestClient to make the request
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
                    assertEquals("Repo1", repositories.get(0).repositoryName());
                    assertEquals("testuser", repositories.get(0).ownerLogin());
                    assertEquals("Repo2", repositories.get(1).repositoryName());
                    assertEquals("testuser", repositories.get(1).ownerLogin());
                });
    }
}
