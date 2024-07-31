package com.atipera.gr.controller;

import com.atipera.gr.dto.RepositoryResponseDto;
import com.atipera.gr.exception.ApplicationException;
import com.atipera.gr.service.GitHubService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

/**
 * Created by bogdan.kalika@gmail.com
 * Date: 7/26/2024
 */
@RestController
@RequestMapping("/api/v1")
public class GitHubController {

    private final GitHubService gitHubService;

    @Autowired
    public GitHubController(GitHubService gitHubService) {
        this.gitHubService = gitHubService;
    }

    @GetMapping(value = "/users/{username}/repositories")
    public Flux<RepositoryResponseDto> userRepositories(@PathVariable String username,
                                                        @RequestHeader("Accept") String accept) {
        validateAcceptHeader(accept);
        return gitHubService.getRepositoriesWithBranches(username);
    }

    private static void validateAcceptHeader(String accept) {
        if (!"application/json".equalsIgnoreCase(accept)) {
            throw new ApplicationException(HttpStatus.BAD_REQUEST, "Invalid Accept header");
        }
    }
}
