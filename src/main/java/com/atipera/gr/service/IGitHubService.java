package com.atipera.gr.service;

import com.atipera.gr.dto.Repository;
import com.atipera.gr.dto.RepositoryResponseDto;
import reactor.core.publisher.Flux;

public interface IGitHubService {

    Flux<RepositoryResponseDto> getRepositoriesWithBranches(String username);

    Flux<Repository> getNotForkedRepositories(String username);
}
