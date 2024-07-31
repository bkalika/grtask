package com.atipera.gr.service;

import com.atipera.gr.dto.*;
import com.github.tomakehurst.wiremock.WireMockServer;
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
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Created by bogdan.kalika@gmail.com
 * Date: 7/31/2024
 */
//@ExtendWith({SpringExtension.class})
//@ExtendWith({SpringExtension.class, WireMockExtension.class})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWireMock(port = 8443)
//@ExtendWith(WireMockExtension.class)
@DirtiesContext
public class GitHubServiceWireMockTest {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private GitHubService gitHubService;

    @Autowired
    private WireMockServer wireMockServer;

    @BeforeEach
    void setUp() {
        WireMock.reset();
    }

    @Test
    void getNotForkedRepositories_ShouldReturnNonForkedRepos() {
        wireMockServer.stubFor(get(urlEqualTo("/users/testuser/repos?type=all"))
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
                                    "fork": true,
                                    "branches_url": "https://api.github.com/repos/testuser/Repo2/branches{/branch}"
                                },
                                {
                                    "name": "Repo3",
                                    "owner": {"login": "testuser"},
                                    "fork": false,
                                    "branches_url": "https://api.github.com/repos/testuser/Repo3/branches{/branch}"
                                }
                            ]
                        """)
                ));

        var resultMono = gitHubService.getNotForkedRepositories("testuser");

        Repository expected = new Repository("Repo1", new Repository.Owner("testuser"), false, "https://api.github.com/repos/testuser/Repo1/branches{/branch}");
        Repository expected2 = new Repository("Repo3", new Repository.Owner("testuser"), false, "https://api.github.com/repos/testuser/Repo3/branches{/branch}");

        StepVerifier.create(resultMono)
                .expectNext(expected)
                .expectNext(expected2)
                .verifyComplete();
    }

    @Test
    void getBranchesForRepository_ShouldReturnBranchesForRepository() {
        wireMockServer.stubFor(get(urlEqualTo("/repos/testuser/Repo1/branches"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody("""
                            [
                                {
                                    "name": "chapter",
                                    "commit": {
                                        "sha": "44081",
                                        "url": "https://api.github.com/repos/bkalika/backend-social-network/commits/44081bcaf8b08e6d620ec72e1312207eee2f6203"
                                    },
                                    "protected": false
                                },
                                {
                                    "name": "main",
                                    "commit": {
                                        "sha": "612df450a",
                                        "url": "https://api.github.com/repos/bkalika/backend-social-network/commits/612df450aae881a18139846139b696609bdb043c"
                                    },
                                    "protected": false
                                }
                            ]
                        """)
                ));

        Repository repository = new Repository("Repo1", new Repository.Owner("testuser"), false, "http://localhost:8443/repos/testuser/Repo1/branches{/branch}");
        var resultMono = gitHubService.getBranchesForRepository(repository);

        Branch expected = new Branch("chapter", new Branch.Commit("44081"));
        Branch expected2 = new Branch("main", new Branch.Commit("612df450a"));

        StepVerifier.create(resultMono)
                .expectNext(expected)
                .expectNext(expected2)
                .verifyComplete();
    }
}
