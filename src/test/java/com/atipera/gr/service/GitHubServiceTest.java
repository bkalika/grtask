package com.atipera.gr.service;

import com.atipera.gr.dto.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class GitHubServiceTest {

    private GitHubService gitHubService;

    @Mock
    private WebClient webClientMock;

    @Mock
    private WebClient.RequestHeadersSpec requestHeadersSpecMock;

    @Mock
    private WebClient.RequestHeadersUriSpec requestHeadersUriSpecMock;

    @Mock
    private WebClient.ResponseSpec responseSpecMock;

    @BeforeEach
    void setUp() {
        gitHubService = new GitHubService(webClientMock);
    }

    @Test
    void getNotForkedRepositories_ShouldReturnNonForkedRepos() {
        when(webClientMock.get()).thenReturn(requestHeadersUriSpecMock);
        when(requestHeadersUriSpecMock.uri(any(Function.class))).thenReturn(requestHeadersSpecMock);
        when(requestHeadersSpecMock.retrieve()).thenReturn(responseSpecMock);
        when(responseSpecMock.bodyToFlux(Repository.class)).thenReturn(Flux.fromIterable(getRepositories()));

        Flux<Repository> result = gitHubService.getNotForkedRepositories("testuser");

        StepVerifier.create(result)
                .assertNext(repository -> {
                    assertEquals("Repo1", repository.name());
                    assertEquals("testuser", repository.owner().login());
                    assertFalse(repository.fork());
                    assertEquals("https://api.github.com/repos/Repo1", repository.branchesUrl());
                })
                .assertNext(repository -> {
                    assertEquals("Repo2", repository.name());
                    assertEquals("testuser", repository.owner().login());
                    assertFalse(repository.fork());
                    assertEquals("https://api.github.com/repos/Repo2", repository.branchesUrl());
                })
                .expectComplete()
                .verify();
    }

    @Test
    void getBranchesForRepository_ShouldReturnBranchesForRepository() {
        when(webClientMock.get()).thenReturn(requestHeadersUriSpecMock);
        when(requestHeadersUriSpecMock.uri(anyString())).thenReturn(requestHeadersSpecMock);
        when(requestHeadersSpecMock.retrieve()).thenReturn(responseSpecMock);
        when(responseSpecMock.bodyToFlux(Branch.class)).thenReturn(Flux.fromIterable(getBranches()));

        Mono<RepositoryResponseDto> result = gitHubService.getBranchesForRepository(getRepositories().getFirst());

        StepVerifier.create(result)
                .expectNextMatches(repositoryResponseDto ->
                        "Repo1".equals(repositoryResponseDto.repositoryName()) &&
                                "testuser".equals(repositoryResponseDto.ownerLogin()) &&
                                getBranches().getFirst().name().equals(repositoryResponseDto.branches().getFirst().name()) &&
                                getBranches().get(1).name().equals(repositoryResponseDto.branches().get(1).name())
                )
                .expectComplete()
                .verify();
    }

    @Test
    void getRepositoriesWithBranches_ShouldReturnRepositoriesWithBranches() {
        // for getNotForkedRepositories
        when(webClientMock.get()).thenReturn(requestHeadersUriSpecMock);
        when(requestHeadersUriSpecMock.uri(any(Function.class))).thenReturn(requestHeadersSpecMock);
        when(requestHeadersSpecMock.retrieve()).thenReturn(responseSpecMock);
        when(responseSpecMock.bodyToFlux(Repository.class)).thenReturn(Flux.fromIterable(getRepositories()));

        // for getBranchesForRepository
        when(webClientMock.get()).thenReturn(requestHeadersUriSpecMock);
        when(requestHeadersUriSpecMock.uri(anyString())).thenReturn(requestHeadersSpecMock);
        when(requestHeadersSpecMock.retrieve()).thenReturn(responseSpecMock);
        when(responseSpecMock.bodyToFlux(Branch.class)).thenReturn(Flux.fromIterable(getBranches()));

        Flux<RepositoryResponseDto> result = gitHubService.getRepositoriesWithBranches("testuser");

        StepVerifier.create(result)
                .assertNext(rrd -> {
                    assertEquals("Repo1", rrd.repositoryName());
                    assertEquals("testuser", rrd.ownerLogin());
                    assertEquals(getBranches().getFirst().name(), rrd.branches().getFirst().name());
                    assertEquals(getBranches().getFirst().commit().sha(), rrd.branches().getFirst().lastCommitSha());
                })
                .assertNext(rrd -> {
                    assertEquals("Repo2", rrd.repositoryName());
                    assertEquals("testuser", rrd.ownerLogin());
                    assertEquals(getBranches().getFirst().name(), rrd.branches().getFirst().name());
                    assertEquals(getBranches().getFirst().commit().sha(), rrd.branches().getFirst().lastCommitSha());
                })
                .expectComplete()
                .verify();
    }

    private List<Repository> getRepositories() {
        Owner owner = new Owner("testuser");

        Repository repo1 = new Repository("Repo1", owner, false, "https://api.github.com/repos/Repo1");

        Repository repo2 = new Repository("Repo2", owner, false, "https://api.github.com/repos/Repo2");

        return Arrays.asList(repo1, repo2);
    }

    private List<Branch> getBranches() {
        Branch.Commit commit1 = new Branch.Commit("123lj123");
        Branch branch1 = new Branch("main", commit1);

        Branch.Commit commit2 = new Branch.Commit("4123jkhkj12");
        Branch branch2 = new Branch("develop", commit2);

        return Arrays.asList(branch1, branch2);
    }
}
