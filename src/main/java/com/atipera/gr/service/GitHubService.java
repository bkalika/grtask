package com.atipera.gr.service;

import com.atipera.gr.dto.Branch;
import com.atipera.gr.dto.BranchResponseDto;
import com.atipera.gr.dto.Repository;
import com.atipera.gr.dto.RepositoryResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Comparator;

/**
 * Created by bogdan.kalika@gmail.com
 * Date: 7/26/2024
 */
@Slf4j
@Service
public class GitHubService implements IGitHubService {

    private final WebClient webClient;

    @Value("${service.github.token}")
    private String token;

    public GitHubService(WebClient webClient) {
        this.webClient = webClient;
    }

    @Override
    public Flux<RepositoryResponseDto> getRepositoriesWithBranches(String username) {
        return getNotForkedRepositories(username)
                .flatMap(this::getBranchesForRepository)
                .sort(Comparator.comparing(RepositoryResponseDto::repositoryName));
    }

    @Override
    public Flux<Repository> getNotForkedRepositories(String username) {
        return this.webClient.get().uri("/users/{username}/repos?type=all", username)
                .header("Accept", "application/vnd.github+json")
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .bodyToFlux(Repository.class)
                .filter(repository -> !repository.isFork());
    }

    public Mono<RepositoryResponseDto> getBranchesForRepository(Repository repository) {
        String branchesUrl = repository.getBranchesUrl().replace("{/branch}", "");
        return this.webClient.get()
                .uri(branchesUrl)
                .header("Accept", "application/vnd.github+json")
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .bodyToFlux(Branch.class)
                .map(branch -> new BranchResponseDto(branch.getName(), branch.getCommit().getSha()))
                .collectList()
                .map(branches -> new RepositoryResponseDto(repository.getName(), repository.getOwner().getLogin(), branches));
    }
}
