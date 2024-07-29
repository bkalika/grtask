package com.atipera.gr.service;

import com.atipera.gr.dto.Branch;
import com.atipera.gr.dto.Owner;
import com.atipera.gr.dto.Repository;
import com.atipera.gr.dto.RepositoryResponseDto;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
        when(requestHeadersUriSpecMock.uri(anyString(), anyString())).thenReturn(requestHeadersSpecMock);
        when(requestHeadersSpecMock.header(anyString(), any())).thenReturn(requestHeadersSpecMock);
        when(requestHeadersSpecMock.header(anyString(), anyString())).thenReturn(requestHeadersSpecMock);
        when(requestHeadersSpecMock.retrieve()).thenReturn(responseSpecMock);
        when(responseSpecMock.bodyToFlux(Repository.class)).thenReturn(Flux.fromIterable(getRepositories()));

        Flux<Repository> result = gitHubService.getNotForkedRepositories("testuser");

        StepVerifier.create(result)
                .assertNext(repository -> {
                    assertEquals("Repo1", repository.getName());
                    assertEquals("testuser", repository.getOwner().getLogin());
                    assertFalse(repository.isFork());
                    assertEquals("https://api.github.com/repos/Repo1", repository.getBranchesUrl());
                })
                .assertNext(repository -> {
                    assertEquals("Repo2", repository.getName());
                    assertEquals("testuser", repository.getOwner().getLogin());
                    assertFalse(repository.isFork());
                    assertEquals("https://api.github.com/repos/Repo2", repository.getBranchesUrl());
                })
                .expectComplete()
                .verify();
    }

    @Test
    void getBranchesForRepository_ShouldReturnBranchesForRepository() {
        when(webClientMock.get()).thenReturn(requestHeadersUriSpecMock);
        when(requestHeadersUriSpecMock.uri(anyString())).thenReturn(requestHeadersSpecMock);
        when(requestHeadersSpecMock.header(anyString(), any())).thenReturn(requestHeadersSpecMock);
        when(requestHeadersSpecMock.header(anyString(), anyString())).thenReturn(requestHeadersSpecMock);
        when(requestHeadersSpecMock.retrieve()).thenReturn(responseSpecMock);
        when(responseSpecMock.bodyToFlux(Branch.class)).thenReturn(Flux.fromIterable(getBranches()));

        Mono<RepositoryResponseDto> result = gitHubService.getBranchesForRepository(getRepositories().getFirst());

        StepVerifier.create(result)
                .expectNextMatches(repositoryResponseDto ->
                    "Repo1".equals(repositoryResponseDto.repositoryName()) &&
                    "testuser".equals(repositoryResponseDto.ownerLogin()) &&
                            getBranches().getFirst().getName().equals(repositoryResponseDto.branches().getFirst().name()) &&
                        getBranches().get(1).getName().equals(repositoryResponseDto.branches().get(1).name())
                )
                .expectComplete()
                .verify();
    }

    @Test
    void getRepositoriesWithBranches_ShouldReturnRepositoriesWithBranches() {
        // for getNotForkedRepositories
        when(webClientMock.get()).thenReturn(requestHeadersUriSpecMock);
        when(requestHeadersUriSpecMock.uri(anyString(), anyString())).thenReturn(requestHeadersSpecMock);
        when(requestHeadersSpecMock.header(anyString(), any())).thenReturn(requestHeadersSpecMock);
//        when(requestHeadersSpecMock.header(anyString(), anyString())).thenReturn(requestHeadersSpecMock);
        when(requestHeadersSpecMock.retrieve()).thenReturn(responseSpecMock);
        when(responseSpecMock.bodyToFlux(Repository.class)).thenReturn(Flux.fromIterable(getRepositories()));

        // for getBranchesForRepository
        when(webClientMock.get()).thenReturn(requestHeadersUriSpecMock);
        when(requestHeadersUriSpecMock.uri(anyString())).thenReturn(requestHeadersSpecMock);
        when(requestHeadersSpecMock.header(anyString(), any())).thenReturn(requestHeadersSpecMock);
//        when(requestHeadersSpecMock.header(anyString(), anyString())).thenReturn(requestHeadersSpecMock);
        when(requestHeadersSpecMock.retrieve()).thenReturn(responseSpecMock);
        when(responseSpecMock.bodyToFlux(Branch.class)).thenReturn(Flux.fromIterable(getBranches()));

        Flux<RepositoryResponseDto> result = gitHubService.getRepositoriesWithBranches("testuser");

        StepVerifier.create(result)
                .assertNext(rrd -> {
                    assertEquals("Repo1", rrd.repositoryName());
                    assertEquals("testuser", rrd.ownerLogin());
                    assertEquals(getBranches().getFirst().getName(), rrd.branches().getFirst().name());
                    assertEquals(getBranches().getFirst().getCommit().getSha(), rrd.branches().getFirst().lastCommitSha());
                })
                .assertNext(rrd -> {
                    assertEquals("Repo2", rrd.repositoryName());
                    assertEquals("testuser", rrd.ownerLogin());
                    assertEquals(getBranches().getFirst().getName(), rrd.branches().getFirst().name());
                    assertEquals(getBranches().getFirst().getCommit().getSha(), rrd.branches().getFirst().lastCommitSha());
                })
                .expectComplete()
                .verify();
    }

    private List<Repository> getRepositories() {
        Owner owner = new Owner();
        owner.setLogin("testuser");

        Repository repo1 = new Repository();
        repo1.setName("Repo1");
        repo1.setOwner(owner);
        repo1.setFork(false);
        repo1.setBranchesUrl("https://api.github.com/repos/Repo1");

        Repository repo2 = new Repository();
        repo2.setName("Repo2");
        repo2.setOwner(owner);
        repo2.setFork(false);
        repo2.setBranchesUrl("https://api.github.com/repos/Repo2");

        return Arrays.asList(repo1, repo2);
    }

    private List<Branch> getBranches() {
        Branch branch1 = new Branch();
        branch1.setName("main");
        Branch.Commit commit1 = new Branch.Commit();
        commit1.setSha("123lj123");
        branch1.setCommit(commit1);

        Branch branch2 = new Branch();
        branch2.setName("develop");
        Branch.Commit commit2 = new Branch.Commit();
        commit2.setSha("4123jkhkj12");
        branch2.setCommit(commit2);

        return Arrays.asList(branch1, branch2);
    }
}
