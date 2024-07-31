package com.at.gr.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by bogdan.kalika@gmail.com
 * Date: 7/26/2024
 */
public record Repository(String name, Owner owner, boolean fork, @JsonProperty("branches_url") String branchesUrl) {

    public record Owner(String login) {
    }
}
