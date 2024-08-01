package com.at.gr.service;

import com.at.gr.dto.RepositoryResponseDto;
import reactor.core.publisher.Flux;

public interface IGitHubService {

    Flux<RepositoryResponseDto> getRepositoriesWithBranches(String username);
}
