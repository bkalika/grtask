package com.atipera.gr.service;

import com.atipera.gr.dto.Branch;
import com.atipera.gr.dto.BranchResponseDto;
import com.atipera.gr.dto.Repository;
import com.atipera.gr.dto.RepositoryResponseDto;
import lombok.extern.slf4j.Slf4j;
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
        return this.webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/users/{username}/repos")
                        .queryParam("type", "all")
                        .build(username))
                .retrieve()
                .bodyToFlux(Repository.class)
                .filter(repository -> !repository.fork());
    }

    public Mono<RepositoryResponseDto> getBranchesForRepository(Repository repository) {
        String branchesUrl = repository.branchesUrl().replace("{/branch}", "");
        return this.webClient.get()
                .uri(branchesUrl)
                .retrieve()
                .bodyToFlux(Branch.class)
                .map(branch -> new BranchResponseDto(branch.name(), branch.commit().sha()))
                .collectList()
                .map(branches -> new RepositoryResponseDto(repository.name(), repository.owner().login(), branches));
    }
}
