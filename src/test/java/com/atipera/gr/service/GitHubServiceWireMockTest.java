package com.atipera.gr.service;

import com.atipera.gr.dto.Branch;
import com.atipera.gr.dto.Repository;
import com.atipera.gr.dto.RepositoryResponseDto;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.test.annotation.DirtiesContext;
import reactor.test.StepVerifier;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

/**
 * Created by bogdan.kalika@gmail.com
 * Date: 7/31/2024
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWireMock(port = 8443)
@DirtiesContext
public class GitHubServiceWireMockTest {

    private final GitHubService gitHubService;

    private final WireMockServer wireMockServer;

    @Autowired
    public GitHubServiceWireMockTest(GitHubService gitHubService, WireMockServer wireMockServer) {
        this.gitHubService = gitHubService;
        this.wireMockServer = wireMockServer;
    }

    @BeforeEach
    void setUp() {
        WireMock.reset();
    }

    @Test
    void getNotForkedRepositories_ShouldReturnNonForkedRepos() throws IOException {
        var jsonResponse = new String(Files.readAllBytes(Paths.get("src/test/resources/repos.json")));
        wireMockServer.stubFor(get(urlPathMatching("/users/.+/repos"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody(jsonResponse)
                ));

        var resultFlux = gitHubService.getNotForkedRepositories("testuser");

        var repository1Expected = new Repository("Repo1", new Repository.Owner("testuser"), false, "http://localhost:8443/repos/testuser/Repo1/branches{/branch}");
        var repository2Expected = new Repository("Repo3", new Repository.Owner("testuser"), false, "http://localhost:8443/repos/testuser/Repo3/branches{/branch}");

        StepVerifier.create(resultFlux)
                .expectNext(repository1Expected)
                .expectNext(repository2Expected)
                .verifyComplete();
    }

    @Test
    void getBranchesForRepository_ShouldReturnBranchesForRepository() throws IOException {
        var jsonResponse = new String(Files.readAllBytes(Paths.get("src/test/resources/branches.json")));
        wireMockServer.stubFor(get(urlPathMatching("/repos/.+/.+/branches"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody(jsonResponse)
                ));

        var repository = new Repository("Repo1", new Repository.Owner("testuser"), false, "http://localhost:8443/repos/testuser/Repo1/branches{/branch}");
        var resultFlux = gitHubService.getBranchesForRepository(repository);

        var branch1Expected = new Branch("chapter", new Branch.Commit("44081"));
        var branch2Expected = new Branch("main", new Branch.Commit("612df450a"));

        StepVerifier.create(resultFlux)
                .expectNext(branch1Expected)
                .expectNext(branch2Expected)
                .verifyComplete();
    }

    @Test
    void getRepositoriesWithBranches_ShouldReturnRepositoriesWithBranches() throws IOException {
        var jsonResponseRepos = new String(Files.readAllBytes(Paths.get("src/test/resources/repos.json")));
        var jsonResponseBranches = new String(Files.readAllBytes(Paths.get("src/test/resources/branches.json")));
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

        var resultFlux = gitHubService.getRepositoriesWithBranches("testuser");

        var branchResponseDto = new RepositoryResponseDto.BranchResponseDto("chapter", "44081");
        var branchResponseDto2 = new RepositoryResponseDto.BranchResponseDto("main", "612df450a");
        var branchResponseDtos = List.of(branchResponseDto, branchResponseDto2);
        var expected = new RepositoryResponseDto("Repo1", "testuser", branchResponseDtos);
        var expected2 = new RepositoryResponseDto("Repo3", "testuser", branchResponseDtos);

        StepVerifier.create(resultFlux)
                .expectNext(expected)
                .expectNext(expected2)
                .verifyComplete();
    }
}
