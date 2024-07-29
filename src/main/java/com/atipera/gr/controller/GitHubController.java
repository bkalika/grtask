package com.atipera.gr.controller;

import com.atipera.gr.dto.RepositoryResponseDto;
import com.atipera.gr.exception.ApplicationException;
import com.atipera.gr.service.IGitHubService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.util.regex.Pattern;

/**
 * Created by bogdan.kalika@gmail.com
 * Date: 7/26/2024
 */
@RestController
@RequestMapping("/api/v1")
public class GitHubController {

    private static final Pattern USERNAME_PATTERN = Pattern.compile("^[a-zA-Z0-9_-]{3,30}$");

    private final IGitHubService iGitHubService;

    @Autowired
    public GitHubController(IGitHubService iGitHubService) {
        this.iGitHubService = iGitHubService;
    }

    @GetMapping(value = "/users/{username}/repositories")
    public Flux<RepositoryResponseDto> userRepositories(@PathVariable String username,
                                                        @RequestHeader("Accept") String accept) {
        validateUsername(username);
        return iGitHubService.getRepositoriesWithBranches(username);
    }

    private static void validateUsername(String username) {
        if (!USERNAME_PATTERN.matcher(username).matches()) {
            throw new ApplicationException(HttpStatus.BAD_REQUEST, "Invalid username");
        }
    }
}
