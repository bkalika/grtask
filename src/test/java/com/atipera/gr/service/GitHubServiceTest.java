package com.atipera.gr.service;

import com.atipera.gr.dto.Owner;
import com.atipera.gr.dto.Repository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
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
    private WebClient.RequestBodySpec requestBodyMock;

    @Mock
    private WebClient.RequestBodyUriSpec requestBodyUriMock;

    @Mock
    private WebClient.ResponseSpec responseSpecMock;

    @BeforeEach
    void setUp() {
        gitHubService = new GitHubService(webClientMock);
    }

//    @BeforeEach
//    void setUp() {
//        WebClient.Builder webClientBuilder = mock(WebClient.Builder.class);
//        webClient = mock(WebClient.class);
//        when(webClientBuilder.baseUrl(any())).thenReturn(webClientBuilder);
//        when(webClientBuilder.build()).thenReturn(webClient);
//        this.gitHubService = new GitHubService(webClient);
//    }

    @Test
    void getNotForkedRepositories_ShouldReturnNonForkedRepos() {
        WebClient.RequestHeadersUriSpec requestHeadersUriSpec = mock(WebClient.RequestHeadersUriSpec.class);
        WebClient.RequestHeadersSpec requestHeadersSpec = mock(WebClient.RequestHeadersSpec.class);
        WebClient.RequestHeadersSpec<?> requestHeadersSpecWithHeader = mock(WebClient.RequestHeadersSpec.class);
        WebClient.ResponseSpec responseSpec = mock(WebClient.ResponseSpec.class);

        when(webClientMock.get()).thenReturn(requestHeadersUriSpec);
//        when(requestHeadersSpec.headers(any())).thenReturn(requestHeadersSpec);
//        when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersUriSpec.uri(anyString(), anyString())).thenReturn(requestHeadersSpec);
//        when(requestHeadersSpec.header(anyString())).thenReturn(requestHeadersSpecWithHeader);
        when(requestHeadersSpec.header(anyString(), any())).thenReturn(requestHeadersSpecWithHeader);
        when(requestHeadersSpec.header(anyString(), anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);

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

        when(responseSpec.bodyToFlux(Repository.class)).thenReturn(Flux.fromIterable(Arrays.asList(repo1, repo2)));

        // Act
        Flux<Repository> result = gitHubService.getNotForkedRepositories("testuser");

        // Assert
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

    // https://github.com/eugenp/tutorials/blob/master/spring-reactive-modules/spring-reactive-client/src/test/java/com/baeldung/reactive/service/EmployeeServiceUnitTest.java
    @Test
    void getNotForked() {
        when(webClientMock.get()).thenReturn(requestHeadersUriSpecMock);
        when(requestHeadersUriSpecMock.uri(anyString(), anyString())).thenReturn(requestHeadersUriSpecMock);
//        when(requestHeadersSpecMock.headers(any())).thenReturn(requestHeadersUriSpecMock);
        when(requestHeadersSpecMock.header(anyString(), any(String[].class))).thenReturn(requestHeadersUriSpecMock);
        when(requestHeadersSpecMock.retrieve()).thenReturn(responseSpecMock);

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
        when(responseSpecMock.bodyToFlux(Repository.class)).thenReturn(Flux.fromIterable(Arrays.asList(repo1, repo2)));
        Flux<Repository> result = gitHubService.getNotForkedRepositories("testuser");

    }

}