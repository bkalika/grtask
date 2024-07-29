package com.atipera.gr.controller;

import com.atipera.gr.dto.Branch;
import com.atipera.gr.dto.RepositoryResponseDto;
import com.atipera.gr.service.GitHubService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@WebFluxTest(controllers = GitHubController.class)
class GitHubControllerTest {

    @MockBean
    private GitHubService gitHubService;

    private final WebTestClient webClient;

    @Autowired
    public GitHubControllerTest(WebTestClient webClient) {
        this.webClient = webClient;
    }

    @Test
    void testUserRepositories() {
        List<Branch> branches = new ArrayList<>();
        branches.add(new Branch("master", "master"));
        branches.add(new Branch("branch1", "branch1"));
        RepositoryResponseDto repositoryResponseDto = new RepositoryResponseDto("Repo1", "testlogin", branches);

        when(gitHubService.getRepositoriesWithBranches(anyString())).thenReturn(Flux.just(repositoryResponseDto));

        webClient.get()
                .uri("/api/v1/users/testuser/repositories")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk();

        verify(gitHubService, times(1)).getRepositoriesWithBranches(anyString());
    }

    @Test
    void testUserRepositories_when_content_is_null() {
        webClient.get()
                .uri("/api/v1/users/testuser/repositories")
                .exchange()
                .expectStatus().is5xxServerError(); // FIXME: make 400 status

        verify(gitHubService, times(0)).getRepositoriesWithBranches(anyString());
    }
}
