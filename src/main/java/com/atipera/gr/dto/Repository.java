package com.atipera.gr.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by bogdan.kalika@gmail.com
 * Date: 7/26/2024
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record Repository(String name, Owner owner, boolean fork, @JsonProperty("branches_url") String branchesUrl) {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Owner(String login) {
    }
}
