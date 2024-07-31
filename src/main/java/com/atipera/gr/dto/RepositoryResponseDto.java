package com.atipera.gr.dto;

import java.util.List;

/**
 * Created by bogdan.kalika@gmail.com
 * Date: 7/26/2024
 */
public record RepositoryResponseDto(String repositoryName, String ownerLogin, List<BranchResponseDto> branches) {

    public record BranchResponseDto(String name, String lastCommitSha) {
    }
}
