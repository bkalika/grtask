package com.atipera.gr.controller;

import com.atipera.gr.dto.ApplicationExceptionDto;
import com.atipera.gr.dto.BranchResponseDto;
import com.atipera.gr.dto.RepositoryResponseDto;
import com.atipera.gr.service.GitHubService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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
        List<BranchResponseDto> branches = new ArrayList<>();
        branches.add(new BranchResponseDto("master", "asdf"));
        branches.add(new BranchResponseDto("branch1", "asdfasd"));
        RepositoryResponseDto repositoryResponseDto = new RepositoryResponseDto("Repo1", "testlogin", branches);

        when(gitHubService.getRepositoriesWithBranches(anyString())).thenReturn(Flux.just(repositoryResponseDto));

        webClient.get()
                .uri("/api/v1/users/testuser/repositories")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(RepositoryResponseDto.class)
                .value(response -> {
                    assertNotNull(response);
                    assertEquals(1, response.size());
                    RepositoryResponseDto dto = response.getFirst();
                    assertEquals("Repo1", dto.repositoryName());
                    assertEquals("testlogin", dto.ownerLogin());
                    assertEquals(branches, dto.branches());
                });

        verify(gitHubService, times(1)).getRepositoriesWithBranches(anyString());
    }

    @Test
    void testUserRepositories_when_accept_is_null() {
        webClient.get()
                .uri("/api/v1/users/testuser/repositories")
                .exchange()
                .expectStatus().is5xxServerError()
                .expectBody(ApplicationExceptionDto.class)
                .value(response -> {
                    assertNotNull(response);
                    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), response.getStatus());
                    assertEquals("400 BAD_REQUEST \"Required header 'Accept' is not present.\"", response.getMessage());
                });

        verify(gitHubService, times(0)).getRepositoriesWithBranches(anyString());
    }

    @Test
    void testUserRepositories_when_wrong_username() {
        webClient.get()
                .uri("/api/v1/users/te/repositories")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().is4xxClientError()
                .expectBody(ApplicationExceptionDto.class)
                .value(response -> {
                    assertNotNull(response);
                    assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
                    assertEquals("Invalid username", response.getMessage());
                });

        verify(gitHubService, times(0)).getRepositoriesWithBranches(anyString());
    }
}
